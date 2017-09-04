package club.lovemo.questionbank.lovemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import club.lovemo.questionbank.R;
import club.lovemo.questionbank.entity.Feedback;
import club.lovemo.questionbank.entity.MyUser;
import club.lovemo.questionbank.utils.AppConstants;
import club.lovemo.questionbank.utils.Utils;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 信息反馈页面
 */

public class FeedbackActivity extends AppCompatActivity{
    private EditText et_feedback;
    private ProgressBar feedback_pb;
    private Button feedback_submit;
    private String ID;
    private String feedback_text;
    private MyUser myUser;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedback_submit=(Button)findViewById(R.id.feedback_submit_btn);
        et_feedback=(EditText) findViewById(R.id.et_feedback);
        feedback_pb=(ProgressBar)findViewById(R.id.feedback_pb_progress);
        feedback_pb.bringToFront();
        myUser= BmobUser.getCurrentUser(MyUser.class);
        Toolbar feedback_toolbar=(Toolbar)findViewById(R.id.feedback_toolbar);
        feedback_toolbar.setNavigationIcon(R.mipmap.back);
        feedback_toolbar.setTitle(R.string.text_feedback);
        setSupportActionBar(feedback_toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //设置NavigationIcon的点击事件,需要放在setSupportActionBar之后设置才会生效,
        //因为setSupportActionBar里面也会setNavigationOnClickListener
        //返回登录界面
        feedback_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackActivity.this.finish();
                FeedbackActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });
        ID=getIntent().getStringExtra("id");
        Utils.print(AppConstants.LogTag,"获取到的ID："+ID);
        feedback_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Utils.print(AppConstants.LogTag,"点击了提交按钮！！！");
                feedback_text=et_feedback.getText().toString().trim();
                if (!feedback_text.equals("")){
                    submit_feedback();
                }else{
                    Utils.showToast(FeedbackActivity.this,"请输入您要反馈的内容在进行提交，谢谢！");
                }

            }
        });

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
    private void submit_feedback(){
        feedback_pb.setVisibility(View.VISIBLE);
        feedback_submit.setEnabled(false);
        et_feedback.setEnabled(false);
        Feedback feedback = new Feedback();
        feedback.setQbobjectid(ID);
        feedback.setUserobjectid(myUser.getObjectId());
        feedback.setFeedbacktext(feedback_text);
        feedback.setIsdispose(false);
        feedback.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    feedback_pb.setVisibility(View.INVISIBLE);
                    et_feedback.setEnabled(true);
                    Utils.print(AppConstants.LogTag,"添加数据成功，返回objectId为："+objectId);
                    Utils.showToast(FeedbackActivity.this,"反馈信息提交成功！");
                    finish();
                    FeedbackActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                }else{
                    feedback_pb.setVisibility(View.INVISIBLE);
                    feedback_submit.setEnabled(true);
                    et_feedback.setEnabled(true);
                    Utils.print(AppConstants.LogTag,"创建数据失败：" + e.getMessage());
                    Utils.showToast(FeedbackActivity.this,"反馈信息提交失败！");

                }
            }
        });

    }
}
