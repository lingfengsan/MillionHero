import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by 618 on 2018/1/8.
 *
 * @author lingfengsan
 */
public class Main {
    private static final int NUM_OF_ANSWERS = 3;
    private static final String QUESTION_FLAG="?";

    public static void main(String[] args) throws IOException {
        BufferedReader bf=new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String str=bf.readLine();
            try {
                if(str.length()==0){
                    run();
                }
            } catch (Exception e) {
                System.out.println("error");
            }

        }
    }

    private static void run() throws InterruptedException {
//       记录开始时间
        long startTime;
//       记录结束时间
        long endTime;
        startTime = System.currentTimeMillis();
        //获取图片
        File image = new Phone().getImage();
        System.out.println("获取图片成功" + image.getAbsolutePath());
        //图像识别
        Long beginOfDectect=System.currentTimeMillis();
        String questionAndAnswers = new TessOCR().getOCR(image);
        if (image != null)
            image.delete();
        System.out.println("识别成功");
        System.out.println("识别时间："+(System.currentTimeMillis()-beginOfDectect));
        if(!questionAndAnswers.contains(QUESTION_FLAG)){
            return;
        }
        //获取问题和答案
        System.out.println("检测到题目");
        Information information = new Information(questionAndAnswers);
        String question = information.getQuestion();
        String[] answers = information.getAns();
        System.out.println("问题:" + question);
        System.out.println("答案：");
        for (String answer : answers) {
            System.out.println(answer);
        }
        //搜索
        long countQuestion = 1;
        long[] countQA = new long[3];
        long[] countAnswer = new long[3];

        int maxIndex = 0;

        Search[] searchQA = new Search[3];
        Search[] searchAnswers = new Search[3];
        FutureTask<Long>[] futureQA = new FutureTask[NUM_OF_ANSWERS];
        FutureTask<Long>[] futureAnswers = new FutureTask[NUM_OF_ANSWERS];
        FutureTask<Long> futureQuestion = new FutureTask<Long>(new Search(question));
        new Thread(futureQuestion).start();
        for (int i = 0; i < NUM_OF_ANSWERS; i++) {
            searchQA[i] = new Search(question + " " + answers[i]);
            searchAnswers[i] = new Search(answers[i]);
            futureQA[i] = new FutureTask<Long>(searchQA[i]);
            futureAnswers[i] = new FutureTask<Long>(searchAnswers[i]);
            new Thread(futureQA[i]).start();
            new Thread(futureAnswers[i]).start();
        }
        try {
            while (!futureQuestion.isDone()) {}
            countQuestion = futureQuestion.get();
            for (int i = 0; i < NUM_OF_ANSWERS; i++) {
                while (!futureQA[i].isDone()) {}
                countQA[i] = futureQA[i].get();
                while (!futureAnswers[i].isDone()) {}
                countAnswer[i] = futureAnswers[i].get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        float[] ans = new float[NUM_OF_ANSWERS];
        for (int i = 0; i < NUM_OF_ANSWERS; i++) {
            ans[i] = (float)countQA[i] / (float)(countQuestion * countAnswer[i]);
            maxIndex = (ans[i]>ans[maxIndex] ) ? i : maxIndex;
        }
        //根据pmi值进行打印搜索结果
        int[] rank=rank(ans);
        for (int i : rank) {
            System.out.print(answers[i]);
            System.out.print(" countQA:"+countQA[i]);
            System.out.print(" countAnswer:"+countAnswer[i]);
            System.out.println(" ans:"+ans[i]);
        }

        System.out.println("--------最终结果-------");
        System.out.println(answers[maxIndex]);
        endTime = System.currentTimeMillis();
        float excTime = (float) (endTime - startTime) / 1000;

        System.out.println("执行时间：" + excTime + "s");
    }

    /**
     *
     * @param floats pmi值
     * @return 返回排序的rank
     */
    private static int[] rank(float[] floats){
        int[] rank=new int[NUM_OF_ANSWERS];
        float[] f=Arrays.copyOf(floats,3);
        Arrays.sort(f);
        for (int i = 0; i < NUM_OF_ANSWERS; i++) {
            for (int j = 0; j < NUM_OF_ANSWERS; j++) {
                if(f[i]==floats[j]){
                    rank[i]=j;
                }
            }
        }
        return rank;
    }
}
