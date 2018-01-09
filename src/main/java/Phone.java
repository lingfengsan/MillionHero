import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 618 on 2018/1/8.
 *
 * @author lingfengsan
 */
public class Phone {
    /**
     * 此处应更改为自己的adb目录
     */
    private static final String ADB_PATH = "D:\\software\\Android\\android-sdk\\platform-tools\\adb";
    private static final String HERO_PATH = Phone.class.getResource("/").getPath();

    File getImage() {
        //获取当前时间作为名字
        Date current = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String curDate = df.format(current);
        System.out.println(curDate);
        File curPhoto = new File(HERO_PATH, curDate + ".png");
        //截屏存到手机本地
        try {
            Runtime.getRuntime().exec(ADB_PATH
                    + " shell /system/bin/screencap -p /sdcard/screenshot.png");
            Thread.sleep(1000);
            //将截图放在电脑本地
            Runtime.getRuntime().exec(ADB_PATH
                    + " pull /sdcard/screenshot.png " + curPhoto.getAbsolutePath());
            Thread.sleep(500);
            //返回当前图片名字
            return curPhoto;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("获取图片失败");
        return null;
    }

    public static void main(String[] args) {
        new Phone().getImage();
    }
}
