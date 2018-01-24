package pojo;

/**
 * Created by lingfengsan on 2018/1/22.
 * @author lingfengsan
 */
public class Config {
    private static String adbPath;
    private static String photoPath;
    private static String APP_ID;
    private static String API_KEY;
    private static String SECRET_KEY;
    private static int searchSelection;

    public static int getSearchSelection() {
        return searchSelection;
    }

    public static void setSearchSelection(int searchSelection) {
        Config.searchSelection = searchSelection;
    }



    public static String getAdbPath() {
        return adbPath;
    }

    public static void setAdbPath(String adbPath) {
        Config.adbPath = adbPath;
    }

    public static String getPhotoPath() {
        return photoPath;
    }

    public static void setPhotoPath(String photoPath) {
        Config.photoPath = photoPath;
    }

    public static String getAppId() {
        return APP_ID;
    }

    public static void setAppId(String appId) {
        APP_ID = appId;
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public static void setApiKey(String apiKey) {
        API_KEY = apiKey;
    }

    public static String getSecretKey() {
        return SECRET_KEY;
    }

    public static void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    public static void set(String key, String value) {
        if ("ADB_PATH".equals(key)) {
            adbPath = value;
        } else if ("PHOTO_PATH".equals(key)) {
            photoPath = value;
        } else if ("APP_ID".equals(key)) {
            APP_ID = value;
        } else if ("API_KEY".equals(key)) {
            API_KEY = value;
        } else if ("SECRET_KEY".equals(key)) {
            SECRET_KEY = value;
        }
    }


}
