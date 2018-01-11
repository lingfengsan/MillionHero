package entity.cddh;

import java.util.Arrays;

/**
 * Created by Doodlister on 2018/1/11.
 * @author Doodlister
 * 冲顶大会  Envent
 */
public class Event {

    private int answerTime;
    private int correctOption;
    private String desc;
    private int displayOrder;
    private int liveId;
    private String[] options;
    private int questionId;
    private long showTime;
    private int[] stats;
    private int status;
    private String type;


    public int getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(int answerTime) {
        this.answerTime = answerTime;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public int getLiveId() {
        return liveId;
    }

    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public int[] getStats() {
        return stats;
    }

    public void setStats(int[] stats) {
        this.stats = stats;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Event{" +
                "answerTime=" + answerTime +
                ", correctOption=" + correctOption +
                ", desc='" + desc + '\'' +
                ", displayOrder=" + displayOrder +
                ", liveId=" + liveId +
                ", options=" + Arrays.toString(options) +
                ", questionId=" + questionId +
                ", showTime=" + showTime +
                ", stats=" + Arrays.toString(stats) +
                ", status=" + status +
                ", type='" + type + '\'' +
                '}';
    }

    public Event(String[] options, int[] stats) {
        this.options = options;
        this.stats = stats;
    }

    public Event(int answerTime, int correctOption, String desc, int displayOrder, int liveId, String[] options, int questionId, long showTime, int[] stats, int status, String type) {
        this.answerTime = answerTime;
        this.correctOption = correctOption;
        this.desc = desc;
        this.displayOrder = displayOrder;
        this.liveId = liveId;
        this.options = options;
        this.questionId = questionId;
        this.showTime = showTime;
        this.stats = stats;
        this.status = status;
        this.type = type;
    }
}
