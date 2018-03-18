package similarity.impl;

import similarity.Similarity;

/**
 * Created by 618 on 2018/1/24.
 * @author lingfengsan
 */
public class SimilarityFactory {
    public static Similarity getSimlarity(int selection,String question,String answer){
        switch (selection){
            case 2:{
                return new BaiDuSimilarity(question,answer);
            }
            default:{
                return new PmiSimilarity(question,answer);
            }
        }
    }
}
