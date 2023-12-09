package controller;

import logger.Logger;
import model.Session;
import model.User;
import service.*;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class FtpServerController {
    private final SessionService sessionService = SessionService.getSessionService();
    private final AuthenticationService authenticationService = AuthenticationService.getAuthenticationService();

    public boolean newUserCanConnect(int maxConnectionNum) {
        List<Session> activeSessions = sessionService.getActiveSessions();
        int activeSessionsNum = activeSessions.size();
        return activeSessionsNum < maxConnectionNum;
    }

    public boolean checkIfUserExist(String username) {
        return authenticationService.userExists(username);
    }

    public User processLogin(String username, String password, Logger logger, Socket clientSocket) throws IOException {
        if(!authenticationService.userExists(username))
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
}
