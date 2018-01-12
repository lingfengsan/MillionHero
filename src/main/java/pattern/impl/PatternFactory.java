package pattern.impl;

import ocr.OCR;
import pattern.Pattern;

/**
 * Created by 618 on 2018/1/12.
 * @author lingfengsan
 */
public class PatternFactory {
    public Pattern getPattern(int choice, OCR ocr){
        switch (choice){
            case 1:{
                return new MillionHeroPattern(ocr);
            }
            case 2:{
                return new SummitMeetingPattern(ocr);
            }
            default:{
                return new MillionHeroPattern(ocr);
            }
        }

    }
}
