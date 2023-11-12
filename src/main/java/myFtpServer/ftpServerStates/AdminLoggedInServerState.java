package myFtpServer.ftpServerStates;

import commandHandling.*;
import controller.FtpServerController;
import model.User;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.IOException;

public class AdminLoggedInServerState implements FtpServerState{
    private final UI ui;
    BaseCommandHandler commandHandler;

    public AdminLoggedInServerState(UI ui){
        this.ui = ui;
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        switch (command) {
            case "RETR": // to retrieve file from the server
                commandHandler = new RetrCommandHandler();
                break;
            case "STOR": // to store file on server
                commandHandler = new StorCommandHandler();
                break;
            case "DELE": // to delete a file
                commandHandler = new DeleCommandHandler();
                break;
            case "TYPE": //  to set the type of file to be transferred
                commandHandler = new TypeCommandHandler();
                break;
            case "CDUP": // to change to the parent of the current directory
                commandHandler = new CdupCommandHandler();
                break;
            case "LIST": // to list files in a directory
                commandHandler = new ListCommandHandler();
                break;
            case "PWD": // to print the current working directory
                commandHandler = new PwdCommandHandler();
                break;
            case "EPSV":
                commandHandler = new EpsvCommandHandler();
                break;
            case "CREATE": // to create new user; used with arguments [[username, password, isAdmin]]
                commandHandler = new CreateCommandHandler();
                break;
            case "USERS": // to list all users
                commandHandler = new UsersCommandHandler();
                break;
            case "LOG": // to display log file content
                commandHandler = new LogCommandHandler();
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
        return commandHandler.handleCommand(arguments, user, ui);
    }
}
