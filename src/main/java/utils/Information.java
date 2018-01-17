package utils;

import java.util.Arrays;

/**
 * Created by 618 on 2018/1/12.
 * @author lingfengsan
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

    @Override
    public String toString() {
        return "{\"Information\":{"
                + "\"question\":\"" + question + "\""
                + ", \"ans\":" + Arrays.toString(ans)
                + "}}";
    }
}
