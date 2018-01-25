package gui;

import com.baidu.aip.nlp.AipNlp;
import org.apache.log4j.Logger;
import pojo.Config;
import similarity.impl.BaiDuSimilarity;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by 618 on 2018/1/24.
 *
 * @author lingfengsan
 */
public class BaiduNlpSettingDialog {
    private static JDialog dialog;
    private static Container dialogPane;
    private static JTextField appIdText;
    private static JTextField apiKeyText;
    private static JTextField secretKeyText;
    private static Logger logger = Logger.getLogger(BaiDuOCRSettingDialog.class);

    /**
     * @param f 设置OCR界面
     */
    public BaiduNlpSettingDialog(JFrame f) {
        dialog = new JDialog(f, "输入", true);
        dialog.setBounds(20, 200, 400, 220);
        dialog.setLayout(null);
        dialogPane = dialog.getContentPane();
        addAppId();
        addApiKey();
        addSecretKey();
        addSetFinishButton();
        dialog.setVisible(true);
    }

    /**
     * 创建文本域用于用户输入APP_ID
     */
    private static void addAppId() {
        JLabel adbPathLabel = new JLabel("APP_ID：");
        adbPathLabel.setBounds(10, 20, 120, 25);
        dialogPane.add(adbPathLabel);
        appIdText = new JTextField(Config.getNlpAppId(), 50);
        appIdText.setBounds(130, 20, 250, 25);
        dialogPane.add(appIdText);
    }

    /**
     * 创建文本域用于用户输入API_KEY
     */
    private static void addApiKey() {
        JLabel label = new JLabel("API_KEY：");
        label.setBounds(10, 50, 120, 25);
        dialogPane.add(label);
        apiKeyText = new JTextField(Config.getNlpApiKey(), 50);
        apiKeyText.setBounds(130, 50, 250, 25);
        dialogPane.add(apiKeyText);
    }

    /**
     * 创建文本域用于用户输入SECRET_KEY
     */
    private static void addSecretKey() {
        JLabel label = new JLabel("SECRET_KEY：");
        label.setBounds(10, 80, 120, 25);
        dialogPane.add(label);
        secretKeyText = new JTextField(Config.getNlpSecretKey(), 50);
        secretKeyText.setBounds(130, 80, 250, 25);
        dialogPane.add(secretKeyText);
    }

    /**
     * 增加设置完成按钮
     */
    private static void addSetFinishButton() {
        final JButton setFinishButton = new JButton("设置完成");
        setFinishButton.setBounds(160, 110, 100, 25);
        dialogPane.add(setFinishButton);
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Config.set("NlpAPP_ID", appIdText.getText());
                Config.set("NlpAPI_KEY", apiKeyText.getText());
                Config.set("NlpSECRET_KEY", secretKeyText.getText());

                try {
                    Utils.storeConfig();
                    AipNlp nlpClient = new AipNlp(Config.getNlpAppId().trim(),
                            Config.getNlpApiKey().trim(), Config.getNlpSecretKey().trim());
                    // 可选：设置网络连接参数
                    nlpClient.setConnectionTimeoutInMillis(2000);
                    nlpClient.setSocketTimeoutInMillis(60000);
                    BaiDuSimilarity.setClient(nlpClient);
                } catch (IOException e1) {
                    logger.error("存储配置失败");
                }
                dialog.setVisible(false);
            }
        };
        setFinishButton.addActionListener(listener);
    }
}
