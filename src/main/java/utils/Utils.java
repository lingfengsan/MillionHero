package utils;

import pojo.Config;
import pojo.Information;

import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * Created by lingfengsan on 2018/1/12.
 *
 * @author lingfengsan
 */
public class Utils {
    /**
     * ABD_PATH此处应更改为自己的adb目录
     * HERO_PATH更改自己存放图片的地址
     */
    private String adbPath;
    private String imagePath;
    private static final Long MIN_IMAGE_SIZE = 1000L;
    private static Properties heroProperties = new Properties();
    public Utils(){}
    public Utils(String adbPath, String imagePath) {
        this.adbPath = adbPath;
        this.imagePath = imagePath;
    }

    public void openBrowser(String path) {
        try {
            //获取操作系统的名字
            String osName = System.getProperty("os.name", "");

            if (osName.startsWith("Mac OS")) {
                //苹果的打开方式
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
                openURL.invoke(null, new Object[]{path});
            } else if (osName.startsWith("Windows")) {
                //windows的打开方式。
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + path);
            } else if (osName.startsWith("Linux")) {
                //Linux的打开方式
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(URI.create(path));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public Information getInformation(String str) {
        //先去除空行
        str = str.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").
                replaceAll("^((\r\n)|\n)", "");
        str=str.replace(" ","");
        //问号统一替换为英文问号防止报错
        str=str.replace("？","?");
        int begin=(str.charAt(1)>='0'&& str.charAt(1)<='9')?2:1;
        str=str.replaceFirst("\\.","");
        String question = str.trim().substring(begin, str.indexOf('?') + 1);
        question = question.replaceAll("((\r\n)|\n)", "");
        System.out.println(question);
        String remain = str.substring(str.indexOf("?") + 1);
        String[] ans = remain.trim().split("\n");
        return new Information(question,ans);
    }
    public String getImage() {
        //获取当前时间作为名字
        Date current = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String curDate = df.format(current);
        File curPhoto = new File(imagePath, curDate + ".png");
        //截屏存到手机本地
        try {
                Process process = Runtime.getRuntime().exec(adbPath
                        + " shell /system/bin/screencap -p /sdcard/screenshot.png");
                process.waitFor();
                //将截图放在电脑本地
                process = Runtime.getRuntime().exec(adbPath
                        + " pull /sdcard/screenshot.png " + curPhoto.getAbsolutePath());
                process.waitFor();
                if(!curPhoto.exists() || curPhoto.length() < MIN_IMAGE_SIZE){
                    System.err.println("截取图片失败，请检查环境搭建");
                }
                //返回当前图片名字
            return curPhoto.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("获取图片失败");
        return null;
    }


    /**
     * 对rank值进行排序
     *
     * @param floats pmi值
     * @return 返回排序的rank
     */
    public static int[] rank(double[] floats) {
        int[] rank = new int[floats.length];
        double[] f = Arrays.copyOf(floats, floats.length);
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
    public static void storeConfig() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("hero.properties", false);
        heroProperties.setProperty("APP_ID", Config.getAppId());
        heroProperties.setProperty("API_KEY", Config.getApiKey());
        heroProperties.setProperty("SECRET_KEY",Config.getSecretKey());
        heroProperties.setProperty("ADB_PATH", Config.getAdbPath());
        heroProperties.setProperty("PHOTO_PATH", Config.getPhotoPath());
        heroProperties.setProperty("NlpAPP_ID", Config.getNlpAppId());
        heroProperties.setProperty("NlpAPI_KEY", Config.getNlpApiKey());
        heroProperties.setProperty("NlpSECRET_KEY", Config.getNlpSecretKey());
        heroProperties.store(fileOutputStream, "million hero properties");
        fileOutputStream.close();
    }
    public static void loadConfig() throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream("hero.properties"));
        heroProperties.load(in);
        for (String key : heroProperties.stringPropertyNames()) {
            Config.set(key, heroProperties.getProperty(key));
        }
        in.close();
    }
    public static void initialConfig() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("hero.properties", true);
        heroProperties.setProperty("ADB_PATH", "D:\\adb\\adb");
        heroProperties.setProperty("PHOTO_PATH", "D:\\Photo");
        heroProperties.setProperty("APP_ID", "APP_ID");
        heroProperties.setProperty("API_KEY", "API_KEY");
        heroProperties.setProperty("SECRET_KEY", "SECRET_KEY");
        heroProperties.setProperty("NlpAPP_ID", "NlpAPP_ID");
        heroProperties.setProperty("NlpAPI_KEY", "NlpAPI_KEY");
        heroProperties.setProperty("NlpSECRET_KEY", "NlpSECRET_KEY");
        heroProperties.store(fileOutputStream, "million hero properties");
        fileOutputStream.close();
    }
    public static void main(String[] args) {
        String adb = "D:\\software\\Android\\android-sdk\\platform-tools\\adb";
        String imagePath = "D:\\Photo";
        Utils utils = new Utils(adb, imagePath);
        utils.getImage();
    }
}
