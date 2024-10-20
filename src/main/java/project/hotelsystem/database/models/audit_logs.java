package project.hotelsystem.database.models;

import java.time.LocalDateTime;

public class audit_logs {

    int id;
    String action;
    String user;
    LocalDateTime timestamp;

    public audit_logs(int id, String action, String user, LocalDateTime timestamp) {
        this.id = id;
        this.action = action;
        this.user = user;
        this.timestamp = timestamp;
    }

    public audit_logs(String action, String user, LocalDateTime timestamp) {
        this.action = action;
        this.user = user;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "audit_logs{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", user='" + user + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
