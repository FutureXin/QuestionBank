package club.lovemo.questionbank.lovemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import club.lovemo.questionbank.R;
import club.lovemo.questionbank.entity.MyUser;
import club.lovemo.questionbank.utils.AppConstants;
import club.lovemo.questionbank.utils.SharedPreferencesUtils;
import club.lovemo.questionbank.utils.Utils;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by John.
 */

public class LoginActivity extends AppCompatActivity{
    private SharedPreferencesUtils preferencesUtils;
    private static final int LOGIN_ERROR=0;//登录失败
    private static final int LOGIN=1;//登录成功
    private static final int THREAD_ERROR=2;
    private static final int NO_NETWORK=3;
    private static final int QUERY_USER_ERROR=4;
    private static final int RESET_SUCCESS=5;
    private static final int RESET_ERROR=6;
    private static final int RESET_THREAD_ERROR=7;
    private static final int USER_OR_PASS_ERROR =8 ;
    private EditText username;
    private EditText password;
    private ProgressDialog login_dialog;
    private ProgressDialog send_email_dialog;
    private AlertDialog reset_password_dialog;
    private String email=null;
    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case LOGIN_ERROR:
                    login_dialog.cancel();
                    Utils.showToast(LoginActivity.this,"登录失败！！！");
                    break;
                case LOGIN:
                    login_dialog.cancel();
                    final MyUser user = BmobUser.getCurrentUser(MyUser.class);
                    if(user.getEmailVerified()){
                        startActivity(new Intent(LoginActivity.this, QuestionListActivity.class));
                        LoginActivity.this.finish();
                    }else {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle(R.string.text_account_not_activated)
                                .setMessage("账号："+user.getUsername()+"邮箱未激活，请到" +user.getEmail()  + "邮箱进行激活操作。\n"+"未收到邮件或邮件已过期？\n点击发送按钮重新发送激活邮件！")
                                .setNegativeButton(R.string.text_send, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        BmobUser.requestEmailVerify(user.getEmail(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e==null){
                                                    Utils.showToast(LoginActivity.this,"请求验证邮件成功，请到" + user.getEmail() + "邮箱中进行激活。");
                                                    Utils.print(AppConstants.LogTag,"请求验证邮件成功，请到" + user.getEmail() + "邮箱中进行激活。");
                                                }else{
                                                    if(e.getMessage().contains(AppConstants.NO_NETWORK_ERROR_INFO)){
                                                        Utils.showToast(LoginActivity.this,getResources().getString(R.string.text_no_network));
                                                    }else {
                                                        Utils.showToast(LoginActivity.this,getResources().getString(R.string.text_splash_send_email_error));
                                                        Utils.print(AppConstants.LogTag,"请求验证邮箱失败，报错信息："+e.getMessage());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                })
                                .setNeutralButton(R.string.text_close, null)
                                .setCancelable(false)
                                .show();
                    }
                    break;
                case THREAD_ERROR:
                    login_dialog.cancel();
                    break;
                case NO_NETWORK:
                    if(send_email_dialog!=null){
                        send_email_dialog.cancel();
                    }
                    if(login_dialog!=null){
                        login_dialog.cancel();
                    }
                    Utils.showToast(LoginActivity.this,getResources().getString(R.string.text_no_network));
                    break;
                case QUERY_USER_ERROR:
                    send_email_dialog.cancel();
                    Utils.showToast(LoginActivity.this,"查询用户绑定邮箱失败。");
                    break;
                case RESET_SUCCESS:
                    send_email_dialog.cancel();
                    Show_Email_Dialog(username.getText().toString().trim(),email);
                    break;
                case RESET_ERROR:
                    send_email_dialog.cancel();
                    Utils.showToast(LoginActivity.this,"发送重置密码邮件失败！！！");
                    break;
                case RESET_THREAD_ERROR:
                    send_email_dialog.cancel();
                    Utils.showToast(LoginActivity.this,"发送重置密码请求线程报错！！！");
                    break;
                case USER_OR_PASS_ERROR:
                    login_dialog.cancel();
                    Utils.showToast(LoginActivity.this,"用户名或密码错误！");
                    break;
                default:
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login_btn=(Button)findViewById(R.id.login_login);
        Button reg_btn=(Button)findViewById(R.id.login_reg);
        TextView back_pass=(TextView)findViewById(R.id.login_password_back);
        username=(EditText) findViewById(R.id.et_login_username);
        password=(EditText) findViewById(R.id.et_login_user_password);
        //设置hint字体
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
        preferencesUtils=new SharedPreferencesUtils(LoginActivity.this);
        username.setText(preferencesUtils.getUserName());
        if(preferencesUtils.getUserName()!=null) {
            username.setSelection(username.getText().length());
        }
        Toolbar login_toolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(login_toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //final View my_view= LayoutInflater.from(this).inflate(R.layout.item_login, null);
        //点击登录按钮后进行判断无误后发起登录
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断软键盘是否关闭，为关闭则关闭
                Utils.hideInputWindow(LoginActivity.this);
                if(!username.getText().toString().trim().equals("")){
                    if(!password.getText().toString().trim().equals("")){
                        login_dialog = new ProgressDialog(LoginActivity.this, ProgressDialog.STYLE_SPINNER);
                        login_dialog.setMessage(getResources().getString(R.string.text_logging));
                        login_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        login_dialog.setCanceledOnTouchOutside(false);
                        login_dialog.setCancelable(false);
                        login_dialog.show();
                        Utils.print(AppConstants.LogTag,Utils.MD5Encoder(password.getText().toString().trim()));
                        MyLoginThread(username.getText().toString().trim(),Utils.MD5Encoder(password.getText().toString().trim()));
                    }else {
                        Utils.showToast(LoginActivity.this,"请输入密码后进行登录！！！");
                    }
                }else {
                    Utils.showToast(LoginActivity.this,"请输入账号后进行登录！！！");
                }
//                startActivity(new Intent(LoginActivity.this, DialogActivity.class));
            }
        });
        //跳转至注册页面
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断键盘是否关闭，没关闭则关闭
                Utils.hideInputWindow(LoginActivity.this);
                Intent reg_intent=new Intent();
                reg_intent.setClass(LoginActivity.this, RegisteredActivity.class);
                LoginActivity.this.startActivity(reg_intent);
                LoginActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });
        //重置密码
        back_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!username.getText().toString().trim().equals("")){
                    Dialog();
                    reset_password_dialog.show();
                }else{
                    Utils.showToast(LoginActivity.this,"请输入要重置密码的账号！！！");
                }
            }
        });
    }
    private void MyLoginThread(final String username,final String password){
        new Thread(){
            public void run(){
                final Message msg = Message.obtain();
                try {
                    BmobUser.loginByAccount(username, password, new LogInListener<MyUser>() {

                        @Override
                        public void done(MyUser myUser, BmobException e) {
                            if(e==null){
                                msg.what=LOGIN;
                                preferencesUtils.setUserName(myUser.getUsername());
                                Utils.print(AppConstants.LogTag,"登录成功");
                                //toast("登录成功:");
                                //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                                //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                                mHandler.sendMessage(msg);
                            }else{
                                if(!e.getMessage().contains(AppConstants.NO_NETWORK_ERROR_INFO)){
                                    BmobUser.loginByAccount(username.toLowerCase(), password, new LogInListener<MyUser>() {
                                        @Override
                                        public void done(MyUser user, BmobException e) {
                                            if(user!=null){
                                                msg.what=LOGIN;
                                                preferencesUtils.setUserName(user.getUsername());
                                                mHandler.sendMessage(msg);
                                            }else {
                                                Utils.print(AppConstants.LogTag,"通过邮箱和密码登录失败，报错信息："+e.getMessage());
                                                if(e.getErrorCode()==9016) {
                                                    msg.what = NO_NETWORK;
                                                    mHandler.sendMessage(msg);
                                                }else if(e.getErrorCode()==101){
                                                    msg.what = USER_OR_PASS_ERROR;
                                                    mHandler.sendMessage(msg);
                                                }else {
                                                    msg.what = LOGIN_ERROR;
                                                    mHandler.sendMessage(msg);
                                                }
                                            }
                                        }
                                    });
                                }else if(e.getMessage().contains(AppConstants.NO_NETWORK_ERROR_INFO)){
                                    msg.what = NO_NETWORK;
                                    mHandler.sendMessage(msg);
                                }else {
                                    Utils.print(AppConstants.LogTag,"通过用户名和密码登录失败，报错信息："+e.getMessage());
                                    msg.what = LOGIN_ERROR;
                                    mHandler.sendMessage(msg);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    msg.what=THREAD_ERROR;
                    mHandler.sendMessage(msg);
                    Utils.print(AppConstants.LogTag,"MyLoginThread报错，报错信息："+e.getMessage());
                }
            }
        }.start();
    }
    private void Dialog(){
        reset_password_dialog = new AlertDialog.Builder(LoginActivity.this)
                    .setTitle(R.string.text_password_reset)
                    .setMessage(getResources().getString(R.string.text_is_reset_account)+" "+ username.getText().toString().trim()+" "+getResources().getString(R.string.text_de_password))
                    .setNeutralButton(R.string.text_cancel, null)
                    .setNegativeButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!username.getText().toString().trim().equals("")) {
                                send_email_dialog = new ProgressDialog(LoginActivity.this, ProgressDialog.STYLE_SPINNER);
                                send_email_dialog.setMessage(getResources().getString(R.string.text_sending_email));
                                send_email_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                send_email_dialog.setCanceledOnTouchOutside(false);
                                send_email_dialog.setCancelable(false);
                                send_email_dialog.show();
                                MyReset_Password_Thread(username.getText().toString().trim());
                            }

                        }
                    })
                    .create();
    }
    private void MyReset_Password_Thread(final String username){
        new Thread(){
            public void run(){
                final Message msg = Message.obtain();
                try {
                    BmobQuery<BmobUser> query = new BmobQuery<>();
                    query.addWhereEqualTo("username", username);
                    query.findObjects(new FindListener<BmobUser>() {
                        @Override
                        public void done(List<BmobUser> object, BmobException e) {
                            if(e==null){
                                email=object.get(0).getEmail();
                                BmobUser.resetPasswordByEmail(email, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            msg.what=RESET_SUCCESS;
                                            Utils.print(AppConstants.LogTag,"重置密码请求发送成功，请到" +email  + "邮箱进行密码重置操作");
                                            mHandler.sendMessage(msg);
                                        }else{
                                            Utils.print(AppConstants.LogTag,"重置密码请求发送失败:" + e.getMessage());
                                            if(e.getMessage().contains(AppConstants.NO_NETWORK_ERROR_INFO)){
                                                msg.what=NO_NETWORK;
                                                mHandler.sendMessage(msg);
                                            }else {
                                                msg.what=RESET_ERROR;
                                                mHandler.sendMessage(msg);
                                            }
                                        }
                                    }
                                });
                            }else{
                                Utils.print(AppConstants.LogTag,"查询用户信息失败:" + e.getMessage());
                                if(e.getMessage().contains(AppConstants.NO_NETWORK_ERROR_INFO)){
                                    msg.what=NO_NETWORK;
                                    mHandler.sendMessage(msg);
                                }else {
                                    msg.what=QUERY_USER_ERROR;
                                    mHandler.sendMessage(msg);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    msg.what=RESET_THREAD_ERROR;
                    Utils.print(AppConstants.LogTag,"MyReset_Password_Thread线程报错，报错信息："+e.getMessage());
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }
    private void Show_Email_Dialog(String username,String email){
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(R.string.text_password_reset)
                .setMessage("账号："+username+"重置密码请求发送成功，请到" +email  + "邮箱进行密码重置操作。")
                .setNegativeButton(R.string.text_ok, null)
                .setCancelable(false)
                .show();
    }
}
