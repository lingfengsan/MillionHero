package similarity.impl;

import com.baidu.aip.nlp.AipNlp;
import org.json.JSONObject;
import pojo.NlpConfig;
import similarity.Similarity;

import java.util.HashMap;

/**
 * Created by lingfengsan on 2018/1/23.
 *
 * @author lingfengsan
 */
public class BaiDuSimilarity implements Similarity {
    //设置APPID/AK/SK
//    private AipNlp client = new AipNlp(NlpConfig.getAppId(),
//            NlpConfig.getApiKey(),NlpConfig.getSecretKey());
    private AipNlp client;
    private String question;
    private String answer;
    BaiDuSimilarity(String question,String answer){
        this.question=question;
        this.answer=answer;
    }

    @Override
    public Double call() {
        long start = System.currentTimeMillis();
        // 传入可选参数调用接口
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("model", "CNN");
        // 短文本相似度
        JSONObject res = client.simnet(question, answer, options);
        float time = (System.currentTimeMillis() - start) / 1000f;
        double score = res.getDouble("score");
        System.out.println("短文本相似度处理时间" + time);
        return score;
    }



    public static void main(String[] args) {
        BaiDuSimilarity baiDuSimilarity = new BaiDuSimilarity("浙富股份","万事通自考网");
        double score = baiDuSimilarity.call();
        System.out.println(score);
    }

    public void setClient(AipNlp client) {
        this.client = client;
    }
}
