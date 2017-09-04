package club.lovemo.questionbank.lovemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import club.lovemo.questionbank.R;
import club.lovemo.questionbank.entity.Delete;
import club.lovemo.questionbank.entity.MyUser;
import club.lovemo.questionbank.permission.PermissionListener;
import club.lovemo.questionbank.permission.PermissionManager;
import club.lovemo.questionbank.utils.AppConstants;
import club.lovemo.questionbank.utils.DBHelperUtils;
import club.lovemo.questionbank.utils.Utils;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by John.
 */

public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CAMERA=1;
    private CoordinatorLayout container;
    private static final int LOGIN_SUCCESS=0;
    private static final int LOGIN=1;
    private static final int THREAD_ERROR=2;
    private static final int SEND_EMAIL=3;
    private static final int SEND_EMAIL_SUCCESS=4;
    private static final int SEND_EMAIL_ERROR=5;
    private static final int NO_NETWORK = 6;
    private static final int GET_EMAIL_ERROR = 7;
    private static final String APP_ID = "b7c43259f2ef61c78d4f56f5292ac405";
    private static final boolean DEVELOPER_MODE = true;
    private PermissionManager helper;
    private AlertDialog email_dialog;
    private String Email;
    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    startActivity(new Intent(SplashActivity.this, QuestionListActivity.class));
                    SplashActivity.this.finish();
                    break;
                case LOGIN:
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                    break;
                case THREAD_ERROR:
                    Utils.showToast(SplashActivity.this,"初始化或自动检查更新失败！");
                    SplashActivity.this.finish();
                    break;
                case SEND_EMAIL:
                    final MyUser User = BmobUser.getCurrentUser(MyUser.class);
                    email_dialog=new AlertDialog.Builder(SplashActivity.this)
                            .setTitle(R.string.text_account_not_activated)
                            .setMessage(getResources().getString(R.string.text_splash_account)+" "+User.getUsername()+" "+getResources().getString(R.string.text_splash_activated))
                            .setPositiveButton(R.string.text_send, null)
                            .setNeutralButton(R.string.text_login, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    SplashActivity.this.finish();
                                }
                            })
                            .setNegativeButton(R.string.text_close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .create();
                    email_dialog.show();
                    email_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MySendThread(User);
                        }
                    });
                    break;
                case SEND_EMAIL_SUCCESS:
                    email_dialog.cancel();
                    new AlertDialog.Builder(SplashActivity.this)
                            .setMessage(getResources().getString(R.string.text_splash_email_send_success)+ " " +Email + " "+getResources().getString(R.string.text_splash_email_send_success_activated))
                            .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    SplashActivity.this.finish();
                                }
                            })
                            .setNeutralButton(R.string.text_out, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SplashActivity.this.finish();
                                }
                            })
                            .show();
                    break;
                case SEND_EMAIL_ERROR:
                    Utils.showToast(SplashActivity.this,getResources().getString(R.string.text_splash_send_email_error));
                    break;
                case NO_NETWORK:
                    Utils.showToast(SplashActivity.this,getResources().getString(R.string.text_no_network));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);//自动更新接口
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        TextView tv_show_app_name=(TextView)findViewById(R.id.tv_show_app_name);
        tv_show_app_name.setVisibility(View.VISIBLE);
//        BmobUpdateAgent.initAppVersion();//初始化AppVersion表
        container=(CoordinatorLayout)findViewById(R.id.container);
        Utils.print(AppConstants.LogTag,isMarshmallow()+"版本是否大于6.0");
        if(isMarshmallow()){
            Permission_to_request();
        }else{
            MyThread();
        }

//        if (DEVELOPER_MODE) {
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                                .detectLeakedSqlLiteObjects()
//                                .detectLeakedClosableObjects()
//                                .penaltyLog()
//                                .penaltyDeath()
//                                .build());
//            }




    }
    private void Permission_to_request(){
        helper = PermissionManager.with(SplashActivity.this)
                //添加权限请求码
                .addRequestCode(SplashActivity.REQUEST_CODE_CAMERA)
                //设置权限，可以添加多个权限
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE)
                //设置权限监听器
                .setPermissionsListener(new PermissionListener() {

                    @Override
                    public void onGranted() {
                        //当权限被授予时调用
//                        Utils.showToast(SplashActivity.this,"获取权限成功");
                        MyThread();
                    }

                    @Override
                    public void onDenied() {
                        //用户拒绝该权限时调用
//                        Utils.showToast(SplashActivity.this,"权限被拒绝");
                        openSettingActivity(SplashActivity.this,getResources().getString(R.string.text_splash_open_the_necessary_permissions));
                    }

                    @Override
                    public void onShowRationale(String[] permissions) {
                        //当用户拒绝某权限时并点击`不再提醒`的按钮时，下次应用再请求该权限时，需要给出合适的响应（比如,给个展示对话框来解释应用为什么需要该权限）
                        Snackbar.make(container, R.string.text_splash_need_to_access,  Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.text_ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //必须调用该`setIsPositive(true)`方法
                                        helper.setIsPositive(true);
                                        helper.request();
                                    }
                                }).show();
                    }
                })
                //请求权限
                .request();
    }
    private void MyThread(){
        new Thread(){
            public void run(){
                final Message msg = Message.obtain();
                try {
                    sleep(1500);
                    //初始化Bmob
                    Bmob.initialize(SplashActivity.this, APP_ID);
                    BmobUpdateAgent.update(SplashActivity.this);
                    DBHelperUtils dbHelperUtils = DBHelperUtils.getInstance();
                    int count = dbHelperUtils.getDBCount();
                    dbHelperUtils.DBClose();
                    //判断本地数据库中是否存在数据，如果存在的话就获取后台是否有删除的数据
                    if(count>0){
                        BmobQuery<Delete> bmobQuery=new BmobQuery<>();
                        bmobQuery.setLimit(1000).findObjects(new FindListener<Delete>() {
                            @Override
                            public void done(List<Delete> list, BmobException e) {
                                if(e==null){
                                    if(list.size()>0){
                                        DBHelperUtils dbHelperUtils = DBHelperUtils.getInstance();
                                        for (int i=0;i<list.size();i++){
                                            dbHelperUtils.Delete_question(list.get(i).getQuestion_objectId());
                                        }
                                        Utils.print(AppConstants.LogTag,"获取云端数据成功并删除");
                                        dbHelperUtils.DBClose();
                                        CheckUser(msg);
                                    }else {
                                        Utils.print(AppConstants.LogTag,"云端无删除数据");
                                        CheckUser(msg);
                                    }
                                }else {
                                    Utils.print(AppConstants.LogTag,"获取删除的题目ID失败："+e.getMessage());
                                    CheckUser(msg);
                                }
                            }
                        });
                    }else {
                        Utils.print(AppConstants.LogTag,"本地数据库中无数据！");
                        CheckUser(msg);
                    }
                } catch (Exception e) {
                    Utils.print(AppConstants.LogTag,"初始化报错："+e.toString()+"Message:"+e.getMessage());
                    msg.what=THREAD_ERROR;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }
    public void CheckUser(Message msg){
        final MyUser User = BmobUser.getCurrentUser(MyUser.class);
        if(User != null){
            // 允许用户使用应用
            Utils.print(AppConstants.LogTag,"User:"+User.getEmailVerified());
            if(User.getEmailVerified()!=null){
                if(User.getEmailVerified()){
                    msg.what=LOGIN_SUCCESS;
                    mHandler.sendMessage(msg);
                }else {
                    msg.what=SEND_EMAIL;
                    mHandler.sendMessage(msg);
                }
            }else {
                msg.what=LOGIN;
                mHandler.sendMessage(msg);
                BmobUser.logOut();
            }
        }else{
            //缓存用户对象为空时， 可打开用户登录界面…
            msg.what=LOGIN;
            mHandler.sendMessage(msg);
        }
    }
    @Override
    public void onRequestPermissionsResult( int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                helper.onPermissionResult(permissions, grantResults);
                break;
        }
    }
    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
    private void showMessageOKCancel(final SplashActivity context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.text_ok, okListener)
                .setNegativeButton(R.string.text_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SplashActivity.this.finish();
                    }
                }).create().show();

    }
    private void openSettingActivity(final SplashActivity activity, String message) {

        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
                SplashActivity.this.finish();
            }
        });
    }
    private void MySendThread(final MyUser User){
        new Thread(){
            @Override
            public void run() {
                final Message msg = Message.obtain();
                BmobQuery<BmobUser> query = new BmobQuery<>();
                query.addWhereEqualTo("username", User.getUsername());
                query.findObjects(new FindListener<BmobUser>() {
                    @Override
                    public void done(List<BmobUser> object, BmobException e) {
                        if (e == null) {
                            Email=object.get(0).getEmail();
                            BmobUser.requestEmailVerify(Email, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        msg.what = SEND_EMAIL_SUCCESS;
                                        mHandler.sendMessage(msg);
                                        Utils.print(AppConstants.LogTag, "请求验证邮件发送成功，请到" + Email + "邮箱中进行激活。");
                                    } else {
                                        if (e.getMessage().contains(AppConstants.NO_NETWORK_ERROR_INFO)) {
                                            msg.what = NO_NETWORK;
                                            mHandler.sendMessage(msg);
                                        } else {
                                            msg.what = SEND_EMAIL_ERROR;
                                            mHandler.sendMessage(msg);
                                            Utils.print(AppConstants.LogTag, "请求验证邮箱发送失败，报错信息：" + e.getMessage());
                                        }
                                    }
                                }
                            });
                        }else {
                            if(e.getMessage().contains(AppConstants.NO_NETWORK_ERROR_INFO)){
                                msg.what = NO_NETWORK;
                                mHandler.sendMessage(msg);
                            }else {
                                msg.what = GET_EMAIL_ERROR;
                                mHandler.sendMessage(msg);
                                Utils.print(AppConstants.LogTag, "获取邮箱地址错误，报错信息：" + e.getMessage());
                            }
                        }
                    }
                });
            }
        }.start();
    }
}
