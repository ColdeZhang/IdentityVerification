package site.deercloud.identityverification.HttpServer.Yggdrasil.sessionserver;

import java.util.Map;
import java.util.Objects;

public class SessionTokenCache {
    private Map<String, String> serverID_token;
    private Map<String, String> serverID_ip;
    private Map<String, Long> serverID_time;

    public void add(String serverID, String token, String ip){
        serverID_token.put(serverID, token);
        serverID_ip.put(serverID, ip);
        serverID_time.put(serverID, System.currentTimeMillis() + 30000);
    }

    public boolean verifyIsExpire(String serverID, String ip){
        if (!serverID_token.containsKey(serverID)){
            return true;
        }
        if (serverID_time.get(serverID) < System.currentTimeMillis()){
            serverID_token.remove(serverID);
            serverID_ip.remove(serverID);
            serverID_time.remove(serverID);
            return true;
        }
        return false;
    }

    public String getToken(String serverID){
        return serverID_token.get(serverID);
    }
}
