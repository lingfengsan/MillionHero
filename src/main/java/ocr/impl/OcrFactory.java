package ocr.impl;

import ocr.Ocr;

/**
 * Created by 618 on 2018/1/12.
 * @author lingfengsan
 */
public class OcrFactory {
    public Ocr getOcr(int choice){
        switch (choice){
            case 1:{
                System.out.println("欢迎使用TessOCR");
                return new TessOcr();
            }
            case 2:{
                System.out.println("欢迎使用百度OCR");
                return new BaiDuOcr();
            }
            default:{
                System.out.println("欢迎使用TessOCR");
                return new TessOcr();
            }
        }

    }
}
