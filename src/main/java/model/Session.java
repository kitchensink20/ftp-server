package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sessionId;
    @ManyToOne
    @JoinColumn(name = "userId", unique = true)
    private User user;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime startTime;
    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private boolean isActive;

    public Session() { }

    public Session(User user) {
        this.user = user;
        this.startTime = LocalDateTime.now();
        this.isActive = true;
    }

    public int getSessionId() {
        return sessionId;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }
}
