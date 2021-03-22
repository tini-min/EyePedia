package com.example.eyepedia.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eyepedia.Menu_papago;
import com.example.eyepedia.R;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

//import android.support.v7.app.AppCompatActivity;

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

        view.findViewById(R.id.load_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("text/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("content", "해보자!");

                startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE);
            }
        });
        textView = (TextView) view.findViewById(R.id.textView);
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
        String[] strArray = content.split("\\.|\\?");
        List<Integer> indArray = new ArrayList<Integer>();
        for (String str : strArray) {
            if (indArray.isEmpty()) {
                indArray.add(content.indexOf(str));
            } else indArray.add(content.indexOf(str, indArray.get(indArray.size()-1)));
        }
        indArray.add(content.length());

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        for (int i = 0; i < indArray.size() - 1; i++) {
            int finalI = i;
            Log.i(TAG, String.valueOf(content.length()) + " / " + indArray.get(finalI) + " / " + indArray.get(finalI+1));
            Log.i(TAG, content.substring(indArray.get(finalI), indArray.get(finalI + 1)));
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    new Thread() {
                        @Override
                        public void run() {
                            if (((MainActivity)getActivity()).TranslateStatus) {
                                String word = content.substring(indArray.get(finalI), indArray.get(finalI + 1));
                                Menu_papago papago = new Menu_papago();
                                String resultWord;
                                resultWord = papago.getTranslation(word, "en", "ko");

                                Bundle papagoBundle = new Bundle();
                                papagoBundle.putString("resultWord", resultWord);

                                Message msg = papago_handler.obtainMessage();
                                msg.setData(papagoBundle);
                                papago_handler.sendMessage(msg);
                            }
                        }
                    }.start();
                    Log.i(TAG, content.substring(indArray.get(finalI), indArray.get(finalI + 1)));
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
            //result_translation.setText(resultWord);
            ((MainActivity)getActivity()).setText(resultWord);
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