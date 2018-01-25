package similarity.impl;

import pojo.Config;
import search.Search;
import search.impl.SearchFactory;
import similarity.Similarity;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by lingfengsan on 2018/1/23.
 *
 * @author lingfengsan
 */
public class PmiSimilarity implements Similarity {
    private SearchFactory searchFactory = new SearchFactory();
    private String question;
    private String answer;
    PmiSimilarity(String question,String answer){
        this.question=question;
        this.answer=answer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Double call() {
        FutureTask[] futureTasks = new FutureTask[2];
        Search[] searches = new Search[2];
        searches[0] = searchFactory.getSearch(Config.getSearchSelection()
                , (question + " " + answer), false);
        searches[1] = searchFactory.getSearch(Config.getSearchSelection()
                , answer, false);
        for (int i = 0; i < searches.length; i++) {
            futureTasks[i] = new FutureTask<Long>(searches[i]);
            new Thread(futureTasks[i]).start();
        }
        while (true) {
            if (futureTasks[0].isDone() && futureTasks[1].isDone()) {
                break;
            }
        }
        try {
            Long countQA=(Long)futureTasks[0].get();
            Long countA=(Long)futureTasks[1].get();
            System.out.println(answer+(double)countQA/(double)countA);
            return (double)countQA/(double)countA;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0.0;

    }

}
