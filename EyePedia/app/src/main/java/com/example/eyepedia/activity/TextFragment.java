package com.example.eyepedia.activity;

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
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TextFragment extends Fragment {
    @Nullable
    private static final String TAG = TextFragment.class.getSimpleName();
    private static final int OPEN_DIRECTORY_REQUEST_CODE = 10;
    TextView textView;


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
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
}