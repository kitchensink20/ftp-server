package myFtpServer.ftpServerStates;

import controller.FtpServerController;
import model.User;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AdminLoggedInServerState implements FtpServerState{
    private FtpServerController controller;
    private UI ui;

    public AdminLoggedInServerState(FtpServerController controller, UI ui){
        this.controller = controller;
        this.ui = ui;
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        switch (command) {
            case "CREATE": // to create new user; used with arguments [[username, password, isAdmin]]
                FtpResponse createResponse = controller.createUserByAdmin(user, arguments);
                return createResponse;
            case "USERS": // to list all users
                FtpResponse usersResponse = controller.getUsersListByAdmin(user);
                return usersResponse;
            case "LOG": // to display log file content
                FtpResponse logResponse = controller.viewLogFileByAdmin(user);
                return logResponse;
            case "TYPE":
                return new FtpResponse(200, "Type set to I");
            case "CDUP":
                FtpResponse cdupResponse = controller.changeToParentDirectory(user);
                return cdupResponse;
            case "LIST":
                FtpResponse listResponse = controller.listDirectoryContent(user);
                return listResponse;
            case "PWD":
                return new FtpResponse(257, user.getHomeDirectory() + "\\ is the current directory");
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
