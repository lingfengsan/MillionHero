import ocr.OCR;
import ocr.impl.OCRFactory;
import pattern.Pattern;
import pattern.impl.PatternFactory;
import utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;

/**
 * Created by lingfengsan on 2018/1/16.
 *
 * @author lingfengsan
 */
public class MainGUI {

    private static final OCRFactory OCR_FACTORY = new OCRFactory();
    private static final PatternFactory PATTERN_FACTORY = new PatternFactory();
    private static JTextField adbPathText;
    private static JTextField imagePathText;
    private static Pattern pattern;
    private static ButtonGroup ocrSelectionButton=new ButtonGroup();
    private static int ocrSelection=1;
    private static int patternSelection=1;
    private static JTextArea resultTextArea;
    public static void main(String[] args) {
        // 创建 JFrame 实例
        JFrame frame = new JFrame("答题助手");
        // Setting the width and height of frame
        frame.setSize(500, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

//        创建面板
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);
        addAdbPath(panel);
        addImagePath(panel);
        addOCRSelection(panel);
        addSetFinishButton(panel);
        addRunButton(panel);
        addResultTextArea(panel);
        // 设置界面可见
        frame.setVisible(true);
    }
//    创建文本域用于用户输入adb路径
    private static void addAdbPath(JPanel panel) {
        JLabel adbPathLabel = new JLabel("adb路径：");
        adbPathLabel.setBounds(10, 20, 100, 25);
        panel.add(adbPathLabel);
        adbPathText = new JTextField("D:\\adb\\adb", 30);
        adbPathText.setBounds(100, 20, 165, 25);
        panel.add(adbPathText);
    }
    //         创建图片存放路径
    private static void addImagePath(JPanel panel) {
        JLabel imagePathLabel = new JLabel("图片存放路径：");
        imagePathLabel.setBounds(10, 40, 100, 25);
        panel.add(imagePathLabel);
        imagePathText = new JTextField("D:\\Photo", 30);
        imagePathText.setBounds(100, 40, 165, 25);
        panel.add(imagePathText);
    }
    //         创建图片存放路径
    private static void addOCRSelection(JPanel panel) {
        JLabel ocrSelectionLabel = new JLabel("OCR方式：");
        ocrSelectionLabel.setBounds(10, 60, 100, 25);
        addOCRSelection(panel,100,"TessOCR",1);
        addOCRSelection(panel,300,"BaiDuOCR",2);
        panel.add(ocrSelectionLabel);
    }
    private static void addOCRSelection(JPanel panel, int X, String text, final int selection){
        final JRadioButton ocrButton=new JRadioButton(text);
        ocrButton.setBounds(X, 60, 165, 25);
        ocrSelectionButton.add(ocrButton);
        panel.add(ocrButton);
        ActionListener listener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ocrSelection=selection;
            }
        };
        ocrButton.addActionListener(listener);
    }
    //增加设置完成按钮
    private static void addSetFinishButton(JPanel panel){
        final JButton setFinishButton=new JButton("设置完成");
        setFinishButton.setBounds(40, 90, 100, 25);
        panel.add(setFinishButton);
        ActionListener listener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Utils utils=new Utils(adbPathText.getText(),imagePathText.getText());
                OCR ocr = OCR_FACTORY.getOcr(ocrSelection);
                pattern = PATTERN_FACTORY.getPattern(patternSelection, ocr, utils);
            }
        };
        setFinishButton.addActionListener(listener);
    }
    //增加获取答案按钮
    private static void addRunButton(JPanel panel){
        final JButton runButton=new JButton("获取答案");
        runButton.setBounds(160, 90, 100, 25);
        panel.add(runButton);
        ActionListener listener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String result=pattern.run();
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
        resultTextArea=new JTextArea();
        resultTextArea.setBounds(10, 120, 400, 400);
        panel.add(resultTextArea);
    }
}