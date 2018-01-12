package ocr;

import java.io.File;

/**
 * Created by 618 on 2018/1/12.
 * @author lingfengsan
 */
public interface OCR {
    /**
     *获取识别图片后的结果
     * @param path String
     * @return String
     */
    String getOCR(String path);
}
