package com.example.instashare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.instashare.R;

public class ViewPagerAdapter_Upload extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] buttonImages =
            {R.id.button1, R.id.button2, R.id.button3, R.id.button4};
    private EditText editText;
    private ButtonClickListener buttonClickListener;

    public void setButtonClickListener(ButtonClickListener buttonClickListener)
    {
        this.buttonClickListener = buttonClickListener;
    }

    public ViewPagerAdapter_Upload(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return buttonImages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = null;

        switch (position) {
            case 0:
                view = layoutInflater.inflate(R.layout.custom_layout1, container, false);
                break;
            case 1:
                view = layoutInflater.inflate(R.layout.custom_layout2, container, false);
                ViewPager viewPager = (ViewPager) container;
                viewPager.addView(view);

                // Lấy ra EditText của trang đầu tiên
                editText = view.findViewById(R.id.button2);
                break;
            case 2:
                view = layoutInflater.inflate(R.layout.custom_layout3, container, false);
                break;
            case 3:
                view = layoutInflater.inflate(R.layout.custom_layout4, container, false);
                break;
        }

        if (view != null && position != 1) {
            Button button = view.findViewById(buttonImages[position]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (buttonClickListener != null) {
                        if(position != 1) {
                            if (editText != null)
                                editText.setText("");
                            buttonClickListener.onButtonClick(position);
                        }
                    }
                }
            });

            container.addView(view);
        }

        return view;
    }

    public interface ButtonClickListener {
        void onButtonClick(int position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    // Getter cho EditText
    public EditText getEditText() {
        return editText;
    }

    public void focusEditText() {
        editText.requestFocus();
    }
}
