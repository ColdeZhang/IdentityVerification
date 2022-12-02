package site.deercloud.identityverification.Utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class Utils {

//    public static Map<String,String> ParseQueryString(String formData ) {
//        Map<String,String> result = new HashMap<>();
//        if(formData== null || formData.trim().length() == 0) {
//            return result;
//        }
//        final String[] items = formData.split("&");
//        Arrays.stream(items).forEach(item ->{
//            final String[] keyAndVal = item.split("=");
//            if( keyAndVal.length == 2) {
//                try{
//                    final String key = URLDecoder.decode( keyAndVal[0],"utf8");
//                    final String val = URLDecoder.decode( keyAndVal[1],"utf8");
//                    result.put(key,val);
//                }catch (UnsupportedEncodingException ignored) {}
//            }
//        });
//        return result;
//    }

    /**
     * 根据玩家名字从Mojiang服务器获取UUID，如果不存在则返回null
     * @param name 玩家名字
     */
    public static String getUUIDFromRemote(String name) throws IOException {
        String url = "https://".concat("api.")
                        .concat("mojang.")
                        .concat("com/")
                        .concat("users/")
                        .concat("profiles/")
                        .concat("minecraft/")
                        .concat(name);
        String reply = sendGet(url);
        if (reply == null){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(reply);
        return jsonObject.getString("id");
    }

    /**
     * 根据UUID从Mojiang服务器获取玩家正版信息 如果失败则返回null
     * @param uuid 玩家UUID
     */
    public static JSONObject getProfileFromRemote(String uuid) throws IOException {
        String url = "https://".concat("sessionserver.")
                .concat("mojang.")
                .concat("com/")
                .concat("session/")
                .concat("minecraft/")
                .concat("profile/")
                .concat(uuid);
        if (!isUUIDExist(uuid)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(sendGet(url));
        if (jsonObject == null){
            return null;
        }
        String profile_base64 = jsonObject.getJSONArray("properties").getObject(0, JSONObject.class).getString("value");
        String profile = new String(Base64.getDecoder().decode(profile_base64));
        return JSONObject.parseObject(profile);
    }

    public static Boolean isUUIDExist(String uuid) throws IOException {
        String url = "https://".concat("sessionserver.")
                .concat("mojang.")
                .concat("com/")
                .concat("session/")
                .concat("minecraft/")
                .concat("profile/")
                .concat(uuid);
        String reply = sendGet(url);
        if (reply == null){
            return false;
        }
        return reply.contains("id");
    }

    public static byte[] getSkinFromRemote(String uuid) throws IOException {
        JSONObject jsonObject = getProfileFromRemote(uuid);
        String skin_base64 = jsonObject.getJSONObject("textures").getJSONObject("SKIN").getString("url");
        return Base64.getDecoder().decode(skin_base64);
    }

    public static String sendGet(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值GET
        con.setRequestMethod("GET");

        //添加请求头
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            return null;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public static boolean isUUIDMatchName(String uuid, String name) throws IOException {
        String reply = getUUIDFromRemote(name);
        if (reply == null) {
            return false;
        }
        return Objects.equals(reply, uuid);
    }
}
