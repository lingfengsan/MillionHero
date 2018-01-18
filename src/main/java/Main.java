import ocr.OCR;
import ocr.impl.OCRFactory;
import pattern.Pattern;
import pattern.impl.CommonPattern;
import utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 * Created by 618 on 2018/1/8.
 *
 * @author lingfengsan
 */
public class Main {
    /**
     * ADB_PATH为自己的adb驱动目录，可以放在resource目录下，也可以自己指定
     * IMAGE_PATH为本机图片存放目录，必须是已存在目录
     */
    private static final String ADB_PATH = "D:\\adb\\adb";
    private static final String IMAGE_PATH = "D:\\Photo";
    private static final OCRFactory OCR_FACTORY = new OCRFactory();
    private static final CommonPattern COMMON_PATTERN = new CommonPattern();
    private static final Utils UTILS = new Utils(ADB_PATH, IMAGE_PATH);

    public static void main(String[] args) throws UnsupportedEncodingException {
        Scanner sc=new Scanner(System.in);
        COMMON_PATTERN.setUtils(UTILS);
        System.out.println("请选择您要使用的文字识别方式\n1.TessOCR\n2.百度OCR");
        System.out.println("默认使用TessOCR，选择后回车,不能为空");
        int selection=sc.nextInt();
        COMMON_PATTERN.setOcr(OCR_FACTORY.getOcr(selection));
        System.out.println("请选择您要进入的游戏\n1.百万英雄\n2.冲顶大会");
        System.out.println("默认为百万英雄，选择后回车");
        selection = sc.nextInt();
        COMMON_PATTERN.setPatterSelection(selection);
        COMMON_PATTERN.setSearchSelection(1);

        while (true) {
            String str = sc.nextLine();
            if ("exit".equals(str)) {
                System.out.println("ヾ(￣▽￣)Bye~Bye~");
                break;
            } else {
                if (str.length() == 0) {
                    System.out.print("开始答题");
                    System.out.println(COMMON_PATTERN.run());
                }
            }
        }


    }
}
