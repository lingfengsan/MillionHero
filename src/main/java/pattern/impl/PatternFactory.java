package pattern.impl;

import ocr.OCR;
import pattern.Pattern;
import utils.Utils;

/**
 * Created by 618 on 2018/1/12.
 *
 * @author lingfengsan
 */
public class PatternFactory {
    public Pattern getPattern(int choice, OCR ocr, Utils utils) {
        switch (choice) {
            case 1: {
                return new MillionHeroPattern(ocr, utils);
            }
            case 2: {
                return new SummitMeetingPattern(ocr, utils);
            }
            default: {
                return new MillionHeroPattern(ocr, utils);
            }
        }

    }
}
