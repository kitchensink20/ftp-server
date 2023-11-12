package myFtpServer.ftpServerStates;

import controller.FtpServerController;
import logger.Logger;
import model.User;
import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.IOException;
import java.net.Socket;

public class NotLoggedInServerState implements FtpServerState{
    private final FtpServerController controller;
    private final Logger logger;
    private final Socket clientSocket;
    private final FtpServer ftpServer;
    private final UI ui;

    public NotLoggedInServerState(FtpServer ftpServer, Socket clientSocket){
        this.ftpServer = ftpServer;
        this.logger = ftpServer.getLogger();
        this.controller = ftpServer.getController();
        this.ui = ftpServer.getUi();
        this.clientSocket = clientSocket;
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String argument = ftpRequest.getArguments();

        switch (command) {
            case "USER":
                boolean userExists = controller.checkIfUserExist(argument);
                if (userExists) {
                    user.setUsername(argument);
                    return new FtpResponse(331, "Username okay, need password");
                } else
                    return new FtpResponse(530, "Username does not exist");
            case "PASS":
                if (user.getUsername() == null)
                    return new FtpResponse(530, "Username needed");

                User loggedInUser = controller.processLogin(user.getUsername(), argument, logger, clientSocket);
                if (loggedInUser == null)
                    return new FtpResponse(530, "Wrong password");
                else {
                    setUserData(user, loggedInUser);
                    if (user.getIsAdmin())
                        ftpServer.setState(new AdminLoggedInServerState(ui));
                    else
                        ftpServer.setState(new UserLoggedInServerState(ui));

                    return new FtpResponse(230, "User successfully logged in");
                }
            case "SYST":
                return new FtpResponse(215, "NAME " + System.getProperty("os.name") + " VERSION " + System.getProperty("os.version"));
            case "QUIT":
                if(user != null)
                    controller.processLogOut(user.getUsername());
                return new FtpResponse(221, "Service closing control connection");
            default:
                return new FtpResponse(502, "Command not implemented");
        }
    }

    private void setUserData(User initialUser, User loggedInUser) {
        initialUser.setUserId(loggedInUser.getUserId());
        initialUser.setUsername(loggedInUser.getUsername());
        initialUser.setPassword(loggedInUser.getPassword());
        initialUser.setHomeDirectory(loggedInUser.getHomeDirectory());
        initialUser.setAdmin(loggedInUser.getIsAdmin());
    }
}
