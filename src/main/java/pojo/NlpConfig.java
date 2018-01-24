package pojo;

/**
 * Created by 618 on 2018/1/24.
 *
 * @author lingfengsan
 *         百度NLP短文本相似度应用需要用到的配置
 */
public class NlpConfig {
    private static String APP_ID;
    private static String API_KEY;
    private static String SECRET_KEY;

    public static String getAppId() {
        return APP_ID;
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getSecretKey() {
        return SECRET_KEY;
    }

    public static void set(String key, String value) {
        if ("APP_ID".equals(key)) {
            APP_ID = value;
        } else if ("API_KEY".equals(key)) {
            API_KEY = value;
        } else if ("SECRET_KEY".equals(key)) {
            SECRET_KEY = value;
        }
    }
}
