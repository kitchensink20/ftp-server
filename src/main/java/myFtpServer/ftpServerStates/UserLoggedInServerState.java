package myFtpServer.ftpServerStates;

import commandHandling.*;
import controller.FtpServerController;
import model.User;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;
import userMemento.UserCaretaker;
import view.UI;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

public class UserLoggedInServerState implements FtpServerState {
    private UI ui;
    private static ConcurrentHashMap<Integer, UserCaretaker> userCaretakers = new ConcurrentHashMap<>();
    BaseCommandHandler commandHandler;

    public UserLoggedInServerState(UI ui) {
        this.ui = ui;
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        while(true) {
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
                case "PWD":  // to print the current working directory
                    commandHandler = new PwdCommandHandler();
                    break;
                case "EPSV":
                    commandHandler = new EpsvCommandHandler();
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
                default:
                    return new FtpResponse(502, "Command not implemented");
            }
            return commandHandler.handleCommand(arguments, user, ui);
        }
    }

    public static UserCaretaker getUserCaretaker(int id) {
        return userCaretakers.computeIfAbsent(id, k -> new UserCaretaker());
    }
}
