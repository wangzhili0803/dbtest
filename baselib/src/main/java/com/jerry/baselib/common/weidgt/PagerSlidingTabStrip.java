/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jerry.baselib.common.weidgt;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.jerry.baselib.R;
import com.jerry.baselib.common.util.DisplayUtil;

public class PagerSlidingTabStrip extends HorizontalScrollView {

	public OnPageChangeListener delegatePageListener;

	private LinearLayout.LayoutParams mParams;
	protected LinearLayout tabsContainer;
	protected List<TextView> textViews = new ArrayList<>();
	protected ViewPager viewPager;
	private Paint rectPaint;
	private RectF mStripBounds = new RectF();

	protected int tabCount;
	private int drawingPosition;
	private int currentPosition;
	private float currentPositionOffset;

	private int underlineColor = 0x1A000000;
	protected int selectColor = 0xff931111;
	private int defaultColor = 0xff8A8A8F;

	private int scrollOffset = 52;
	private int cornersRadius;
	private int indicatorHeight = 8;
	private int underlineHeight = 2;
	private int indicatorWidth = 12;
	private int tabPadding = 14;
	private int tabTextSize = 13;

	private int tabBackgroundResId = R.color.transparent;
	private int lastScrollX;
	private SmartIndicationInterpolator indicationInterpolator;

	private boolean tabShouldExpand;
	private boolean lineShouldExpand;

	public PagerSlidingTabStrip(Context context) {
		this(context, null);
	}

	public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setFillViewport(true);
		setWillNotDraw(false);

		tabsContainer = new LinearLayout(context);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(tabsContainer);

		DisplayMetrics dm = getResources().getDisplayMetrics();

		scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
		indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
		indicatorWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				indicatorWidth, dm);
		underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
		tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);
		tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabPadding, dm);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

		underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
		selectColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsSelectedColor, selectColor);
		defaultColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDefaultColor, defaultColor);
		indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight);
		indicatorWidth = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorWidth, indicatorWidth);
		underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight);
		tabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground,
				tabBackgroundResId);
		tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPadding);
		scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset);
		tabShouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, tabShouldExpand);
		lineShouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsUnderlineExpand, lineShouldExpand);

		a.recycle();

		rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setStyle(Style.FILL);

		if (tabShouldExpand) {
			mParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
		} else {
			mParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		}

		if (lineShouldExpand) {
			indicationInterpolator = new SmartIndicationInterpolator();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			this.setOutlineProvider(new ViewOutlineProvider() {
				@Override
				public void getOutline(View view, Outline outline) {
					outline.setRect(0, DisplayUtil.dip2px(8), view.getWidth(), view.getHeight());
				}
			});
		}
	}

    public void setViewPager(ViewPager pager) {
        this.viewPager = pager;
        viewPager.addOnPageChangeListener(new PageListener());
		notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

	/**
	 * 动态加载数据的页面调用
	 */
	public void notifyDataSetChanged() {
        if (viewPager == null) {
            return;
        }
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter == null) {
            return;
        }
        textViews.clear();
        tabsContainer.removeAllViews();
        tabCount = pagerAdapter.getCount();
        viewPager.setOffscreenPageLimit(tabCount);
        for (int i = 0; i < tabCount; i++) {
			addTextTab(i, pagerAdapter.getPageTitle(i));
        }
        updateTabStyles();
		changeTabTextColor(currentPosition);
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                currentPosition = viewPager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });
	}

	private void addTextTab(final int position, CharSequence title) {
		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setGravity(Gravity.CENTER);
		tab.setSingleLine();
		textViews.add(tab);
		addTab(position, tab);
	}

	protected void addTab(final int position, View tab) {
		tab.setFocusable(true);
		tab.setOnClickListener(view -> setCurrentItem(position));
		if (!tabShouldExpand) {
			tab.setPadding(tabPadding, 0, tabPadding, 0);
		}
		tabsContainer.addView(tab, position, mParams);
	}

	public View getTab(int i) {
		return tabsContainer.getChildAt(i);
	}

    /**
     * 设置选中和未选中的样式
     */
	private void updateTabStyles() {
		for (int i = 0; i < tabCount; i++) {
			View v = tabsContainer.getChildAt(i);
			v.setBackgroundResource(tabBackgroundResId);
			if (v instanceof TextView) {
				TextView tab = (TextView) v;
				tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
				tab.setTextColor(defaultColor);
			}
		}
	}

	public void setTabTextColor(int selectColor, int defaultColor) {
		this.selectColor = ContextCompat.getColor(getContext(), selectColor);
		this.defaultColor = ContextCompat.getColor(getContext(), defaultColor);
	}

    /**
     * 含有排序tab的样式
     */
    private void changeTabTextColor(int position) {
        if (textViews == null || textViews.size() == 0 || position > textViews.size()) {
			return;
		}
		for (int i = 0; i < tabCount; i++) {
			// TextView tab = (TextView) tabsContainer.getChildAt(i);
			textViews.get(i).setTextColor(i == position ? selectColor : defaultColor);
		}
	}

    public int getCurrentItem() {
        return currentPosition;
    }

    public void setCurrentItem(int position) {
        if (position == currentPosition) {
            return;
        }
        currentPosition = position;
        if (tabCount > position) {
            scrollToChild(position, tabsContainer.getChildAt(position).getWidth());
        }
        changeTabTextColor(position);
        viewPager.setCurrentItem(position);
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isInEditMode() || tabCount == 0) {
			return;
		}

		final int height = getHeight();

		// draw underline
		rectPaint.setColor(underlineColor);
		canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

		// draw indicator line
		rectPaint.setColor(selectColor);

		// default: line below current tab
		View currentTab = tabsContainer.getChildAt(drawingPosition);
		float left = currentTab.getLeft();
		float right = currentTab.getRight();
		float padding;
		if (lineShouldExpand) {
			padding = (Math.abs(left - right) - indicatorWidth) / 2f;
			left += padding;
			right -= padding;
		}

		View mNextTab;
		// if there is an offset, start interpolating left and right coordinates between current and next tab

		if (currentPositionOffset > 0f && drawingPosition < tabCount - 1) {
			if (lineShouldExpand) {
				float startOffset = indicationInterpolator.getLeftEdge(currentPositionOffset);
				float endOffset = indicationInterpolator.getRightEdge(currentPositionOffset);
				mNextTab = tabsContainer.getChildAt(drawingPosition + 1);
				padding = (Math.abs(mNextTab.getLeft() - mNextTab.getRight()) - indicatorWidth) /
						2f;
				float nextStart = mNextTab.getLeft() + padding;
				float nextEnd = mNextTab.getRight() - padding;
				left = (int) (startOffset * nextStart + (1.0f - startOffset) * left);
				right = (int) (endOffset * nextEnd + (1.0f - endOffset) * right);
			} else {
				mNextTab = tabsContainer.getChildAt(drawingPosition + 1);
				final float nextTabLeft = mNextTab.getLeft();
				final float nextTabRight = mNextTab.getRight();
				left = currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * left;
				right = currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * right;
			}
		}
		mStripBounds.set(left, height - indicatorHeight, right, height);
		if (cornersRadius == 0) {
			canvas.drawRect(mStripBounds, rectPaint);
		} else {
			canvas.drawRoundRect(mStripBounds, cornersRadius, cornersRadius, rectPaint);
		}
	}

	protected void scrollToChild(int position, int offset) {
		if (tabCount == 0) {
			return;
		}
		int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;
		if (position > 0 || offset > 0) {
			newScrollX -= scrollOffset;
		}
		if (newScrollX != lastScrollX) {
			lastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}
	}

	private class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			drawingPosition = position;
			currentPositionOffset = positionOffset;
			if (tabCount > position) {
				scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));
			}
			invalidate();
			if (delegatePageListener != null) {
				delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				scrollToChild(viewPager.getCurrentItem(), 0);
			}
			if (delegatePageListener != null) {
				delegatePageListener.onPageScrollStateChanged(state);
			}
		}

		@Override
		public void onPageSelected(int position) {
			if (delegatePageListener != null) {
				delegatePageListener.onPageSelected(position);
			}
			changeTabTextColor(position);
		}

	}

	public void setIndicatorHeight(int indicatorLineHeightPx) {
		this.indicatorHeight = indicatorLineHeightPx;
		invalidate();
	}

	public void setIndicatorWidth(int indicatorWidth) {
		this.indicatorWidth = indicatorWidth;
		invalidate();
	}

    public void setUnderlineExpand(boolean lineShouldExpand) {
        this.lineShouldExpand = lineShouldExpand;
        if (indicationInterpolator == null) {
            indicationInterpolator = new SmartIndicationInterpolator();
        }
        invalidate();
    }

	public void setUnderlineColor(int underlineColor) {
		this.underlineColor = underlineColor;
		invalidate();
	}

	public void setUnderlineHeight(int underlineHeightPx) {
		this.underlineHeight = underlineHeightPx;
		invalidate();
	}

	public void setTextSize(int textSizePx) {
		this.tabTextSize = textSizePx;
		invalidate();
	}

	public int getTextSize() {
		return tabTextSize;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		currentPosition = savedState.currentPosition;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPosition = currentPosition;
		return savedState;
	}

	static class SavedState extends BaseSavedState {

		int currentPosition;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPosition = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPosition);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	private static class SmartIndicationInterpolator {

		private static final float DEFAULT_INDICATOR_INTERPOLATION_FACTOR = 3.0f;

		private final Interpolator leftEdgeInterpolator;
		private final Interpolator rightEdgeInterpolator;

		SmartIndicationInterpolator() {
			this(DEFAULT_INDICATOR_INTERPOLATION_FACTOR);
		}

		SmartIndicationInterpolator(float factor) {
			leftEdgeInterpolator = new AccelerateInterpolator(factor);
			rightEdgeInterpolator = new DecelerateInterpolator(factor);
		}

		float getLeftEdge(float offset) {
			return leftEdgeInterpolator.getInterpolation(offset);
		}

		float getRightEdge(float offset) {
			return rightEdgeInterpolator.getInterpolation(offset);
		}

	}

	public interface OnChangeListener {

		void onArrowChange(int position, int sort);
	}
}