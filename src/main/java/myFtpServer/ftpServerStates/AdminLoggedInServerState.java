package myFtpServer.ftpServerStates;

import controller.FtpServerController;
import model.User;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;
import java.net.ServerSocket;

public class AdminLoggedInServerState implements FtpServerState{
    private FtpServerController controller;

    public AdminLoggedInServerState(FtpServerController controller){
        this.controller = controller;
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

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
                FtpResponse cdupResponse = controller.changeToParentDirectory(user);
                return cdupResponse;
            case "LIST": // to list files in a directory
                // TO MODIFY (not working properly for now)
                FtpResponse listResponse = controller.listDirectoryContent(user);
                return listResponse;
            case "PWD": // to print the current working directory
                // TO MODIFY (not working properly for now)
                return new FtpResponse(257, user.getHomeDirectory() + "\\ is the current directory");
            case "EPSV":
                ServerSocket passiveSocket = new ServerSocket(0);
                int dataPort = passiveSocket.getLocalPort();
                return new FtpResponse(229, " Entering Extended Passive Mode (|||" + dataPort + "|)");
            case "CREATE": // to create new user; used with arguments [[username, password, isAdmin]]
                FtpResponse createResponse = controller.createUserByAdmin(user, arguments);
                return createResponse;
            case "USERS": // to list all users
                FtpResponse usersResponse = controller.getUsersListByAdmin(user);
                return usersResponse;
            case "LOG": // to display log file content
                FtpResponse logResponse = controller.viewLogFileByAdmin(user);
                return logResponse;
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
