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
 * Created by John.
 */

public class ShowShortActivity extends AppCompatActivity {
    private TextView show_short_topic;
    private TextView show_short_answer;
    private TextView show_short_detailed;
    private TextView short_detailed;
    private Button show_short_answer_btn;
    private String ID;
    private QuestionBankDB questionBankDB;
    private MyUser myUser;
    private Toolbar short_toolbar;
    private TextView show_short_url;
    private TextView show_short_praise_count;
    private ImageButton show_short_praise_btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_short);

        show_short_topic = findViewById(R.id.show_short_topic);
        show_short_answer = findViewById(R.id.show_short_answer);
        show_short_detailed = findViewById(R.id.show_short_detailed);
        short_detailed = findViewById(R.id.short_detailed);
        show_short_answer_btn = findViewById(R.id.show_short_btn);
        show_short_praise_btn= findViewById(R.id.show_short_praise);
        show_short_praise_count= findViewById(R.id.show_short_praise_count);
        show_short_url= findViewById(R.id.show_short_url_text);
        myUser= BmobUser.getCurrentUser(MyUser.class);
        ID = getIntent().getStringExtra("id");
        Utils.print(AppConstants.LogTag, ID + "题目");
        getDBData();
        setPraiseIcon();
        show_short_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questionBankDB.getPROVENANCE_URL()!=null||!questionBankDB.getPROVENANCE_URL().equals("")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(questionBankDB.getPROVENANCE_URL()));
                    startActivity(intent);
                }
            }
        });
        show_short_praise_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<Praise> praiseQuery = new BmobQuery<>();
                praiseQuery.addWhereEqualTo("UserId",myUser.getObjectId()).addWhereEqualTo("QuestionId",ID)
                        .findObjects(new FindListener<Praise>() {
                            @Override
                            public void done(List<Praise> list, BmobException e) {
                                if(e==null){
                                    if(list!=null&&list.size()>0){
                                        Utils.showToast(ShowShortActivity.this,getResources().getString(R.string.text_been_great));
                                        show_short_praise_btn.setBackgroundResource(R.mipmap.been_praised);
                                    }else {
                                        addPraise(myUser.getObjectId(),ID);
                                    }
                                }else{
                                    if(e.getErrorCode()==101){
                                        addPraise(myUser.getObjectId(),ID);
                                    }else if(e.getErrorCode()==9016){
                                        Utils.showToast(ShowShortActivity.this,getResources().getString(R.string.text_no_network));
                                    }else {
                                        Utils.showToast(ShowShortActivity.this,"error:"+e.getMessage()+e.getErrorCode());
                                    }
                                }
                            }
                        });
            }
        });
        show_short_answer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (show_short_answer.getVisibility() == View.GONE) {
                    show_short_answer.setVisibility(View.VISIBLE);
                    if(questionBankDB.getDETAILED()!=null&&!questionBankDB.getDETAILED().equals("")){
                        show_short_detailed.setVisibility(View.VISIBLE);
                        short_detailed.setVisibility(View.VISIBLE);
                    }
                    show_short_answer_btn.setText(R.string.text_hidden_answer);
                } else {
                    show_short_answer.setVisibility(View.GONE);
                    show_short_detailed.setVisibility(View.GONE);
                    short_detailed.setVisibility(View.GONE);
                    show_short_answer_btn.setText(R.string.text_show_answer);
                }

            }
        });
        short_toolbar = findViewById(R.id.short_toolbar);
        short_toolbar.setNavigationIcon(R.mipmap.back);
        setSupportActionBar(short_toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //设置NavigationIcon的点击事件,需要放在setSupportActionBar之后设置才会生效,
        //因为setSupportActionBar里面也会setNavigationOnClickListener
        //返回登录界面
        short_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ShowShortActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        //设置toolBar上的MenuItem点击事件
        short_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_feedback://点击了反馈按钮
                        Intent feedback_intent=new Intent();
                        feedback_intent.setClass(ShowShortActivity.this,FeedbackActivity.class);
                        feedback_intent.putExtra("id",ID);
                        ShowShortActivity.this.startActivity(feedback_intent);
                        ShowShortActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
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
                                                Utils.showToast(ShowShortActivity.this,getResources().getString(R.string.text_no_network));
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
    }
    private void setCollectionIcon(){
        BmobQuery<Collection> collectionQuery = new BmobQuery<>();
        collectionQuery.addWhereEqualTo("UserId",myUser.getObjectId()).addWhereEqualTo("QuestionId",ID)
                .findObjects(new FindListener<Collection>() {
                    @Override
                    public void done(List<Collection> list, BmobException e) {
                        if(e==null){
                            if(list!=null&&list.size()>0){
                                short_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.already_collected);//改变图标样式
                            }
                        }else{
                            short_toolbar.getMenu().findItem(R.id.action_collect).setVisible(false);
                            if(e.getErrorCode()==9016){
                                Utils.showToast(ShowShortActivity.this,getResources().getString(R.string.text_no_network));
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
                                show_short_praise_btn.setBackgroundResource(R.mipmap.been_praised);
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
                    Utils.showToast(ShowShortActivity.this , getResources().getString(R.string.text_collection_success));
                    short_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.already_collected);//改变图标样式
                }else {
                    Utils.showToast(ShowShortActivity.this, getResources().getString(R.string.text_collection_failure));
                    if(e.getErrorCode()==9016){
                        Utils.showToast(ShowShortActivity.this,getResources().getString(R.string.text_no_network));
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
                    Utils.showToast(ShowShortActivity.this,getResources().getString(R.string.text_cancel_collection_success));
                    short_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.not_collect);//改变图标样式
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
                    Utils.showToast(ShowShortActivity.this,getResources().getString(R.string.text_thumb_success));
                    String praiseCount=questionBankDB.getPRAISE()+1+"";
                    show_short_praise_count.setText(praiseCount);
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
                    show_short_praise_btn.setBackgroundResource(R.mipmap.been_praised);
                }else{
                    if(e.getErrorCode()==9016){
                        Utils.showToast(ShowShortActivity.this,getResources().getString(R.string.text_no_network));
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

            if (questionBankDB != null) {
                String Topic="\u3000\u3000" + questionBankDB.getTOPIC();
                show_short_topic.setText(Topic);
                if(questionBankDB.getANSWER_0()!=null&&!questionBankDB.getANSWER_0().equals("")) {
                    String Answer=questionBankDB.getANSWER_0();
                    show_short_answer.setText(Answer);
                }else {
                    show_short_answer.setText(R.string.text_no_answer);
                }
                String Detailed=questionBankDB.getDETAILED();
                show_short_detailed.setText(Detailed);
                if(questionBankDB.getPROVENANCE_URL()!=null&&!questionBankDB.getPROVENANCE_URL().equals("")){
                    show_short_url.setVisibility(View.VISIBLE);
                }else {
                    show_short_url.setVisibility(View.INVISIBLE);
                }
                String praise_count=questionBankDB.getPRAISE()+"";
                show_short_praise_count.setText(praise_count);
            }
            getWebData(ID);
        } catch (Exception a) {
            Utils.print(AppConstants.LogTag, a.toString());
            getWebData(ID);
        }
    }
    private void getWebData(String id) {
        BmobQuery<QuestionBank> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(id, new QueryListener<QuestionBank>() {
            @Override
            public void done(QuestionBank object, BmobException e) {
                if (e == null) {
                    DBHelperUtils dbHelperUtils = DBHelperUtils.getInstance();
                    dbHelperUtils.updateQuestion(object);
                    dbHelperUtils.DBClose();
                    String Topic="\u3000\u3000" + object.getTOPIC();
                    show_short_topic.setText(Topic);
                    if(questionBankDB.getANSWER_0()!=null&&!questionBankDB.getANSWER_0().equals("")) {
                        String Answer=object.getANSWER_0();
                        show_short_answer.setText(Answer);
                    }else {
                        show_short_answer.setText(R.string.text_no_answer);
                    }
                    String Detailed=object.getDETAILED();
                    show_short_detailed.setText(Detailed);
                    if(questionBankDB.getPROVENANCE_URL()!=null&&!questionBankDB.getPROVENANCE_URL().equals("")){
                        show_short_url.setVisibility(View.VISIBLE);
                    }else {
                        show_short_url.setVisibility(View.INVISIBLE);
                    }
                    String praise_count=questionBankDB.getPRAISE()+"";
                    show_short_praise_count.setText(praise_count);
                } else {
                    Utils.print(AppConstants.LogTag, "查询失败：" + e.getMessage());
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
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            finish();
            this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
