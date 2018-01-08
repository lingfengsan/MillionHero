import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

/**
 * Created by 618 on 2018/1/8.
 */
public class Search implements Callable {
    private final String question;
    public Search(String question){
        this.question=question;
    }
    public long search(String question) throws IOException {
        StringBuilder sb = new StringBuilder();
        String path = "http://www.baidu.com/s?tn=ichuner&lm=-1&word=" +
                URLEncoder.encode(question , "gb2312") + "&rn=1";
        URL url = new URL(path);
        BufferedReader breader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = null;
        while ((line = breader.readLine()) != null) {
            if (line.contains("百度为您找到相关结果约")) {
                int start = line.indexOf("百度为您找到相关结果约") + 11;
                line = line.substring(start);
                int end = line.indexOf("个");
                line = line.substring(0, end);
                break;
            }

        }
        line=line.replace(",","");
        return Long.valueOf(line);
    }
    public static void main(String[] args) throws Exception {
        Search search = new Search("阿尔茨海默症又被称为什么?");
        System.out.println(search.call());
    }

    @Override
    public Long call() throws Exception {
        return search(question);
    }
}
