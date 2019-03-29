package com.taracdia.minesweeper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Chronometer;

import java.util.Locale;

public class MyTimer extends Chronometer {
    //extending chronometer so that it will count from 0 to 999 instead of looking like a normal timer

    int count = 0;
    OnChronometerTickListener onChronometerTickListener = new OnChronometerTickListener() {
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            if (count <= 999) {
                String s = String.format(Locale.US, "%1$03d", count);
                setText(s);
                count++;
            } else {
                setText(R.string.nines);
            }
        }
    };

    public MyTimer(Context context) {
        super(context);
        setOnChronometerTickListener(onChronometerTickListener);
    }

    public MyTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnChronometerTickListener(onChronometerTickListener);
    }

    public MyTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnChronometerTickListener(onChronometerTickListener);
    }

    public void resetCount(){
        count = 0;
        setText(R.string.triple_zero);
    }
}
