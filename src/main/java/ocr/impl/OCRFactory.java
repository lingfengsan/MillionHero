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
                return new TessOCR();
            }
            case 2:{
                System.out.println("百度OCR正在完善，已配置为TessOCR");
                return new TessOCR();
            }
            default:{
                return new TessOCR();
            }
        }

    }
}
