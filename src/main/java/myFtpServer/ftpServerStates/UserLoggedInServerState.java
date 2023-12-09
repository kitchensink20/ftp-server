package myFtpServer.ftpServerStates;

import commandHandling.*;
import enums.ServerMode;
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
    private final Socket clientSocket;
    private ServerMode serverMode;
    private ServerSocket passiveDataServerSocket;
    private Socket activeDataSocket;

    public UserLoggedInServerState(FtpServer ftpServer) {
        this.clientSocket = ftpServer.getClientSocket();
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        BaseCommandHandler commandHandler;
        switch (command) {
            case "RETR": // to retrieve file from the server
                if(serverMode.equals(ServerMode.ACTIVE))
                    commandHandler = new RetrCommandHandler(activeDataSocket);
                else if(serverMode.equals(ServerMode.PASSIVE))
                    commandHandler = new RetrCommandHandler(passiveDataServerSocket.accept());
                else
                    return new FtpResponse(425, "Can't open data connection. Choose FTP server mode");
                break;
            case "STOR": // to store file on server
                if(serverMode.equals(ServerMode.ACTIVE))
                    commandHandler = new StorCommandHandler(activeDataSocket, user.getHomeDirectory());
                else if(serverMode.equals(ServerMode.PASSIVE))
                    commandHandler = new StorCommandHandler(passiveDataServerSocket.accept(), user.getHomeDirectory());
                else
                    return new FtpResponse(425, "Can't open data connection. Choose FTP server mode");
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
                if(serverMode.equals(ServerMode.ACTIVE))
                    commandHandler = new ListCommandHandler(activeDataSocket, user.getHomeDirectory());
                else if(serverMode.equals(ServerMode.PASSIVE))
                    commandHandler = new ListCommandHandler(passiveDataServerSocket.accept(), user.getHomeDirectory());
                else
                    return new FtpResponse(425, "Can't open data connection. Choose FTP server mode");
                break;
            case "PORT": // to enter active mode
                serverMode = ServerMode.ACTIVE;
                commandHandler = new PortCommandHandler(); // !!! do not work for now
                activeDataSocket = ((PortCommandHandler) commandHandler).getDataSocket(arguments);
                break;
            case "EPRT": // to enter active mode
                serverMode = ServerMode.ACTIVE;
                commandHandler = new EprtCommandHandler(); // !!! do not work for now
                activeDataSocket = ((EprtCommandHandler) commandHandler).getDataSocket(arguments);
                break;
            case "PWD":  // to print the current working directory
                commandHandler = new PwdCommandHandler(user.getHomeDirectory());
                break;
            case "PASV":  // to enter the passive mode
                serverMode = ServerMode.PASSIVE;
                passiveDataServerSocket = new ServerSocket(0);
                commandHandler = new PasvCommandHandler(clientSocket, passiveDataServerSocket);
                break;
            case "EPSV": // to enter the extended passive mode
                serverMode = ServerMode.PASSIVE;
                passiveDataServerSocket = new ServerSocket(0);
                commandHandler = new EpsvCommandHandler(passiveDataServerSocket);
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
