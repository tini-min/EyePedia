package com.example.eyepedia.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.eyepedia.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextFragment extends Fragment {
    @Nullable
    private TextView textView;
    private ConstraintLayout constraintLayout;
    private Button btn;
    final static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView.findViewById(R.id.textView);
        constraintLayout.findViewById((R.id.layout));
        Spannable spannable = (Spannable) textView.getText();
        String content = textView.getText().toString();
        String[] strArray = content.split("\\.");
        List<Integer> indArray = new ArrayList<Integer>();
        for(String str : strArray) { indArray.add(content.indexOf(str)); }
        indArray.add(content.length());

        // Obtain MotionEvent object
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
//        float x = (gazeInfo.screenState == ScreenState.INSIDE_OF_SCREEN ? filteredPoint[0] : 0.0f);
//        float y = (gazeInfo.screenState == ScreenState.INSIDE_OF_SCREEN ? filteredPoint[1] : 0.0f);
        float x = 800.0f;
        float y = 500.0f;
        // List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;
        //MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, metaState);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        for(int i = 0; i < indArray.size() - 1; i++) {
            int finalI = i;
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    //textView.dispatchTouchEvent(motionEvent);
                    //Log.i(TAG, content.substring(indArray.get(finalI), indArray.get(finalI + 1)));
                }
            }, indArray.get(finalI), indArray.get(finalI + 1) - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        btn.findViewById(R.id.button);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                constraintLayout.dispatchTouchEvent(MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, metaState));
                constraintLayout.dispatchTouchEvent(MotionEvent.obtain(downTime + 100, eventTime + 200, MotionEvent.ACTION_UP, x, y, metaState));
            }
        });

    }

    public void mOnFileRead(View v){
        String read = ReadTextFile(filePath);
        textView.setText(read);
    }

    public String ReadTextFile(String path) {
        StringBuffer strBuffer = new StringBuffer();
        try {
            InputStream is = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = reader.readLine()) != null) {
                strBuffer.append(line + "\n");
            }

            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return strBuffer.toString();
    }
}
