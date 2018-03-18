package ocr;

import java.io.File;

/**
 * Created by 618 on 2018/1/12.
 * @author lingfengsan
 */
public interface Ocr {
    /**
     *获取识别图片后的结果
     * @param file String
     * @return String
     */
    String getOCR(File file);
}
