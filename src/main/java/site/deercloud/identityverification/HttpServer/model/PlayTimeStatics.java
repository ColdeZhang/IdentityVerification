package site.deercloud.identityverification.HttpServer.model;

import java.util.ArrayList;

public class PlayTimeStatics {

    public Integer id;

    public String playerUuid;

    public Long afkTime;

    public Long activeTime;

    public Long onlineTime;

    public Long joinTime;

    public Long quitTime;

    public Long recordTime;

    public static Long totalActivityTimeOf(ArrayList<PlayTimeStatics> list) {
        Long total = 0L;
        for (PlayTimeStatics item : list) {
            total += item.activeTime;
        }
        return total;
    }
}
