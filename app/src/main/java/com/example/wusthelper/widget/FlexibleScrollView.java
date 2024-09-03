package com.example.wusthelper.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import com.example.wusthelper.R;

/**
 * 有弹性的scrollView
 *
 * @author Asche
 * @github asche910
 * @date 2019年7月31日
 */
public class FlexibleScrollView extends ScrollView {
    //不能超出界面
    private static final int OVER_SCROLL_NONE = 0x1;
    //可以下拉
    private static final int OVER_SCROLL_TOP = 0x2;
    //可以上拉
    private static final int OVER_SCROLL_BOTTOM=0x4;
    private static final String TAG = "FlexibleScrollView";
    // 移动因子，是一个百分比，比如手指移动了100px，那么view只移动50px，目的是达到一个延迟的效果。
    private static final float MOVE_FACTOR = 0.3f;
    // 手指松开时，界面回到原始位置动画所需的时间
    private static final int ANIM_TIME = 300;
    // ScrollView唯一的一个子view
    private View contentView;
    // 手指按下时的Y值，用于计算移动中的移动距离
    // 如果按下时不能上拉或者下拉，会在手指移动时更新为当前手指的Y值。
    private float startY;
    // 用于记录正常的布局位置
    private Rect originalRect = new Rect();
    // 记录手指按下时是否可以下拉
    private boolean canPullDown = false;
    // 记录手指按下时是否可以上拉
    private boolean canPullUp = false;
    // 在手指滑动时的过程中记录是否移动了布局
    private boolean isMoved = false;

    private int overScrollMode;

    // 是否支持下拉
    private boolean enablePullDown = true;
    private OverScroll overScroll;
    public FlexibleScrollView(Context context) {
        this(context, null);
    }

    public FlexibleScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexibleScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.FlexibleScrollView);
        overScrollMode=array.getInt(R.styleable.FlexibleScrollView_overScrollMode,OVER_SCROLL_TOP|OVER_SCROLL_BOTTOM);
    }

    /**
     * 关闭下拉回弹
     * @param enablePullDown
     */
    public void setEnablePullDown(boolean enablePullDown) {
        this.enablePullDown = enablePullDown;
    }

    /**
     * 在加载完xml后获取唯一的一个childview
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            // 获取第一个childview
            contentView = getChildAt(0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (contentView == null)
            return;
        // scrollview唯一的一个子view的位置信息，这个位置信息在整个生命周期中保持不变
        originalRect.set(contentView.getLeft(), contentView.getTop(), contentView.getRight(), contentView.getBottom());
    }

    // 在触摸事件中处理上拉和下拉的逻辑
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (contentView == null) {
            return super.dispatchTouchEvent(ev);
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 判断是否可以上拉或者下拉
                canPullDown = isCanPullDown();
                canPullUp = isCanPullUp();
                // 记录按下时的Y值
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                boolean exhaustion=false;
                if (overScroll!=null)exhaustion=overScroll.overScrolledActionUp(startY,contentView,ev);
                if (!isMoved||exhaustion)
                    break; // 如果没有移动布局或者时间已近处理完，则跳过执行
                // 开启动画
                TranslateAnimation anim = new TranslateAnimation(0, 0, contentView.getTop(), originalRect.top);
                // 设置动画时间
                anim.setDuration(ANIM_TIME);
                // 给view设置动画
                contentView.setAnimation(anim);
                // 设置回到正常的布局位置
                contentView.layout(originalRect.left, originalRect.top, originalRect.right, originalRect.bottom);
                // 将标志位重置
                canPullDown = false;
                canPullUp = false;
                isMoved = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 在移动过程既没有达到上拉的程度，又没有达到下拉的程度
                if (!canPullDown && !canPullUp) {
                    startY = ev.getY();
                    canPullDown = isCanPullDown();
                    canPullUp = isCanPullUp();
                    break;
                }
                // 计算手指移动的距离
                float nowY = ev.getY();
                int deltaY = (int) (nowY - startY);
                // 是否应该移动布局
                // 1.可以下拉，并且手指向下移动
                // 2.可以上拉，并且手指向上移动
                // 3.既可以上拉也可以下拉，当ScrollView包裹的控件比scrollView还小
                boolean shouldMove = (canPullDown && deltaY > 0) || (canPullUp && deltaY < 0) || (canPullDown && canPullUp);
                if (shouldMove) {
                    // 计算偏移量
                    boolean flag=false;
                    if (overScroll!=null)flag=overScroll.overScrolling(startY,deltaY,contentView,ev);
                    int offset = (int) (deltaY * MOVE_FACTOR);
                    if (!enablePullDown && offset > 0||
                            overScrollMode==OVER_SCROLL_TOP&&offset<0||
                            overScrollMode==OVER_SCROLL_BOTTOM&&offset> 0||
                            overScrollMode==OVER_SCROLL_NONE||
                            flag
                    )
                        break;
                    contentView.layout(originalRect.left, originalRect.top + offset, originalRect.right, originalRect.bottom + offset);
                    isMoved = true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判断是否滚动到顶部
     *
     * @return
     */
    private boolean isCanPullDown() {
        return getScrollY() == 0 || contentView.getHeight() < getHeight() + getScrollY();
    }

    /**
     * 判断是否滚动到底部
     */
    private boolean isCanPullUp() {
        return contentView.getHeight() <= getScrollY() + getHeight();
    }

    public void setOverScroll(OverScroll overScroll) {
        this.overScroll = overScroll;
    }
}