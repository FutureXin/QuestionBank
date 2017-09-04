package club.lovemo.questionbank.lovemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.File;

import club.lovemo.questionbank.R;
import club.lovemo.questionbank.entity.MyUser;
import club.lovemo.questionbank.utils.AppConstants;
import club.lovemo.questionbank.utils.Utils;
import cn.bmob.v3.BmobUser;

/**
 * Created by John.
 */

public class UserCenterActivity extends AppCompatActivity{
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private MyUser myUser;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        imageView=(ImageView) findViewById(R.id.user_center_image_view);
        RelativeLayout relativeLayout_feedback=(RelativeLayout)findViewById(R.id.user_center_relative_feedback);
        RelativeLayout relativeLayout_about=(RelativeLayout)findViewById(R.id.user_center_relative_about);
        myUser= BmobUser.getCurrentUser(MyUser.class);
        Toolbar user_center_toolbar=(Toolbar)findViewById(R.id.user_center_toolbar);
        user_center_toolbar.setNavigationIcon(R.mipmap.back);
        setSupportActionBar(user_center_toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //设置NavigationIcon的点击事件,需要放在setSupportActionBar之后设置才会生效,
        //因为setSupportActionBar里面也会setNavigationOnClickListener
        //返回登录界面
        user_center_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCenterActivity.this.finish();
                UserCenterActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });
        TextView username=(TextView)findViewById(R.id.user_center_username);
        username.setText(myUser.getUsername());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChoosePicDialog();
            }
        });
        relativeLayout_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showToast(UserCenterActivity.this,"点击了反馈");
            }
        });
        relativeLayout_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showToast(UserCenterActivity.this,"点击了关于");
            }
        });
    }
    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "image.jpg"));
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Utils.print(AppConstants.LogTag, "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", (int)Utils.dip2Dimension(80,UserCenterActivity.this));
        intent.putExtra("outputY", (int)Utils.dip2Dimension(80,UserCenterActivity.this));
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = Utils.toRoundBitmap(photo, tempUri); // 这个时候的图片已经被处理成圆形的了
            imageView.setBackgroundResource(0);
            imageView.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了

//        String imagePath = Utils.savePhoto(bitmap, Environment
//                .getExternalStorageDirectory().getAbsolutePath(), String
//                .valueOf(System.currentTimeMillis()));
//        if(imagePath != null){
//            // 拿着imagePath上传了
//            // ...
//        }
    }
}

