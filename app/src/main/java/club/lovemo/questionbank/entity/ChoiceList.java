package club.lovemo.questionbank.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by John.
 */

public class ChoiceList extends BmobObject{
    private String options_id;
    private String options;

    public ChoiceList() {
    }

    public ChoiceList(String options_id, String options) {
        this.options_id = options_id;
        this.options = options;
    }

    @Override
    public String toString() {
        return "ChoiceList{" +
                "options_id='" + options_id + '\'' +
                ", options='" + options + '\'' +
                '}';
    }

    public String getOptions_id() {
        return options_id;
    }

    public void setOptions_id(String options_id) {
        this.options_id = options_id;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
