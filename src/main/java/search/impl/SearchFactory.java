package search.impl;


import search.Search;

import java.io.UnsupportedEncodingException;

/**
 * Created by lingfengsan on 2018/1/18.
 * @author lingfengsan
 */
public class SearchFactory {
    public Search getSearch(int choice,String question,Boolean needOpenBrowser){
        switch (choice){
            case 1:{
                return new BaiDuSearch(question,needOpenBrowser);
            }
            case 2:{
                return new SoGouSearch(question,needOpenBrowser);
            }
            default:{
                return new BaiDuSearch(question,needOpenBrowser);
            }
        }

    }
}
