import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

/**
 * Created by 618 on 2018/1/8.
 * @author lingfengsan
 */
public class SearchAndOpen implements Callable {
    private final String question;

    SearchAndOpen(String question) {
        this.question = question;
    }
    private Long searchAndOpen(String question) throws IOException {
        String path = "http://www.baidu.com/s?tn=ichuner&lm=-1&word=" +
                URLEncoder.encode(question, "gb2312") + "&rn=20";
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + path);
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
