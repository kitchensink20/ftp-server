package myFtpServer.ftpServerStates;

import commandHandling.*;
import model.User;
import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AdminLoggedInServerState implements FtpServerState {
    private final UI ui;
    private final Socket clientSocket;
    private StringBuilder currentDirectoryPath;
    private ServerSocket dataSocket;

    public AdminLoggedInServerState(FtpServer ftpServer, String currentDirectoryPath) {
        this.currentDirectoryPath =  new StringBuilder(currentDirectoryPath);
        this.ui = ftpServer.getUi();
        this.clientSocket = ftpServer.getClientSocket();
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        BaseCommandHandler commandHandler;
        switch (command) {
            case "RETR": // to retrieve file from the server
                commandHandler = new RetrCommandHandler(dataSocket);
                break;
            case "STOR": // to store file on server
                commandHandler = new StorCommandHandler(dataSocket);
                break;
            case "DELE": // to delete a file
                commandHandler = new DeleCommandHandler(currentDirectoryPath.toString());
                break;
            case "TYPE": //  to set the type of file to be transferred
                commandHandler = new TypeCommandHandler();
                break;
            case "CDUP": // to change to the parent of the current directory
                commandHandler = new CdupCommandHandler(currentDirectoryPath);
                break;
            case "ERPT":
                commandHandler = new ErptCommandHandler();
                break;
            case "LIST": // to list files in a directory
                commandHandler = new ListCommandHandler(dataSocket, currentDirectoryPath.toString());
                break;
            case "CWD": // to change the current directory to the specified one
                commandHandler = new CwdCommandHandler(currentDirectoryPath);
                break;
            case "PWD": // to print the current working directory
                commandHandler = new PwdCommandHandler(currentDirectoryPath.toString());
                break;
            case "PASV":
                dataSocket = new ServerSocket(0);
                commandHandler = new PasvCommandHandler(clientSocket, dataSocket);
                break;
            case "EPSV":
                dataSocket = new ServerSocket(0);
                commandHandler = new EpsvCommandHandler(dataSocket);
                break;
            case "CREATE": // to create new user; used with arguments [[username, password, isAdmin]]
                commandHandler = new CreateCommandHandler();
                break;
            case "USERS": // to list all users
                commandHandler = new UsersCommandHandler(ui);
                break;
            case "LOG": // to display log file content
                commandHandler = new LogCommandHandler(ui);
                break;
            case "ACCT": // to specify the account information
                return new FtpResponse(230, "username: " + user.getUsername() + "; has admin rights: " + user.getIsAdmin());
            case "SYST": // to display server specification
                return new FtpResponse(215, "NAME " + System.getProperty("os.name") + " VERSION " + System.getProperty("os.version"));
            case "QUIT": // to end session
                commandHandler = new QuitCommandHandler();
                dataSocket.close();
                clientSocket.close();
                break;
            default:
                return new FtpResponse(502, "Command not implemented");
        }
        return commandHandler.handleCommand(arguments, user);
    }
}
