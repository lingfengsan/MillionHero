import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by 618 on 2018/1/8.
 *
 * @author lingfengsan
 */
public class Main {
    private static final String QUESTION_FLAG = "?";

    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String str = bf.readLine();
            if("exit".equals(str)){
                System.out.println("ヾ(￣▽￣)Bye~Bye~");
                break;
            }else{
                if (str.length() == 0) {
                    run();
                }
            }
        }
    }

    private static void run() {
//       记录开始时间
        long startTime;
//       记录结束时间
        long endTime;
        startTime = System.currentTimeMillis();
        //获取图片
        File image = new Phone().getImage();
        System.out.println("图片获取成功");
        //图像识别
        Long beginOfDetect = System.currentTimeMillis();
        String questionAndAnswers = new TessOCR().getOCR(image);
        System.out.println("识别成功");
        System.out.println("识别时间：" + (System.currentTimeMillis() - beginOfDetect));
        if (questionAndAnswers==null || !questionAndAnswers.contains(QUESTION_FLAG)) {
            System.out.println("问题识别失败，输入回车继续运行");
            return;
        }
        //获取问题和答案
        System.out.println("检测到题目");
        Information information = new Information(questionAndAnswers);
        String question = information.getQuestion();
        String[] answers = information.getAns();
        if (question == null) {
            System.err.println("问题不存在，输入回车继续运行");
            return;
        } else if (answers.length < 1) {
            System.err.println("检测不到答案，输入回车继续运行");
            return;
        }
        System.out.println("问题:" + question);
        System.out.println("答案：");
        for (String answer : answers) {
            System.out.println(answer);
        }
        //搜索
        long countQuestion = 1;
        long[] countQA = new long[answers.length];
        long[] countAnswer = new long[answers.length];

        int maxIndex = 0;

        Search[] searchQA = new Search[3];
        Search[] searchAnswers = new Search[3];
        FutureTask[] futureQA = new FutureTask[answers.length];
        FutureTask[] futureAnswers = new FutureTask[answers.length];
        FutureTask futureQuestion = new FutureTask<Long>(new SearchAndOpen(question));
        new Thread(futureQuestion).start();
        for (int i = 0; i < answers.length; i++) {
            searchQA[i] = new Search(question + " " + answers[i]);
            searchAnswers[i] = new Search(answers[i]);

            futureQA[i] = new FutureTask<Long>(searchQA[i]);
            futureAnswers[i] = new FutureTask<Long>(searchAnswers[i]);
            new Thread(futureQA[i]).start();
            new Thread(futureAnswers[i]).start();
        }
        try {
            while (!futureQuestion.isDone()) {
            }
            countQuestion = (Long)futureQuestion.get();
            for (int i = 0; i < answers.length; i++) {
                while (true) {
                    if(futureAnswers[i].isDone() && futureQA[i].isDone()){
                        break;
                    }
                }
                countQA[i] = (Long)futureQA[i].get();
                countAnswer[i] = (Long)futureAnswers[i].get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        float[] ans = new float[answers.length];
        for (int i = 0; i < answers.length; i++) {
            ans[i] = (float) countQA[i] / (float) (countQuestion * countAnswer[i]);
            maxIndex = (ans[i] > ans[maxIndex]) ? i : maxIndex;
        }
        //根据pmi值进行打印搜索结果
        int[] rank = rank(ans);
        for (int i : rank) {
            System.out.print(answers[i]);
            System.out.print(" countQA:" + countQA[i]);
            System.out.print(" countAnswer:" + countAnswer[i]);
            System.out.println(" ans:" + ans[i]);
        }

        System.out.println("--------最终结果-------");
        System.out.println(answers[maxIndex]);
        endTime = System.currentTimeMillis();
        float excTime = (float) (endTime - startTime) / 1000;

        System.out.println("执行时间：" + excTime + "s");
    }

    /**
     * @param floats pmi值
     * @return 返回排序的rank
     */
    private static int[] rank(float[] floats) {
        int[] rank = new int[floats.length];
        float[] f = Arrays.copyOf(floats, floats.length);
        Arrays.sort(f);
        for (int i = 0; i < floats.length; i++) {
            for (int j = 0; j < floats.length; j++) {
                if (f[i] == floats[j]) {
                    rank[i] = j;
                }
            }
        }
        return rank;
    }
}
