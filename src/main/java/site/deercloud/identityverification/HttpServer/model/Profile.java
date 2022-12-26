package site.deercloud.identityverification.HttpServer.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Profile {
    public String uuid;
    public String belongTo;

    public String name;
    public String textures;
    public String textures_signature;
    public String uploadableTextures;
    public String uploadableTextures_signature;

    public Boolean is_genuine;

    public JSONObject serialToJSONObject(Boolean with_properties,Boolean unsigned) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", uuid);
        jsonObject.put("name", name);
        if (!with_properties) {
            return jsonObject;
        }

        JSONArray properties = new JSONArray();
        JSONObject property = new JSONObject();

        if (textures != null) {
            property.put("name", "textures");
            property.put("value", textures);
            if (!unsigned) {
                property.put("signature", textures_signature);
            }
            properties.add(property);
        }
        if (uploadableTextures != null) {
            property.put("name", "uploadableTextures");
            property.put("value", uploadableTextures);
            if (!unsigned) {
                property.put("signature", uploadableTextures_signature);
            }
            properties.add(property);
        }
        jsonObject.put("properties", properties);
        return jsonObject;
    }

    public Profile(JSONObject jsonObject) {
        uuid = jsonObject.getString("id");
        name = jsonObject.getString("name");
        JSONArray properties = jsonObject.getJSONArray("properties");
        for (int i = 0; i < properties.size(); i++) {
            JSONObject property = properties.getJSONObject(i);
            if (property.containsKey("textures")) {
                textures = property.getString("textures");
                textures_signature = property.getString("textures_signature");
            }
            if (property.containsKey("uploadableTextures")) {
                uploadableTextures = property.getString("uploadableTextures");
                uploadableTextures_signature = property.getString("uploadableTextures_signature");
            }
        }
    }

    public Profile() {
    }
}
