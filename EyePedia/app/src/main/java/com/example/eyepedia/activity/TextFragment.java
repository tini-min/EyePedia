package com.example.eyepedia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eyepedia.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.example.eyepedia.Readurl;

public class TextFragment extends Fragment {
    @Nullable
    private static final String TAG = TextFragment.class.getSimpleName();
    private static final int OPEN_DIRECTORY_REQUEST_CODE = 10;
    private TextView textView;
    final static String filePath = Environment.DIRECTORY_DOWNLOADS+"/test.txt";

    public TextFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        textView = (TextView) view.findViewById(R.id.textView);

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "파일 불러옴", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.putExtra("content", "해보자!");

//                Intent intent = new Intent(getActivity().getApplicationContext(),SubActivity.class);
                intent.setType("text/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("Name","BAD");
                startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE);
//                startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE);
                Readurl.main();
            }
        });
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spannable spannable = (Spannable) textView.getText();
        String content = textView.getText().toString();
        String[] strArray = content.split("\\.");
        List<Integer> indArray = new ArrayList<Integer>();
        for (String str : strArray) {
            indArray.add(content.indexOf(str));
        }
        indArray.add(content.length());

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        for (int i = 0; i < indArray.size() - 1; i++) {
            int finalI = i;
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    ((MainActivity)getActivity()).setText(content.substring(indArray.get(finalI), indArray.get(finalI + 1)));
                    Log.i(TAG, content.substring(indArray.get(finalI), indArray.get(finalI + 1)));
                }
            }, indArray.get(finalI), indArray.get(finalI + 1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
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