package similarity.impl;

import com.baidu.aip.ocr.AipOcr;
import pojo.Config;
import similarity.Similarity;

/**
 * Created by lingfengsan on 2018/1/23.
 * @author lingfengsan
 */
public class BaiDuSimilarity implements Similarity{
    //设置APPID/AK/SK
    private static AipOcr CLIENT=new AipOcr("10732092",
            "pdAtmzlooEbrcfYG4l0kIluf", "sHjPBnKt58crPuFogTgQ5Wki0TrHYO2c");
    @Override
    public float calculateSimilarity(String question, String answer) {
        return 0;
    }

    @Override
    public Long call() throws Exception {
        return null;
    }
}
