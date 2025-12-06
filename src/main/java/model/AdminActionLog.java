package model;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class AdminActionLog {
    private final String id;
    private final String adminId;
    private final String action;
    private final String actionType;
    private final String reason;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public AdminActionLog(String id, String adminId, String action, String actionType, String reason) {
        this.id = id;
        this.adminId = adminId;
        this.action = action;
        this.actionType = actionType;
        this.reason = reason;
    }
}
