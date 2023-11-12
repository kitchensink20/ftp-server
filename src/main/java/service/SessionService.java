package service;

import model.Session;
import model.User;
import repository.implementations.SessionRepositoryImpl;
import repository.interfaces.SessionRepository;

import java.util.Collections;
import java.util.List;

public class SessionService {
    private SessionRepository sessionRepository;
    public SessionService() {
        this.sessionRepository = new SessionRepositoryImpl();
    }

    public List<Session> getByUserId(int userId) {
        return sessionRepository.getByUserId(userId);
    }

    public Session createSession(Session session) {
        return sessionRepository.createSession(session);
    }

    public List<Session> getActiveSessions() { return sessionRepository.getActiveSessions(); }

    public Session getActiveSessionForUser(int userId) { return sessionRepository.getActiveSessionForUser(userId); }

    public List<Session> getAllSessionsForUser(User user) {
        if(user != null)
            return sessionRepository.getByUserId(user.getUserId());
        return Collections.emptyList();
    }

    public Session modifySessionStatus(int sessionId) { return sessionRepository.updateSessionStatus(sessionId); }

    public void deleteSession(int sessionId) {
        sessionRepository.deleteSession(sessionId);
    }
}
