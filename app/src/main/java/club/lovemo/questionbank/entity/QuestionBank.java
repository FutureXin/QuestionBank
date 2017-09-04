package club.lovemo.questionbank.entity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by John.
 */

public class QuestionBank extends BmobObject implements Serializable {
    private String TOPIC;
    private String QUESTION_TYPE;
    private String OPTION_A;
    private String OPTION_B;
    private String OPTION_C;
    private String OPTION_D;
    private String OPTION_E;
    private String OPTION_F;
    private String OPTION_G;
    private String OPTION_H;
    private String OPTION_I;
    private String ANSWER_0;
    private String ANSWER_1;
    private String ANSWER_2;
    private String ANSWER_3;
    private String ANSWER_4;
    private String DETAILED;
    private Integer PRAISE;
    private String PROVENANCE_URL;
    private String CHARACTERISTIC;

    public QuestionBank(){

    }

    @Override
    public String toString() {
        return "QuestionBank{" +
                "TOPIC='" + TOPIC + '\'' +
                ", QUESTION_TYPE='" + QUESTION_TYPE + '\'' +
                ", OPTION_A='" + OPTION_A + '\'' +
                ", OPTION_B='" + OPTION_B + '\'' +
                ", OPTION_C='" + OPTION_C + '\'' +
                ", OPTION_D='" + OPTION_D + '\'' +
                ", OPTION_E='" + OPTION_E + '\'' +
                ", OPTION_F='" + OPTION_F + '\'' +
                ", OPTION_G='" + OPTION_G + '\'' +
                ", OPTION_H='" + OPTION_H + '\'' +
                ", OPTION_I='" + OPTION_I + '\'' +
                ", ANSWER_0='" + ANSWER_0 + '\'' +
                ", ANSWER_1='" + ANSWER_1 + '\'' +
                ", ANSWER_2='" + ANSWER_2 + '\'' +
                ", ANSWER_3='" + ANSWER_3 + '\'' +
                ", ANSWER_4='" + ANSWER_4 + '\'' +
                ", DETAILED='" + DETAILED + '\'' +
                ", PRAISE='" + PRAISE + '\'' +
                ", PROVENANCE_URL='" + PROVENANCE_URL + '\'' +
                ", CHARACTERISTIC='" + CHARACTERISTIC + '\'' +
                '}';
    }

    public QuestionBank(String TOPIC, String QUESTION_TYPE, String OPTION_A, String OPTION_B, String OPTION_C, String OPTION_D, String OPTION_E, String OPTION_F, String OPTION_G, String OPTION_H, String OPTION_I, String ANSWER_0, String ANSWER_1, String ANSWER_2, String ANSWER_3, String ANSWER_4,Integer PRAISE,String PROVENANCE_URL,String DETAILED, String CHARACTERISTIC) {
        this.TOPIC = TOPIC;
        this.QUESTION_TYPE = QUESTION_TYPE;
        this.OPTION_A = OPTION_A;
        this.OPTION_B = OPTION_B;
        this.OPTION_C = OPTION_C;
        this.OPTION_D = OPTION_D;
        this.OPTION_E = OPTION_E;
        this.OPTION_F = OPTION_F;
        this.OPTION_G = OPTION_G;
        this.OPTION_H = OPTION_H;
        this.OPTION_I = OPTION_I;
        this.ANSWER_0 = ANSWER_0;
        this.ANSWER_1 = ANSWER_1;
        this.ANSWER_2 = ANSWER_2;
        this.ANSWER_3 = ANSWER_3;
        this.ANSWER_4 = ANSWER_4;
        this.DETAILED = DETAILED;
        this.PRAISE = PRAISE;
        this.PROVENANCE_URL = PROVENANCE_URL;
        this.CHARACTERISTIC = CHARACTERISTIC;
    }

    public String getTOPIC() {
        return TOPIC;
    }

    public void setTOPIC(String TOPIC) {
        this.TOPIC = TOPIC;
    }

    public String getQUESTION_TYPE() {
        return QUESTION_TYPE;
    }

    public void setQUESTION_TYPE(String QUESTION_TYPE) {
        this.QUESTION_TYPE = QUESTION_TYPE;
    }

    public String getOPTION_A() {
        return OPTION_A;
    }

    public void setOPTION_A(String OPTION_A) {
        this.OPTION_A = OPTION_A;
    }

    public String getOPTION_B() {
        return OPTION_B;
    }

    public void setOPTION_B(String OPTION_B) {
        this.OPTION_B = OPTION_B;
    }

    public String getOPTION_C() {
        return OPTION_C;
    }

    public void setOPTION_C(String OPTION_C) {
        this.OPTION_C = OPTION_C;
    }

    public String getOPTION_D() {
        return OPTION_D;
    }

    public void setOPTION_D(String OPTION_D) {
        this.OPTION_D = OPTION_D;
    }

    public String getOPTION_E() {
        return OPTION_E;
    }

    public void setOPTION_E(String OPTION_E) {
        this.OPTION_E = OPTION_E;
    }

    public String getOPTION_F() {
        return OPTION_F;
    }

    public void setOPTION_F(String OPTION_F) {
        this.OPTION_F = OPTION_F;
    }

    public String getOPTION_G() {
        return OPTION_G;
    }

    public void setOPTION_G(String OPTION_G) {
        this.OPTION_G = OPTION_G;
    }

    public String getOPTION_H() {
        return OPTION_H;
    }

    public void setOPTION_H(String OPTION_H) {
        this.OPTION_H = OPTION_H;
    }

    public String getOPTION_I() {
        return OPTION_I;
    }

    public void setOPTION_I(String OPTION_I) {
        this.OPTION_I = OPTION_I;
    }

    public String getANSWER_0() {
        return ANSWER_0;
    }

    public void setANSWER_0(String ANSWER_0) {
        this.ANSWER_0 = ANSWER_0;
    }

    public String getANSWER_1() {
        return ANSWER_1;
    }

    public void setANSWER_1(String ANSWER_1) {
        this.ANSWER_1 = ANSWER_1;
    }

    public String getANSWER_2() {
        return ANSWER_2;
    }

    public void setANSWER_2(String ANSWER_2) {
        this.ANSWER_2 = ANSWER_2;
    }

    public String getANSWER_3() {
        return ANSWER_3;
    }

    public void setANSWER_3(String ANSWER_3) {
        this.ANSWER_3 = ANSWER_3;
    }

    public String getANSWER_4() {
        return ANSWER_4;
    }

    public void setANSWER_4(String DETAILED) {
        this.DETAILED = DETAILED;
    }

    public String getDETAILED() {
        return DETAILED;
    }

    public void setDETAILED(String DETAILED) {
        this.DETAILED = DETAILED;
    }

    public Integer getPRAISE() {
        return PRAISE;
    }

    public void setPRAISE(Integer PRAISE) {
        this.PRAISE = PRAISE;
    }

    public String getPROVENANCE_URL() {
        return PROVENANCE_URL;
    }

    public void setPROVENANCE_URL(String PROVENANCE_URL) {
        this.PROVENANCE_URL = PROVENANCE_URL;
    }

    public String getCHARACTERISTIC() {
        return CHARACTERISTIC;
    }

    public void setCHARACTERISTIC(String CHARACTERISTIC) {
        this.CHARACTERISTIC = CHARACTERISTIC;
    }
}
