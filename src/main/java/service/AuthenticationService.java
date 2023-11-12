package service;

import logger.Logger;
import model.Session;
import model.User;

import java.io.IOException;
import java.net.Socket;

public class AuthenticationService {
    private UserService userService;
    private SessionService sessionService;

    public AuthenticationService(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    public boolean userExists(String username) {
        User user = userService.getByUsername(username);
        return user != null;
    }

    public boolean userAllowedToHaveNewSession(String username) {
        User user = userService.getByUsername(username);
        if(user.getIsAdmin())
            return true;
        boolean userHasActiveSession = sessionService.getActiveSessionForUser(user.getUserId()) == null;
        return userHasActiveSession;
    }

    public User authenticate(String username, String password, Logger logger, Socket clientSocket) throws IOException {
        User currentUser = userService.getByUsername(username);
        if(currentUser != null && currentUser.getPassword().equals(password)){
            if (sessionService.getAllSessionsForUser(currentUser).isEmpty()){
                Session newSession = new Session(currentUser);
                sessionService.createSession(newSession);
                logger.writeLogInEventToFile(clientSocket.getInetAddress().getHostAddress(), currentUser.getUsername());
            }
            return currentUser;
        }
        return null;
    }

    public void signOut(String username) {
        User user = userService.getByUsername(username);
        Session activeSession = sessionService.getActiveSessionForUser(user.getUserId());
        if(activeSession != null)
            sessionService.modifySessionStatus(activeSession.getSessionId());
    }
}
