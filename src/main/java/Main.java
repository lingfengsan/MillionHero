import ocr.OCR;
import ocr.impl.OCRFactory;
import pattern.Pattern;
import pattern.impl.PatternFactory;
import utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by 618 on 2018/1/8.
 *
 * @author lingfengsan
 */
public class Main {
    /**
     * ADB_PATH为自己的adb驱动目录,Windows无需修改
     * IMAGE_PATH为本机图片存放目录
     */
//    private static final String ADB_PATH = "D:\\software\\Android\\android-sdk\\platform-tools\\adb";
    private static final String ADB_PATH = new File("").getAbsolutePath()+"\\target\\classes\\adb\\adb";
    private static final String IMAGE_PATH = "D:\\Photo";
    private static final OCRFactory OCR_FACTORY = new OCRFactory();
    private static final PatternFactory PATTERN_FACTORY = new PatternFactory();
    private static final Utils UTILS = new Utils(ADB_PATH, IMAGE_PATH);

    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("请选择您要使用的文字识别方式\n1.TessOCR\n2.百度OCR");
        System.out.println("默认使用TessOCR，选择后回车");
        OCR ocr = OCR_FACTORY.getOcr(Integer.valueOf(bf.readLine()));
        System.out.println("请选择您要进入的游戏\n1.百万英雄\n2.冲顶大会");
        System.out.println("默认为百万英雄，选择后回车");
        Pattern pattern = PATTERN_FACTORY.getPattern(Integer.valueOf(bf.readLine()), ocr, UTILS);
        while (true) {
            String str = bf.readLine();
            if ("exit".equals(str)) {
                System.out.println("ヾ(￣▽￣)Bye~Bye~");
                break;
            } else {
                if (str.length() == 0) {
                    System.out.print("开始答题");
                    pattern.run();
                }
            }
        }


    }
}
