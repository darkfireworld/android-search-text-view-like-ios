package com.example.myapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * 搜索View
 */
public class SearchTextView extends FrameLayout {
    //输入文本控件
    private static class InputText extends EditText {
        int autoClearImg = 0;

        public InputText(Context context) {
            super(context);
            //焦点监听器
            setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    refreshAutoClearImg();
                }
            });
            //文字变化监听器
            addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    refreshAutoClearImg();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        /**
         * 设置自动清除图片资源
         *
         * @param autoClearImg 如果为0，则表示不现实该ICON
         */
        public void setAutoClearImgRedId(int autoClearImg) {
            this.autoClearImg = autoClearImg > 0 ? autoClearImg : 0;
            refreshAutoClearImg();
        }

        /**
         * 设置放大镜
         */
        public void setMagnifierResId(int resId) {
            if (resId <= 0) {
                setCompoundDrawables(null, getCompoundDrawables()[1], getCompoundDrawables()[2], getCompoundDrawables()[3]);
            } else {
                //打开 ICON
                Drawable drawable = getContext().getResources().getDrawable(resId);
                // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                //设置放大器
                setCompoundDrawables(drawable, getCompoundDrawables()[1], getCompoundDrawables()[2], getCompoundDrawables()[3]);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //具有图片的时候，才能进行清空
                if (getCompoundDrawables()[2] != null) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    boolean isInnerWidth = x > (getWidth() - getTotalPaddingRight() - 10) && x < (getWidth() - getPaddingRight() + 10);
                    boolean isInnerHeight = y > 0 && y < getHeight();
                    if (isInnerWidth && isInnerHeight) {
                        this.setText("");
                    }
                }
            }
            return super.onTouchEvent(event);
        }

        /**
         * 发生事件后，自动检测是否显示 自动清空ICON
         */
        private void refreshAutoClearImg() {
            if (autoClearImg != 0 && getText().length() > 0 && isFocused()) {
                //打开 ICON
                Drawable drawable = getContext().getResources().getDrawable(autoClearImg);
                // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], drawable, getCompoundDrawables()[3]);
            } else {
                //清空 ICON
                setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], null, getCompoundDrawables()[3]);
            }
        }
    }

    InputText it_main;
    InputTextChangeListener listener;

    /**
     * 文本变化监听器
     */
    public interface InputTextChangeListener {
        void onChange(@Nullable CharSequence text);
    }

    /**
     * dp转px工具
     */
    static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public SearchTextView(Context context) {
        this(context, null);
    }

    public SearchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置 基本搜索
        {
            it_main = new InputText(getContext());
            this.addView(it_main);
            {
                LayoutParams lp_it_main = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                lp_it_main.setMargins(dp2px(getContext(), 8), dp2px(getContext(), 6), dp2px(getContext(), 8), dp2px(getContext(), 6));
                it_main.setLayoutParams(lp_it_main);
                it_main.setPadding(dp2px(getContext(), 8), 0, dp2px(getContext(), 8), 0);
                it_main.setHint("搜索");
                it_main.setGravity(Gravity.CENTER_VERTICAL);
                it_main.setCompoundDrawablePadding(dp2px(getContext(), 8));
                it_main.setTextSize(14);
                it_main.setTextColor(Color.parseColor("#A9A9A9"));
                it_main.setSingleLine();
                {
                    ShapeDrawable shapeDrawable = new ShapeDrawable(new Shape() {
                        @Override
                        public void draw(Canvas canvas, Paint paint) {
                            paint.setStyle(Paint.Style.FILL);
                            paint.setColor(Color.parseColor("#FFFFFF"));
                            paint.setAntiAlias(true);
                            canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), 20, 15, paint);
                        }
                    });
                    it_main.setBackgroundDrawable(shapeDrawable);
                }
                //设置文本监听器
                it_main.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (listener != null) {
                            if (s != null && s.length() > 0) {
                                listener.onChange(s);
                            } else {
                                listener.onChange(null);
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }
        //配置属性
        {
            boolean input_enable = false;
            int magnifier_image = 0;
            int delete_image = 0;
            //读取自定义属性
            if (attrs != null) {
                TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.search_text_view);
                //是否允许输入
                input_enable = ta.getBoolean(R.styleable.search_text_view_input_enable, false);
                //是否允许输入空格
                magnifier_image = ta.getResourceId(R.styleable.search_text_view_magnifier_image, 0);
                //自动清除内容ICON
                delete_image = ta.getResourceId(R.styleable.search_text_view_delete_image, 0);
                ta.recycle();
            }
            setInputTextEnable(input_enable);
            setMagnifierResId(magnifier_image);
            setDeleteResId(delete_image);
        }

    }

    /**
     * 设置文本监听器
     */
    public void setInputTextChangeListener(InputTextChangeListener listener) {
        this.listener = listener;
    }

    /**
     * 设置文本是否允许输入
     */
    public void setInputTextEnable(boolean enable) {
        it_main.setEnabled(enable);
    }

    /**
     * 设置自动清空的ICON
     */
    public void setDeleteResId(int imageResId) {
        it_main.setAutoClearImgRedId(imageResId);
    }

    /**
     * 设置放大镜
     */
    public void setMagnifierResId(int resId) {
        it_main.setMagnifierResId(resId);
    }
}
