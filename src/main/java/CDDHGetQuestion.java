import com.alibaba.fastjson.JSON;

import entity.cddh.CDEntity;
import entity.cddh.Data;
import entity.cddh.Event;
import org.junit.Test;
import utils.Utils;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class CDDHGetQuestion implements GetQuestion {
     private static final String questionUrl = "http://htpmsg.jiecaojingxuan.com/msg/current";
    private static String formatJson(String json){
        return json.replace("\\","").replace("\"[","[").replace("]\"","]");

    }



    public  static Information getQuestionInformation(){

        return new Information(getQuestionStr());
    }
    public  static String getQuestionStr(){
        String qaStr ="";
        // String questioJson = formatJson(Utils.getResponseByGet(questionUrl));
        //  Map parse = (Map) JSON.parse(questioJson);
        InputStream in = CDDHGetQuestion.class.getResourceAsStream("/json/answer.json");

        String testJson =  formatJson(Utils.convertStreamToString(in));
        CDEntity parse = JSON.parseObject(testJson, CDEntity.class);

        if("no data".equals(parse.getMsg())){
            //未开始
        }else{
            if("成功".equals(parse.getMsg())){
                Event event = parse.getData().getEvent();
                String desc = event.getDesc().replace(".", "..");
                qaStr += desc + "\n";
                for(String opt:event.getOptions()){
                    qaStr += opt+"\n\n";
                }

            }
        }
        return qaStr.substring(0,qaStr.length()-2);
    }
}
