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
                return new BaiDuOCR();
            }
            default:{
                return new TessOCR();
            }
        }

    }
}
