package com.jerry.baselib.common.weidgt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jerry.baselib.R;
import com.jerry.baselib.common.util.DisplayUtil;
import com.jerry.baselib.common.util.ParseUtil;

import java.util.Locale;

public class NumberPicker extends LinearLayout {

    /**
     * The number of items show in the selector wheel.
     */
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 5;

    /**
     * The default update interval during long press.
     */
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;

    /**
     * The index of the middle selector item.
     */
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = SELECTOR_WHEEL_ITEM_COUNT / 2;

    /**
     * The coefficient by which to adjust (divide) the max fling velocity.
     */
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;

    /**
     * The the duration for adjusting the selector wheel.
     */
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;

    /**
     * The duration of scrolling while snapping to a given position.
     */
    private static final int SNAP_SCROLL_DURATION = 300;
    /**
     * The strength of fading in the top and bottom while drawing the selector.
     */
    private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;

    /**
     * Constant for unspecified size.
     */
    private static final int SIZE_UNSPECIFIED = -1;
    /**
     * The text for showing the current value.
     */
    private final TextView mInputText;
    /**
     * The distance between the two selection dividers.
     */
    private final int mSelectionDividersDistance;
    /**
     * The min height of this widget.
     */
    private final int mMinHeight;
    /**
     * The max height of this widget.
     */
    private final int mMaxHeight;
    /**
     * The max width of this widget.
     */
    private final int mMinWidth;
    /**
     * Flag whether to compute the max width.
     */
    private final boolean mComputeMaxWidth;
    /**
     * The height of the text.
     */
    private final int mTextSize;
    /**
     * Cache for the string representation of selector indices.
     */
    private final SparseArray<String> mSelectorIndexToStringCache = new SparseArray<>();
    /**
     * The selector indices whose value are show by the selector.
     */
    private final int[] mSelectorIndices = new int[SELECTOR_WHEEL_ITEM_COUNT];
    /**
     * The {@link Paint} for drawing the selector.
     */
    private final Paint mSelectorWheelPaint;
    /**
     * The {@link Drawable} for pressed virtual (increment/decrement) buttons.
     */
    private final Drawable mVirtualButtonPressedDrawable;
    /**
     * The {@link PickerScroller} responsible for flinging the selector.
     */
    private final PickerScroller mFlingScroller;
    /**
     * The {@link PickerScroller} responsible for adjusting the selector.
     */
    private final PickerScroller mAdjustScroller;
    /**
     * The back ground color used to optimize scroller fading.
     */
    private final int mSolidColor;
    /**
     * Divider for showing item to be selected while scrolling
     */
    private final Drawable mSelectionDivider;
    /**
     * The height of the selection divider.
     */
    private final int mSelectionDividerHeight;
    /**
     * Helper class for managing pressed state of the virtual buttons.
     */
    private final PressedStateHelper mPressedStateHelper;
    /**
     * The max width of this widget.
     */
    private int mMaxWidth;
    /**
     * The height of the gap between text elements if the selector wheel.
     */
    private int mSelectorTextGapHeight;
    /**
     * The values to be displayed instead the indices.
     */
    private String[] mDisplayedValues;
    /**
     * Lower value of the range of numbers allowed for the NumberPicker
     */
    private int mMinValue;
    /**
     * Upper value of the range of numbers allowed for the NumberPicker
     */
    private int mMaxValue;
    /**
     * Current value of this NumberPicker
     */
    private int mValue;
    /**
     * Listener to be notified upon current value change.
     */
    private OnValueChangeListener mOnValueChangeListener;
    /**
     * Listener to be notified upon scroll state change.
     */
    private OnScrollListener mOnScrollListener;
    /**
     * Formatter for for displaying the current value.
     */
    private Formatter mFormatter;
    /**
     * The height of a selector element (text + gap).
     */
    private int mSelectorElementHeight;
    /**
     * The initial offset of the scroll selector.
     */
    private int mInitialScrollOffset = Integer.MIN_VALUE;
    /**
     * The current offset of the scroll selector.
     */
    private int mCurrentScrollOffset;
    /**
     * The previous Y coordinate while scrolling the selector.
     */
    private int mPreviousScrollerY;
    /**
     * Handle to the reusable command for changing the current value from long press by one.
     */
    private ChangeCurrentByOneFromLongPressCommand mChangeCurrentByOneFromLongPressCommand;
    /**
     * The Y position of the last down event.
     */
    private float mLastDownEventY;
    /**
     * The Y position of the last down or move event.
     */
    private float mLastDownOrMoveEventY;
    /**
     * Determines speed during touch scrolling.
     */
    private VelocityTracker mVelocityTracker;
    /**
     * @see ViewConfiguration#getScaledTouchSlop()
     */
    private int mTouchSlop;
    /**
     * @see ViewConfiguration#getScaledMinimumFlingVelocity()
     */
    private int mMinimumFlingVelocity;
    /**
     * @see ViewConfiguration#getScaledMaximumFlingVelocity()
     */
    private int mMaximumFlingVelocity;
    /**
     * Flag whether the selector should wrap around.
     */
    private boolean mWrapSelectorWheel;
    /**
     * The current scroll state of the number picker.
     */
    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    /**
     * Flag whether to ignore move events - we ignore such when we show in IME to prevent the content from scrolling.
     */
    private boolean mIngonreMoveEvents;
    /**
     * Flag whether to show soft input on tap.
     */
    private boolean mShowSoftInputOnTap;
    /**
     * The top of the top selection divider.
     */
    private int mTopSelectionDividerTop;
    /**
     * The bottom of the bottom selection divider.
     */
    private int mBottomSelectionDividerBottom;
    /**
     * Whether the increment virtual button is pressed.
     */
    private boolean mIncrementVirtualButtonPressed;
    /**
     * Whether the decrement virtual button is pressed.
     */
    private boolean mDecrementVirtualButtonPressed;
    /**
     * The keycode of the last handled DPAD down event.
     */
    private int mLastHandledDownDpadKeyCode = -1;
    /**
     * The initial select value
     */
    private int currentIndex;

    public NumberPicker(Context context) {
        this(context, null);
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setFadingEdgeLength(DisplayUtil.dip2px(30));
        // process style attributes
        TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.NumberPicker, defStyle, 0);
        final int layoutResId = attributesArray.getResourceId(R.styleable.NumberPicker_internalLayout, R.layout.number_picker_with_selector_wheel);

        mSolidColor = attributesArray.getColor(R.styleable.NumberPicker_solidColor, ContextCompat.getColor(context, R.color.transparent));

        mSelectionDivider = ContextCompat.getDrawable(context, R.drawable.np_numberpicker_selection_divider);

        mSelectionDividerHeight = attributesArray.getDimensionPixelSize(R.styleable.NumberPicker_selectionDividerHeight, DisplayUtil.dip2px(1));

        mSelectionDividersDistance = attributesArray
            .getDimensionPixelSize(R.styleable.NumberPicker_selectionDividersDistance, DisplayUtil.dip2px(35));

        mMinHeight = attributesArray.getDimensionPixelSize(R.styleable.NumberPicker_internalMinHeight, SIZE_UNSPECIFIED);

        mMaxHeight = attributesArray.getDimensionPixelSize(R.styleable.NumberPicker_internalMaxHeight, DisplayUtil.dip2px(180));
        if (mMinHeight != SIZE_UNSPECIFIED && mMaxHeight != SIZE_UNSPECIFIED && mMinHeight > mMaxHeight) {
            throw new IllegalArgumentException("minHeight > maxHeight");
        }

        mMinWidth = attributesArray.getDimensionPixelSize(R.styleable.NumberPicker_internalMinWidth, DisplayUtil.dip2px(110));
        mMaxWidth = attributesArray.getDimensionPixelSize(R.styleable.NumberPicker_internalMaxWidth, SIZE_UNSPECIFIED);
        if (mMinWidth != SIZE_UNSPECIFIED && mMaxWidth != SIZE_UNSPECIFIED && mMinWidth > mMaxWidth) {
            throw new IllegalArgumentException("minWidth > maxWidth");
        }

        mComputeMaxWidth = mMaxWidth == SIZE_UNSPECIFIED;

        mVirtualButtonPressedDrawable = ContextCompat.getDrawable(context, R.drawable.transparent_drawable);

        attributesArray.recycle();

        mPressedStateHelper = new PressedStateHelper();

        setWillNotDraw(false);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutResId, this, true);

        // input text
        mInputText = findViewById(R.id.np_numberpicker_input);
        mInputText.setTextColor(Color.parseColor("#333333"));
        mInputText.setRawInputType(InputType.TYPE_CLASS_NUMBER);// mInputText.setFilters(new InputFilter[] { new InputTextFilter() });

        // initialize constants
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity() / SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT;
        mTextSize = (int) mInputText.getTextSize();

        // create the selector wheel paint
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(mTextSize);
        paint.setTypeface(mInputText.getTypeface());
        ColorStateList colors = mInputText.getTextColors();
        int color = colors.getColorForState(ENABLED_STATE_SET, Color.WHITE);
        paint.setColor(color);
        mSelectorWheelPaint = paint;

        // create the fling and adjust scrollers
        mFlingScroller = new PickerScroller(getContext(), null, true);
        mAdjustScroller = new PickerScroller(getContext(), new DecelerateInterpolator(2.5f));

        updateInputTextView();
    }

    /**
     * Utility to reconcile a desired size and state, with constraints imposed by a MeasureSpec. Will take the desired size, unless a different size
     * is imposed by the constraints. The returned value is a compound integer, with the resolved size in the {@link #MEASURED_SIZE_MASK} bits and
     * optionally the bit {@link #MEASURED_STATE_TOO_SMALL} set if the resulting size is smaller than the size the view wants to be.
     *
     * @param size How big the view wants to be
     * @param measureSpec Constraints imposed by the parent
     * @return Size information bit mask as defined by {@link #MEASURED_SIZE_MASK} and {@link #MEASURED_STATE_TOO_SMALL}.
     */
    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            default:
                break;
        }
        return result | (childMeasuredState & MEASURED_STATE_MASK);
    }

    private static String formatNumberWithLocale(int value) {
        return String.format(Locale.getDefault(), "%d", value);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int msrdWdth = getMeasuredWidth();
        final int msrdHght = getMeasuredHeight();

        // Input text centered horizontally.
        final int inptTxtMsrdWdth = mInputText.getMeasuredWidth();
        final int inptTxtMsrdHght = mInputText.getMeasuredHeight();
        final int inptTxtLeft = (msrdWdth - inptTxtMsrdWdth) / 2;
        final int inptTxtTop = (msrdHght - inptTxtMsrdHght) / 2;
        final int inptTxtRight = inptTxtLeft + inptTxtMsrdWdth;
        final int inptTxtBottom = inptTxtTop + inptTxtMsrdHght;
        mInputText.layout(inptTxtLeft, inptTxtTop, inptTxtRight, inptTxtBottom);

        if (changed) {
            // need to do all this when we know our size
            initializeSelectorWheel();
            initializeFadingEdges();
            mTopSelectionDividerTop = (getHeight() - mSelectionDividersDistance) / 2 - mSelectionDividerHeight;
            mBottomSelectionDividerBottom = mTopSelectionDividerTop + 2 * mSelectionDividerHeight + mSelectionDividersDistance;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try greedily to fit the max width and height.
        final int newWidthMeasureSpec = makeMeasureSpec(widthMeasureSpec, mMaxWidth);
        final int newHeightMeasureSpec = makeMeasureSpec(heightMeasureSpec, mMaxHeight);
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
        // Flag if we are measured with width or height less than the respective min.
        // min.
        final int widthSize = resolveSizeAndStateRespectingMinSize(mMinWidth, getMeasuredWidth(), widthMeasureSpec);
        final int heightSize = resolveSizeAndStateRespectingMinSize(mMinHeight, getMeasuredHeight(), heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * Move to the final position of a scroller. Ensures to force finish the scroller and if it is not at its final position a scroll of the selector
     * wheel is performed to fast forward to the final position.
     *
     * @param pickerScroller The scroller to whose final position to get.
     * @return True of the a move was performed, i.e. the scroller was not in final position.
     */
    private boolean moveToFinalScrollerPosition(PickerScroller pickerScroller) {
        pickerScroller.forceFinished(true);
        int amountToScroll = pickerScroller.getFinalY() - pickerScroller.getCurrY();
        int futureScrollOffset = (mCurrentScrollOffset + amountToScroll) % mSelectorElementHeight;
        int overshootAdjustment = mInitialScrollOffset - futureScrollOffset;
        if (overshootAdjustment != 0) {
            if (Math.abs(overshootAdjustment) > mSelectorElementHeight / 2) {
                if (overshootAdjustment > 0) {
                    overshootAdjustment -= mSelectorElementHeight;
                } else {
                    overshootAdjustment += mSelectorElementHeight;
                }
            }
            amountToScroll += overshootAdjustment;
            scrollBy(0, amountToScroll);
            return true;
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                removeAllCallbacks();
                mInputText.setVisibility(View.INVISIBLE);
                mLastDownOrMoveEventY = mLastDownEventY = event.getY();
                mIngonreMoveEvents = false;
                mShowSoftInputOnTap = false;
                // Handle pressed state before any state change.
                if (mLastDownEventY < mTopSelectionDividerTop) {
                    if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                        mPressedStateHelper.buttonPressDelayed(PressedStateHelper.BUTTON_DECREMENT);
                    }
                } else if (mLastDownEventY > mBottomSelectionDividerBottom) {
                    if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                        mPressedStateHelper.buttonPressDelayed(PressedStateHelper.BUTTON_INCREMENT);
                    }
                }
                // Make sure we support flinging inside scrollables.
                getParent().requestDisallowInterceptTouchEvent(true);
                if (!mFlingScroller.isFinished()) {
                    mFlingScroller.forceFinished(true);
                    mAdjustScroller.forceFinished(true);
                    onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
                } else if (!mAdjustScroller.isFinished()) {
                    mFlingScroller.forceFinished(true);
                    mAdjustScroller.forceFinished(true);
                } else if (mLastDownEventY < mTopSelectionDividerTop) {
                    postChangeCurrentByOneFromLongPress(false, ViewConfiguration.getLongPressTimeout());
                } else if (mLastDownEventY > mBottomSelectionDividerBottom) {
                    postChangeCurrentByOneFromLongPress(true, ViewConfiguration.getLongPressTimeout());
                }
                return true;
            }
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                if (mIngonreMoveEvents) {
                    break;
                }
                float currentMoveY = event.getY();
                if (mScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    int deltaDownY = (int) Math.abs(currentMoveY - mLastDownEventY);
                    if (deltaDownY > mTouchSlop) {
                        removeAllCallbacks();
                        onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                    }
                } else {
                    int deltaMoveY = (int) ((currentMoveY - mLastDownOrMoveEventY));
                    scrollBy(0, deltaMoveY);
                    invalidate();
                }
                mLastDownOrMoveEventY = currentMoveY;
            }
            break;
            case MotionEvent.ACTION_UP: {
                removeChangeCurrentByOneFromLongPress();
                mPressedStateHelper.cancel();
                VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(initialVelocity) > mMinimumFlingVelocity) {
                    fling(initialVelocity);
                    onScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
                } else {
                    int eventY = (int) event.getY();
                    int deltaMoveY = (int) Math.abs(eventY - mLastDownEventY);
                    if (deltaMoveY <= mTouchSlop) { // && deltaTime <
                        // ViewConfiguration.getTapTimeout())
                        // {
                        if (mShowSoftInputOnTap) {
                            mShowSoftInputOnTap = false;
                        } else {
                            int selectorIndexOffset = (eventY / mSelectorElementHeight) - SELECTOR_MIDDLE_ITEM_INDEX;
                            if (selectorIndexOffset > 0) {
                                changeValueByOne(true);
                                mPressedStateHelper.buttonTapped(PressedStateHelper.BUTTON_INCREMENT);
                            } else if (selectorIndexOffset < 0) {
                                changeValueByOne(false);
                                mPressedStateHelper.buttonTapped(PressedStateHelper.BUTTON_DECREMENT);
                            }
                        }
                    } else {
                        ensureScrollWheelAdjusted();
                    }
                    if (mOnValueChangeListener != null) {
                        mOnValueChangeListener.onValueChange(this, SELECTOR_MIDDLE_ITEM_INDEX, mValue);
                    }
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                removeAllCallbacks();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                removeAllCallbacks();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:
                        if (mWrapSelectorWheel || (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) ? getValue() < getMaxValue() : getValue() > getMinValue()) {
                            requestFocus();
                            mLastHandledDownDpadKeyCode = keyCode;
                            removeAllCallbacks();
                            if (mFlingScroller.isFinished()) {
                                changeValueByOne(keyCode == KeyEvent.KEYCODE_DPAD_DOWN);
                            }
                            return true;
                        }
                        break;
                    case KeyEvent.ACTION_UP:
                        if (mLastHandledDownDpadKeyCode == keyCode) {
                            mLastHandledDownDpadKeyCode = -1;
                            return true;
                        }
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                removeAllCallbacks();
                break;
            default:
                break;
        }
        return super.dispatchTrackballEvent(event);
    }

    @Override
    public void computeScroll() {
        PickerScroller pickerScroller = mFlingScroller;
        if (pickerScroller.isFinished()) {
            pickerScroller = mAdjustScroller;
            if (pickerScroller.isFinished()) {
                return;
            }
        }
        pickerScroller.computeScrollOffset();
        int currentScrollerY = pickerScroller.getCurrY();
        if (mPreviousScrollerY == 0) {
            mPreviousScrollerY = pickerScroller.getStartY();
        }
        scrollBy(0, currentScrollerY - mPreviousScrollerY);
        mPreviousScrollerY = currentScrollerY;
        if (pickerScroller.isFinished()) {
            onScrollerFinished(pickerScroller);
        } else {
            invalidate();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mInputText.setEnabled(enabled);
    }

    @Override
    public void scrollBy(int x, int y) {
        int[] selectorIndices = mSelectorIndices;
        if (!mWrapSelectorWheel && y > 0 && selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] <= mMinValue) {
            mCurrentScrollOffset = mInitialScrollOffset;
            return;
        }
        if (!mWrapSelectorWheel && y < 0 && selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] >= mMaxValue) {
            mCurrentScrollOffset = mInitialScrollOffset;
            return;
        }
        mCurrentScrollOffset += y;
        while (mCurrentScrollOffset - mInitialScrollOffset > mSelectorTextGapHeight) {
            mCurrentScrollOffset -= mSelectorElementHeight;
            decrementSelectorIndices(selectorIndices);
            setValueInternal(selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX], false);
            if (!mWrapSelectorWheel && selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] <= mMinValue) {
                mCurrentScrollOffset = mInitialScrollOffset;
            }
        }
        while (mCurrentScrollOffset - mInitialScrollOffset < -mSelectorTextGapHeight) {
            mCurrentScrollOffset += mSelectorElementHeight;
            incrementSelectorIndices(selectorIndices);
            setValueInternal(selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX], false);
            if (!mWrapSelectorWheel && selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] >= mMaxValue) {
                mCurrentScrollOffset = mInitialScrollOffset;
            }
        }
    }

    @Override
    public int getSolidColor() {
        return mSolidColor;
    }

    /**
     * Sets the listener to be notified on change of the current value.
     *
     * @param onValueChangedListener The listener.
     */
    public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
        mOnValueChangeListener = onValueChangedListener;
    }

    /**
     * Set listener to be notified for scroll state changes.
     *
     * @param onScrollListener The listener.
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    /**
     * Set the formatter to be used for formatting the current value.
     * <p>
     * Note: If you have provided alternative values for the values this formatter is never invoked.
     * </p>
     *
     * @param formatter The formatter object. If formatter is <code>null</code>, {@link String#valueOf(int)} will be used.
     * @see #setDisplayedValues(String[])
     */
    public void setFormatter(Formatter formatter) {
        if (formatter == mFormatter) {
            return;
        }
        mFormatter = formatter;
        initializeSelectorWheelIndices();
        updateInputTextView();
    }

    /**
     * Computes the max width if no such specified as an attribute.
     */
    private void tryComputeMaxWidth() {
        if (!mComputeMaxWidth) {
            return;
        }
        int maxTextWidth = 0;
        if (mDisplayedValues == null) {
            float maxDigitWidth = 0;
            for (int i = 0; i <= 9; i++) {
                final float digitWidth = mSelectorWheelPaint.measureText(formatNumberWithLocale(i));
                if (digitWidth > maxDigitWidth) {
                    maxDigitWidth = digitWidth;
                }
            }
            int numberOfDigits = 0;
            int current = mMaxValue;
            while (current > 0) {
                numberOfDigits++;
                current = current / 10;
            }
            maxTextWidth = (int) (numberOfDigits * maxDigitWidth);
        } else {
            for (String mDisplayedValue : mDisplayedValues) {
                final float textWidth = mSelectorWheelPaint.measureText(mDisplayedValue);
                if (textWidth > maxTextWidth) {
                    maxTextWidth = (int) textWidth;
                }
            }
        }
        maxTextWidth += mInputText.getPaddingLeft() + mInputText.getPaddingRight();
        if (mMaxWidth != maxTextWidth) {
            if (maxTextWidth > mMinWidth) {
                mMaxWidth = maxTextWidth;
            } else {
                mMaxWidth = mMinWidth;
            }
            invalidate();
        }
    }

    /**
     * Sets whether the selector wheel shown during flinging/scrolling should wrap around the {@link NumberPicker#getMinValue()} and {@link
     * NumberPicker#getMaxValue()} values. <p> By default if the range (max - min) is more than the number of items shown on the selector wheel the
     * selector wheel wrapping is enabled. </p> <p> <strong>Note:</strong> If the number of items, i.e. the range ( {@link #getMaxValue()} - {@link
     * #getMinValue()}) is less than the number of items shown on the selector wheel, the selector wheel will not wrap. Hence, in such a case calling
     * this method is a NOP. </p>
     *
     * @param wrapSelectorWheel Whether to wrap.
     */
    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        final boolean wrappingAllowed = (mMaxValue - mMinValue) >= mSelectorIndices.length;
        if ((!wrapSelectorWheel || wrappingAllowed) && wrapSelectorWheel != mWrapSelectorWheel) {
            mWrapSelectorWheel = wrapSelectorWheel;
        }
    }

    /**
     * Returns the value of the picker.
     *
     * @return The value.
     */
    public int getValue() {
        return mDisplayedValues == null ? mValue : ParseUtil.parseInt(mDisplayedValues[currentIndex]);
    }

    public void setValue(int value) {
        setValueInternal(value, true);
    }

    /**
     * Returns the min value of the picker.
     *
     * @return The min value
     */
    public int getMinValue() {
        return mMinValue;
    }

    /**
     * Sets the min value of the picker.
     *
     * @param minValue The min value inclusive.
     * <p/>
     * <strong>Note:</strong> The length of the displayed values array set via {@link #setDisplayedValues(String[])} must be equal to the range of
     * selectable numbers which is equal to {@link #getMaxValue()} - {@link #getMinValue()} + 1.
     */
    public void setMinValue(int minValue) {
        if (mMinValue == minValue) {
            return;
        }
        if (minValue < 0) {
            throw new IllegalArgumentException("minValue must be >= 0");
        }
        mMinValue = minValue;
        if (mMinValue > mValue) {
            mValue = mMinValue;
        }
        initializeSelectorWheelIndices();
        updateInputTextView();
        tryComputeMaxWidth();
        invalidate();
    }

    /**
     * Returns the max value of the picker.
     *
     * @return The max value.
     */
    public int getMaxValue() {
        return mMaxValue;
    }

    /**
     * Sets the max value of the picker.
     *
     * @param maxValue The max value inclusive.
     * <p/>
     * <strong>Note:</strong> The length of the displayed values array set via {@link #setDisplayedValues(String[])} must be equal to the range of
     * selectable numbers which is equal to {@link #getMaxValue()} - {@link #getMinValue()} + 1.
     */
    public void setMaxValue(int maxValue) {
        if (mMaxValue == maxValue) {
            return;
        }
        if (maxValue < 0) {
            throw new IllegalArgumentException("maxValue must be >= 0");
        }
        mMaxValue = maxValue;
        if (mMaxValue < mValue) {
            mValue = mMaxValue;
        }
        initializeSelectorWheelIndices();
        updateInputTextView();
        tryComputeMaxWidth();
        invalidate();
    }

    /**
     * Sets the values to be displayed.
     *
     * @param displayedValues The displayed values.
     * <p/>
     * <strong>Note:</strong> The length of the displayed values array must be equal to the range of selectable numbers which is equal to {@link
     * #getMaxValue()} - {@link #getMinValue()} + 1.
     */
    public void setDisplayedValues(String[] displayedValues) {
        if (mDisplayedValues == displayedValues) {
            return;
        }
        mDisplayedValues = displayedValues;
        if (mDisplayedValues != null) {
            // Allow text entry rather than strictly numeric entry.
            mInputText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        } else {
            mInputText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        }
        updateInputTextView();
        initializeSelectorWheelIndices();
        tryComputeMaxWidth();
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    @Override
    protected void onDetachedFromWindow() {
        removeAllCallbacks();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float x = (getRight() - getLeft()) / 2;
        float y = mCurrentScrollOffset;

        // draw the virtual buttons pressed state if needed
        if (mVirtualButtonPressedDrawable != null && mScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            if (mDecrementVirtualButtonPressed) {
                // mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
                mVirtualButtonPressedDrawable.setState(PRESSED_ENABLED_STATE_SET);
                mVirtualButtonPressedDrawable.setBounds(0, 0, getRight(), mTopSelectionDividerTop);
                mVirtualButtonPressedDrawable.draw(canvas);
            }
            if (mIncrementVirtualButtonPressed) {
                // mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
                mVirtualButtonPressedDrawable.setState(PRESSED_ENABLED_STATE_SET);
                mVirtualButtonPressedDrawable.setBounds(0, mBottomSelectionDividerBottom, getRight(), getBottom());
                mVirtualButtonPressedDrawable.draw(canvas);
            }
        }

        // draw the selector wheel
        int[] selectorIndices = mSelectorIndices;
        for (int i = 0; i < selectorIndices.length; i++) {
            int selectorIndex = selectorIndices[i];
            String scrollSelectorValue = mSelectorIndexToStringCache.get(selectorIndex);
            // Do not draw the middle item if input is visible since the input
            // is shown only if the wheel is static and it covers the middle
            // item. Otherwise, if the user starts editing the text via the
            // IME he may see a dimmed version of the old value intermixed
            // with the new one.
            if (i != SELECTOR_MIDDLE_ITEM_INDEX || mInputText.getVisibility() != VISIBLE) {
                if (scrollSelectorValue != null) {
                    canvas.drawText(scrollSelectorValue, x, y, mSelectorWheelPaint);
                }
            }
            y += mSelectorElementHeight;
        }

        // draw the selection dividers
        if (mSelectionDivider != null) {
            // draw the top divider
            int topOfTopDivider = mTopSelectionDividerTop;
            int bottomOfTopDivider = topOfTopDivider + mSelectionDividerHeight;
            mSelectionDivider.setBounds(0, topOfTopDivider, getRight(), bottomOfTopDivider);
            mSelectionDivider.draw(canvas);

            // draw the bottom divider
            int bottomOfBottomDivider = mBottomSelectionDividerBottom;
            int topOfBottomDivider = bottomOfBottomDivider - mSelectionDividerHeight;
            mSelectionDivider.setBounds(0, topOfBottomDivider, getRight(), bottomOfBottomDivider);
            mSelectionDivider.draw(canvas);
        }
    }

    /**
     * Makes a measure spec that tries greedily to use the max value.
     *
     * @param measureSpec The measure spec.
     * @param maxSize The max value for the size.
     * @return A measure spec greedily imposing the max size.
     */
    private int makeMeasureSpec(int measureSpec, int maxSize) {
        if (maxSize == SIZE_UNSPECIFIED) {
            return measureSpec;
        }
        final int size = MeasureSpec.getSize(measureSpec);
        final int mode = MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return measureSpec;
            case MeasureSpec.AT_MOST:
                return MeasureSpec.makeMeasureSpec(Math.min(size, maxSize), MeasureSpec.EXACTLY);
            case MeasureSpec.UNSPECIFIED:
                return MeasureSpec.makeMeasureSpec(maxSize, MeasureSpec.EXACTLY);
            default:
                throw new IllegalArgumentException("Unknown measure mode: " + mode);
        }
    }

    /**
     * Utility to reconcile a desired size and state, with constraints imposed by a MeasureSpec. Tries to respect the min size, unless a different
     * size is imposed by the constraints.
     *
     * @param minSize The minimal desired size.
     * @param measuredSize The currently measured size.
     * @param measureSpec The current measure spec.
     * @return The resolved size and state.
     */
    @SuppressLint("NewApi")
    private int resolveSizeAndStateRespectingMinSize(int minSize, int measuredSize, int measureSpec) {
        if (minSize != SIZE_UNSPECIFIED) {
            final int desiredWidth = Math.max(minSize, measuredSize);
            return resolveSizeAndState(desiredWidth, measureSpec, 0);
        } else {
            return measuredSize;
        }
    }

    /**
     * Resets the selector indices and clearFile the cached string representation of these indices.
     */
    private void initializeSelectorWheelIndices() {
        mSelectorIndexToStringCache.clear();
        int[] selectorIndices = mSelectorIndices;
        int current = getValue();
        int selectorIndex;
        if (mDisplayedValues == null) {
            for (int i = 0; i < mSelectorIndices.length; i++) {
                selectorIndex = current + (i - SELECTOR_MIDDLE_ITEM_INDEX);
                if (mWrapSelectorWheel) {
                    selectorIndex = getWrappedSelectorIndex(selectorIndex);
                }
                selectorIndices[i] = selectorIndex;
                ensureCachedScrollSelectorValue(selectorIndices[i]);
            }
        } else {
            initializeSelectorWheelIndicesOfDisplayValues(current);
        }
    }

    private void initializeSelectorWheelIndicesOfDisplayValues(int current) {
        int selectorIndex;
        int length = mDisplayedValues.length;
        for (int i = 0; i < length; i++) {
            if (mDisplayedValues[i].equals(String.valueOf(current))) {
                currentIndex = i;
                break;
            }
        }
        for (int i = 0, j = mSelectorIndices.length; i < j; i++) {
            int index = currentIndex + (i - SELECTOR_MIDDLE_ITEM_INDEX);
            selectorIndex = index < 0 ? index + length : index < length ? index : index - length;
            mSelectorIndices[i] = selectorIndex;
            String scrollSelectorValue = mSelectorIndexToStringCache.get(selectorIndex);
            if (scrollSelectorValue != null) {
                return;
            }
            mSelectorIndexToStringCache.put(selectorIndex, mDisplayedValues[selectorIndex]);
        }
    }

    /**
     * Sets the current value of this NumberPicker.
     *  @param current The new value of the NumberPicker.
     */
    private void setValueInternal(int current, boolean focusUpdate) {
        if (mValue == current && !focusUpdate) {
            return;
        } else if (null != mDisplayedValues) {
            currentIndex = current;
        }
        // Wrap around the values if we go past the start or end
        if (mWrapSelectorWheel) {
            current = getWrappedSelectorIndex(current);
        } else {
            current = Math.max(current, mMinValue);
            current = Math.min(current, mMaxValue);
        }
        mValue = current;
        updateInputTextView();
        initializeSelectorWheelIndices();
        invalidate();
    }

    /**
     * Changes the current value by one which is increment or decrement based on the passes argument. decrement the current value.
     *
     * @param increment True to increment, false to decrement.
     */
    private void changeValueByOne(boolean increment) {
        mInputText.setVisibility(View.INVISIBLE);
        if (!moveToFinalScrollerPosition(mFlingScroller)) {
            moveToFinalScrollerPosition(mAdjustScroller);
        }
        mPreviousScrollerY = 0;
        if (increment) {
            mFlingScroller.startScroll(0, 0, 0, -mSelectorElementHeight, SNAP_SCROLL_DURATION);
        } else {
            mFlingScroller.startScroll(0, 0, 0, mSelectorElementHeight, SNAP_SCROLL_DURATION);
        }
        invalidate();
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] selectorIndices = mSelectorIndices;
        int totalTextHeight = selectorIndices.length * mTextSize;
        float totalTextGapHeight = (getBottom() - getTop()) - totalTextHeight;
        float textGapCount = selectorIndices.length;
        mSelectorTextGapHeight = (int) (totalTextGapHeight / textGapCount + 0.5f);
        mSelectorElementHeight = mTextSize + mSelectorTextGapHeight;
        // Ensure that the middle item is positioned the same as the text in
        // mInputText
        int editTextTextPosition = mInputText.getBaseline() + mInputText.getTop();
        mInitialScrollOffset = editTextTextPosition - (mSelectorElementHeight * SELECTOR_MIDDLE_ITEM_INDEX);
        mCurrentScrollOffset = mInitialScrollOffset;
        updateInputTextView();
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength((getBottom() - getTop() - mTextSize) * 4 / 5);
    }

    /**
     * Callback invoked upon completion of a given <code>scroller</code>.
     */
    private void onScrollerFinished(PickerScroller pickerScroller) {
        if (pickerScroller == mFlingScroller) {
            if (!ensureScrollWheelAdjusted()) {
                updateInputTextView();
            }
            onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
            if (mOnValueChangeListener != null) {
                mOnValueChangeListener.onValueChange(this, SELECTOR_MIDDLE_ITEM_INDEX, mValue);
            }
        } else {
            if (mScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                updateInputTextView();
            }
        }
    }

    /**
     * Handles transition to a given <code>scrollState</code>
     */
    private void onScrollStateChange(int scrollState) {
        if (mScrollState == scrollState) {
            return;
        }
        mScrollState = scrollState;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChange(this, scrollState);
        }
    }

    /**
     * Flings the selector with the given <code>velocityY</code>.
     */
    private void fling(int velocityY) {
        mPreviousScrollerY = 0;

        if (velocityY > 0) {
            mFlingScroller.fling(0, 0, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        } else {
            mFlingScroller.fling(0, Integer.MAX_VALUE, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        }

        invalidate();
    }

    /**
     * @return The wrapped index <code>selectorIndex</code> value.
     */
    private int getWrappedSelectorIndex(int selectorIndex) {
        if (selectorIndex > mMaxValue) {
            return mMinValue + (selectorIndex - mMaxValue) % (mMaxValue - mMinValue) - 1;
        } else if (selectorIndex < mMinValue) {
            return mMaxValue - (mMinValue - selectorIndex) % (mMaxValue - mMinValue) + 1;
        }
        return selectorIndex;
    }

    /**
     * Increments the <code>selectorIndices</code> whose string representations will be displayed in the selector.
     */
    private void incrementSelectorIndices(int[] selectorIndices) {
        System.arraycopy(selectorIndices, 1, selectorIndices, 0, selectorIndices.length - 1);
        int nextScrollSelectorIndex;
        if (mDisplayedValues == null) {
            nextScrollSelectorIndex = selectorIndices[selectorIndices.length - 2] + 1;
            if (mWrapSelectorWheel && nextScrollSelectorIndex > mMaxValue) {
                nextScrollSelectorIndex = mMinValue;
            }
            selectorIndices[selectorIndices.length - 1] = nextScrollSelectorIndex;
            ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
        } else {
            if (currentIndex == 22) {
                nextScrollSelectorIndex = 0;
            } else {
                nextScrollSelectorIndex = selectorIndices[3] + 1;
            }
            selectorIndices[4] = nextScrollSelectorIndex;
            mSelectorIndexToStringCache.put(nextScrollSelectorIndex, mDisplayedValues[nextScrollSelectorIndex]);
        }

    }

    /**
     * Decrements the <code>selectorIndices</code> whose string representations will be displayed in the selector.
     */
    private void decrementSelectorIndices(int[] selectorIndices) {
        System.arraycopy(selectorIndices, 0, selectorIndices, 1, selectorIndices.length - 1);
        int nextScrollSelectorIndex;
        if (mDisplayedValues == null) {
            nextScrollSelectorIndex = selectorIndices[1] - 1;
            if (mWrapSelectorWheel && nextScrollSelectorIndex < mMinValue) {
                nextScrollSelectorIndex = mMaxValue;
            }
            selectorIndices[0] = nextScrollSelectorIndex;
            ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
        } else {
            if (currentIndex == 2) {
                nextScrollSelectorIndex = 24;
            } else {
                nextScrollSelectorIndex = selectorIndices[1] - 1;
            }
            selectorIndices[0] = nextScrollSelectorIndex;
            mSelectorIndexToStringCache.put(nextScrollSelectorIndex, mDisplayedValues[nextScrollSelectorIndex]);
        }

    }

    /**
     * Ensures we have a cached string representation of the given <code>
     * selectorIndex</code> to avoid multiple instantiations of the same string.
     */
    private void ensureCachedScrollSelectorValue(int selectorIndex) {
        SparseArray<String> cache = mSelectorIndexToStringCache;
        String scrollSelectorValue = cache.get(selectorIndex);
        if (scrollSelectorValue != null) {
            return;
        }
        if (selectorIndex < mMinValue || selectorIndex > mMaxValue) {
            scrollSelectorValue = "";
        } else {
            scrollSelectorValue = formatNumber(selectorIndex);
        }
        cache.put(selectorIndex, scrollSelectorValue);
    }

    private String formatNumber(int value) {
        return (mFormatter != null) ? mFormatter.format(value) : formatNumberWithLocale(value);
    }

    /**
     * Updates the view of this NumberPicker. If displayValues were specified in the string corresponding to the index specified by the current value
     * will be returned. Otherwise, the formatter specified in {@link #setFormatter} will be used to format the number.
     *
     * @return Whether the text was updated.
     */
    private void updateInputTextView() {
        String text = "";
        if (mDisplayedValues == null) {
            text = formatNumber(mValue);
        } else {
            if (mValue - mMinValue + 1 > (mDisplayedValues.length - 1)) {
                for (String mDisplayedValue : mDisplayedValues) {
                    if (mDisplayedValue.equals(String.valueOf(mValue))) {
                        text = mDisplayedValue;
                        break;
                    }
                }
            } else {
                text = mDisplayedValues[mValue - mMinValue];
            }
        }
        mInputText.setText(text);
    }

    /**
     * Posts a command for changing the current value by one.
     *
     * @param increment Whether to increment or decrement the value.
     */
    private void postChangeCurrentByOneFromLongPress(boolean increment, long delayMillis) {
        if (mChangeCurrentByOneFromLongPressCommand == null) {
            mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
        } else {
            removeCallbacks(mChangeCurrentByOneFromLongPressCommand);
        }
        mChangeCurrentByOneFromLongPressCommand.setStep(increment);
        postDelayed(mChangeCurrentByOneFromLongPressCommand, delayMillis);
    }

    /**
     * Removes the command for changing the current value by one.
     */
    private void removeChangeCurrentByOneFromLongPress() {
        if (mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(mChangeCurrentByOneFromLongPressCommand);
        }
    }

    /**
     * Removes all pending callback from the message queue.
     */
    private void removeAllCallbacks() {
        if (mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(mChangeCurrentByOneFromLongPressCommand);
        }
        mPressedStateHelper.cancel();
    }

    /**
     * Ensures that the scroll wheel is adjusted i.e. there is no offset and the middle element is in the middle of the widget.
     *
     * @return Whether an adjustment has been made.
     */
    private boolean ensureScrollWheelAdjusted() {
        // adjust to the closest value
        int deltaY = mInitialScrollOffset - mCurrentScrollOffset;
        if (deltaY != 0) {
            mPreviousScrollerY = 0;
            if (Math.abs(deltaY) > mSelectorElementHeight / 2) {
                deltaY += (deltaY > 0) ? -mSelectorElementHeight : mSelectorElementHeight;
            }
            mAdjustScroller.startScroll(0, 0, 0, deltaY, SELECTOR_ADJUSTMENT_DURATION_MILLIS);
            invalidate();
            return true;
        }
        return false;
    }

    /**
     * Interface to listen for changes of the current value.
     */
    public interface OnValueChangeListener {

        /**
         * Called upon a change of the current value.
         *
         * @param picker The NumberPicker associated with this listener.
         * @param oldVal The previous value.
         * @param newVal The new value.
         */
        void onValueChange(NumberPicker picker, int oldVal, int newVal);
    }

    /**
     * Interface to listen for the picker scroll state.
     */
    interface OnScrollListener {

        /**
         * The view is not scrolling.
         */
        int SCROLL_STATE_IDLE = 0;

        /**
         * The user is scrolling using touch, and his finger is still on the screen.
         */
        int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * The user had previously been scrolling using touch and performed a fling.
         */
        int SCROLL_STATE_FLING = 2;

        /**
         * Callback invoked while the number picker scroll state has changed.
         *
         * @param view The view whose scroll state is being reported.
         * @param scrollState The current scroll state. One of {@link #SCROLL_STATE_IDLE}, {@link #SCROLL_STATE_TOUCH_SCROLL} or {@link
         * #SCROLL_STATE_IDLE}.
         */
        void onScrollStateChange(NumberPicker view, int scrollState);
    }

    /**
     * Interface used to format current value into a string for presentation.
     */
    public interface Formatter {

        /**
         * Formats a string representation of the current value.
         *
         * @param value The currently selected value.
         * @return A formatted string representation.
         */
        String format(int value);
    }

    private class PressedStateHelper implements Runnable {

        static final int BUTTON_INCREMENT = 1;
        static final int BUTTON_DECREMENT = 2;

        private static final int MODE_PRESS = 1;
        private static final int MODE_TAPPED = 2;

        private int mManagedButton;
        private int mMode;

        public void cancel() {
            mMode = 0;
            mManagedButton = 0;
            NumberPicker.this.removeCallbacks(this);
            if (mIncrementVirtualButtonPressed) {
                mIncrementVirtualButtonPressed = false;
                invalidate(0, mBottomSelectionDividerBottom, getRight(), getBottom());
            }
            mDecrementVirtualButtonPressed = false;
        }

        void buttonPressDelayed(int button) {
            cancel();
            mMode = MODE_PRESS;
            mManagedButton = button;
            NumberPicker.this.postDelayed(this, ViewConfiguration.getTapTimeout());
        }

        void buttonTapped(int button) {
            cancel();
            mMode = MODE_TAPPED;
            mManagedButton = button;
            NumberPicker.this.post(this);
        }

        @Override
        public void run() {
            switch (mMode) {
                case MODE_PRESS: {
                    switch (mManagedButton) {
                        case BUTTON_INCREMENT: {
                            mIncrementVirtualButtonPressed = true;
                            invalidate(0, mBottomSelectionDividerBottom, getRight(), getBottom());
                        }
                        break;
                        case BUTTON_DECREMENT: {
                            mDecrementVirtualButtonPressed = true;
                            invalidate(0, 0, getRight(), mTopSelectionDividerTop);
                        }
                        default:
                            break;
                    }
                }
                break;
                case MODE_TAPPED: {
                    switch (mManagedButton) {
                        case BUTTON_INCREMENT: {
                            if (!mIncrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
                            }
                            mIncrementVirtualButtonPressed ^= true;
                            invalidate(0, mBottomSelectionDividerBottom, getRight(), getBottom());
                        }
                        break;
                        case BUTTON_DECREMENT: {
                            if (!mDecrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
                            }
                            mDecrementVirtualButtonPressed ^= true;
                            invalidate(0, 0, getRight(), mTopSelectionDividerTop);
                        }
                        default:
                            break;
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

    /**
     * Command for changing the current value from a long press by one.
     */
    private class ChangeCurrentByOneFromLongPressCommand implements Runnable {

        private boolean mIncrement;

        private void setStep(boolean increment) {
            mIncrement = increment;
        }

        @Override
        public void run() {
            changeValueByOne(mIncrement);
            /*
            The speed for updating the value form long press.
			*/
            postDelayed(this, DEFAULT_LONG_PRESS_UPDATE_INTERVAL);
        }
    }
}
