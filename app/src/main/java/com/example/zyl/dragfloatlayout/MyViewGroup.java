package com.example.zyl.dragfloatlayout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.tfhr02.myapplication.R;

/**
 * Created by TFHR02 on 2018/3/13.
 */
public class MyViewGroup extends LinearLayout {
    private static final String TAG = "MyViewGroup";
    private ViewDragHelper mViewDragHelper;
    private LinearLayout ll_header, ll_header1_1;
    private View rg_Tab;
    private ScrollView nsv;

    public MyViewGroup(Context context) {
        this(context, null);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mViewDragHelper = ViewDragHelper.create(this, 3f, new ViewDragCallBack());
    }

    /**
     * 是否拦截子view的触摸事件
     */
    private boolean intercept = true;

    private int nsv_currentTop = -1;
    /**
     * 上一次偏移量
     */
    private float last_ratio;

    private int ll_header_bottom;

    private class ViewDragCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child.getId() == ll_header.getId() || child.getId() == nsv.getId();
        }

        /**
         * 处理水平方向上的拖动
         *
         * @param child 拖动的View
         * @param left  移动到x轴的距离
         * @param dx    建议的移动的x距离
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //两个if主要是让view在ViewGroup中
            if (left < getPaddingLeft()) {
                return getPaddingLeft();
            }

            if (left > getWidth() - child.getMeasuredWidth()) {
                return getWidth() - child.getMeasuredWidth();
            }
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            ll_header_bottom = ll_header.getBottom();
            if (child.getId() == ll_header.getId()) {//如果拖拽的是头
                int minTop = -(ll_header.getHeight() - rg_Tab.getHeight());
                if (top < minTop) {
                    return minTop;
                } else if (top > 0) {
                    return 0;
                }
            } else if (child.getId() == nsv.getId()) {//如果拖拽的是内容
                if (top < rg_Tab.getHeight()) {
                    return rg_Tab.getHeight();
                } else if (top > ll_header_bottom) {
                    return ll_header_bottom;
                }
            }
            return top;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            if (changedView.getId() == ll_header.getId()) {
                rg_Tab.offsetTopAndBottom(dy);
                nsv.offsetTopAndBottom(dy);
            } else if (changedView.getId() == nsv.getId()) {
                if (nsv_currentTop == -1) nsv_currentTop = ll_header_bottom;
                //tab占整个滑动区域的比率
                float tabH = rg_Tab.getHeight() * 1.0f;
                float ratio = tabH / (nsv_currentTop - tabH);
//                Log.i(TAG, "ratio: " + ratio);
                if (Math.abs(top) > rg_Tab.getHeight()) {//如果tab完全展开后
                    intercept = true;
                } else {
                    intercept = false;
                }

                float translationY;
                if (!Float.isInfinite(ratio)) {
                    //注意：这里的top（tab高<=top<=header1_1高）代表Y轴坐标...
                    translationY = (top - nsv_currentTop) * (1 + ratio);
                } else {//无穷大，表示ll_header滑动到最上面时，又拖拽nsv
                    translationY = -rg_Tab.getHeight();
                }
                //让tab向上位移出现
                rg_Tab.setTranslationY(translationY);

                float ratios = Math.abs(translationY / nsv_currentTop);
                if (onMyViewGroupScrollListener != null) {
                    if (last_ratio != ratios) {
                        last_ratio = ratios;
                        onMyViewGroupScrollListener.translationY(last_ratio);
                    }
                }
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            nsv_currentTop = -1;
            if (releasedChild.getId() == ll_header.getId()) {
                int minTop = -(ll_header.getHeight() - rg_Tab.getHeight());
                mViewDragHelper.flingCapturedView(0, minTop, 0, 0);
            } else if (releasedChild.getId() == nsv.getId()) {
//                int ll_header_bottom = ll_header.getBottom();
//                Log.i(TAG, "ll_header_bottom: " + ll_header_bottom);
                if (rg_Tab.getTranslationY() != 0) {
                    mViewDragHelper.settleCapturedViewAt(0, rg_Tab.getHeight());
                }
//                mViewDragHelper.flingCapturedView(0, rg_Tab.getHeight(), 0, ll_header.getBottom());
            }
            invalidate();
        }
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {//如果还在惯性滑动
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private OnMyViewGroupScrollListener onMyViewGroupScrollListener;

    public interface OnMyViewGroupScrollListener {
        void translationY(float ratio);
    }

    public void setOnMyViewGroupScrollListener(OnMyViewGroupScrollListener onMyViewGroupScrollListener) {
        this.onMyViewGroupScrollListener = onMyViewGroupScrollListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!intercept) {
            return false;
        } else {
            return true;
        }
        //boolean b = mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ll_header = (LinearLayout) findViewById(R.id.ll_header);
        ll_header1_1 = (LinearLayout) findViewById(R.id.ll_header1_1);

        rg_Tab = findViewById(R.id.rg_Tab);
        nsv = (ScrollView) findViewById(R.id.nsv);
        nsv.getLayoutParams().height = DisplayUtil.Height(getContext());
    }

    private int last_animatedValue = 0;

    public void scrollHeader() {
        intercept = true;
        this.last_ratio = 0;


        last_animatedValue = rg_Tab.getHeight();
        ValueAnimator animator = ObjectAnimator.ofInt(rg_Tab.getHeight(), ll_header_bottom);
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                nsv.offsetTopAndBottom(animatedValue - last_animatedValue);
                last_animatedValue = animatedValue;

                if (onMyViewGroupScrollListener!=null){
                    float animatedFraction = valueAnimator.getAnimatedFraction();

                    rg_Tab.setTranslationY(-ll_header_bottom*(1-animatedFraction));
                    onMyViewGroupScrollListener.translationY(1-animatedFraction);
                }
            }
        });
        animator.start();
    }
}
