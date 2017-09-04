package club.lovemo.questionbank.lovemo;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import club.lovemo.questionbank.R;
import club.lovemo.questionbank.utils.AppConstants;
import club.lovemo.questionbank.utils.Utils;


/**
 * 下拉刷新的ListView
 *
 * @author Kevin
 *
 */
public class RefreshListView extends ListView implements OnScrollListener,
		android.widget.AdapterView.OnItemClickListener {

	private static final int STATE_PULL_REFRESH = 0;// 下拉刷新
	private static final int STATE_RELEASE_REFRESH = 1;// 松开刷新
	private static final int STATE_REFRESHING = 2;// 正在刷新

	private View mHeaderView;
	private int startY = -1;// 滑动起点的y坐标
	private int mHeaderViewHeight;

	private int mCurrentState = STATE_PULL_REFRESH;// 当前状态
	private TextView tvTitle;
	private TextView tvTime;
	private ImageView ivArrow;
	private ProgressBar pbProgress;
	private RotateAnimation animUp;
	private RotateAnimation animDown;

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeaderView();
		initFooterView();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView();
		initFooterView();
	}

	public RefreshListView(Context context) {
		super(context);
		initHeaderView();
		initFooterView();
	}

	/**
	 * 初始化头布局
	 */
	private void initHeaderView() {
		mHeaderView = View.inflate(getContext(), R.layout.refresh_header, null);
		this.addHeaderView(mHeaderView);

		tvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title);
		tvTime = (TextView) mHeaderView.findViewById(R.id.tv_time);
		ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arr);
		pbProgress = (ProgressBar) mHeaderView.findViewById(R.id.pb_progress);

		mHeaderView.measure(0, 0);
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();

		mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);// 隐藏头布局

		initArrowAnim();

		tvTime.setText(getResources().getString(R.string.text_final_refresh_time)+ getCurrentTime()+"");
	}

	/*
	 * 初始化脚布局
	 */
	private void initFooterView() {
		mFooterView = View.inflate(getContext(),
				R.layout.refresh_listview_footer, null);
		this.addFooterView(mFooterView);

		mFooterView.measure(0, 0);
		mFooterViewHeight = mFooterView.getMeasuredHeight();

		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);// 隐藏

		this.setOnScrollListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		Utils.print(AppConstants.LogTag,ev.getRawY()+"Y"+mHeaderViewHeight+"Bar"+QuestionListActivity.tv_title_he+":::::"+QuestionListActivity.linear_he);
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startY = (int) ev.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				if (startY == -1) {// 确保startY有效
					startY = (int) ev.getRawY();
				}

				if (mCurrentState == STATE_REFRESHING) {// 正在刷新时不做处理
					break;
				}

				int endY = (int) ev.getRawY();
				int dy = endY - startY;// 移动偏移量
				if (dy > 0 && getFirstVisiblePosition() == 0) {// 只有下拉并且当前是第一个item,才允许下拉

//					Utils.print(AppConstants.LogTag,dy+"偏移量");
					int padding = dy - mHeaderViewHeight;// 计算padding
					//Utils.print(AppConstants.LogTag,"padding:"+padding+":");
					if(padding>0) {
						mHeaderView.setPadding(0, padding/3, 0, 0);// 设置当前padding
					}else{
						mHeaderView.setPadding(0, padding, 0, 0);// 设置当前padding
					}
					if (padding > 0 && mCurrentState != STATE_RELEASE_REFRESH) {// 状态改为松开刷新
						mCurrentState = STATE_RELEASE_REFRESH;
						refreshState();
					} else if (padding <0 && mCurrentState != STATE_PULL_REFRESH) {// 改为下拉刷新状态
						mCurrentState = STATE_PULL_REFRESH;
						refreshState();
					}
					if(mCurrentState!=STATE_PULL_REFRESH) {
						return true;
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				startY = -1;// 重置

				if (mCurrentState == STATE_RELEASE_REFRESH) {
					mCurrentState = STATE_REFRESHING;// 正在刷新
					mHeaderView.setPadding(0, 0, 0, 0);// 显示
					refreshState();
				} else if (mCurrentState == STATE_PULL_REFRESH) {
					mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);// 隐藏
				}

				break;

			default:
				break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 刷新下拉控件的布局
	 */
	private void refreshState() {
		switch (mCurrentState) {
			case STATE_PULL_REFRESH:
				tvTitle.setText(getResources().getString(R.string.text_down_refresh));
				ivArrow.setVisibility(View.VISIBLE);
				pbProgress.setVisibility(View.INVISIBLE);
				ivArrow.startAnimation(animDown);
				break;
			case STATE_RELEASE_REFRESH:
				tvTitle.setText(getResources().getString(R.string.text_loosen_refresh));
				ivArrow.setVisibility(View.VISIBLE);
				pbProgress.setVisibility(View.INVISIBLE);
				ivArrow.startAnimation(animUp);
				break;
			case STATE_REFRESHING:
				tvTitle.setText(getResources().getString(R.string.text_refreshing));
				ivArrow.clearAnimation();// 必须先清除动画,才能隐藏
				ivArrow.setVisibility(View.INVISIBLE);
				pbProgress.setVisibility(View.VISIBLE);

				if (mListener != null) {
					mListener.onRefresh();
				}
				break;

			default:
				break;
		}
	}

	/**
	 * 初始化箭头动画
	 */
	private void initArrowAnim() {
		// 箭头向上动画
		animUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animUp.setDuration(200);
		animUp.setFillAfter(true);

		// 箭头向下动画
		animDown = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animDown.setDuration(200);
		animDown.setFillAfter(true);

	}

	OnRefreshListener mListener;
	private View mFooterView;
	private int mFooterViewHeight;

	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}

	interface OnRefreshListener {
		void onRefresh();

		void onLoadMore();// 加载下一页数据
	}

	/*
	 * 收起下拉刷新的控件
	 */
	public void onRefreshComplete(boolean success) {
		if (isLoadingMore) {// 正在加载更多...
			mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);// 隐藏脚布局
			isLoadingMore = false;
		} else {
			mCurrentState = STATE_PULL_REFRESH;
			tvTitle.setText(getResources().getString(R.string.text_down_refresh));
			ivArrow.setVisibility(View.VISIBLE);
			pbProgress.setVisibility(View.INVISIBLE);

			mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);// 隐藏

			if (success) {
				tvTime.setText(getResources().getString(R.string.text_final_refresh_time) + getCurrentTime()+"");
			}
		}
	}

	/**
	 * 获取当前时间
	 */
	public String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	private boolean isLoadingMore;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE
				|| scrollState == SCROLL_STATE_FLING) {
                Utils.print(AppConstants.LogTag,"getCount"+(getCount()-1-getHeaderViewsCount()));
			if (getLastVisiblePosition() == getCount() - 1 && !isLoadingMore&&(getCount()-1-getHeaderViewsCount())%QuestionListActivity.PageNumber==0) {// 滑动到最后
				System.out.println("到底了.....");
				mFooterView.setPadding(0, 0, 0, 0);// 显示
				setSelection(getCount() - 1);// 改变ListView显示位置

				isLoadingMore = true;

				if (mListener != null) {
					mListener.onLoadMore();
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {

	}

	OnItemClickListener mItemClickListener;

	@Override
	public void setOnItemClickListener(
			android.widget.AdapterView.OnItemClickListener listener) {
		super.setOnItemClickListener(this);

		mItemClickListener = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		if (mItemClickListener != null) {
			mItemClickListener.onItemClick(parent, view, position
					- getHeaderViewsCount(), id);
		}
	}

}
