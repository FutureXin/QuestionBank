package club.lovemo.questionbank.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by John
 * 收藏类
 */

public class Collection extends BmobObject {
    private String UserId;
    private String QuestionId;
    private String Type;
    private String Topic;
    private String CHARACTERISTIC;
    private boolean isDel;

    @Override
    public String toString() {
        return "Collection{" +
                "UserId='" + UserId + '\'' +
                ", QuestionId='" + QuestionId + '\'' +
                ", Type='" + Type + '\'' +
                ", Topic='" + Topic + '\'' +
                ", CHARACTERISTIC='" + CHARACTERISTIC + '\'' +
                ", isDel=" + isDel +
                '}';
    }

    public Collection() {
    }

    public Collection(String userId, String questionId, String type, String topic, String CHARACTERISTIC,boolean isDel) {
        this.UserId = userId;
        this.QuestionId = questionId;
        this.Type = type;
        this.Topic = topic;
        this.isDel = isDel;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(String questionId) {
        QuestionId = questionId;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }

    public String getCHARACTERISTIC() {
        return CHARACTERISTIC;
    }

    public void setCHARACTERISTIC(String CHARACTERISTIC) {
        this.CHARACTERISTIC = CHARACTERISTIC;
    }

    public boolean isDel() {
        return isDel;
    }

    public void setDel(boolean del) {
        isDel = del;
    }
}
