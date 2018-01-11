/**
 * Created by 618 on 2018/1/8.
 * @author lingfengsan
 */
public class Information {
    private String question;
    private String[] ans;

    Information(String str) {
        //先去除空行
        str = str.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").
                replaceAll("^((\r\n)|\n)", "");
        str=str.replace('.',' ').replace(" ","");
        //问号统一替换为英文问号防止报错
        str=str.replace("？","?");
        int begin=(str.charAt(1)>='0'&& str.charAt(1)<=9)?2:1;
        question = str.trim().substring(begin, str.indexOf('?') + 1);
        question = question.replaceAll("((\r\n)|\n)", "");
        System.out.println(question);
        String remain = str.substring(str.indexOf("?") + 1);
        ans = remain.trim().split("\n");
    }

    String getQuestion() {
        return question;
    }

    String[] getAns() {
        return ans;
    }

    public static void main(String[] args) {
        String testStr = "8..阿尔茨海默症又被称为什么?\n" +
                "老年痴呆症\n" +
                "\n" +
                "癫痫症\n" +
                "\n" +
                "小儿麻痹症";


        Information information= new Information(testStr);
        String que=information.getQuestion();
        String[] ans=information.getAns();
        System.out.println(que);
        for (String an : ans) {
            System.out.println(an);
        }
    }

}
