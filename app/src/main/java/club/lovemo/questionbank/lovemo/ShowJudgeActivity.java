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

public class ShowJudgeActivity extends AppCompatActivity {
    private TextView show_judge_topic;
    private TextView show_judge_answer;
    private TextView show_judge_detailed;
    private TextView judge_detailed;
    private Button show_judge_answer_btn;
    private String ID;
    private QuestionBankDB questionBankDB;
    private Toolbar judge_toolbar;
    private MyUser myUser;
    private TextView show_judge_url;
    private TextView show_judge_praise_count;
    private ImageButton show_judge_praise_btn;
    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_judge);

        judge_toolbar= findViewById(R.id.judge_toolbar);
        show_judge_topic= findViewById(R.id.show_judge_topic);
        show_judge_answer= findViewById(R.id.show_judge_answer);
        show_judge_detailed= findViewById(R.id.show_judge_detailed);
        judge_detailed= findViewById(R.id.judge_detailed);
        show_judge_answer_btn= findViewById(R.id.show_judge_btn);
        show_judge_praise_btn= findViewById(R.id.show_judge_praise);
        show_judge_praise_count= findViewById(R.id.show_judge_praise_count);
        show_judge_url= findViewById(R.id.show_judge_url_text);
        myUser= BmobUser.getCurrentUser(MyUser.class);
        ID=getIntent().getStringExtra("id");
        Utils.print(AppConstants.LogTag,ID+"题目");
        getDBData();
        setPraiseIcon();
        show_judge_answer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(show_judge_answer.getVisibility()==View.GONE){
                    show_judge_answer.setVisibility(View.VISIBLE);
                    if(questionBankDB.getDETAILED()!=null&&!questionBankDB.getDETAILED().equals("")){
                        show_judge_detailed.setVisibility(View.VISIBLE);
                        judge_detailed.setVisibility(View.VISIBLE);
                    }
                    show_judge_answer_btn.setText(R.string.text_hidden_answer);
                }else{
                    show_judge_answer.setVisibility(View.GONE);
                    show_judge_detailed.setVisibility(View.GONE);
                    judge_detailed.setVisibility(View.GONE);
                    show_judge_answer_btn.setText(R.string.text_show_answer);
                }

            }
        });
        judge_toolbar.setNavigationIcon(R.mipmap.back);
        setSupportActionBar(judge_toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //设置NavigationIcon的点击事件,需要放在setSupportActionBar之后设置才会生效,
        //因为setSupportActionBar里面也会setNavigationOnClickListener
        //返回登录界面
        judge_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ShowJudgeActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });
        //设置toolBar上的MenuItem点击事件
        judge_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_feedback://点击了反馈按钮
                        Intent feedback_intent=new Intent();
                        feedback_intent.setClass(ShowJudgeActivity.this,FeedbackActivity.class);
                        feedback_intent.putExtra("id",ID);
                        ShowJudgeActivity.this.startActivity(feedback_intent);
                        ShowJudgeActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
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
                                                Utils.showToast(ShowJudgeActivity.this,getResources().getString(R.string.text_no_network));
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
        show_judge_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questionBankDB.getPROVENANCE_URL()!=null||!questionBankDB.getPROVENANCE_URL().equals("")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(questionBankDB.getPROVENANCE_URL()));
                    startActivity(intent);
                }
            }
        });
        //点击了点赞按钮
        show_judge_praise_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<Praise> praiseQuery = new BmobQuery<>();
                praiseQuery.addWhereEqualTo("UserId",myUser.getObjectId()).addWhereEqualTo("QuestionId",ID)
                        .findObjects(new FindListener<Praise>() {
                            @Override
                            public void done(List<Praise> list, BmobException e) {
                                if(e==null){
                                    if(list!=null&&list.size()>0){
                                        Utils.showToast(ShowJudgeActivity.this,getResources().getString(R.string.text_been_great));
                                        show_judge_praise_btn.setBackgroundResource(R.mipmap.been_praised);
                                    }else {
                                        addPraise(myUser.getObjectId(),ID);
                                    }
                                }else{
                                    if(e.getErrorCode()==101){
                                        addPraise(myUser.getObjectId(),ID);
                                    }else if(e.getErrorCode()==9016){
                                        Utils.showToast(ShowJudgeActivity.this,getResources().getString(R.string.text_no_network));
                                    }else {
                                        Utils.showToast(ShowJudgeActivity.this,"error:"+e.getMessage()+e.getErrorCode());
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
                                judge_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.already_collected);//改变图标样式
                            }
                        }else{
                            MenuItem menuItem=judge_toolbar.getMenu().findItem(R.id.action_collect);
                            menuItem.setVisible(false);
                            if(e.getErrorCode()==9016){
                                Utils.showToast(ShowJudgeActivity.this,getResources().getString(R.string.text_no_network));
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
                                show_judge_praise_btn.setBackgroundResource(R.mipmap.been_praised);
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
                    Utils.showToast(ShowJudgeActivity.this , getResources().getString(R.string.text_collection_success));
                    judge_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.already_collected);//改变图标样式
                }else {
                    Utils.showToast(ShowJudgeActivity.this, getResources().getString(R.string.text_collection_failure));
                    if(e.getErrorCode()==9016){
                        Utils.showToast(ShowJudgeActivity.this,getResources().getString(R.string.text_no_network));
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
                    Utils.showToast(ShowJudgeActivity.this,getResources().getString(R.string.text_cancel_collection_success));
                    judge_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.not_collect);//改变图标样式
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
                    Utils.showToast(ShowJudgeActivity.this,getResources().getString(R.string.text_thumb_success));
                    String praiseCount=questionBankDB.getPRAISE()+1+"";
                    show_judge_praise_count.setText(praiseCount);
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
                    show_judge_praise_btn.setBackgroundResource(R.mipmap.been_praised);
                }else{
                    if(e.getErrorCode()==9016){
                        Utils.showToast(ShowJudgeActivity.this,getResources().getString(R.string.text_no_network));
                    }else {
                        Utils.print(AppConstants.LogTag, "error:" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            }
        });
    }
    private void getDBData(){
        try {
            DBHelperUtils dbHelperUtils=DBHelperUtils.getInstance();
            questionBankDB=dbHelperUtils.getQuestionListById(ID);
            dbHelperUtils.DBClose();
            if(questionBankDB!=null){
                String Topic="\u3000\u3000"+questionBankDB.getTOPIC();
                show_judge_topic.setText(Topic);
                if(questionBankDB.getANSWER_0()!=null&&!questionBankDB.getANSWER_0().equals("")) {
                    String Answer="\u3000\u3000" + questionBankDB.getANSWER_0();
                    show_judge_answer.setText(Answer);
                }else {
                    show_judge_answer.setText(R.string.text_no_answer);
                }
                String Detailed=questionBankDB.getDETAILED();
                show_judge_detailed.setText(Detailed);
                setPraiseCountAndShowUrlBtn();
            }
            getWebData(ID);
        }catch (Exception a){
            Utils.print(AppConstants.LogTag,a.toString());
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
                    String Topic="\u3000\u3000"+questionBankDB.getTOPIC();
                    show_judge_topic.setText(Topic);
                    if(questionBankDB.getANSWER_0()!=null&&!questionBankDB.getANSWER_0().equals("")){
                        String Answer="\u3000\u3000"+object.getANSWER_0();
                        show_judge_answer.setText(Answer);
                    }else {
                        show_judge_answer.setText(R.string.text_no_answer);
                    }
                    String Detailed=questionBankDB.getDETAILED();
                    show_judge_detailed.setText(Detailed);
                    setPraiseCountAndShowUrlBtn();
                }else{
                    Utils.print(AppConstants.LogTag,"查询失败：" + e.getMessage());
                }
            }
        });
    }
    private void setPraiseCountAndShowUrlBtn(){
        if(questionBankDB.getPROVENANCE_URL()!=null&&!questionBankDB.getPROVENANCE_URL().equals("")){
            show_judge_url.setVisibility(View.VISIBLE);
        }else {
            show_judge_url.setVisibility(View.INVISIBLE);
        }
        String praise_count=questionBankDB.getPRAISE()+"";
        show_judge_praise_count.setText(praise_count);
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
}