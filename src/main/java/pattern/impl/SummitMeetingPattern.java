package pattern.impl;

import ocr.OCR;
import pattern.Pattern;
import search.Search;
import search.impl.BaiDuSearch;
import utils.ImageHelper;
import utils.Information;
import utils.Utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by 618 on 2018/1/12.
 *
 * @author lingfengsan
 */
public class SummitMeetingPattern implements Pattern {
    private static final String QUESTION_FLAG = "?";
    private static final int START_X = 80;
    private static final int START_Y = 300;
    private static final int WIDTH = 900;
    private static final int HEIGHT = 700;
    private OCR ocr;
    private Utils utils;
    private ImageHelper imageHelper = new ImageHelper();
    SummitMeetingPattern(OCR ocr, Utils utils) {
        System.out.println("欢迎您进入冲顶大会游戏模式");
        this.ocr = ocr;
        this.utils = utils;
    }

    @Override
    public String run() throws UnsupportedEncodingException {
        //       记录开始时间
        long startTime;
        //       记录结束时间
        long endTime;
        StringBuilder sb=new StringBuilder();
        startTime = System.currentTimeMillis();
        //获取图片
        String imagePath = utils.getImage();
        System.out.println("图片获取成功");
        //裁剪图片
        imageHelper.cutImage(imagePath,imagePath,START_X,START_Y,WIDTH,HEIGHT);
        //图像识别
        Long beginOfDetect = System.currentTimeMillis();
        String questionAndAnswers = ocr.getOCR(new File(imagePath));
        System.out.println("识别成功");
        System.out.println("识别时间：" + (System.currentTimeMillis() - beginOfDetect));
        if (questionAndAnswers == null || !questionAndAnswers.contains(QUESTION_FLAG)) {
            sb.append("问题识别失败，输入回车继续运行\n");
            return sb.toString();
        }
        //获取问题和答案
        System.out.println("检测到题目");
        Information information = utils.getInformation(questionAndAnswers);
        String question = information.getQuestion();
        String[] answers = information.getAns();
        if (question == null) {
            sb.append("问题不存在，继续运行\n");
            return sb.toString();
        } else if (answers.length < 1) {
            sb.append("检测不到答案，继续运行\n");
            return sb.toString();
        }
        sb.append("问题:").append(question).append("\n");
        sb.append("答案：\n");
        for (String answer : answers) {
            sb.append(answer).append("\n");
        }
        //搜索
        long countQuestion = 1;
        int numOfAnswer = answers.length > 3 ? 4 : answers.length;
        long[] countQA = new long[numOfAnswer];
        long[] countAnswer = new long[numOfAnswer];

        int maxIndex = 0;

        Search[] searchQA = new Search[numOfAnswer];
        Search[] searchAnswers = new Search[numOfAnswer];
        FutureTask[] futureQA = new FutureTask[numOfAnswer];
        FutureTask[] futureAnswers = new FutureTask[numOfAnswer];
        FutureTask futureQuestion = new FutureTask<Long>(new BaiDuSearch(question, true));
        new Thread(futureQuestion).start();
        for (int i = 0; i < numOfAnswer; i++) {
            searchQA[i] = new BaiDuSearch((question + " " + answers[i]), false);
            searchAnswers[i] = new BaiDuSearch(answers[i], false);

            futureQA[i] = new FutureTask<Long>(searchQA[i]);
            futureAnswers[i] = new FutureTask<Long>(searchAnswers[i]);
            new Thread(futureQA[i]).start();
            new Thread(futureAnswers[i]).start();
        }
        try {
            while (!futureQuestion.isDone()) {
            }
            countQuestion = (Long) futureQuestion.get();
            for (int i = 0; i < numOfAnswer; i++) {
                while (true) {
                    if (futureAnswers[i].isDone() && futureQA[i].isDone()) {
                        break;
                    }
                }
                countQA[i] = (Long) futureQA[i].get();
                countAnswer[i] = (Long) futureAnswers[i].get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        float[] ans = new float[numOfAnswer];
        for (int i = 0; i < numOfAnswer; i++) {
            ans[i] = (float) countQA[i] / (float) (countQuestion * countAnswer[i]);
            maxIndex = (ans[i] > ans[maxIndex]) ? i : maxIndex;
        }
        //根据pmi值进行打印搜索结果
        int[] rank = Utils.rank(ans);
        for (int i : rank) {

            sb.append(answers[i]);
            sb.append(" countQA:").append(countQA[i]);
            sb.append(" countAnswer:").append(countAnswer[i]);
            sb.append(" ans:").append(ans[i]).append("\n");
        }

        sb.append("--------最终结果-------\n");
        sb.append(answers[maxIndex]);
        endTime = System.currentTimeMillis();
        float excTime = (float) (endTime - startTime) / 1000;

        sb.append("执行时间：").append(excTime).append("s").append("\n");
        return sb.toString();
    }
}
