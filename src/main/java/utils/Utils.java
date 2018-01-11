package utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Doodlister on 2018/1/11.
 *
 * @author Doodlister
 */
public class Utils {
    /**
     * 向指定发送get请求,得到响应内容
     */
    public static String getResponseByGet(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(url);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    // 获取响应实体
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {

                        return EntityUtils.toString(entity);
                    }
                } else {
                    //throw new Exception("状态不对");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
