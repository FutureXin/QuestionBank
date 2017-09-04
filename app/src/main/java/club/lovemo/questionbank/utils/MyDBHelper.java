package club.lovemo.questionbank.utils;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper{

	public MyDBHelper() {
		super(BmobApplication.getContext(), "QuestionBank.db", null, AppConstants.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String QUESTION_TABLE="create table QUESTION_TABLE(objectId not null," +
				"TOPIC not null," +
				"QUESTION_TYPE not null," +
				"OPTION_A," +
				"OPTION_B," +
				"OPTION_C," +
				"OPTION_D," +
				"OPTION_E," +
				"OPTION_F," +
				"OPTION_G," +
				"OPTION_H," +
				"OPTION_I," +
				"OPTION_J," +
				"ANSWER_0," +
				"ANSWER_1," +
				"ANSWER_2," +
				"ANSWER_3," +
				"ANSWER_4," +
				"DETAILED," +
				"createdAt," +
				"updatedAt," +
				"PRAISE," +
				"PROVENANCE_URL," +
				"CHARACTERISTIC not null" +
				");";
		db.execSQL(QUESTION_TABLE);
		Utils.print(AppConstants.LogTag,"QUESTION_TABLE创建完成");

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
