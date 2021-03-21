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

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static android.app.Activity.RESULT_OK;

//implements View.OnClickListener
public class TextFragment extends Fragment  {
    @Nullable
    private static final String TAG = TextFragment.class.getSimpleName();
    private TextView textView;

    private static final int PICK_PDF_FILE = 2;
    private final static int OPEN_DIRECTORY_REQUEST_CODE = 1000;
    boolean uriToLoad;


    private EditText translationText;
    private Button translationButton;
    private TextView resultText;
    private String result;

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            StringBuilder output = new StringBuilder();
            String clientId = "lW72OjMsgLdaTMMxlzzh"; // 애플리케이션 클라이언트 아이디 값";
            String clientSecret = "0ejOfnbVHH"; // 애플리케이션 클라이언트 시크릿 값";
            try {
                // 번역문을 UTF-8으로 인코딩합니다.
                String text = URLEncoder.encode(translationText.getText().toString(), "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";

                // 파파고 API와 연결을 수행합니다.
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                // 번역할 문장을 파라미터로 전송합니다.
                String postParams = "source=en&target=ko&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();

                // 번역 결과를 받아옵니다.
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    output.append(inputLine);
                }
                br.close();
            } catch(Exception ex) {
                Log.e("SampleHTTP", "Exception in processing response.", ex);
                ex.printStackTrace();
            }
            result = output.toString();
            return null;
        }

        protected void onPostExecute(Integer a) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            if(element.getAsJsonObject().get("errorMessage") != null) {
                Log.e("번역 오류", "번역 오류가 발생했습니다. " +
                        "[오류 코드: " + element.getAsJsonObject().get("errorCode").getAsString() + "]");
            } else if(element.getAsJsonObject().get("message") != null) {
                // 번역 결과 출력
                resultText.setText(element.getAsJsonObject().get("message").getAsJsonObject().get("result")
                        .getAsJsonObject().get("translatedText").getAsString());
            }

        }

    }




    public TextFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void exonClick(View view) {};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_text, container, false);

        translationText = (EditText) view.findViewById(R.id.translationText);
        translationButton = (Button) view.findViewById(R.id.translationButton);
        resultText = (TextView) view.findViewById(R.id.resultText);

        translationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundTask().execute();
            }
        });

        view.findViewById(R.id.explicit_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "명시적 인텐트", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.putExtra("content", "해보자!");
//                startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE);

                Intent intent = new Intent(getActivity().getApplicationContext(),SubActivity.class);
                intent.setType("text/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("Name","BAD");
                startActivity(intent);
//                startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE);
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
//        String text = "";
//        for(int i=0; i<20; i++)
//            text += i + "\n";
//        textView.setText(text);
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