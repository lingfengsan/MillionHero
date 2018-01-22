import gui.BaiDuOCRSettingDialog;
import ocr.OCR;
import ocr.impl.OCRFactory;
import org.apache.log4j.Logger;
import pattern.impl.CommonPattern;
import pojo.Config;
import utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by lingfengsan on 2018/1/16.
 *
 * @author lingfengsan
 */
public class MainGUI {
    private static Logger logger = Logger.getLogger(MainGUI.class);
    private static final OCRFactory OCR_FACTORY = new OCRFactory();
    private static JTextField adbPathText;
    private static JTextField imagePathText;
    private static ButtonGroup ocrSelectionButton = new ButtonGroup();
    private static ButtonGroup searchSelectionButton = new ButtonGroup();
    private static ButtonGroup patternSelectionButton = new ButtonGroup();
    private static int ocrSelection = 1;
    private static int patternSelection = 1;
    private static int searchSelection = 1;
    private static JTextArea resultTextArea;
    private static JFrame frame = new JFrame("答题助手");
    private static final CommonPattern COMMON_PATTERN = new CommonPattern();
    private static Properties heroProperties = new Properties();

    public static void main(String[] args) throws IOException {
        // Setting the width and height of frame
        frame.setSize(500, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try{
            loadConfig();
        }catch (Exception e){
            initConfig();
        }
        loadConfig();
//        创建面板
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);
        addAdbPath(panel);
        addImagePath(panel);
        addOCRSelection(panel);
        addSearchSelection(panel);
        addSetFinishButton(panel);
        addRunButton(panel);
        addResultTextArea(panel);
        addPatternSelection(panel);
        // 设置界面可见
        frame.setVisible(true);
    }

    //    创建文本域用于用户输入adb路径
    private static void addAdbPath(JPanel panel) {
        JLabel adbPathLabel = new JLabel("adb路径：");
        adbPathLabel.setBounds(10, 20, 100, 25);
        panel.add(adbPathLabel);
        adbPathText = new JTextField(heroProperties.getProperty("ADB_PATH"), 30);
        adbPathText.setBounds(100, 20, 100, 25);
        panel.add(adbPathText);
    }

    //         创建图片存放路径
    private static void addImagePath(JPanel panel) {
        JLabel imagePathLabel = new JLabel("图片存放路径：");
        imagePathLabel.setBounds(10, 45, 100, 25);
        panel.add(imagePathLabel);
        imagePathText = new JTextField(heroProperties.getProperty("PHOTO_PATH"), 30);
        imagePathText.setBounds(100, 45, 100, 25);
        panel.add(imagePathText);
    }

    //         创建OCR选择
    private static void addOCRSelection(JPanel panel) {
        JLabel ocrSelectionLabel = new JLabel("OCR方式：");
        ocrSelectionLabel.setBounds(10, 70, 100, 25);
        addOCRSelection(panel, 100, "TessOCR", 1);
        addOCRSelection(panel, 300, "BaiDuOCR", 2);
        panel.add(ocrSelectionLabel);
    }

    private static void addOCRSelection(JPanel panel, int X, String text, final int selection) {
        final JRadioButton ocrButton = new JRadioButton(text);
        ocrButton.setBounds(X, 70, 165, 25);
        ocrSelectionButton.add(ocrButton);
        panel.add(ocrButton);
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ocrSelection = selection;
                OCR ocr = OCR_FACTORY.getOcr(ocrSelection);
                if (selection == 2) {
                    new BaiDuOCRSettingDialog(frame);
                }
                COMMON_PATTERN.setOcr(ocr);
            }
        };
        ocrButton.addActionListener(listener);
    }

    //         创建搜索选择
    private static void addSearchSelection(JPanel panel) {
        JLabel searchSelectionLabel = new JLabel("搜索方式：");
        searchSelectionLabel.setBounds(10, 95, 100, 25);
        addSearchSelection(panel, 100, "百度", 1);
        addSearchSelection(panel, 300, "搜狗", 2);
        panel.add(searchSelectionLabel);
    }

    private static void addSearchSelection(JPanel panel, int X, String text, final int selection) {
        final JRadioButton searchButton = new JRadioButton(text);
        searchButton.setBounds(X, 95, 165, 25);
        searchSelectionButton.add(searchButton);
        panel.add(searchButton);
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchSelection = selection;
            }
        };
        searchButton.addActionListener(listener);
    }

    //         创建模式选择
    private static void addPatternSelection(JPanel panel) {
        JLabel patternSelectionLabel = new JLabel("游戏模式：");
        patternSelectionLabel.setBounds(10, 120, 100, 25);
        addPatternSelection(panel, 100, "百万英雄", 1);
        addPatternSelection(panel, 300, "冲顶大会", 2);
        panel.add(patternSelectionLabel);
    }

    private static void addPatternSelection(JPanel panel, int X, String text, final int selection) {
        final JRadioButton patternButton = new JRadioButton(text);
        patternButton.setBounds(X, 120, 165, 25);
        patternSelectionButton.add(patternButton);
        panel.add(patternButton);
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                patternSelection = selection;
            }
        };
        patternButton.addActionListener(listener);
    }

    //增加设置完成按钮
    private static void addSetFinishButton(JPanel panel) {
        final JButton setFinishButton = new JButton("设置完成");
        setFinishButton.setBounds(40, 145, 120, 25);
        panel.add(setFinishButton);
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Utils utils = new Utils(adbPathText.getText() + "\\adb", imagePathText.getText());
                COMMON_PATTERN.setUtils(utils);
                COMMON_PATTERN.setPatterSelection(patternSelection);
                COMMON_PATTERN.setSearchSelection(searchSelection);
            }
        };
        setFinishButton.addActionListener(listener);
    }

    //增加获取答案按钮
    private static void addRunButton(JPanel panel) {
        final JButton runButton = new JButton("获取答案");
        runButton.setBounds(160, 145, 120, 25);
        panel.add(runButton);
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String result = COMMON_PATTERN.run();
                    System.out.println(result);
                    resultTextArea.setText(result);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
            }
        };
        runButton.addActionListener(listener);
    }

    //         创建图片存放路径
    private static void addResultTextArea(JPanel panel) {
        resultTextArea = new JTextArea();
        resultTextArea.setBounds(10, 170, 400, 400);
        panel.add(resultTextArea);
    }

    private static void initConfig() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("hero.properties", true);
        heroProperties.setProperty("ADB_PATH", "D:\\adb\\adb");
        heroProperties.setProperty("PHOTO_PATH", "D:\\Photo");
        heroProperties.setProperty("APP_ID", "10697064");
        heroProperties.setProperty("API_KEY", "Y2Dyel1bZwvsVRS00RZ9iBzh");
        heroProperties.setProperty("SECRET_KEY", "ED50nYFA3GbhM9AdyoZhC0qqweP9WjtY");
        heroProperties.store(fileOutputStream, "million hero properties");
        fileOutputStream.close();
    }

    private static void loadConfig() throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream("hero.properties"));
        heroProperties.load(in);
        for (String key : heroProperties.stringPropertyNames()) {
            Config.set(key, heroProperties.getProperty(key));
        }
        in.close();
    }


}