package controller;

import fileHandling.FileHandler;
import logger.Logger;
import model.Session;
import model.User;
import myFtpServer.protocol.FtpResponse;
import service.*;
import userMemento.UserCaretaker;
import userMemento.UserMemento;
import view.UI;

import java.io.*;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FtpServerController {
    private final UserService userService;
    private final SessionService sessionService;
    private final FileService fileService;
    private final AuthenticationService authenticationService;
    private final UI ui;

    public FtpServerController(UI ui) {
        this.ui = ui;
        this.sessionService = new SessionService();
        this.fileService = new FileService();
        this.userService = new UserService();
        this.authenticationService = new AuthenticationService(userService, sessionService);
    }

    public boolean newUserCanConnect(int maxConnectionNum) {
        List<Session> activeSessions = sessionService.getActiveSessions();
        int activeSessionsNum = activeSessions.size();
        return activeSessionsNum < maxConnectionNum;
    }

    public boolean checkIfUserExist(String username) {
        return authenticationService.userExists(username);
    }

    public boolean checkIfUserAllowedToHaveNewSession(String username) {
        User user = userService.getByUsername(username);
        return user.getIsAdmin() || authenticationService.userAllowedToHaveNewSession(username);
    }

    public User processLogin(String username, String password, Logger logger, Socket clientSocket) throws IOException {
        if(!authenticationService.userExists(username)) // || !authenticationService.userAllowedToHaveNewSession(username))
            return null;

        return authenticationService.authenticate(username, password, logger, clientSocket);
    }

    public void processLogOut(String username) {
        authenticationService.signOut(username);
    }

    public void deactivateSessionIfActive(int userId) {
        Session activeSession = sessionService.getActiveSessionForUser(userId);
        if(activeSession != null)
            sessionService.modifySessionStatus(activeSession.getSessionId());
    }

    public FtpResponse listDirectoryContent(User user) {
        System.out.println("from list" + user.getUsername());
        if(user == null) return null;

        Path currentDirectory = Paths.get(user.getHomeDirectory()).toAbsolutePath();
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(currentDirectory)) {
            for(Path file : stream)
                ui.displayDirectoryContent(String.valueOf(file.getFileName()));
            return new FtpResponse(226, "Transfer complete");
        } catch (IOException e) {
            return new FtpResponse(550, "Can't lust directory content");
        }
    }

    public FtpResponse changeToParentDirectory(User user) {
        Path currentDirectory = Paths.get(user.getHomeDirectory()).toAbsolutePath();
        Path parentDirectory = currentDirectory.getParent();
        if(parentDirectory != null) {
            currentDirectory = parentDirectory;
            return new FtpResponse(200, "Changed to parent directory");
        } else
            return new FtpResponse(550, "Failed to change directory: No parent directory");
    }


    public FtpResponse createUserByAdmin(User currentUser, String arguments) throws IOException {
        if(currentUser == null || !currentUser.getIsAdmin())
            return new FtpResponse(550, "Permission denied");

        String[] argumentsSplit = arguments.split(" ");
        if(argumentsSplit.length != 3)
            return new FtpResponse(501, "Syntax error in parameters or arguments. Try like this: CREATE [[username, password, isAdmin]]");

        String username = argumentsSplit[0];
        String password = argumentsSplit[1];
        Boolean isAdmin = argumentsSplit[2].equalsIgnoreCase("true") ? Boolean.TRUE : argumentsSplit[2].equalsIgnoreCase("false") ? Boolean.FALSE : null;

        if(isAdmin == null)
            return new FtpResponse(501, "Syntax error in parameters or arguments. Argument №3 must be true or false");

        boolean usernameIsBusy = userService.getByUsername(username) != null;
        if(usernameIsBusy)
            return new FtpResponse(501, "Syntax error in parameters or arguments. Username is already occupied");

        User user = new User(username, password, isAdmin);
        userService.createUser(user);
        return new FtpResponse(230, "User successfully created");
    }

    public FtpResponse getUsersListByAdmin(User currentUser) throws IOException {
        if(currentUser == null || !currentUser.getIsAdmin())
            return new FtpResponse(550, "Permission denied");

        List<User> users = userService.getAllUsers();
        ui.displayUsersList(users);
        return new FtpResponse(212, "List of users successfully retrieved");
    }

    public FtpResponse viewLogFileByAdmin(User currentUser) throws IOException {
        if(currentUser == null || !currentUser.getIsAdmin())
            return new FtpResponse(550, "Permission denied");

        List<String> fileContent = FileHandler.getLogFileContent();
        ui.displayLogFileContent(fileContent);
        return new FtpResponse(212, "Transfer complete. Log file content successfully displayed");
    }

    public FtpResponse changeUser(User user, UserCaretaker userCaretaker, String arguments) throws IOException {
        if(arguments.isEmpty() || arguments.isBlank())
            return new FtpResponse(501, "Syntax error in parameters or arguments. Try like this: ALTER -username [[new username]] / ALTER -password [[new password]]");

        if (user == null)
            return new FtpResponse(550, "Permission denied");

        String[] argumentsSplit = arguments.split(" ");
        if(argumentsSplit.length != 2)
            return new FtpResponse(501, "Syntax error in parameters or arguments. Try like this: ALTER -username [[new username]] / ALTER -password [[new password]]");

        if(argumentsSplit[0].equals("-username"))
            return changeUsername(user, userCaretaker, argumentsSplit[1]);
        else if(argumentsSplit[0].equals("-password"))
            return changePassword(user, userCaretaker, argumentsSplit[1]);
        else
            return new FtpResponse(501, "Syntax error in parameters or arguments. Try like this: ALTER -username [[new username]] / ALTER -password [[new password]]");
    }

    public FtpResponse restoreUserState(User user, UserCaretaker userCaretaker) throws IOException {
        UserMemento restoredUserMemento = userCaretaker.getLastSavedMemento();
        if(restoredUserMemento == null)
            return new FtpResponse(202, "No action taken, user state is current");

        user.restoreFromMemento(restoredUserMemento);
        userService.updateUser(user);
        return new FtpResponse(250, "User state restored to the latest one");
    }

    private FtpResponse changeUsername(User user, UserCaretaker userCaretaker, String newUsername) throws IOException {
        boolean usernameIsBusy = userService.getByUsername(newUsername) != null;
        if(usernameIsBusy)
            return new FtpResponse(501, "Syntax error in parameters or arguments. Username is already occupied");

        userCaretaker.saveNewMemento(user.saveToMemento());
        user.setUsername(newUsername);
        userService.updateUser(user);
        return new FtpResponse(200, "User successfully updated");
    }

    private FtpResponse changePassword(User user, UserCaretaker userCaretaker, String newPassword) throws IOException {
        boolean passwordsTheSame  = user.getPassword().equals(newPassword);
        if(passwordsTheSame)
            return new FtpResponse(501, "Syntax error in parameters or arguments. Password is the same");

        userCaretaker.saveNewMemento(user.saveToMemento());
        user.setPassword(newPassword);
        userService.updateUser(user);
        return new FtpResponse(200, "User successfully updated");
    }
}

    /*
    public void getFileListForCurrentUser(User currentUser) throws  IOException {
        List<File> files = fileService.getAllUserFiles(currentUser);
        ui.displayFileList(files);
    }*/

   /* public void getFileListForUserByAdmin(User currentUser) throws IOException {
        String inputUsername = ui.requestUsernameInput();
        User user = userService.getByUsername(inputUsername);
        if(user == null)
            ui.displayUserNotFoundMessage();
        else {
            List<File> files = fileService.getAllUserFiles(user);
            ui.displayFileList(files);
        }
    }
*/

/*   */

/*
 */
