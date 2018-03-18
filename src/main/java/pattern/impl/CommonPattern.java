package pattern.impl;

import com.baidu.aip.nlp.AipNlp;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ocr.OCR;
import pattern.Pattern;
import search.impl.SearchFactory;
import similarity.Similarity;
import similarity.impl.BaiDuSimilarity;
import similarity.impl.SimilarityFactory;
import utils.ImageHelper;
import pojo.Information;
import utils.Utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;

/**
 * Created by lingfengsan on 2018/1/18.
 *
 * @author lingfengsan
 */
public class CommonPattern implements Pattern {
    private static final String QUESTION_FLAG = "?";
    private static int[] startX = {100, 100, 80};
    private static int[] startY = {300, 300, 300};
    private static int[] width = {900, 900, 900};
    private static int[] height = {900, 900, 700};
    private ImageHelper imageHelper = new ImageHelper();
    private SearchFactory searchFactory = new SearchFactory();
    private int patterSelection;
    private int searchSelection;
    private OCR ocr;
    private Utils utils;
    private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("similarity").build();

    private ExecutorService pools = new ThreadPoolExecutor(7, 12,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());


    public void setPatterSelection(int patterSelection) {
        this.patterSelection = patterSelection;
    }

    public void setSearchSelection(int searchSelection) {
        this.searchSelection = searchSelection;
    }

    public void setOcr(OCR ocr) {
        this.ocr = ocr;
    }

    public void setUtils(Utils utils) {
        this.utils = utils;
    }


    @Override
    public String run() throws UnsupportedEncodingException {
        //       记录开始时间
        long startTime;
        //       记录结束时间
        long endTime;
        StringBuilder sb = new StringBuilder();
        startTime = System.currentTimeMillis();
        //获取图片
        String imagePath = utils.getImage();
        System.out.println("图片获取成功");
        //裁剪图片
        imageHelper.cutImage(imagePath, imagePath,
                startX[patterSelection], startY[patterSelection], width[patterSelection], height[patterSelection]);
        //图像识别
        Long beginOfDetect = System.currentTimeMillis();
        String questionAndAnswers = ocr.getOCR(new File(imagePath));
        sb.append(questionAndAnswers);
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
        //求相关性
        int numOfAnswer = answers.length > 3 ? 4 : answers.length;
        double[] result = new double[numOfAnswer];
        Similarity[] similarities = new Similarity[numOfAnswer];
        FutureTask[] futureTasks = new FutureTask[numOfAnswer];
        BaiDuSimilarity.setClient(new AipNlp("10732092", "pdAtmzlooEbrcfYG4l0kIluf",
                "sHjPBnKt58crPuFogTgQ5Wki0TrHYO2c"));
        for (int i = 0; i < numOfAnswer; i++) {
            similarities[i] = SimilarityFactory.getSimlarity(2, question, answers[i]);
            futureTasks[i] = new FutureTask<Double>(similarities[i]);
            pools.execute(futureTasks[i]);
        }
        for (int i = 0; i < numOfAnswer; i++) {
            while (true) {
                if (futureTasks[i].isDone()) {
                    break;
                }
            }
            try {
                result[i] = (Double) futureTasks[i].get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        //搜索

        FutureTask[] futureQuestion = new FutureTask[1];
        futureQuestion[0] = new FutureTask<Long>(searchFactory.getSearch(searchSelection, question, true));
        pools.execute(futureQuestion[0]);


        //根据pmi值进行打印搜索结果
        int[] rank = Utils.rank(result);
        for (int i : rank) {
            sb.append(answers[i]);
            sb.append(" 相似度为:").append(result[i]).append("\n");
        }

        sb.append("--------最终结果-------\n");
        sb.append(answers[rank[rank.length - 1]]);
        endTime = System.currentTimeMillis();
        float excTime = (float) (endTime - startTime) / 1000;

        sb.append("执行时间：").append(excTime).append("s").append("\n");
        return sb.toString();
    }
}
