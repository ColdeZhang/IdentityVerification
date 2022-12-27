package site.deercloud.identityverification.HttpServer.model;

import com.alibaba.fastjson.JSONObject;

public class BanRecord {
    public Integer id;
    public String uuid;
    public Long ban_time;
    public String ban_reason;
    public Long create_time;
    public Long update_time;

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("uuid", uuid);
        jsonObject.put("ban_time", ban_time);
        jsonObject.put("ban_reason", ban_reason);
        jsonObject.put("create_time", create_time);
        jsonObject.put("update_time", update_time);
        return jsonObject;
    }
}
