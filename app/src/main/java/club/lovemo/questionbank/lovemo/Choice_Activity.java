package club.lovemo.questionbank.lovemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import club.lovemo.questionbank.R;
import club.lovemo.questionbank.utils.BaseViewHolder;
import club.lovemo.questionbank.utils.MyGridView;

public class Choice_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        initView();
    }

    private void initView() {
        MyGridView grid_view=(MyGridView) findViewById(R.id.grid_view);
        MyGridView grid_view2=(MyGridView) findViewById(R.id.grid_view2);
        grid_view.setAdapter(baseAdapter_j);
        grid_view2.setAdapter(baseAdapter_l);

        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                //Utils.showToast(mContext,""+id);
                //Toast.makeText(mContext, "你点击了第" + position + "项", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(Choice_Activity.this, QuestionListActivity.class));
//                finish();
                Choice_Activity.this.setResult(position);
                Choice_Activity.this.finish();
                Choice_Activity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });
        grid_view2.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                Choice_Activity.this.setResult(position+ getResources().getStringArray(R.array.category_list_j).length);
                Choice_Activity.this.finish();
                Choice_Activity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Choice_Activity.this.setResult(-1);
            Choice_Activity.this.finish();
            Choice_Activity.this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
  BaseAdapter baseAdapter_j=new  BaseAdapter() {

      @Override
      public int getCount() {
          // TODO Auto-generated method stub
          return getResources().getStringArray(R.array.category_list_j).length;
      }

      @Override
      public Object getItem(int position) {
          // TODO Auto-generated method stub
          return position;
      }

      @Override
      public long getItemId(int position) {
          // TODO Auto-generated method stub
          return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
          if (convertView == null) {
              convertView = LayoutInflater.from(Choice_Activity.this).inflate(
                      R.layout.grid_item, parent, false);
          }
          TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
          ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
          iv.setBackgroundResource(R.mipmap.code_android);

          tv.setText(getResources().getStringArray(R.array.category_list_j)[position]);
          return convertView;
      }
  };
    BaseAdapter baseAdapter_l=new BaseAdapter() {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return getResources().getStringArray(R.array.category_list_l).length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(Choice_Activity.this).inflate(
                        R.layout.grid_item, parent, false);
            }
            TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
            ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
            iv.setBackgroundResource(R.mipmap.code_android);

            tv.setText(getResources().getStringArray(R.array.category_list_l)[position]);
            return convertView;
        }
    };
}