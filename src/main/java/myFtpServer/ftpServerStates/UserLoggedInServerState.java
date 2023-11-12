package myFtpServer.ftpServerStates;

import controller.FtpServerController;
import jakarta.persistence.criteria.CriteriaBuilder;
import model.User;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;
import userMemento.UserCaretaker;
import view.UI;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class UserLoggedInServerState implements FtpServerState {
    private FtpServerController controller;
    private UI ui;
    private static ConcurrentHashMap<Integer, UserCaretaker> userCaretakers = new ConcurrentHashMap<>();

    public UserLoggedInServerState(FtpServerController controller, UI ui) {
        this.controller = controller;
        this.ui = ui;
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();
        UserCaretaker userCaretaker = getUserCaretaker(user.getUserId());

        while(true) {
            switch (command) {
                case "TYPE":
                    return new FtpResponse(200, "Type set to I");
                case "CDUP":
                    return new FtpResponse(550, "Permission denied");
                case "LIST":
                    return controller.listDirectoryContent(user);
                case "PWD":
                    return new FtpResponse(257, user.getHomeDirectory() + "\\ is the current directory");
                case "ALTER": // to change user data such as username or password; used as ALTER -username [[new username]] or ALTER -password [[new password]]
                    FtpResponse alterResponse = controller.changeUser(user, userCaretaker, arguments);
                    return alterResponse;
                case "RESTORE":
                    FtpResponse restoreResponse = controller.restoreUserState(user, userCaretaker);
                    return restoreResponse;
                case "SYST": // to display server specification
                    return new FtpResponse(215, "NAME " + System.getProperty("os.name") + " VERSION " + System.getProperty("os.version"));
                case "QUIT": // to end session
                    controller.processLogOut(user.getUsername());
                    return new FtpResponse(221, "Service closing control connection");
                default:
                    return new FtpResponse(502, "Command not implemented");
            }
        }
    }

    private UserCaretaker getUserCaretaker(int id) {
        return userCaretakers.computeIfAbsent(id, k -> new UserCaretaker());
    }
}
