package club.lovemo.questionbank.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by John.
 */

public class Delete extends BmobObject{
    private String question_objectId;

    public Delete(String question_objectId){
        this.question_objectId=question_objectId;
    }

    public void setQuestion_objectId(String question_objectId){
        this.question_objectId=question_objectId;
    }
    public String getQuestion_objectId(){
        return question_objectId;
    }
}
