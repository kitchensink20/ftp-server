package myFtpServer.ftpServerStates;

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
    private FtpServerController controller;
    private static ConcurrentHashMap<Integer, UserCaretaker> userCaretakers = new ConcurrentHashMap<>();

    public UserLoggedInServerState(FtpServerController controller) {
        this.controller = controller;
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();
        UserCaretaker userCaretaker = getUserCaretaker(user.getUserId());

        while(true) {
            switch (command) {
                case "RETR": // to retrieve file from the server
                    // TO DO
                    break;
                case "STOR": // to store file on server
                    // TO DO
                    break;
                case "DELE": // to delete a file
                    // TO DO
                    break;
                case "TYPE": //  to set the type of file to be transferred
                    // TO DO
                    break;
                case "CDUP": // to change to the parent of the current directory
                    // TO MODIFY (not working properly for now)
                    return new FtpResponse(550, "Permission denied");
                case "LIST": // to list files in a directory
                    // TO MODIFY (not working properly for now
                    return controller.listDirectoryContent(user);
                case "PWD":  // to print the current working directory
                    // TO MODIFY (not working properly for now)
                    return new FtpResponse(257, user.getHomeDirectory() + "\\ is the current directory");
                case "EPSV":
                    ServerSocket passiveSocket = new ServerSocket(0);
                    int dataPort = passiveSocket.getLocalPort();
                    return new FtpResponse(229, " Entering Extended Passive Mode (|||" + dataPort + "|)");
                case "ALTER": // to change user data such as username or password; used as ALTER -username [[new username]] or ALTER -password [[new password]]
                    FtpResponse alterResponse = controller.changeUser(user, userCaretaker, arguments);
                    return alterResponse;
                case "RESTORE": // to restore user state
                    FtpResponse restoreResponse = controller.restoreUserState(user, userCaretaker);
                    return restoreResponse;
                case "ACCT": // to specify the account information
                    return new FtpResponse(230, "username: " + user.getUsername() + "; has admin rights: " + user.getIsAdmin());
                case "SYST": // to display server specification
                    return new FtpResponse(215, "NAME " + System.getProperty("os.name") + " VERSION " + System.getProperty("os.version"));
                case "QUIT": // to end session
                    controller.processLogOut(user.getUsername());
                    return new FtpResponse(221, "Service closing control connection");
                default:
                    return new FtpResponse(502, "Command not implemented");
            }
            return new FtpResponse(200, ":(");
        }
    }

    private UserCaretaker getUserCaretaker(int id) {
        return userCaretakers.computeIfAbsent(id, k -> new UserCaretaker());
    }
}
