package ocr.impl;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import ocr.OCR;

import java.awt.*;
import java.io.File;

/**
 * Created by 618 on 2018/1/8.
 *
 * @author lingfengsan
 */
public class TessOCR implements OCR {
    private ITesseract instance;

    TessOCR() {
        instance = new Tesseract();
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        instance.setLanguage("chi_sim");
        //Set the tessdata path
        instance.setDatapath(tessDataFolder.getAbsolutePath());
    }

    @Override
    public String getOCR(String path) {
        Long start = System.currentTimeMillis();
        File file = new File(path);
        String result = null;
        try {
            result = instance.doOCR(file);
        } catch (TesseractException e) {
            System.err.println("tessOCR提取图片文字信息失败");
        }
        float time=(System.currentTimeMillis()-start)/(1000f);
        System.out.println("tessOCR提取信息成功，耗时："+time+"s");
        return result;
    }

    public static void main(String[] args) {
        String path = "D:\\23910392848779368.png";
        TessOCR tessOCR = new TessOCR();
        System.out.println(tessOCR.getOCR(path));
    }
}
