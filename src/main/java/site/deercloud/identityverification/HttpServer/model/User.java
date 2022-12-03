package site.deercloud.identityverification.HttpServer.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class User {
    public String uuid;
    public String password;
    public String email;
    // 用户类型 0 普通用户 1 管理员 2 控制台用户
    public Integer role = 0;
    public Long createTime;
    public Long updateTime;

    public JSONObject serialToJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", uuid);
        JSONArray properties = new JSONArray();
        JSONObject property = new JSONObject();
        property.put("name", "preferredLanguage");
        property.put("value", "zh_CN");
        properties.add(property);
        jsonObject.put("properties", properties);
        return jsonObject;
    }
}
