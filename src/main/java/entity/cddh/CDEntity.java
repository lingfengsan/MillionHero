package entity.cddh;

import java.util.Map;

/**
 * Created by Doodlister on 2018/1/11.
 * @author Doodlister
 * 冲顶大会 问题实体
 */
public class CDEntity {
    private int code;
    private String msg;
    private Data data;
    private String type;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "CDEntity{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", type='" + type + '\'' +
                '}';
    }
}
