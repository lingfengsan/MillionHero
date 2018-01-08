/**
 * Created by 618 on 2018/1/8.
 */
public class Information {
    private String question;
    private String[] ans;

    public Information(String str) {
        //先去除空行
        System.out.println(str);
        str = str.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").
                replaceAll("^((\r\n)|\n)", "");
        str=str.replace('.',' ').replace(" ","");
        System.out.println(str);
        int begin=(str.charAt(1)>='0'&& str.charAt(1)<=9)?2:1;
        question = str.trim().substring(begin, str.indexOf('?') + 1);
        System.out.println("___________");
        System.out.println(question);
        String remain = str.substring(str.indexOf("?") + 1);
        ans = remain.trim().split("\n");
    }

    public String getQuestion() {
        return question;
    }

    public String[] getAns() {
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
