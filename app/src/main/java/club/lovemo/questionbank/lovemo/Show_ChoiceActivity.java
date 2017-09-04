package club.lovemo.questionbank.lovemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

public class Show_ChoiceActivity extends AppCompatActivity {
    private TextView show_choice_topic;
    private TextView show_choice_answer;
    private TextView show_choice_a;
    private TextView show_choice_b;
    private TextView show_choice_c;
    private TextView show_choice_d;
    private TextView show_choice_e;
    private TextView show_choice_f;
    private TextView show_choice_g;
    private TextView show_choice_h;
    private TextView show_choice_i;
    private TextView show_choice_detailed;
    private TextView choice_detailed;
    private TextView show_choice_url;
    private TextView show_choice_praise_count;
    private ImageButton show_choice_praise_btn;
    private Button show_choice_answer_btn;
    private QuestionBankDB questionBankDB;
    private Toolbar choice_toolbar;
    private String answer;
    private String ID;
    private MyUser myUser;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_choice);

        choice_toolbar=(Toolbar) findViewById(R.id.choice_toolbar);
        show_choice_topic = (TextView) findViewById(R.id.show_choice_topic);
        show_choice_answer = (TextView) findViewById(R.id.show_choice_answer);
        show_choice_a = (TextView) findViewById(R.id.show_choice_a);
        show_choice_b = (TextView) findViewById(R.id.show_choice_b);
        show_choice_c = (TextView) findViewById(R.id.show_choice_c);
        show_choice_d = (TextView) findViewById(R.id.show_choice_d);
        show_choice_e = (TextView) findViewById(R.id.show_choice_e);
        show_choice_f = (TextView) findViewById(R.id.show_choice_f);
        show_choice_g = (TextView) findViewById(R.id.show_choice_g);
        show_choice_h = (TextView) findViewById(R.id.show_choice_h);
        show_choice_i = (TextView) findViewById(R.id.show_choice_i);
        show_choice_detailed = (TextView) findViewById(R.id.show_choice_detailed);
        choice_detailed = (TextView) findViewById(R.id.choice_detailed);
        show_choice_praise_btn=(ImageButton)findViewById(R.id.show_choice_praise);
        show_choice_praise_count=(TextView)findViewById(R.id.show_choice_praise_count);
        show_choice_url=(TextView)findViewById(R.id.show_choice_url_text);
        show_choice_answer_btn = (Button) findViewById(R.id.show_choice_btn);
        myUser= BmobUser.getCurrentUser(MyUser.class);
        ID = getIntent().getStringExtra("id");
        Utils.print(AppConstants.LogTag,ID+"choiceID");
        getDBData();//获取云端数据！
        setPraiseIcon();
        show_choice_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questionBankDB.getPROVENANCE_URL()!=null||!questionBankDB.getPROVENANCE_URL().equals("")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(questionBankDB.getPROVENANCE_URL()));
                    startActivity(intent);
                }
            }
        });
        show_choice_praise_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<Praise> praiseQuery = new BmobQuery<>();
                praiseQuery.addWhereEqualTo("UserId",myUser.getObjectId()).addWhereEqualTo("QuestionId",ID)
                        .findObjects(new FindListener<Praise>() {
                            @Override
                            public void done(List<Praise> list, BmobException e) {
                                if(e==null){
                                    if(list!=null&&list.size()>0){
                                        Utils.showToast(Show_ChoiceActivity.this,getResources().getString(R.string.text_been_great));
                                        show_choice_praise_btn.setBackgroundResource(R.mipmap.been_praised);
                                    }else {
                                        addPraise(myUser.getObjectId(),ID);
                                    }
                                }else{
                                    if(e.getErrorCode()==101){
                                        addPraise(myUser.getObjectId(),ID);
                                    }else if(e.getErrorCode()==9016){
                                        Utils.showToast(Show_ChoiceActivity.this,getResources().getString(R.string.text_no_network));
                                    }else {
                                        Utils.showToast(Show_ChoiceActivity.this,"error:"+e.getMessage()+e.getErrorCode());
                                    }
                                }

                            }
                        });
            }
        });
        show_choice_answer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (show_choice_answer.getVisibility() == View.GONE) {
                    show_choice_answer.setVisibility(View.VISIBLE);
                    show_choice_answer_btn.setText(R.string.text_hidden_answer);
                    show_choice_answer.setText(answer);
                    if(questionBankDB.getDETAILED()!=null&&!questionBankDB.getDETAILED().equals("")) {
                        show_choice_detailed.setVisibility(View.VISIBLE);
                        choice_detailed.setVisibility(View.VISIBLE);
                    }
                } else {
                    show_choice_answer.setVisibility(View.GONE);
                    show_choice_detailed.setVisibility(View.GONE);
                    choice_detailed.setVisibility(View.GONE);
                    show_choice_answer_btn.setText(R.string.text_show_answer);
                    show_choice_answer.setText("");
                }
            }
        });
        choice_toolbar.setNavigationIcon(R.mipmap.back);
        setSupportActionBar(choice_toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //设置NavigationIcon的点击事件,需要放在setSupportActionBar之后设置才会生效,
        //因为setSupportActionBar里面也会setNavigationOnClickListener
        //返回主界面
        choice_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_ChoiceActivity.this.finish();
                Show_ChoiceActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });
        //设置toolBar上的MenuItem点击事件
        choice_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_feedback://点击了反馈按钮
                        Intent feedback_intent=new Intent();
                        feedback_intent.setClass(Show_ChoiceActivity.this,FeedbackActivity.class);
                        feedback_intent.putExtra("id",ID);
                        Show_ChoiceActivity.this.startActivity(feedback_intent);
                        Show_ChoiceActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
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
                                                Utils.showToast(Show_ChoiceActivity.this,getResources().getString(R.string.text_no_network));
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
                                choice_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.already_collected);//改变图标样式
                            }
                        }else{
                            choice_toolbar.getMenu().findItem(R.id.action_collect).setVisible(false);
                           if(e.getErrorCode()==9016){
                                Utils.showToast(Show_ChoiceActivity.this,getResources().getString(R.string.text_no_network));
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
                                show_choice_praise_btn.setBackgroundResource(R.mipmap.been_praised);
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
                    Utils.showToast(Show_ChoiceActivity.this , getResources().getString(R.string.text_collection_success));
                    choice_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.already_collected);//改变图标样式
                }else {
                    Utils.showToast(Show_ChoiceActivity.this, getResources().getString(R.string.text_collection_failure));
                    if(e.getErrorCode()==9016){
                        Utils.showToast(Show_ChoiceActivity.this,getResources().getString(R.string.text_no_network));
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
                   Utils.showToast(Show_ChoiceActivity.this,getResources().getString(R.string.text_cancel_collection_success));
                    choice_toolbar.getMenu().findItem(R.id.action_collect).setIcon(R.mipmap.not_collect);//改变图标样式
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
                    Utils.showToast(Show_ChoiceActivity.this,getResources().getString(R.string.text_thumb_success));
                    String praiseCount=questionBankDB.getPRAISE()+1+"";
                    show_choice_praise_count.setText(praiseCount);
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
                    show_choice_praise_btn.setBackgroundResource(R.mipmap.been_praised);
                }else{
                    if(e.getErrorCode()==9016){
                        Utils.showToast(Show_ChoiceActivity.this,getResources().getString(R.string.text_no_network));
                    }else {
                        Utils.print(AppConstants.LogTag, "error:" + e.getMessage() + "," + e.getErrorCode());
                    }
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

    private void getDBData(){
        try {
            DBHelperUtils dbHelperUtils = DBHelperUtils.getInstance();
            questionBankDB = dbHelperUtils.getQuestionListById(ID);
            dbHelperUtils.DBClose();
            if(questionBankDB!=null){
                Utils.print(AppConstants.LogTag, questionBankDB.getTOPIC() + "题目,updatetime"+questionBankDB.getUpdatedAt());
                setOptionText();
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
                        setOptionText();
                    }else{
                       Utils.print(AppConstants.LogTag,"查询失败：" + e.getMessage());
                    }
                }
            });
        }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            finish();
            Show_ChoiceActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
        private void setOptionText(){
            if (questionBankDB.getOPTION_A() != null && !questionBankDB.getOPTION_A().equals("")) {
                String A="A:" + questionBankDB.getOPTION_A();
                show_choice_a.setText(A);
                show_choice_a.setVisibility(View.VISIBLE);
            }
            if (questionBankDB.getOPTION_B() != null && !questionBankDB.getOPTION_B().equals("")) {
                String B="B:" + questionBankDB.getOPTION_B();
                show_choice_b.setText(B);
                show_choice_b.setVisibility(View.VISIBLE);
            }
            if(questionBankDB.getOPTION_C()!=null&&!questionBankDB.getOPTION_C().equals("")){
                String C="C:"+questionBankDB.getOPTION_C();
                show_choice_c.setText(C);
                show_choice_c.setVisibility(View.VISIBLE);
            }
            if(questionBankDB.getOPTION_D()!=null&&!questionBankDB.getOPTION_D().equals("")){
                String D="D:"+questionBankDB.getOPTION_D();
                show_choice_d.setText(D);
                show_choice_d.setVisibility(View.VISIBLE);
            }
            if(questionBankDB.getOPTION_E()!=null&&!questionBankDB.getOPTION_E().equals("")){
                String E="E:"+questionBankDB.getOPTION_E();
                show_choice_e.setText(E);
                show_choice_e.setVisibility(View.VISIBLE);
            }
            if(questionBankDB.getOPTION_F()!=null&&!questionBankDB.getOPTION_F().equals("")){
                String F="F:"+questionBankDB.getOPTION_F();
                show_choice_f.setText(F);
                show_choice_f.setVisibility(View.VISIBLE);
            }
            if(questionBankDB.getOPTION_G()!=null&&!questionBankDB.getOPTION_G().equals("")){
                String G="G:"+questionBankDB.getOPTION_G();
                show_choice_g.setText(G);
                show_choice_g.setVisibility(View.VISIBLE);
            }
            if(questionBankDB.getOPTION_H()!=null&&!questionBankDB.getOPTION_H().equals("")){
                String H="H:"+questionBankDB.getOPTION_H();
                show_choice_h.setText(H);
                show_choice_h.setVisibility(View.VISIBLE);
            }
            if(questionBankDB.getOPTION_I()!=null&&!questionBankDB.getOPTION_I().equals("")){
                String I="I:"+questionBankDB.getOPTION_I();
                show_choice_i.setText(I);
                show_choice_i.setVisibility(View.VISIBLE);
            }
            if(questionBankDB.getDETAILED()!=null&&!questionBankDB.getDETAILED().equals("")){
                String Detailed=questionBankDB.getDETAILED();
                show_choice_detailed.setText(Detailed);
            }
            if(questionBankDB.getPROVENANCE_URL()!=null&&!questionBankDB.getPROVENANCE_URL().equals("")){
                show_choice_url.setVisibility(View.VISIBLE);
            }else {
                show_choice_url.setVisibility(View.INVISIBLE);
            }
            String praise_count=questionBankDB.getPRAISE()+"";
            show_choice_praise_count.setText(praise_count);
            answer=questionBankDB.getANSWER_0()+questionBankDB.getANSWER_1()+questionBankDB.getANSWER_2()+questionBankDB.getANSWER_3()+questionBankDB.getANSWER_4();
            String Topic="\u3000\u3000"+questionBankDB.getTOPIC();
            show_choice_topic.setText(Topic);
        }

    }

