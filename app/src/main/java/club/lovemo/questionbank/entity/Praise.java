package club.lovemo.questionbank.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by John.
 */

public class Praise extends BmobObject {
    private String UserId;
    private String QuestionId;

    @Override
    public String toString() {
        return "Praise{" +
                "UserId='" + UserId + '\'' +
                ", QuestionId='" + QuestionId + '\'' +
                '}';
    }

    public Praise() {
    }

    public Praise(String userId, String questionId) {
        UserId = userId;
        QuestionId = questionId;
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
}
