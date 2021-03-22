package com.example.eyepedia.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eyepedia.ActivityResultEvent;
import com.example.eyepedia.Constants;
import com.example.eyepedia.EventBus;
import com.example.eyepedia.KeySets;
import com.example.eyepedia.R;
import com.example.eyepedia.Menu_papago;
import com.example.eyepedia.R;

import java.text.BreakIterator;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

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

    public TextFragment() {
        // Required empty public constructor
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getInstance().register(this);
    }
    @Override
    public void onDestroyView() {
        EventBus.getInstance().unregister(this);
        super.onDestroyView();
    }
    @SuppressWarnings("unused")
    @Subscribe
    public void onActivityResultEvent(@NonNull ActivityResultEvent event) {
        onActivityResult(event.getRequestCode(), event.getResultCode(), event.getData());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.RequestCode.REQUEST_CODE_NAME_INPUT: {
                Log.i("show me the Log", String.valueOf(requestCode));
                if (resultCode == Activity.RESULT_OK) {
                    String text = data.getStringExtra(KeySets.KEY_NAME_INPUT);
                    if (!TextUtils.isEmpty(text)) {
                        textView.setText(text);
                    }
                }
                break;
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_text, container, false);

//        translationText = (EditText) view.findViewById(R.id.translationText);
//        translationButton = (Button) view.findViewById(R.id.translationButton);
//        resultText = (TextView) view.findViewById(R.id.resultText);

        translationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundTask().execute();
            }
        });
        textView = (TextView) view.findViewById(R.id.textView);
        view.findViewById(R.id.button_input).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "파일 불러옴", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.putExtra("content", "해보자!");

//                Intent intent = new Intent(getActivity().getApplicationContext(),SubActivity.class);
                intent.setType("text/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("Name", "BAD");
                startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE);
            }
        });
        return view;
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
    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() { }

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
}
