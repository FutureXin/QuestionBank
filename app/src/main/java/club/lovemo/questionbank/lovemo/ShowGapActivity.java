package club.lovemo.questionbank.lovemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import club.lovemo.questionbank.R;
import club.lovemo.questionbank.entity.Collection;
import club.lovemo.questionbank.entity.MyUser;
import club.lovemo.questionbank.entity.Praise;
import club.lovemo.questionbank.entity.QuestionBank;
import club.lovemo.questionbank.entity.QuestionBankDB;
import club.lovemo.questionbank.utils.AppConstants;
import club.lovemo.questionbank.utils.DBHelperUtils;
import club.lovemo.questionbank.utils.Utils;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by John
 */

public class ShowGapActivity extends AppCompatActivity {
    private TextView show_gap_0;
    private TextView show_gap_1;
    private TextView show_gap_2;
    private TextView show_gap_3;
    private TextView show_gap_4;
    private TextView show_gap_detailed;
    private TextView gap_detailed;
    private Button show_gap_btn;
    private TextView show_gap_topic;
    private QuestionBankDB questionBankDB;
    private String ID;
    private Toolbar gap_toolbar;
    private MyUser myUser;
    private TextView show_gap_url;
    private TextView show_gap_praise_count;
    private ImageButton show_gap_praise_btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gap);

        gap_toolbar= findViewById(R.id.gap_toolbar);
        show_gap_topic= findViewById(R.id.show_gap_topic);
        show_gap_0= findViewById(R.id.show_gap_a);
        show_gap_1= findViewById(R.id.show_gap_b);
        show_gap_2= findViewById(R.id.show_gap_c);
        show_gap_3= findViewById(R.id.show_gap_d);
        show_gap_4= findViewById(R.id.show_gap_e);
        show_gap_detailed= findViewById(R.id.show_gap_detailed);
        gap_detailed= findViewById(R.id.gap_detailed);
        show_gap_btn= findViewById(R.id.show_gap_btn);
        show_gap_praise_btn= findViewById(R.id.show_gap_praise);
        show_gap_praise_count= findViewById(R.id.show_gap_praise_count);
        show_gap_url= findViewById(R.id.show_gap_url_text);
        myUser= BmobUser.getCurrentUser(MyUser.class);
        ID = getIntent().getStringExtra("id");
        Utils.print(AppConstants.LogTag,ID+"gapID");
        getDBData();
        setPraiseIcon();
        show_gap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (show_gap_0.getVisibility() == View.GONE&show_gap_1.getVisibility() == View.GONE&show_gap_2.getVisibility() == View.GONE&show_gap_3.getVisibility() == View.GONE&show_gap_4.getVisibility() == View.GONE) {
                    show_gap_0.setVisibility(View.VISIBLE);
                    show_gap_1.setVisibility(View.VISIBLE);
                    show_gap_2.setVisibility(View.VISIBLE);
                    show_gap_3.setVisibility(View.VISIBLE);
                    show_gap_4.setVisibility(View.VISIBLE);
                    if(questionBankDB.getDETAILED()!=null&&!questionBankDB.getDETAILED().equals("")) {
                        show_gap_detailed.setVisibility(View.VISIBLE);
                        gap_detailed.setVisibility(View.VISIBLE);
                    }
                    show_gap_btn.setText(R.string.text_hidden_answer);
                } else {
                    show_gap_0.setVisibility(View.GONE);
                    show_gap_1.setVisibility(View.GONE);
                    show_gap_2.setVisibility(View.GONE);
                    show_gap_3.setVisibility(View.GONE);
                    show_gap_4.setVisibility(View.GONE);
                    show_gap_detailed.setVisibility(View.GONE);
                    gap_detailed.setVisibility(View.GONE);
                    show_gap_btn.setText(R.string.text_show_answer);
                }
            }
        });
        gap_toolbar.setNavigationIcon(R.mipmap.back);
        setSupportActionBar(gap_toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //设置NavigationIcon的点击事件,需要放在setSupportActionBar之后设置才会生效,
        //因为setSupportActionBar里面也会setNavigationOnClickListener
        //返回登录界面
        gap_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ShowGapActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });
        //设置toolBar上的MenuItem点击事件
        gap_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_feedback://点击了反馈按钮
                        Intent feedback_intent=new Intent();
                        feedback_intent.setClass(ShowGapActivity.this,FeedbackActivity.class);
                        feedback_intent.putExtra("id",ID);
                        ShowGapActivity.this.startActivity(feedback_intent);
                        ShowGapActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                        break;
                    case R.id.action_collect://点击了收藏按钮
                        BmobQuery<Collection> collectionQuery = new BmobQuery<>();
                        collectionQuery.addWhereEqualTo("UserId",myUser.getObjectId()).addWhereEqualTo("QuestionId",ID)
                                .findObjects(new FindListener<Collection>() {
                                    @Override
                                    public void done(List<Collection> list, BmobException e) {
                                        if(e==null){
                                            if(list!=null&&list.size()>0){
                                                CancelCollection(list.get(0).getObjectId());
                                            }else {
                                                addCollection(myUser.getObjectId(),questionBankDB.getQUESTION_TYPE(),questionBankDB.getTOPIC(),questionBankDB.getCHARACTERISTIC());
                                            }
                                        }else{
                                            if(e.getErrorCode()==101){
                                                addCollection(myUser.getObjectId(),questionBankDB.getQUESTION_TYPE(),questionBankDB.getTOPIC(),questionBankDB.getCHARACTERISTIC());
                                            }else if(e.getErrorCode()==9016){
                                                Utils.showToast(ShowGapActivity.this,getResources().getString(R.string.text_no_network));
                                            }else {
                                                Utils.print(AppConstants.LogTag,"error:"+e.getMessage()+e.getErrorCode());
                                            }
                                        }

                                    }
                                });

                        break;
                }
                return true;
            }
        });
        show_gap_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questionBankDB.getPROVENANCE_URL()!=null||!questionBankDB.getPROVENANCE_URL().equals("")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(questionBankDB.getPROVENANCE_URL()));
                    startActivity(intent);
                }
            }
        });
        show_gap_praise_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<Praise> praiseQuery = new BmobQuery<>();
                praiseQuery.addWhereEqualTo("UserId",myUser.getObjectId()).addWhereEqualTo("QuestionId",ID)
                        .findObjects(new FindListener<Praise>() {
                            @Override
                            public void done(List<Praise> list, BmobException e) {
                                if(e==null){
                                    if(list!=null&&list.size()>0){
                                        Utils.showToast(ShowGapActivity.this,getResources().getString(R.string.text_been_great));
                                        show_gap_praise_btn.setBackgroundResource(R.mipmap.been_praised);
                                    }else {
                                        addPraise(myUser.getObjectId(),ID);
                                    }
                                }else{
                                    if(e.getErrorCode()==101){
                                        addPraise(myUser.getObjectId(),ID);
                                    }else if(e.getErrorCode()==9016){
                                        Utils.showToast(ShowGapActivity.this,getResources().getString(R.string.text_no_network));
                                    }else {
                                        Utils.showToast(ShowGapActivity.this,"error:"+e.getMessage()+e.getErrorCode());
                                    }
                                }
                            }
                        });
            }
        });
    }

    private void setCollectionIcon(){
        BmobQuery<Collection> collectionQuery = new BmobQuery<>();
        collectionQuery.addWhereEqualTo("UserId",myUser.getObjectId()).addWhereEqualTo("QuestionId",ID)
                .findObjects(new FindListener<Collection>() {
                    @Override
                    public void done(List<Collection> list, BmobException e) {
                        if(e==null){
                            if(list!=null&&list.size()>0){
                                gap_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.already_collected);//改变图标样式
                            }
                        }else{
                            gap_toolbar.getMenu().findItem(R.id.action_collect).setVisible(false);
                            if(e.getErrorCode()==9016){
                                Utils.showToast(ShowGapActivity.this,getResources().getString(R.string.text_no_network));
                            }else {
                                Utils.print(AppConstants.LogTag,"error:"+e.getMessage()+e.getErrorCode());
                            }
                        }

                    }
                });
    }
    private void setPraiseIcon(){
        BmobQuery<Praise> praiseQuery = new BmobQuery<>();
        praiseQuery.addWhereEqualTo("UserId",myUser.getObjectId()).addWhereEqualTo("QuestionId",ID)
                .findObjects(new FindListener<Praise>() {
                    @Override
                    public void done(List<Praise> list, BmobException e) {
                        if(e==null){
                            if(list!=null&&list.size()>0){
                                show_gap_praise_btn.setBackgroundResource(R.mipmap.been_praised);
                            }
                        }
                    }
                });
    }
    private void addCollection(String userId,String Type,String Topic,String CHARACTERISTIC){
        final Collection collection=new Collection();
        collection.setUserId(userId);
        collection.setQuestionId(ID);
        collection.setType(Type);
        collection.setTopic(Topic);
        collection.setCHARACTERISTIC(CHARACTERISTIC);
        collection.setDel(false);
        collection.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    Utils.showToast(ShowGapActivity.this , getResources().getString(R.string.text_collection_success));
                    gap_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.already_collected);//改变图标样式
                }else {
                    Utils.showToast(ShowGapActivity.this, getResources().getString(R.string.text_collection_failure));
                    if(e.getErrorCode()==9016){
                        Utils.showToast(ShowGapActivity.this,getResources().getString(R.string.text_no_network));
                    }else {
                        Utils.print(AppConstants.LogTag, "收藏失败" + e.getMessage() + e.getErrorCode());
                    }
                }
            }
        });
    }
    private void CancelCollection(String Id){
        Collection collection = new Collection();
        collection.setObjectId(Id);
        collection.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    Utils.showToast(ShowGapActivity.this,getResources().getString(R.string.text_cancel_collection_success));
                    gap_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.not_collect);//改变图标样式
                }else{
                    Utils.print(AppConstants.LogTag,"失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
    private void addPraise(String userId, final String questionId){
        final Praise praise = new Praise();
        praise.setUserId(userId);
        praise.setQuestionId(questionId);
        praise.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    Utils.showToast(ShowGapActivity.this,getResources().getString(R.string.text_thumb_success));
                    String praiseCount=questionBankDB.getPRAISE()+1+"";
                    show_gap_praise_count.setText(praiseCount);
                    QuestionBank questionBank=new QuestionBank();
                    questionBank.increment("PRAISE");
                    questionBank.update(questionId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            Utils.print(AppConstants.LogTag,"Error："+e.getMessage()+e.getErrorCode());
                        }
                    });
                    try {
                        DBHelperUtils dbHelperUtils = DBHelperUtils.getInstance();
                        QuestionBankDB questionBankDB=dbHelperUtils.getQuestionListById(ID);
                        questionBankDB.setPRAISE(questionBankDB.getPRAISE()+1);
                        dbHelperUtils.updateQuestionDB(questionBankDB);
                        dbHelperUtils.DBClose();
                    } catch (Exception a) {
                        Utils.print(AppConstants.LogTag,"DB报错"+a.toString());
                    }
                    show_gap_praise_btn.setBackgroundResource(R.mipmap.been_praised);
                }else{
                    if(e.getErrorCode()==9016){
                        Utils.showToast(ShowGapActivity.this,getResources().getString(R.string.text_no_network));
                    }else {
                        Utils.print(AppConstants.LogTag, "error:" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            }
        });
    }
    private void getDBData(){
        try {
            DBHelperUtils dbHelperUtils = DBHelperUtils.getInstance();
            questionBankDB = dbHelperUtils.getQuestionListById(ID);
            dbHelperUtils.DBClose();
            Utils.print(AppConstants.LogTag,questionBankDB.toString());
            if(questionBankDB!=null){
                setAnswerText();
                Utils.print(AppConstants.LogTag, questionBankDB.getTOPIC() + "题目");
            }
            getWebData(ID);
        } catch (Exception a) {
            Utils.print(AppConstants.LogTag,"DB报错"+a.toString());
            getWebData(ID);
        }
    }
    private void getWebData(String id){
        BmobQuery<QuestionBank> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(id, new QueryListener<QuestionBank>() {
            @Override
            public void done(QuestionBank object,BmobException e) {
                if(e==null){
                    DBHelperUtils dbHelperUtils = DBHelperUtils.getInstance();
                    dbHelperUtils.updateQuestion(object);
                    dbHelperUtils.DBClose();
                    questionBankDB=new QuestionBankDB();
                    questionBankDB.setObjectId(object.getObjectId());
                    questionBankDB.setTOPIC(object.getTOPIC());
                    questionBankDB.setQUESTION_TYPE(object.getQUESTION_TYPE());
                    questionBankDB.setOPTION_A(object.getOPTION_A());
                    questionBankDB.setOPTION_B(object.getOPTION_B());
                    questionBankDB.setOPTION_C(object.getOPTION_C());
                    questionBankDB.setOPTION_D(object.getOPTION_D());
                    questionBankDB.setOPTION_E(object.getOPTION_E());
                    questionBankDB.setOPTION_F(object.getOPTION_F());
                    questionBankDB.setOPTION_G(object.getOPTION_G());
                    questionBankDB.setOPTION_H(object.getOPTION_H());
                    questionBankDB.setOPTION_I(object.getOPTION_I());
                    questionBankDB.setANSWER_0(object.getANSWER_0());
                    questionBankDB.setANSWER_1(object.getANSWER_1());
                    questionBankDB.setANSWER_2(object.getANSWER_2());
                    questionBankDB.setANSWER_3(object.getANSWER_3());
                    questionBankDB.setANSWER_4(object.getANSWER_4());
                    questionBankDB.setDETAILED(object.getDETAILED());
                    questionBankDB.setPRAISE(object.getPRAISE());
                    questionBankDB.setPROVENANCE_URL(object.getPROVENANCE_URL());
                    questionBankDB.setCreatedAt(object.getCreatedAt());
                    questionBankDB.setUpdatedAt(object.getUpdatedAt());
                    questionBankDB.setCHARACTERISTIC(object.getCHARACTERISTIC());
                    setAnswerText();
                }else{
                    Utils.print(AppConstants.LogTag,"查询失败：" + e.getMessage());
                }
            }
        });

    }
    //如果有Menu,创建完后,系统会自动添加到ToolBar上
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.menu_detailed, menu);
        setCollectionIcon();
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            finish();
            this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void setAnswerText(){
        String Topic="\u3000\u3000"+questionBankDB.getTOPIC();
        show_gap_topic.setText(Topic);
        if (questionBankDB.getANSWER_0() != null && !questionBankDB.getANSWER_0().equals("")) {
            String Answer="答案一：" + questionBankDB.getANSWER_0();
            show_gap_0.setText(Answer);
        }else {
            show_gap_0.setVisibility(View.GONE);
        }
        if (questionBankDB.getANSWER_1() != null && !questionBankDB.getANSWER_1().equals("")) {
            String Answer="答案二：" + questionBankDB.getANSWER_1();
            show_gap_1.setText(Answer);
        }else {
            show_gap_1.setVisibility(View.GONE);
        }
        if(questionBankDB.getANSWER_2()!=null&&!questionBankDB.getANSWER_2().equals("")){
            String Answer="答案三："+questionBankDB.getANSWER_2();
            show_gap_2.setText(Answer);
        }else {
            show_gap_2.setVisibility(View.GONE);
        }
        if(questionBankDB.getANSWER_3()!=null&&!questionBankDB.getANSWER_3().equals("")){
            String Answer="答案四："+questionBankDB.getANSWER_3();
            show_gap_3.setText(Answer);
        }else {
            show_gap_3.setVisibility(View.GONE);
        }
        if(questionBankDB.getANSWER_4()!=null&&!questionBankDB.getANSWER_4().equals("")){
            String Answer="答案五："+questionBankDB.getANSWER_4();
            show_gap_4.setText(Answer);
        }else {
            show_gap_4.setVisibility(View.GONE);
        }
        if(questionBankDB.getDETAILED()!=null&&!questionBankDB.getDETAILED().equals("")){
            String Detailed=questionBankDB.getDETAILED();
            show_gap_detailed.setText(Detailed);
        }else {
            show_gap_detailed.setVisibility(View.GONE);
            gap_detailed.setVisibility(View.GONE);
        }
        if(questionBankDB.getPROVENANCE_URL()!=null&&!questionBankDB.getPROVENANCE_URL().equals("")){
            show_gap_url.setVisibility(View.VISIBLE);
        }else {
            show_gap_url.setVisibility(View.INVISIBLE);
        }
        String praise_count=questionBankDB.getPRAISE()+"";
        show_gap_praise_count.setText(praise_count);

//        answer=questionBankDB.getANSWER_0()+questionBankDB.getANSWER_1()+questionBankDB.getANSWER_2()+questionBankDB.getANSWER_3()+questionBankDB.getANSWER_4();

    }
}
