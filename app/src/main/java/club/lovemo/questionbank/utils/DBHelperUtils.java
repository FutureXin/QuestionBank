package club.lovemo.questionbank.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import club.lovemo.questionbank.entity.QuestionBank;
import club.lovemo.questionbank.entity.QuestionBankDB;

public class DBHelperUtils {
	private static DBHelperUtils instance;
	public static DBHelperUtils getInstance(){
		if(instance==null){
			instance=new DBHelperUtils();
		}
		return instance;
	}
	private MyDBHelper helper;
	private SQLiteDatabase db;
	
	private DBHelperUtils(){
		helper=new MyDBHelper();
		db=helper.getReadableDatabase();
	}
	public void DBClose(){
		helper.close();
		db.close();
		instance=null;
	}
	public boolean insertQuestion(QuestionBank questionBank){
		ContentValues values=new ContentValues();
		values.put("objectId", questionBank.getObjectId());
		values.put("TOPIC", questionBank.getTOPIC());
		values.put("QUESTION_TYPE", questionBank.getQUESTION_TYPE());
		values.put("OPTION_A", questionBank.getOPTION_A());
		values.put("OPTION_B", questionBank.getOPTION_B());
		values.put("OPTION_C", questionBank.getOPTION_C());
		values.put("OPTION_D", questionBank.getOPTION_D());
		values.put("OPTION_E", questionBank.getOPTION_E());
		values.put("OPTION_F", questionBank.getOPTION_F());
		values.put("OPTION_G", questionBank.getOPTION_G());
		values.put("OPTION_H", questionBank.getOPTION_H());
		values.put("OPTION_I", questionBank.getOPTION_I());
		values.put("ANSWER_0", questionBank.getANSWER_0());
		values.put("ANSWER_1", questionBank.getANSWER_1());
		values.put("ANSWER_2", questionBank.getANSWER_2());
		values.put("ANSWER_3", questionBank.getANSWER_3());
		values.put("ANSWER_4", questionBank.getANSWER_4());
		values.put("DETAILED", questionBank.getDETAILED());
		values.put("createdAt", questionBank.getCreatedAt());
		values.put("updatedAt", questionBank.getUpdatedAt());
		values.put("PRAISE", questionBank.getPRAISE());
		values.put("PROVENANCE_URL", questionBank.getPROVENANCE_URL());
		values.put("CHARACTERISTIC", questionBank.getCHARACTERISTIC());
		long rid=db.insert("QUESTION_TABLE", null, values);
		Utils.print(AppConstants.LogTag,"QUESTION_TABLE数据存储成功");
		return rid>0;
	}
	public boolean updateQuestion(QuestionBank questionBank){
		ContentValues values=new ContentValues();
		values.put("objectId", questionBank.getObjectId());
		values.put("TOPIC", questionBank.getTOPIC());
		values.put("QUESTION_TYPE", questionBank.getQUESTION_TYPE());
		values.put("OPTION_A", questionBank.getOPTION_A());
		values.put("OPTION_B", questionBank.getOPTION_B());
		values.put("OPTION_C", questionBank.getOPTION_C());
		values.put("OPTION_D", questionBank.getOPTION_D());
		values.put("OPTION_E", questionBank.getOPTION_E());
		values.put("OPTION_F", questionBank.getOPTION_F());
		values.put("OPTION_G", questionBank.getOPTION_G());
		values.put("OPTION_H", questionBank.getOPTION_H());
		values.put("OPTION_I", questionBank.getOPTION_I());
		values.put("ANSWER_0", questionBank.getANSWER_0());
		values.put("ANSWER_1", questionBank.getANSWER_1());
		values.put("ANSWER_2", questionBank.getANSWER_2());
		values.put("ANSWER_3", questionBank.getANSWER_3());
		values.put("ANSWER_4", questionBank.getANSWER_4());
		values.put("DETAILED", questionBank.getDETAILED());
		values.put("createdAt", questionBank.getCreatedAt());
		values.put("updatedAt", questionBank.getUpdatedAt());
		values.put("PRAISE", questionBank.getPRAISE());
		values.put("PROVENANCE_URL", questionBank.getPROVENANCE_URL());
		values.put("CHARACTERISTIC", questionBank.getCHARACTERISTIC());
		long rid=db.update("QUESTION_TABLE",values,"objectId=?",new String[]{questionBank.getObjectId()});
		Utils.print(AppConstants.LogTag,"QUESTION_TABLE数据修改成功");
		return rid>0;
	}
	public boolean updateQuestionDB(QuestionBankDB questionBank){
		ContentValues values=new ContentValues();
		values.put("objectId", questionBank.getObjectId());
		values.put("TOPIC", questionBank.getTOPIC());
		values.put("QUESTION_TYPE", questionBank.getQUESTION_TYPE());
		values.put("OPTION_A", questionBank.getOPTION_A());
		values.put("OPTION_B", questionBank.getOPTION_B());
		values.put("OPTION_C", questionBank.getOPTION_C());
		values.put("OPTION_D", questionBank.getOPTION_D());
		values.put("OPTION_E", questionBank.getOPTION_E());
		values.put("OPTION_F", questionBank.getOPTION_F());
		values.put("OPTION_G", questionBank.getOPTION_G());
		values.put("OPTION_H", questionBank.getOPTION_H());
		values.put("OPTION_I", questionBank.getOPTION_I());
		values.put("ANSWER_0", questionBank.getANSWER_0());
		values.put("ANSWER_1", questionBank.getANSWER_1());
		values.put("ANSWER_2", questionBank.getANSWER_2());
		values.put("ANSWER_3", questionBank.getANSWER_3());
		values.put("ANSWER_4", questionBank.getANSWER_4());
		values.put("DETAILED", questionBank.getDETAILED());
		values.put("createdAt", questionBank.getCreatedAt());
		values.put("updatedAt", questionBank.getUpdatedAt());
		values.put("PRAISE", questionBank.getPRAISE());
		values.put("PROVENANCE_URL", questionBank.getPROVENANCE_URL());
		values.put("CHARACTERISTIC", questionBank.getCHARACTERISTIC());
		long rid=db.update("QUESTION_TABLE",values,"objectId=?",new String[]{questionBank.getObjectId()});
		Utils.print(AppConstants.LogTag,"QUESTION_TABLE数据修改成功");
		return rid>0;
	}
	public List<QuestionBankDB> getQuestionList(String KEY_ARRAY){
		Cursor cur;
		List<QuestionBankDB> question_array=new ArrayList<>();
		if(KEY_ARRAY==null||KEY_ARRAY.equals("")){
			cur=db.query("QUESTION_TABLE", null, "CHARACTERISTIC=?", new String[]{""}, null, null, null);
		}else{
			cur=db.query("QUESTION_TABLE", null,"CHARACTERISTIC=?", new String[]{KEY_ARRAY}, null, null, null);
		}
		while (cur.moveToNext()) {
			QuestionBankDB question =new QuestionBankDB();
			String objectId=cur.getString(cur.getColumnIndex("objectId"));
			String TOPIC=cur.getString(cur.getColumnIndex("TOPIC"));
			String QUESTION_TYPE=cur.getString(cur.getColumnIndex("QUESTION_TYPE"));
			String OPTION_A=cur.getString(cur.getColumnIndex("OPTION_A"));
			String OPTION_B=cur.getString(cur.getColumnIndex("OPTION_B"));
			String OPTION_C=cur.getString(cur.getColumnIndex("OPTION_C"));
			String OPTION_D=cur.getString(cur.getColumnIndex("OPTION_D"));
			String OPTION_E=cur.getString(cur.getColumnIndex("OPTION_E"));
			String OPTION_F=cur.getString(cur.getColumnIndex("OPTION_F"));
			String OPTION_G=cur.getString(cur.getColumnIndex("OPTION_G"));
			String OPTION_H=cur.getString(cur.getColumnIndex("OPTION_H"));
			String OPTION_I=cur.getString(cur.getColumnIndex("OPTION_I"));
			String ANSWER_0=cur.getString(cur.getColumnIndex("ANSWER_0"));
			String ANSWER_1=cur.getString(cur.getColumnIndex("ANSWER_1"));
			String ANSWER_2=cur.getString(cur.getColumnIndex("ANSWER_2"));
			String ANSWER_3=cur.getString(cur.getColumnIndex("ANSWER_3"));
			String ANSWER_4=cur.getString(cur.getColumnIndex("ANSWER_4"));
			String DETAILED=cur.getString(cur.getColumnIndex("DETAILED"));
			String createdAt=cur.getString(cur.getColumnIndex("createdAt"));
			String updatedAt=cur.getString(cur.getColumnIndex("updatedAt"));
			int PRAISE=cur.getInt(cur.getColumnIndex("PRAISE"));
			String PROVENANCE_URL=cur.getString(cur.getColumnIndex("PROVENANCE_URL"));
			String CHARACTERISTIC=cur.getString(cur.getColumnIndex("CHARACTERISTIC"));

			question.setObjectId(objectId);
			question.setTOPIC(TOPIC);
			question.setQUESTION_TYPE(QUESTION_TYPE);
			question.setOPTION_A(OPTION_A);
			question.setOPTION_B(OPTION_B);
			question.setOPTION_C(OPTION_C);
			question.setOPTION_D(OPTION_D);
			question.setOPTION_E(OPTION_E);
			question.setOPTION_F(OPTION_F);
			question.setOPTION_G(OPTION_G);
			question.setOPTION_H(OPTION_H);
			question.setOPTION_I(OPTION_I);
			question.setANSWER_0(ANSWER_0);
			question.setANSWER_1(ANSWER_1);
			question.setANSWER_2(ANSWER_2);
			question.setANSWER_3(ANSWER_3);
			question.setANSWER_4(ANSWER_4);
			question.setDETAILED(DETAILED);
			question.setCreatedAt(createdAt);
			question.setUpdatedAt(updatedAt);
			question.setPRAISE(PRAISE);
			question.setPROVENANCE_URL(PROVENANCE_URL);
			question.setCHARACTERISTIC(CHARACTERISTIC);
			question_array.add(question);
		}
		if(!cur.isClosed()){
			cur.close();
		}
		return question_array;
	}
    public List<QuestionBankDB> getQuestionList(String [] KEY_ARRAY) {
		List<String> str=new ArrayList<>();
//		for (int i = 0; i < KEY_ARRAY.length; i++) {
//			str.add("'"+KEY_ARRAY[i]+"'");
//		}
		for (String key_array:KEY_ARRAY) {
			str.add("'"+key_array+"'");
		}
		String string=str.toString();
		String sql="SELECT * FROM QUESTION_TABLE WHERE CHARACTERISTIC IN ("+string.substring(1,string.length()-1)+")";
		Utils.print(AppConstants.LogTag,sql+"查询语句");
        Cursor cur;
        List<QuestionBankDB> question_array=new ArrayList<>();
		cur=db.rawQuery(sql,null);
        while (cur.moveToNext()) {
            QuestionBankDB question =new QuestionBankDB();
            String objectId=cur.getString(cur.getColumnIndex("objectId"));
            String TOPIC=cur.getString(cur.getColumnIndex("TOPIC"));
            String QUESTION_TYPE=cur.getString(cur.getColumnIndex("QUESTION_TYPE"));
            String OPTION_A=cur.getString(cur.getColumnIndex("OPTION_A"));
            String OPTION_B=cur.getString(cur.getColumnIndex("OPTION_B"));
            String OPTION_C=cur.getString(cur.getColumnIndex("OPTION_C"));
            String OPTION_D=cur.getString(cur.getColumnIndex("OPTION_D"));
            String OPTION_E=cur.getString(cur.getColumnIndex("OPTION_E"));
            String OPTION_F=cur.getString(cur.getColumnIndex("OPTION_F"));
            String OPTION_G=cur.getString(cur.getColumnIndex("OPTION_G"));
            String OPTION_H=cur.getString(cur.getColumnIndex("OPTION_H"));
            String OPTION_I=cur.getString(cur.getColumnIndex("OPTION_I"));
            String ANSWER_0=cur.getString(cur.getColumnIndex("ANSWER_0"));
            String ANSWER_1=cur.getString(cur.getColumnIndex("ANSWER_1"));
            String ANSWER_2=cur.getString(cur.getColumnIndex("ANSWER_2"));
            String ANSWER_3=cur.getString(cur.getColumnIndex("ANSWER_3"));
            String ANSWER_4=cur.getString(cur.getColumnIndex("ANSWER_4"));
            String DETAILED=cur.getString(cur.getColumnIndex("DETAILED"));
            String createdAt=cur.getString(cur.getColumnIndex("createdAt"));
            String updatedAt=cur.getString(cur.getColumnIndex("updatedAt"));
			int PRAISE=cur.getInt(cur.getColumnIndex("PRAISE"));
			String PROVENANCE_URL=cur.getString(cur.getColumnIndex("PROVENANCE_URL"));
            String CHARACTERISTIC=cur.getString(cur.getColumnIndex("CHARACTERISTIC"));

            question.setObjectId(objectId);
            question.setTOPIC(TOPIC);
            question.setQUESTION_TYPE(QUESTION_TYPE);
            question.setOPTION_A(OPTION_A);
            question.setOPTION_B(OPTION_B);
            question.setOPTION_C(OPTION_C);
            question.setOPTION_D(OPTION_D);
            question.setOPTION_E(OPTION_E);
            question.setOPTION_F(OPTION_F);
            question.setOPTION_G(OPTION_G);
            question.setOPTION_H(OPTION_H);
            question.setOPTION_I(OPTION_I);
            question.setANSWER_0(ANSWER_0);
            question.setANSWER_1(ANSWER_1);
            question.setANSWER_2(ANSWER_2);
            question.setANSWER_3(ANSWER_3);
            question.setANSWER_4(ANSWER_4);
            question.setDETAILED(DETAILED);
            question.setCreatedAt(createdAt);
            question.setUpdatedAt(updatedAt);
			question.setPRAISE(PRAISE);
			question.setPROVENANCE_URL(PROVENANCE_URL);
            question.setCHARACTERISTIC(CHARACTERISTIC);
            question_array.add(question);
        }
		if(!cur.isClosed()){
			cur.close();
		}
        return question_array;
    }
	public QuestionBankDB getQuestionListById(String id) throws Exception{
		Cursor cur;
		List<QuestionBankDB> question_array=new ArrayList<>();
		if(id==null||id.equals("")){
			throw new Exception("id不能为空");
		}else{
			cur=db.query("QUESTION_TABLE", null,"objectId=?", new String[]{id}, null, null, null);
		}
		while (cur.moveToNext()) {
			QuestionBankDB question =new QuestionBankDB();
			String objectId=cur.getString(cur.getColumnIndex("objectId"));
			String TOPIC=cur.getString(cur.getColumnIndex("TOPIC"));
			String QUESTION_TYPE=cur.getString(cur.getColumnIndex("QUESTION_TYPE"));
			String OPTION_A=cur.getString(cur.getColumnIndex("OPTION_A"));
			String OPTION_B=cur.getString(cur.getColumnIndex("OPTION_B"));
			String OPTION_C=cur.getString(cur.getColumnIndex("OPTION_C"));
			String OPTION_D=cur.getString(cur.getColumnIndex("OPTION_D"));
			String OPTION_E=cur.getString(cur.getColumnIndex("OPTION_E"));
			String OPTION_F=cur.getString(cur.getColumnIndex("OPTION_F"));
			String OPTION_G=cur.getString(cur.getColumnIndex("OPTION_G"));
			String OPTION_H=cur.getString(cur.getColumnIndex("OPTION_H"));
			String OPTION_I=cur.getString(cur.getColumnIndex("OPTION_I"));
			String ANSWER_0=cur.getString(cur.getColumnIndex("ANSWER_0"));
			String ANSWER_1=cur.getString(cur.getColumnIndex("ANSWER_1"));
			String ANSWER_2=cur.getString(cur.getColumnIndex("ANSWER_2"));
			String ANSWER_3=cur.getString(cur.getColumnIndex("ANSWER_3"));
			String ANSWER_4=cur.getString(cur.getColumnIndex("ANSWER_4"));
			String DETAILED=cur.getString(cur.getColumnIndex("DETAILED"));
			String createdAt=cur.getString(cur.getColumnIndex("createdAt"));
			String updatedAt=cur.getString(cur.getColumnIndex("updatedAt"));
			int PRAISE=cur.getInt(cur.getColumnIndex("PRAISE"));
			String PROVENANCE_URL=cur.getString(cur.getColumnIndex("PROVENANCE_URL"));
			String CHARACTERISTIC=cur.getString(cur.getColumnIndex("CHARACTERISTIC"));

			question.setObjectId(objectId);
			question.setTOPIC(TOPIC);
			question.setQUESTION_TYPE(QUESTION_TYPE);
			question.setOPTION_A(OPTION_A);
			question.setOPTION_B(OPTION_B);
			question.setOPTION_C(OPTION_C);
			question.setOPTION_D(OPTION_D);
			question.setOPTION_E(OPTION_E);
			question.setOPTION_F(OPTION_F);
			question.setOPTION_G(OPTION_G);
			question.setOPTION_H(OPTION_H);
			question.setOPTION_I(OPTION_I);
			question.setANSWER_0(ANSWER_0);
			question.setANSWER_1(ANSWER_1);
			question.setANSWER_2(ANSWER_2);
			question.setANSWER_3(ANSWER_3);
			question.setANSWER_4(ANSWER_4);
			question.setDETAILED(DETAILED);
			question.setCreatedAt(createdAt);
			question.setUpdatedAt(updatedAt);
			question.setPRAISE(PRAISE);
			question.setPROVENANCE_URL(PROVENANCE_URL);
			question.setCHARACTERISTIC(CHARACTERISTIC);
			question_array.add(question);
		}
		if(!cur.isClosed()){
			cur.close();
		}
		if(question_array.size()>0){
			return question_array.get(0);
		}
		return null;
	}
	public int getDBCount(){
		String sql="select * from QUESTION_TABLE";
		Cursor cur;
		cur=db.rawQuery(sql,null);
		int count=cur.getCount();
		if(!cur.isClosed()){
			cur.close();
		}
		return count;
	}
	public void Delete_question(String questionId){
		int rid=db.delete("QUESTION_TABLE","objectId=?",new String[]{questionId});
		Utils.print(AppConstants.LogTag,"Delete_question return:"+rid);
	}
}
