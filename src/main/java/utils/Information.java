package utils;

/**
 * Created by 618 on 2018/1/12.
 */
public class Information {
    private String question;
    private String[] ans;

    Information(String question,String[] ans) {
        this.question=question;
        this.ans=ans;
    }
    public String getQuestion() {
        return question;
    }

    public String[] getAns() {
        return ans;
    }

}
