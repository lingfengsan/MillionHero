package search.impl;

import search.Search;
import utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by lingfengsan on 2018/1/18.
 * @author lingfengsan
 */
public class SoGouSearch implements Search{
    private Boolean needOpenBrowser;
    private String path;
    public SoGouSearch(String question, Boolean needOpenBrowser)  {
        try{
            this.needOpenBrowser = needOpenBrowser;
            this.path = "https://www.sogou.com/web?query=" +
                    URLEncoder.encode(question, "gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Long search() throws IOException {
        boolean findIt = false;
        String line = null;
        while (!findIt) {
            URL url = new URL(path);
            BufferedReader breaded = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
            while ((line = breaded.readLine()) != null) {
                if (line.contains("搜狗已为您找到约")) {
                    findIt = true;
                    int start = line.indexOf("搜狗已为您找到约") + 8;
                    line = line.substring(start);
                    int end = line.indexOf("条");
                    line = line.substring(0, end);
                    break;
                }

            }
        }
        line = line.replace(",", "");
        return Long.valueOf(line);
    }

    @Override
    public Long call() throws IOException {
        if (needOpenBrowser) {
            new Utils().openBrowser(path);
        }
        return search();
    }

    public static void main(String[] args) {
        try {
            Search search=new SoGouSearch("搜狗",true);
            System.out.println(search.call());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
