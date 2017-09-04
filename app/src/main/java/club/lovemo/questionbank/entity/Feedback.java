package club.lovemo.questionbank.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by John.
 */

public class Feedback extends BmobObject{
    private String qbobjectid;
    private String userobjectid;
    private String feedbacktext;
    private Boolean isdispose;

    public Feedback(String qbobjectid, String userobjectid, String feedbacktext, Boolean isdispose) {
        this.qbobjectid = qbobjectid;
        this.userobjectid = userobjectid;
        this.feedbacktext = feedbacktext;
        this.isdispose = isdispose;
    }

    public Feedback() {
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "qbobjectid='" + qbobjectid + '\'' +
                ", userobjectid='" + userobjectid + '\'' +
                ", feedbacktext='" + feedbacktext + '\'' +
                ", isdispose=" + isdispose +
                '}';
    }

    public String getQbobjectid() {
        return qbobjectid;
    }

    public void setQbobjectid(String qbobjectid) {
        this.qbobjectid = qbobjectid;
    }

    public String getUserobjectid() {
        return userobjectid;
    }

    public void setUserobjectid(String userobjectid) {
        this.userobjectid = userobjectid;
    }

    public String getFeedbacktext() {
        return feedbacktext;
    }

    public void setFeedbacktext(String feedbacktext) {
        this.feedbacktext = feedbacktext;
    }

    public Boolean getIsdispose() {
        return isdispose;
    }

    public void setIsdispose(Boolean isdispose) {
        this.isdispose = isdispose;
    }
}
