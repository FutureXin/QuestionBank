package club.lovemo.questionbank.lovemo;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import club.lovemo.questionbank.R;
import club.lovemo.questionbank.entity.MyUser;
import club.lovemo.questionbank.utils.AppConstants;
import club.lovemo.questionbank.utils.Utils;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by John.
 */

public class RegisteredActivity extends AppCompatActivity{
    private static final int REG_SUCCESS=1;
    private static final int NO_NETWORK=2;
    private static final int USERNAME_DISABLED=3;
    private static final int EMAIL_DISABLED=4;
    private static final int EMAIL_ERROR = 5;
    private EditText username;
    private EditText password;
    private EditText password2;
    private EditText user_email;
    private int statusBarHeight;
    private ProgressDialog dialog;
    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg){
            dialog.cancel();
            switch (msg.what) {
                case REG_SUCCESS:
                    Utils.showToast(RegisteredActivity.this,"注册成功");
                    finish();
                    break;
                case NO_NETWORK:
                    Utils.showToast(RegisteredActivity.this,"无网络，请联网后进行操作！");
                    break;
                case USERNAME_DISABLED:
                    Utils.showToast(RegisteredActivity.this,"该用户名已被注册，请更换其它用户名！");
                    break;
                case EMAIL_DISABLED:
                    Utils.showToast(RegisteredActivity.this,"该邮箱已用于注册，请更换其它邮箱！");
                    break;
                case EMAIL_ERROR:
                    Utils.showToast(RegisteredActivity.this,"电子邮件必须是有效的！");
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        Button login_btn=(Button)findViewById(R.id.reg_login);
        Button reg_btn=(Button)findViewById(R.id.reg_reg);
        username=(EditText) findViewById(R.id.et_reg_username);
        password=(EditText) findViewById(R.id.et_reg_user_password);
        password2=(EditText) findViewById(R.id.et_reg_user_password2);
        //设置hint字体
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
        //设置hint字体
        password2.setTypeface(Typeface.DEFAULT);
        password2.setTransformationMethod(new PasswordTransformationMethod());
        user_email=(EditText) findViewById(R.id.et_reg_user_email);
        //返回登录界面
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断键盘是否关闭，没关闭则关闭
                Utils.hideInputWindow(RegisteredActivity.this);
                RegisteredActivity.this.finish();
                RegisteredActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断键盘是否关闭，没关闭则关闭
                Utils.hideInputWindow(RegisteredActivity.this);
                if(!username.getText().toString().trim().equals("")){
                    if(username.getText().toString().trim().length()>3&username.getText().toString().trim().length()<=30) {
                        if (!password.getText().toString().trim().equals("")) {
                            if(password.getText().toString().trim().length()>=6) {
                                if (!password2.getText().toString().trim().equals("")) {
                                    if (password.getText().toString().trim().equals(password2.getText().toString().trim())) {
                                        if (!user_email.getText().toString().trim().equals("")) {
                                            dialog = new ProgressDialog(RegisteredActivity.this, ProgressDialog.STYLE_SPINNER);
                                            dialog.setMessage(getResources().getString(R.string.text_is_registered));
                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog.setCanceledOnTouchOutside(false);
                                            dialog.setCancelable(false);
                                            dialog.show();
                                            Utils.print(AppConstants.LogTag,Utils.MD5Encoder(password.getText().toString().trim()));
                                            MyThread(username.getText().toString().trim(), Utils.MD5Encoder(password.getText().toString().trim()), user_email.getText().toString().trim().toLowerCase());
                                        } else {
                                            Utils.showToast(RegisteredActivity.this, "请输入邮箱地址进行注册！！！");
                                        }
                                    } else {
                                        Utils.showToast(RegisteredActivity.this, "请输入确认两次输入的密码一致后进行注册！！！");
                                    }
                                } else {
                                    Utils.showToast(RegisteredActivity.this, "请输入确认密码后进行注册！！！");
                                }
                            }else {
                                Utils.showToast(RegisteredActivity.this, "密码不能少于6位！！！");
                            }
                        } else {
                            Utils.showToast(RegisteredActivity.this, "请输入密码后进行注册！！！");
                        }
                    }else {
                        Utils.showToast(RegisteredActivity.this,"账号长度应在3至30位！！！");
                    }
                }else {
                    Utils.showToast(RegisteredActivity.this,"请输入账号后进行注册！！！");
                }
            }
        });
        Toolbar reg_toolbar=(Toolbar)findViewById(R.id.reg_toolbar);
        reg_toolbar.setNavigationIcon(R.mipmap.back);
        setSupportActionBar(reg_toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //设置NavigationIcon的点击事件,需要放在setSupportActionBar之后设置才会生效,
        //因为setSupportActionBar里面也会setNavigationOnClickListener
        //返回登录界面
        reg_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断键盘是否关闭，没关闭则关闭
                Utils.hideInputWindow(RegisteredActivity.this);
                RegisteredActivity.this.finish();
                RegisteredActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });


    }
    //注册用户，并在成功后发送邮件提示进行邮箱激活。
    private void MyThread(final String username,final String password, final String user_email){
        new Thread(){
            public void run(){
                final Message msg = Message.obtain();
                try {
                    Thread.sleep(1000);
                    BmobUser bu = new BmobUser();
                    bu.setUsername(username);
                    bu.setPassword(password);
                    bu.setEmail(user_email);
                    //注意：不能用save方法进行注册
                    bu.signUp(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser s, BmobException e) {
                            if(e==null){
                                //注册成功
                                msg.what=REG_SUCCESS;
                                mHandler.sendMessage(msg);
                                Utils.print(AppConstants.LogTag,"注册成功:" +s.toString());
                            }else{
                                //注册失败
                                Utils.print(AppConstants.LogTag,"注册失败，报错信息："+e.getMessage());
                                if(e.getMessage().contains("already taken")&e.getMessage().contains("username")){
                                    msg.what=USERNAME_DISABLED;
                                    mHandler.sendMessage(msg);
                                }else if(e.getMessage().contains("already taken")&e.getMessage().contains("email")) {
                                    msg.what = EMAIL_DISABLED;
                                    mHandler.sendMessage(msg);
                                }else if(e.getMessage().contains("email Must be a valid email address")){
                                    msg.what = EMAIL_ERROR;
                                    mHandler.sendMessage(msg);
                                }else {
                                    msg.what=NO_NETWORK;
                                    mHandler.sendMessage(msg);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    //线程报错
                    Utils.print(AppConstants.LogTag,"RegThread报错！！！");
                }
            }
        }.start();
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
