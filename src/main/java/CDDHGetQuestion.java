import com.alibaba.fastjson.JSON;

import entity.cddh.CDEntity;
import entity.cddh.Data;
import entity.cddh.Event;
import exception.CorrectAnswerException;
import exception.NoBeginExcetpion;
import org.junit.Test;
import utils.Utils;


import java.io.InputStream;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;


public class CDDHGetQuestion implements GetQuestion {
     private static final String questionUrl = "http://htpmsg.jiecaojingxuan.com/msg/current";
    private static String formatJson(String json){
        return json.replace("\\","").replace("\"[","[").replace("]\"","]");

    }



    public  static Information getQuestionInformation() throws NoBeginExcetpion, CorrectAnswerException {

        return new Information(getQuestionStr());
    }
    public  static String getQuestionStr() throws NoBeginExcetpion, CorrectAnswerException {
        String qaStr ="";
        String questioJson = formatJson(Utils.getResponseByGet(questionUrl));

    //     测试方法  注释上面一行 打开 这两行的注释 可 利用本地json数据进行测试。
       /* InputStream in = CDDHGetQuestion.class.getResourceAsStream("/json/question1.json");
        String questioJson =  formatJson(Utils.convertStreamToString(in));*/
        CDEntity parse = JSON.parseObject(questioJson, CDEntity.class);

        if("no data".equals(parse.getMsg())){
            throw new NoBeginExcetpion();
        }else if("showAnswer".equals(parse.getData().getEvent().getType())){
             Event event = parse.getData().getEvent();
            String str = event.getDesc()+"的答案为"+ (event.getCorrectOption()+1) +"."+event.getOptions()[event.getCorrectOption()];
            throw new CorrectAnswerException(str);
        }else if("成功".equals(parse.getMsg())){
                Event event = parse.getData().getEvent();
                String desc = event.getDesc().replace(".", "..");
                qaStr += desc + "\n";
                for(String opt:event.getOptions()){
                    qaStr += opt+"\n\n";
                }


        }
        return qaStr.substring(0,qaStr.length()-2);
    }
    @Test
    public  void test(){
        try {
            CDDHGetQuestion.getQuestionStr();
        } catch (NoBeginExcetpion noBeginExcetpion) {
            noBeginExcetpion.printStackTrace();
        } catch (CorrectAnswerException e) {
            e.printStackTrace();
        }
    }
}
