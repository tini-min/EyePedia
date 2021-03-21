package com.example.eyepedia.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eyepedia.R;
import com.example.eyepedia.Menu_papago;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Intent;
import android.net.Uri;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import static android.app.Activity.RESULT_OK;

//implements View.OnClickListener
public class TextFragment extends Fragment  {
    @Nullable
    private static final String TAG = TextFragment.class.getSimpleName();
    private TextView textView;

    private static final int PICK_PDF_FILE = 2;
    private final static int OPEN_DIRECTORY_REQUEST_CODE = 1000;
    boolean uriToLoad;
    private BreakIterator target_translation_word;

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

        view.findViewById(R.id.explicit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "명시적 인텐트", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("text/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("content", "해보자!");

                startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE);


            }
        });
        view.findViewById(R.id.implicit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "암시적 인텐트", Toast.LENGTH_SHORT).show();

                //암시적 인텐트 목적에 맞는 호출 : 지도보기, 연락처보기, 인터넷, SNS 공유 등등.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/"));
                startActivity(intent);
            }
        });
//        view.findViewById(R.id.button_to_translation).setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            new Thread(){
//                @Override
//                public void run() {
//                    String word = target_translation_word.getText().toString();
//                    Menu_papago papago = new Menu_papago();
//                    String resultWord;
//                    resultWord= papago.getTranslation(word,"en","ko");
//
//                    Bundle papagoBundle = new Bundle();
//                    papagoBundle.putString("resultWord",resultWord);
//
//                    Message msg = papago_handler.obtainMessage();
//                    msg.setData(papagoBundle);
//                    papago_handler.sendMessage(msg);
//                }
//            }.start();
        textView = (TextView) view.findViewById(R.id.textView);

        // 100 줄의 텍스트를 생성합니다.
        String text = "";
        for(int i=0; i<20; i++)
            text += i + "\n";
        textView.setText(text);
//
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(String.valueOf(uri), "Right?");
                // Perform operations on the document using its URI.
            }
        }
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
                    //content.substring(indArray.get(finalI), indArray.get(finalI + 1));
                    Toast.makeText(getActivity(), content.substring(indArray.get(finalI), indArray.get(finalI + 1)), Toast.LENGTH_LONG).show();
                    Log.i("ansewr", content.substring(indArray.get(finalI), indArray.get(finalI + 1)));
                }
            }, indArray.get(finalI), indArray.get(finalI + 1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
    @SuppressLint("HandlerLeak")
    Handler papago_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String resultWord = bundle.getString("resultWord");
            result_translation.setText(resultWord);
            //Toast.makeText(getApplicationContext(),resultWord,Toast.LENGTH_SHORT).show();
        }
    };




//    public void mOnFileRead(){
//        String read = ReadTextFile(filePath);
//        textView.setText(read);
//    }
//
//    public String ReadTextFile(String path) {
//        StringBuffer strBuffer = new StringBuffer();
//        try {
//            InputStream is = new FileInputStream(path);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                strBuffer.append(line + "\n");
//            }
//
//            reader.close();
//            is.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        }
//        return strBuffer.toString();
//    }
    }