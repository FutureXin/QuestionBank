package club.lovemo.questionbank.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by John on 2016/12/13.
 */

public class Type_Topic extends BmobObject {
    private String objectId;
    private String type;
    private String topic;
    private Integer praise;
    private boolean isDel;

    public Type_Topic() {
    }

    public Type_Topic(String objectId, String type, String topic,Integer praise,boolean isDel) {
        this.objectId = objectId;
        this.type = type;
        this.topic = topic;
        this.praise = praise;
        this.isDel = isDel;
    }

    @Override
    public String toString() {
        return "Type_Topic{" +
                "objectId='" + objectId + '\'' +
                ", type='" + type + '\'' +
                ", topic='" + topic + '\'' +
                ", praise='" + praise + '\'' +
                ", Del='" + isDel + '\'' +
                '}';
    }

    @Override
    public String getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPraise() {
        return praise;
    }

    public void setPraise(Integer praise) {
        this.praise = praise;
    }

    public boolean isDel() {
        return isDel;
    }

    public void setDel(boolean del) {
        isDel = del;
    }
}
