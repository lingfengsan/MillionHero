package pattern;

import java.io.UnsupportedEncodingException;

/**
 * Created by 618 on 2018/1/12.
 * @author lingfengsan
 */
public interface Pattern {
    /**
     *
     * @return 返回运行得到的结果
     * @throws UnsupportedEncodingException 不支持的编码异常
     */
    String run() throws UnsupportedEncodingException;
}
