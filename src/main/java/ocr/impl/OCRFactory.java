package ocr.impl;

import ocr.OCR;

/**
 * Created by 618 on 2018/1/12.
 * @author lingfengsan
 */
public class OCRFactory {
    public OCR getOcr(int choice){
        switch (choice){
            case 1:{
                System.out.println("欢迎使用TessOCR");
                return new TessOCR();
            }
            case 2:{
                System.out.println("欢迎使用百度OCR");
                return new BaiDuOCR();
            }
            default:{
                System.out.println("欢迎使用TessOCR");
                return new TessOCR();
            }
        }

    }
}
