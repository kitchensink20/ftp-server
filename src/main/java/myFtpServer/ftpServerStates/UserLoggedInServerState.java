package myFtpServer.ftpServerStates;

import commandHandling.*;
import model.User;
import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;
import userMemento.UserCaretaker;
import view.UI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class UserLoggedInServerState implements FtpServerState {
    private static final ConcurrentHashMap<Integer, UserCaretaker> userCaretakers = new ConcurrentHashMap<>();
    private final UI ui;
    private final Socket clientSocket;
    private ServerSocket dataServerSocket;

    public UserLoggedInServerState(FtpServer ftpServer) {
        this.clientSocket = ftpServer.getClientSocket();
        this.ui = ftpServer.getUi();
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        BaseCommandHandler commandHandler;
        switch (command) {
            case "RETR": // to retrieve file from the server
                commandHandler = new RetrCommandHandler(dataServerSocket);
                break;
            case "STOR": // to store file on server
                commandHandler = new StorCommandHandler();
                break;
            case "DELE": // to delete a file
                commandHandler = new DeleCommandHandler(user.getHomeDirectory());
                break;
            case "TYPE": //  to set the type of file to be transferred
                commandHandler = new TypeCommandHandler();
                break;
            case "CDUP": // to change to the parent of the current directory
                return new FtpResponse(550, "Permission denied");
            case "LIST": // to list files in a directory
                commandHandler = new ListCommandHandler(dataServerSocket, user.getHomeDirectory());
                break;
            case "ERPT":
                commandHandler = new ErptCommandHandler();
                break;
            case "PWD":  // to print the current working directory
                commandHandler = new PwdCommandHandler(user.getHomeDirectory());
                break;
            //case "CWD": // to change the current directory to the specified one
              //  return new FtpResponse(550, "Permission denied");
            case "PASV":
                dataServerSocket = new ServerSocket(0);
                commandHandler = new PasvCommandHandler(clientSocket, dataServerSocket);
                break;
            case "EPSV":
                dataServerSocket = new ServerSocket(0);
                commandHandler = new EpsvCommandHandler(dataServerSocket);
                break;
            case "ALTER": // to change user data such as username or password; used as ALTER -username [[new username]] or ALTER -password [[new password]]
                commandHandler = new AlterCommandHandler();
                break;
            case "RESTORE": // to restore user state
                commandHandler = new RestoreCommandHandler();
                break;
            case "ACCT": // to specify the account information
                return new FtpResponse(230, "username: " + user.getUsername() + "; has admin rights: " + user.getIsAdmin());
            case "SYST": // to display server specification
                return new FtpResponse(215, "NAME " + System.getProperty("os.name") + " VERSION " + System.getProperty("os.version"));
            case "QUIT": // to end session
                commandHandler = new QuitCommandHandler();
                break;
            default:
                return new FtpResponse(502, "Command not implemented");
        }
        return commandHandler.handleCommand(arguments, user);
    }

    public static UserCaretaker getUserCaretaker(int id) {
        return userCaretakers.computeIfAbsent(id, k -> new UserCaretaker());
    }
}
