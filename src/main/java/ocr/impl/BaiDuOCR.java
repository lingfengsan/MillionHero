package ocr.impl;

import com.baidu.aip.ocr.AipOcr;
import ocr.OCR;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by 618 on 2018/1/12.
 * @author lingfengsan
 */
public class BaiDuOCR implements OCR{
    //设置APPID/AK/SK
    private static final String APP_ID = "10676706";
    private static final String API_KEY = "LUCojrtX9sGTh4pHn1nIa9zT";
    private static final String SECRET_KEY = "39IcxEGfLABxNG68jQ0AKamPM2t0rhHW";
    private static final AipOcr CLIENT=new AipOcr(APP_ID, API_KEY, SECRET_KEY);
    BaiDuOCR(){
        // 可选：设置网络连接参数
        CLIENT.setConnectionTimeoutInMillis(2000);
        CLIENT.setSocketTimeoutInMillis(60000);
    }
    @Override
    public String getOCR(String path) {
        Long start=System.currentTimeMillis();
        // 调用接口
        JSONObject res = CLIENT.basicGeneral(path, new HashMap<String, String>());
        String result=res.toString(2);
        System.out.println((System.currentTimeMillis())-start);
        return result;
    }

    public static void main(String[] args) {
        OCR ocr=new BaiDuOCR();
        String path = "D:\\Photo\\20180111214256.png";
        String result=ocr.getOCR(path);
        System.out.println(result);
    }
}
