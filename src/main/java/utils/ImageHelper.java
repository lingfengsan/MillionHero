package utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by 618 on 2018/1/12.
 *
 * @author lingfengsan
 */
public class ImageHelper {
    /**
     * 图片裁剪通用接口
     *
     * @param src  图片源地址,图片格式PNG
     * @param dest 图片目的地址
     * @param x    图片起始点x坐标
     * @param y    图片起始点y坐标
     * @param w    图片宽度
     * @param h    图片高度
     * @throws IOException 异常处理
     */
    public static void cutImage(String src, String dest, int x, int y, int w, int h) throws IOException {
        Iterator iterator = ImageIO.getImageReadersByFormatName("png");
        ImageReader reader = (ImageReader) iterator.next();
        InputStream in = new FileInputStream(src);
        ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        Rectangle rect = new Rectangle(x, y, w, h);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);
        ImageIO.write(bi, "png", new File(dest));

    }

    public static void main(String[] args) throws IOException {
        String src = "D:\\20180111214256.png";
        String dest=  "D:\\18.png";
        long start=System.currentTimeMillis();
        cutImage(src,dest,100,100,100,100);
        System.out.println(System.currentTimeMillis()-start);
    }
}
