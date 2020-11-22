package com.jerry.baselib.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;

/**
 * Created by wzl on 2018/9/8.
 *
 * @Description spannable string tool
 */
public final class SpannableStringUtils {

    private SpannableStringUtils() {
    }

    public static class Builder {

        private static final int DEFAULT_FOREGROUND_COLOR = 0x333333;
        private static final int DEFAULT_BACKGROUND_COLOR = 0xFFFFFF;
        private SpannableStringBuilder mBuilder;
        private CharSequence text;
        private ClickableSpan clickSpan;
        private ImageSpan imageSpan;

        private int flag;
        @ColorInt
        private int foregroundColor;
        @ColorInt
        private int backgroundColor;
        private int size;

        private int start;
        private int end;

        private int secondStart;
        private int secondEnd;

        private int threeStart;
        private int threeEnd;

        public Builder(CharSequence text) {
            this.text = text;
            flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
            foregroundColor = DEFAULT_FOREGROUND_COLOR;
            mBuilder = new SpannableStringBuilder();
        }

        /**
         * 设置标识
         *
         * @param flag <ul> <li>{@link Spanned#SPAN_EXCLUSIVE_EXCLUSIVE}</li> <li>{@link Spanned#SPAN_EXCLUSIVE_INCLUSIVE}</li> <li>{@link
         *             Spanned#SPAN_INCLUSIVE_INCLUSIVE}</li> <li>{@link Spanned#SPAN_INCLUSIVE_EXCLUSIVE}</li> </ul>
         */
        public Builder setFlag(final int flag) {
            this.flag = flag;
            return this;
        }

        /**
         * 设置背景色
         */
        public Builder setBackgroundColor(@ColorInt int res) {
            this.backgroundColor = res;
            return this;
        }

        /**
         * 设置前景色
         */
        public Builder setForegroundColor(@ColorInt int color) {
            this.foregroundColor = color;
            return this;
        }

        public Builder setStartEnd(int start, int end) {
            this.start = start;
            this.end = end;
            return this;
        }

        /**
         * 暂时只支持相同颜色
         */
        public Builder setSecondStartEnd(int start, int end) {
            this.secondStart = start;
            this.secondEnd = end;
            return this;
        }

        /**
         * 暂时只支持相同颜色
         */
        public Builder setThreeStartEnd(int start, int end) {
            this.threeStart = start;
            this.threeEnd = end;
            return this;
        }

        public Builder setSize(final int size) {
            this.size = size;
            return this;
        }

        public Builder setClickSpan(ClickableSpan clickSpan) {
            this.clickSpan = clickSpan;
            return this;
        }

        public Builder setImageSpan(ImageSpan imageSpan) {
            this.imageSpan = imageSpan;
            return this;
        }

        /**
         * 追加样式
         */
        public Builder append(CharSequence text) {
            if (text == null) {
                return this;
            }
            setSpans();
            this.text = text;
            return this;
        }

        public Builder append(int text) {
            setSpans();
            this.text = String.valueOf(text);
            return this;
        }

        public Builder append(float text) {
            setSpans();
            this.text = String.valueOf(text);
            return this;
        }

        public SpannableStringBuilder build() {
            setSpans();
            return mBuilder;
        }

        private void setSpans() {
            if (text == null) {
                return;
            }
            if (start == 0 && end == 0) {
                start = mBuilder.length();
                mBuilder.append(this.text);
                end = mBuilder.length();
            } else {
                mBuilder.append(this.text);
            }

            if (backgroundColor != DEFAULT_BACKGROUND_COLOR) {
                mBuilder.setSpan(new BackgroundColorSpan(backgroundColor), start, end, flag);
            }

            if (foregroundColor != DEFAULT_FOREGROUND_COLOR) {
                mBuilder.setSpan(new ForegroundColorSpan(foregroundColor), start, end, flag);
            }
            if (secondEnd != 0) {
                mBuilder.setSpan(new ForegroundColorSpan(foregroundColor), secondStart, secondEnd, flag);
            }
            if (threeEnd != 0) {
                mBuilder.setSpan(new ForegroundColorSpan(foregroundColor), threeStart, threeEnd, flag);
            }
            backgroundColor = DEFAULT_BACKGROUND_COLOR;
            foregroundColor = DEFAULT_FOREGROUND_COLOR;

            if (size != 0) {
                mBuilder.setSpan(new AbsoluteSizeSpan(size), start, end, flag);
                size = 0;
            }

            if (clickSpan != null) {
                mBuilder.setSpan(clickSpan, start, end, flag);
                clickSpan = null;
            }
            if (imageSpan != null) {
                mBuilder.setSpan(imageSpan, start, end, flag);
                imageSpan = null;
            }
            flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
            start = 0;
            end = 0;
            secondStart = 0;
            secondEnd = 0;
        }

        public void clear() {
            text = "";
            mBuilder.clear();
        }

        @Override
        public String toString() {
            return mBuilder.toString();
        }
    }

    /**
     * @param originStr      字符源
     * @param keyWord        需要改变颜色的字符
     * @param highLightColor 高亮的颜色
     * @param size           要改变的字符字体大小
     */
    public static CharSequence setSpan(String originStr, String keyWord, @ColorInt int highLightColor, @DimenRes int size) {
        SpannableString ss = new SpannableString(originStr);
        Pattern p = Pattern.compile(keyWord);
        Matcher m = p.matcher(ss);
        while (m.find()) {
            ss.setSpan(new ForegroundColorSpan(highLightColor), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new AbsoluteSizeSpan(size), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }
}
