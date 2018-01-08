import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by 618 on 2018/1/8.
 * @author lingfengsan
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc=new Scanner(System.in);
        while(sc.nextInt()==1){
            try{
                run();
            }catch (Exception e){
                System.out.println("error");
            }

        }
    }
    public static void run() throws IOException {
        long startTime=System.currentTimeMillis();//记录开始时间
        //获取图片
        File image=new Phone().getImage();
        System.out.println("获取图片成功"+image.getAbsolutePath());
        //图像识别
        String questionAndAnswers=new TessOCR().getOCR(image);
        System.out.println("识别成功 = [" + questionAndAnswers + "]");
        //获取问题和答案
        Information information=new Information(questionAndAnswers);
        String question=information.getQuestion();
        String[] answers=information.getAns();
        System.out.println("问题:"+question);
        System.out.println("答案：");
        for (String answer : answers) {
            System.out.println(answer);
        }
        //搜索
        Search search=new Search();
        for (int i = 0; i < 3; i++) {
            System.out.println(answers[i]+search.search(question,answers[i]));
        }
        long endTime=System.currentTimeMillis();//记录结束时间
        float excTime=(float)(endTime-startTime)/1000;

        System.out.println("执行时间："+excTime+"s");
    }
}
