package gui;

import ocr.OCR;
import ocr.impl.BaiDuOCR;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by lingfengsan on 2018/1/19.
 * @author lingfengsan
 */
public class BaiDuOCRSettingDialog {

    private static JDialog dialog;
    private static Container dialogPane;
    private static JTextField appIdText;
    private static JTextField apiKeyText;
    private static JTextField secretKeyText;


    //构造函数
    public BaiDuOCRSettingDialog(JFrame f){
        dialog = new JDialog(f, "输入",true);
        dialog.setBounds(20,200,400,220);
        dialog.setLayout(null);
        dialogPane = dialog.getContentPane();
        addAppId();
        addApiKey();
        addSecretKey();
        addSetFinishButton();
        dialog.setVisible(true);
    }
//    private static String APP_ID = "10697064";
//    private static String API_KEY = "Y2Dyel1bZwvsVRS00RZ9iBzh";
//    private static String SECRET_KEY = "ED50nYFA3GbhM9AdyoZhC0qqweP9WjtY ";
    //    创建文本域用于用户输入APP_ID
    private static void addAppId() {
        JLabel adbPathLabel = new JLabel("APP_ID：");
        adbPathLabel.setBounds(10, 20, 120, 25);
        dialogPane.add(adbPathLabel);
        appIdText = new JTextField("10697064", 50);
        appIdText.setBounds(130, 20, 250, 25);
        dialogPane.add(appIdText);
    }
    //    创建文本域用于用户输入API_KEY
    private static void addApiKey() {
        JLabel label = new JLabel("API_KEY：");
        label.setBounds(10, 50, 120, 25);
        dialogPane.add(label);
        apiKeyText = new JTextField("Y2Dyel1bZwvsVRS00RZ9iBzh", 50);
        apiKeyText.setBounds(130, 50, 250, 25);
        dialogPane.add(apiKeyText);
        System.out.println("test");
    }
    //    创建文本域用于用户输入SECRET_KEY
    private static void addSecretKey() {
        JLabel label = new JLabel("SECRET_KEY：");
        label.setBounds(10, 80, 120, 25);
        dialogPane.add(label);
        secretKeyText = new JTextField("ED50nYFA3GbhM9AdyoZhC0qqweP9WjtY", 50);
        secretKeyText.setBounds(130, 80, 250, 25);
        dialogPane.add(secretKeyText);
    }
    //增加设置完成按钮
    private static void addSetFinishButton(){
        final JButton setFinishButton=new JButton("设置完成");
        setFinishButton.setBounds(160, 110, 100, 25);
        dialogPane.add(setFinishButton);
        ActionListener listener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BaiDuOCR.setApiKey(apiKeyText.getText());
                BaiDuOCR.setAppId(appIdText.getText());
                BaiDuOCR.setSecretKey(secretKeyText.getText());
                dialog.setVisible(false);
            }
        };
        setFinishButton.addActionListener(listener);
    }
}
