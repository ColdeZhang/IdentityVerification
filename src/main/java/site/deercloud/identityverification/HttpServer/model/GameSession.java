package site.deercloud.identityverification.HttpServer.model;

public class GameSession {
    public GameSession() {
        joinTime = System.currentTimeMillis();
        lastActionTime = joinTime;
        afkTime = 0L;
    }

    public void addAfkTime(long time) {
        afkTime += time;
    }

    public void setLastMovement(long time) {
        lastActionTime = time;
    }

    public long getLastMovement() {
        return lastActionTime;
    }
    public Long joinTime;
    public Long lastActionTime;
    public Long afkTime;
}
