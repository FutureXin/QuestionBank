package club.lovemo.questionbank.lovemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import club.lovemo.questionbank.R;
import club.lovemo.questionbank.entity.Collection;
import club.lovemo.questionbank.entity.MyUser;
import club.lovemo.questionbank.entity.QuestionBank;
import club.lovemo.questionbank.entity.QuestionBankDB;
import club.lovemo.questionbank.entity.TypeTopic;
import club.lovemo.questionbank.utils.AppConstants;
import club.lovemo.questionbank.utils.DBHelperUtils;
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

public class QuestionListActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferencesUtils preferencesUtils;
    PopupWindow mPopupWindow;
    private TextView tv_show_category;
    private TextView no_data_text;
    private RefreshListView listView;
    private ProgressBar pb_progress;
    private Toolbar content_toolbar;
    private EditText et_search;//定义EditText
    private MyAdapter adapter;
    private AlertDialog dialog;
    private List<QuestionBank> questionBankList = new ArrayList<>();
    private List<QuestionBankDB> questionBankDBList = new ArrayList<>();
    List<TypeTopic> All_Topic_List = new ArrayList<>();
    static final int SEND_SMS_REQUEST = 0;
    static final int CALL_REQUEST = 1;
    public int category_id;
    private View my_view;
    private View my_reset_email_view;
    private EditText et_old_password;
    private EditText et_new_password;
    private EditText et_new_password2;
    private EditText et_new_email;
    private EditText et_reset_email_password;
    public static int PageNumber=20;
    public int CurrentNumber=1;
    private final static int DROP_DOWN_REFRESH=0;
    private final static int PULL_REFRESH=1;
    private static boolean IS_COLLECTION=false;
    private MyUser myUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        content_toolbar = findViewById(R.id.content_toolbar);
        tv_show_category = findViewById(R.id.tv_title);
        myUser=BmobUser.getCurrentUser(MyUser.class);
        no_data_text = findViewById(R.id.no_data_text);
        my_view= LayoutInflater.from(this).inflate(R.layout.reset_password_dialog, null);
        et_old_password= my_view.findViewById(R.id.et_reset_old_password);
        et_new_password= my_view.findViewById(R.id.et_reset_new_password);
        et_new_password2= my_view.findViewById(R.id.et_reset_new_password2);
        my_reset_email_view= LayoutInflater.from(this).inflate(R.layout.reset_email_dialog, null);
        et_new_email= my_reset_email_view.findViewById(R.id.et_reset_email);
        et_reset_email_password= my_reset_email_view.findViewById(R.id.et_reset_email_password);
        et_reset_email_password.setTypeface(Typeface.DEFAULT);
        et_reset_email_password.setTransformationMethod(new PasswordTransformationMethod());
        //设置hint字体
        et_old_password.setTypeface(Typeface.DEFAULT);
        et_old_password.setTransformationMethod(new PasswordTransformationMethod());
        //设置hint字体
        et_new_password.setTypeface(Typeface.DEFAULT);
        et_new_password.setTransformationMethod(new PasswordTransformationMethod());
        //设置hint字体
        et_new_password2.setTypeface(Typeface.DEFAULT);
        et_new_password2.setTransformationMethod(new PasswordTransformationMethod());
        Dialog();
        preferencesUtils = new SharedPreferencesUtils(QuestionListActivity.this);
        category_id = preferencesUtils.getCategoryID();
        pb_progress = findViewById(R.id.pb_progress);
        listView = findViewById(R.id.content_list);
        et_search = findViewById(R.id.content_search);
        pb_progress.bringToFront();
        no_data_text.bringToFront();

        listView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
                                          @Override
                                          public void onRefresh() {
                                              CurrentNumber=1;
                                              setData(DROP_DOWN_REFRESH);
                                          }

                                          @Override
                                          public void onLoadMore() {
                                              if(All_Topic_List.size()%PageNumber==0) {
                                                  CurrentNumber++;
                                                  setData(PULL_REFRESH);
                                              }
                                          }
                                      });
                et_search.setOnKeyListener(new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                // 先隐藏键盘
                                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                                        .hideSoftInputFromWindow(QuestionListActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                //进行搜索操作的方法，在该方法中可以加入mEditSearchUser的非空判断
                                Search();
                        }
                        return false;
                    }
                });
        tv_show_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionListActivity.this, ChoiceActivity.class);
                startActivityForResult(intent, SEND_SMS_REQUEST);
                QuestionListActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        initData();
        pb_progress.setVisibility(View.VISIBLE);
        setData(DROP_DOWN_REFRESH);
        if(preferencesUtils.getOldVersionCode()!=Utils.getVersionCode(this)){
            preferencesUtils.setShowAbout(true);
            preferencesUtils.setOldVersionCode(Utils.getVersionCode(this));
        }
        if(preferencesUtils.getShowAbout()){
            dialog.show();
        }
        content_toolbar.setNavigationIcon(R.mipmap.user_male);
        content_toolbar.setTitle(myUser.getUsername());
        content_toolbar.setTitleTextAppearance(QuestionListActivity.this,R.style.Toolbar_TitleText);
        setSupportActionBar(content_toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
//            Utils.print(AppConstants.LogTag,"content_toolbar 高度："+content_toolbar.getHeight());
        content_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showToast(QuestionListActivity.this,"用户："+myUser.getUsername());
                Intent intent=new Intent(QuestionListActivity.this,UserCenterActivity.class);
                QuestionListActivity.this.startActivity(intent);
                QuestionListActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        //设置toolBar上的MenuItem点击事件
        content_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_overflow:
                        //弹出自定义overflow
                        popUpMyOverflow();
                        break;
                    case R.id.action_collection:
                        All_Topic_List.clear();
                        Notify();
                        pb_progress.setVisibility(View.VISIBLE);
                        if(!IS_COLLECTION){
                            IS_COLLECTION=true;
                            content_toolbar.getMenu().findItem(R.id.action_collection).setIcon(R.mipmap.already_collected);
                            et_search.setEnabled(false);
                            Utils.showToast(QuestionListActivity.this, "查看我收藏的题目,不支持模糊搜索！");

                        }else {
                            IS_COLLECTION=false;
                            content_toolbar.getMenu().findItem(R.id.action_collection).setIcon(R.mipmap.not_collect);
                            et_search.setEnabled(true);
                            Utils.showToast(QuestionListActivity.this, "返回题目列表");
                        }
                        setData(DROP_DOWN_REFRESH);
                        break;
                }
                return true;
            }
        });

    }

    private void Search() {
        String search = et_search.getText().toString().trim();
        List<TypeTopic> Search_list = new ArrayList<>();
        if (!search.equals("")) {
            getDBDataList();
            Utils.print(AppConstants.LogTag, "搜索" + search);
            if (All_Topic_List != null) {
//                        for (int i = 0; i < All_Topic_List.size(); i++) {
//                            if (All_Topic_List.get(i).getTopic().indexOf(search) != -1) {
//                                Search_list.add(All_Topic_List.get(i));
//                            }
//                        }
                for (TypeTopic type_Topic : All_Topic_List) {
                    if (type_Topic.getTopic().contains(search)) {
                        Search_list.add(type_Topic);
                    }
                }
                All_Topic_List.clear();
                Utils.print(AppConstants.LogTag, All_Topic_List.size() + "All_Topic_List.size");
                All_Topic_List = Search_list;
                Notify();
            } else {
                Utils.showToast(QuestionListActivity.this, "无数据");
            }
        } else {
            getDBDataList();
            Check_category();
            Utils.showToast(QuestionListActivity.this, "请输入关键字");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEND_SMS_REQUEST) {
            Utils.print(AppConstants.LogTag, "选择的是：" + resultCode);
            if (resultCode != -1) {
                if (resultCode != category_id) {
                    et_search.setText("");
                    category_id = resultCode;
                    preferencesUtils.setCategoryID(category_id);
                    All_Topic_List.clear();
                    Notify();
                    CurrentNumber=1;
                    pb_progress.setVisibility(View.VISIBLE);
                    setData(DROP_DOWN_REFRESH);
                    listView.setSelection(0);//切换了类别后回到顶部
                }
            } else {
                Utils.print(AppConstants.LogTag, "没有选择，点击了返回键！" + resultCode);
            }
        } else if (requestCode == CALL_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                Utils.print(AppConstants.LogTag, "调用结果取消了");
            }
        }
    }

    private void setData(int refresh_type) {
        if (category_id >= getResources().getStringArray(R.array.category_list_j).length) {
            Check_category(IDConversion(), getResources().getStringArray(R.array.category_list_l)[category_id - getResources().getStringArray(R.array.category_list_j).length],refresh_type);
        } else {
            Check_category(IDConversion(), getResources().getStringArray(R.array.category_list_j)[category_id],refresh_type);
        }
    }

    private String[] IDConversion() {
        String[] category_array;
        if (category_id == 0) {
            category_array = new String[]{"Android", "Java","Android工程师"};
        } else if (category_id == 1) {
            category_array = new String[]{"iOS工程师", "Objective-C","Swift","iOS"};
        } else if (category_id == 2) {
            category_array = new String[]{"JavaEE工程师", "JavaEE","Java", "JavaScript","HTML5/CSS3","Servlet/JSP","数据库"};
        } else if (category_id == 3) {
            category_array = new String[]{"测试理论","功能测试工程师"};
        } else if (category_id == 4) {
            category_array = new String[]{"测试理论","自动化测试工程师","Python","Linux","自动化测试"};
        } else if (category_id == 5) {
            category_array = new String[]{"测试理论","Python","Linux","性能测试工程师","性能测试"};
        } else if (category_id == 6) {
            category_array = new String[]{"运维工程师","Python","Linux","数据库","运维"};
        } else if (category_id == 7) {
            category_array = new String[]{"数据挖掘工程师"};
        } else {
            category_array = new String[]{getResources().getStringArray(R.array.category_list_zh)[category_id - getResources().getStringArray(R.array.category_list_j).length]};
        }
        return category_array;
    }

    public void getWebDataList(String[] category_array, String category, final int refresh_type) {
        questionBankList.clear();
//        pb_progress.setVisibility(View.VISIBLE);
        et_search.setEnabled(false);
        listView.setEnabled(false);
        if(!IS_COLLECTION){
            final BmobQuery<QuestionBank> questionBankBmobQuery = new BmobQuery<>();
            questionBankBmobQuery.setSkip((CurrentNumber - 1) * PageNumber).setLimit(PageNumber).addWhereContainedIn("CHARACTERISTIC", Arrays.asList(category_array)).findObjects(new FindListener<QuestionBank>() {
                @Override
                public void done(List<QuestionBank> object, BmobException e) {
//                pb_progress.setVisibility(View.INVISIBLE);
                    if (e == null) {
                        getWebDataTrue(object,refresh_type);
                    } else {
                        getWebDataFalse(e,refresh_type);
                        getDBDataList();
                    }
                }
            });
        }else {
            final BmobQuery<Collection> CollectionBmobQuery = new BmobQuery<>();
            CollectionBmobQuery.setSkip((CurrentNumber - 1) * PageNumber).setLimit(PageNumber).addWhereEqualTo("UserId",myUser.getObjectId()).addWhereContainedIn("CHARACTERISTIC", Arrays.asList(category_array)).findObjects(new FindListener<Collection>() {
                @Override
                public void done(List<Collection> object, BmobException e) {
                    if (e == null) {
                        Utils.print(AppConstants.LogTag,object.size()+"coll数据条数");
                        getWebDataCollectionTrue(object,refresh_type);
                    } else {
                        getWebDataFalse(e,refresh_type);
                    }
                }
            });
        }
        Utils.print(AppConstants.LogTag, category + "characteristic");
        tv_show_category.setText(category);
    }
    private void getWebDataCollectionTrue(List<Collection> object,int refresh_type){
        et_search.setEnabled(true);
        listView.setEnabled(true);
        pb_progress.setVisibility(View.INVISIBLE);
        if (refresh_type == DROP_DOWN_REFRESH) {
            All_Topic_List.clear();
        }
        for (int i = 0; i < object.size(); i++) {
            TypeTopic type_topic = new TypeTopic();
            type_topic.setObjectId(object.get(i).getQuestionId());
            type_topic.setType(object.get(i).getType());
            type_topic.setTopic(object.get(i).getTopic());
            type_topic.setDel(object.get(i).isDel());
            All_Topic_List.add(type_topic);
        }
        Check_category();
        listView.onRefreshComplete(true);
    }
    private void getWebDataTrue(List<QuestionBank> object,int refresh_type){
        et_search.setEnabled(true);
        listView.setEnabled(true);
        pb_progress.setVisibility(View.INVISIBLE);
        questionBankList = object;
        Utils.print(AppConstants.LogTag, "题to_string" + questionBankList.get(0).toString());
        DBHelperUtils dbHelperUtils = DBHelperUtils.getInstance();
        if (refresh_type == DROP_DOWN_REFRESH) {
            All_Topic_List.clear();
        }
        for (int i = 0; i < questionBankList.size(); i++) {
            TypeTopic type_topic = new TypeTopic();
            type_topic.setObjectId(questionBankList.get(i).getObjectId());
            type_topic.setType(questionBankList.get(i).getQUESTION_TYPE());
            type_topic.setTopic(questionBankList.get(i).getTOPIC());
            type_topic.setPraise(questionBankList.get(i).getPRAISE());
            type_topic.setDel(false);
            All_Topic_List.add(type_topic);
            try {
                QuestionBankDB questionBankDB = dbHelperUtils.getQuestionListById(questionBankList.get(i).getObjectId());

                if (questionBankDB == null) {
                    dbHelperUtils.insertQuestion(questionBankList.get(i));
                } else {
                    if (!questionBankDB.getUpdatedAt().equals(questionBankList.get(i).getUpdatedAt())) {
                        dbHelperUtils.updateQuestion(questionBankList.get(i));
                    }
                }

            } catch (Exception a) {
                if (dbHelperUtils != null) {
                    dbHelperUtils.DBClose();
                }

            }
        }
        if (dbHelperUtils != null) {
            dbHelperUtils.DBClose();
        }
        Check_category();
        listView.onRefreshComplete(true);
    }
    private void getWebDataFalse(BmobException e,int refresh_type){
        et_search.setEnabled(true);
        listView.setEnabled(true);
        pb_progress.setVisibility(View.INVISIBLE);
        Utils.print(AppConstants.LogTag, "获取云端数据失败：" + e.getMessage() + "," + e.getErrorCode());
        Utils.showToast(QuestionListActivity.this, "获取云端数据失败！");
        if (e.getErrorCode() == 9016) {
            Utils.showToast(QuestionListActivity.this, getResources().getString(R.string.text_no_network));
        }
        if (refresh_type == PULL_REFRESH) {
            CurrentNumber--;
        }
        Check_category();
        listView.onRefreshComplete(false);
    }
    public void initData() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!All_Topic_List.get(i).isDel()){
                    String type = All_Topic_List.get(i).getType();
                    Utils.print(AppConstants.LogTag, type + "type题型");
                    Utils.print(AppConstants.LogTag,i+"被点击！");
                    switch (type) {
                        case "选择题":
                            Intent choice_intent = new Intent();
                            choice_intent.putExtra("id", All_Topic_List.get(i).getObjectId());
                            //                    intent.putExtra("",questionBankDBList.get(i));
                            choice_intent.setClass(QuestionListActivity.this, ShowChoiceActivity.class);
                            QuestionListActivity.this.startActivity(choice_intent);
                            QuestionListActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            break;
                        case "判断题":
                            Intent judge_intent = new Intent();
                            judge_intent.putExtra("id", All_Topic_List.get(i).getObjectId());
                            judge_intent.setClass(QuestionListActivity.this, ShowJudgeActivity.class);
                            QuestionListActivity.this.startActivity(judge_intent);
                            QuestionListActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            break;
                        case "简答题":
                            Intent short_intent = new Intent();
                            short_intent.setClass(QuestionListActivity.this, ShowShortActivity.class);
                            short_intent.putExtra("id", All_Topic_List.get(i).getObjectId());
                            QuestionListActivity.this.startActivity(short_intent);
                            QuestionListActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            break;
                        default:
                            Intent gap_intent = new Intent();
                            gap_intent.putExtra("id", All_Topic_List.get(i).getObjectId());
                            gap_intent.setClass(QuestionListActivity.this, ShowGapActivity.class);
                            QuestionListActivity.this.startActivity(gap_intent);
                            QuestionListActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            break;
                    }
                }else {
                    Utils.showToast(QuestionListActivity.this, "该题目已删除！");
                }
            }
        });
    }

    public void Check_category() {
        if (All_Topic_List.size() > 0) {
            for (int i = 0; i < All_Topic_List.size(); i++) {
                Utils.print(AppConstants.LogTag, i + All_Topic_List.get(i).toString());
            }
            no_data_text.setVisibility(View.INVISIBLE);
        } else {
            Utils.print(AppConstants.LogTag, "无数据");
            no_data_text.setVisibility(View.VISIBLE);
        }
        if (adapter == null) {
            adapter = new MyAdapter();
            Utils.print(AppConstants.LogTag, "setAdapter");
            listView.setAdapter(adapter);
        } else {
            Notify();
        }

    }

    public void Check_category(String[] category_array, String category,int refresh_type) {
        getWebDataList(category_array, category,refresh_type);
    }

    public void Dialog() {
        dialog = new AlertDialog.Builder(QuestionListActivity.this)
                .setTitle(R.string.text_statement)
                .setMessage(R.string.text_statement_info)
                .setCancelable(false)
                .setPositiveButton(R.string.text_ok, null)
                .setNeutralButton(R.string.text_no_show, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        preferencesUtils.setShowAbout(false);
                    }
                })
                .create();
    }

    public void getDBDataList() {
        questionBankDBList.clear();
        DBHelperUtils dbHelperUtils = DBHelperUtils.getInstance();
        try {
            questionBankDBList = dbHelperUtils.getQuestionList(IDConversion());
            dbHelperUtils.DBClose();
        } catch (Exception e) {
            Utils.print(AppConstants.LogTag, "查询本地数据库出错！！！");
            dbHelperUtils.DBClose();
        }
        All_Topic_List.clear();
        for (int i = 0; i < questionBankDBList.size(); i++) {
            All_Topic_List.add(new TypeTopic(questionBankDBList.get(i).getObjectId(), questionBankDBList.get(i).getQUESTION_TYPE(), questionBankDBList.get(i).getTOPIC(),questionBankDBList.get(i).getPRAISE(),false));
        }
        if (category_id >= getResources().getStringArray(R.array.category_list_j).length) {
            tv_show_category.setText(getResources().getStringArray(R.array.category_list_l)[category_id - getResources().getStringArray(R.array.category_list_j).length]);
        } else {
            tv_show_category.setText(getResources().getStringArray(R.array.category_list_j)[category_id]);
        }
        Check_category();
    }

    public void Notify() {
        Utils.print(AppConstants.LogTag, "Notify");
        adapter.notifyDataSetChanged();
    }

    //如果有Menu,创建完后,系统会自动添加到ToolBar上
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 弹出自定义的popWindow
     */
    private void popUpMyOverflow() {
        //获取状态栏高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        //状态栏高度+toolbar的高度
        int yOffset = frame.top + content_toolbar.getHeight();
        if (null == mPopupWindow) {
            //初始化PopupWindow的布局
            View popView = getLayoutInflater().inflate(R.layout.action_overflow_popwindow, null);
            //popView即popupWindow的布局，true设置focusAble.
            mPopupWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
            //必须设置BackgroundDrawable后setOutsideTouchable(true)才会有效
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
            //点击外部关闭。
            mPopupWindow.setOutsideTouchable(true);
            //设置一个动画。
            mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
            //设置Gravity，让它显示在右上角。
            mPopupWindow.showAtLocation(content_toolbar, Gravity.END | Gravity.TOP, 15, yOffset);
            //设置item的点击监听
            popView.findViewById(R.id.ll_item1).setOnClickListener(this);
            popView.findViewById(R.id.ll_item2).setOnClickListener(this);
            popView.findViewById(R.id.ll_item3).setOnClickListener(this);
            popView.findViewById(R.id.ll_item4).setOnClickListener(this);
            popView.findViewById(R.id.ll_item5).setOnClickListener(this);
        } else {
            mPopupWindow.showAtLocation(content_toolbar, Gravity.END | Gravity.TOP, 15, yOffset);
        }

    }
    AlertDialog reset_password_dialog;
    AlertDialog reset_email_dialog;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_item1:
                if(reset_password_dialog==null) {
                    reset_password_dialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.text_reset_password)
                            .setView(my_view)
                            .setCancelable(false)
                            .setPositiveButton(R.string.text_ok, null)
                            .setNeutralButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create();
                    //这里必须要先调show()方法，后面的getButton才有效
                    reset_password_dialog.show();
                    reset_password_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String new_password = et_new_password.getText().toString().trim();
                            String new_password2 = et_new_password2.getText().toString().trim();
                            String old_password = et_old_password.getText().toString().trim();
                            if (!old_password.equals("") && !new_password.equals("")&& !new_password2.equals("")) {
                                if (!old_password.equals(new_password)) {
                                    if (new_password.length() >= 6) {
                                        if(new_password.equals(new_password2)){
                                        BmobUser.updateCurrentUserPassword(Utils.MD5Encoder(old_password), Utils.MD5Encoder(new_password), new UpdateListener() {

                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    Utils.showToast(QuestionListActivity.this, "密码修改成功，可以用新密码进行登录啦");
                                                    BmobUser.logOut();   //清除缓存用户对象
                                                    reset_password_dialog.cancel();
                                                    startActivity(new Intent(QuestionListActivity.this,LoginActivity.class));
                                                    QuestionListActivity.this.finish();
                                                } else {
                                                    Utils.showToast(QuestionListActivity.this, "密码修改失败:" + e.getMessage());
                                                }
                                            }
                                        });
                                        }else {
                                            Utils.showToast(QuestionListActivity.this, "新密码输入不一致！！！");
                                        }
                                    } else {
                                        Utils.showToast(QuestionListActivity.this, "密码不能少于6位！！！");
                                    }
                                } else {
                                    Utils.showToast(QuestionListActivity.this, "密码不能相同！！！");
                                }
                            } else {
                                Utils.showToast(QuestionListActivity.this, "密码不能为空！！！");
                            }

                        }
                    });
                }else {
                    reset_password_dialog.show();
                    et_old_password.setText("");
                    et_new_password.setText("");
                    et_new_password2.setText("");
                }
                break;
            case R.id.ll_item2:
                if(reset_email_dialog==null) {
                    reset_email_dialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.text_reset_email)
                            .setView(my_reset_email_view)
                            .setCancelable(false)
                            .setPositiveButton(R.string.text_ok, null)
                            .setNeutralButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create();
                    reset_email_dialog.show();
                    reset_email_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String new_email = et_new_email.getText().toString().trim();
                            String password = et_reset_email_password.getText().toString().trim();
                            if (!password.equals("") && !new_email.equals("")) {
                                BmobUser.loginByAccount(myUser.getUsername(), Utils.MD5Encoder(password), new LogInListener<MyUser>() {

                                    @Override
                                    public void done(MyUser user, BmobException e) {
                                        if(user!=null){
                                            MyUser newUser = new MyUser();
                                            newUser.setEmail(new_email);
                                            newUser.update(myUser.getObjectId(),new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){
                                                        Utils.showToast(QuestionListActivity.this,"更新邮箱地址成功！");
                                                        BmobUser.logOut();   //清除缓存用户对象
                                                        reset_email_dialog.cancel();
                                                        startActivity(new Intent(QuestionListActivity.this,LoginActivity.class));
                                                        QuestionListActivity.this.finish();
                                                    }else{
                                                        Utils.showToast(QuestionListActivity.this,"更新邮箱地址失败!");
                                                        Utils.print(AppConstants.LogTag,"更新用户信息失败:"+ e.getMessage());
                                                    }
                                                }
                                            });
                                        }else if(e.getErrorCode()==9016){
                                            Utils.showToast(QuestionListActivity.this,getResources().getString(R.string.text_no_network));
                                        }else if(e.getErrorCode()==101){
                                            Utils.showToast(QuestionListActivity.this,"密码错误！");
                                            Utils.print(AppConstants.LogTag,e.getErrorCode()+"code");
                                        }else {
                                            Utils.showToast(QuestionListActivity.this,"操作失败！");
                                        }
                                    }
                                });

                            } else {
                                Utils.showToast(QuestionListActivity.this, "邮箱或密码不能为空！！！");
                            }

                        }
                    });
                }else {
                    reset_email_dialog.show();
                    et_new_email.setText("");
                    et_reset_email_password.setText("");
                }
                break;
            case R.id.ll_item3:
                Intent feedback_intent=new Intent();
                feedback_intent.setClass(QuestionListActivity.this,FeedbackActivity.class);
                feedback_intent.putExtra("id","软件反馈");
                QuestionListActivity.this.startActivity(feedback_intent);
                QuestionListActivity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                break;
            case R.id.ll_item4:
                new AlertDialog.Builder(QuestionListActivity.this)
                        .setTitle(R.string.text_about)
                        .setMessage(getResources().getString(R.string.text_about_info)+"\n版本名称："+Utils.getVersionName(this)+"\n版本号："+Utils.getVersionCode(this))
                        .setPositiveButton(R.string.text_close,null)
                        .show();
                break;
            case R.id.ll_item5:
                Utils.showToast(QuestionListActivity.this, "退出登录");
                BmobUser.logOut();   //清除缓存用户对象
//                BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了
                //退出登录后跳转至登录界面
                QuestionListActivity.this.startActivity(new Intent(QuestionListActivity.this,LoginActivity.class));
                QuestionListActivity.this.finish();
                break;
        }
        //点击PopWindow的item后,关闭此PopWindow
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return All_Topic_List.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return All_Topic_List.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(QuestionListActivity.this, R.layout.item_showlist, null);
                holder = new ViewHolder();
                holder.tv_list_type = convertView.findViewById(R.id.item_show_type);
                holder.tv_list_topic = convertView.findViewById(R.id.item_show_topic);
                holder.tv_list_praise= convertView.findViewById(R.id.item_show_praise);
                holder.praise_image= convertView.findViewById(R.id.item_show_praise_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_list_type.setText(All_Topic_List.get(position).getType());
            holder.tv_list_topic.setText(position + 1 + " . " + All_Topic_List.get(position).getTopic());
            if(IS_COLLECTION){
                holder.tv_list_praise.setVisibility(View.INVISIBLE);
                holder.praise_image.setVisibility(View.INVISIBLE);
            }else{
                holder.tv_list_praise.setVisibility(View.VISIBLE);
                holder.praise_image.setVisibility(View.VISIBLE);
                if(All_Topic_List.get(position).getPraise()!=null){
                    String str=All_Topic_List.get(position).getPraise()+"";
                    holder.tv_list_praise.setText(str);
                }else {
                    holder.tv_list_praise.setText("0");
                }
            }
            return convertView;
        }

    }

    private static class ViewHolder {
        private TextView tv_list_type;
        private TextView tv_list_topic;
        private TextView tv_list_praise;
        private ImageView praise_image;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    long exitTime=0;
    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Utils.showToast(QuestionListActivity.this, "再按一次退出应用");
            exitTime = System.currentTimeMillis();
        } else {
            Utils.showToast(QuestionListActivity.this, "退出成功");
            finish();
            System.exit(0);
        }
    }
}
