import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

/**
 * Created by 618 on 2018/1/8.
 *
 * @author lingfengsan
 */
public class SearchAndOpen implements Callable {
    private final String question;

    SearchAndOpen(String question) {
        this.question = question;
    }

    private Long searchAndOpen(String question) throws IOException {
        String path = null;
        try {
            path = "http://www.baidu.com/s?tn=ichuner&lm=-1&word=" +
                    URLEncoder.encode(question, "gb2312") + "&rn=20";
            //获取操作系统的名字
            String osName = System.getProperty("os.name", "");
            if (osName.startsWith("Mac OS")) {
                //苹果的打开方式
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
                openURL.invoke(null, new Object[]{path});
            } else if (osName.startsWith("Windows")) {
                //windows的打开方式。
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + path);
            } else if (osName.startsWith("Linux")) {
                //Linux的打开方式
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(URI.create(path));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + path);
        return new Search(question).search(question);
    }

    public static void main(String[] args) throws Exception {
        SearchAndOpen search = new SearchAndOpen("阿尔茨海默症又被称为什么?");
        System.out.println(search.call());
    }

    @Override
    public Long call() throws Exception {
        return searchAndOpen(question);
    }
}
