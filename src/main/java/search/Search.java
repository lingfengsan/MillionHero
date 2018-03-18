package search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.Callable;

/**
 * Created by 618 on 2018/1/12.
 *
 * @author lingfengsan
 */
public interface Search extends Callable<Long> {
    /**
     *
     * @return 返回得到查到的搜索数量
     * @throws IOException IO异常
     */
    Long search() throws IOException;
}
