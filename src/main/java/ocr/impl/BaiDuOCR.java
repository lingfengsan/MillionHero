package ocr.impl;

import com.baidu.aip.ocr.AipOcr;
import exception.NoRemainingException;
import ocr.OCR;
import org.json.JSONArray;
import org.json.JSONObject;
import pojo.Config;

import java.io.File;
import java.util.HashMap;

/**
 * Created by 618 on 2018/1/12.
 * @author lingfengsan
 */
public class BaiDuOCR implements OCR{
    //设置APPID/AK/SK
    private static AipOcr CLIENT=new AipOcr(Config.getAppId(), Config.getApiKey(), Config.getSecretKey());
    BaiDuOCR(){
        // 可选：设置网络连接参数
        CLIENT.setConnectionTimeoutInMillis(2000);
        CLIENT.setSocketTimeoutInMillis(60000);
    }
    @Override
    public String getOCR(File file) {
        System.out.println(Config.getAppId());
        System.out.println(Config.getApiKey());
        System.out.println(Config.getSecretKey());
        Long start=System.currentTimeMillis();
        String path=file.getAbsolutePath();
        // 调用接口
        JSONObject res = CLIENT.basicGeneral(path, new HashMap<String, String>());
        String searchResult=res.toString();
        if(searchResult.contains("error_msg")){
            try {
                throw new NoRemainingException("OCR可使用次数不足");
            } catch (NoRemainingException e) {
                return "OCR可使用次数不足,请使用自己的OCR\n" +
                        "获取方式见:\n" +
                        "https://github.com/lingfengsan/MillionHero/wiki/Android操作步骤\n" +
                        "或者您可选择使用TessOCR\n";
            }
        }
        System.out.println(res.toString());
        JSONArray jsonArray=res.getJSONArray("words_result");
        StringBuilder sb=new StringBuilder();
        for (Object aJsonArray : jsonArray) {
            String str=aJsonArray.toString();
            str=str.substring(10,str.lastIndexOf('"'));
            sb.append(str);
            sb.append("\n");
        }
        Long time=(System.currentTimeMillis()-start)/1000;
        System.out.println("tessOCR提取信息成功，耗时："+time+"s");
        return sb.toString();
    }

    public static void main(String[] args) {
        OCR ocr=new BaiDuOCR();
        String path = "D:\\Photo\\20180114002647.png";
        String result=ocr.getOCR(new File(path));
        System.out.println(result);
    }
}
