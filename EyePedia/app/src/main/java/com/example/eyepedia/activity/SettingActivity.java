package com.example.eyepedia.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eyepedia.R;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();
    private ArrayList<ListItem> SettingDataList;
    private enum OnClickType {
        START_CALIBRATION, GAZE_VIEW_STATUS, TRANSLATE_STATUS, INIT_STATUS, DELETE_SETTING
    }
    private boolean GazeViewStatus, TranslateStatus, InitStatus;
    private boolean reset = false;
    public static Activity SetActivity;

    public static MainActivity.RequestCode RequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.InitializeSettingData();

        ListView listView = (ListView)findViewById(R.id.listView);
        final SettingAdapter myAdapter = new SettingAdapter(this, SettingDataList);

        listView.setAdapter(myAdapter);
        SetActivity = SettingActivity.this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        GazeViewStatus = getIntent().getBooleanExtra("GazeViewStatus", false);
        TranslateStatus = getIntent().getBooleanExtra("TranslateStatus", true);
        InitStatus = getIntent().getBooleanExtra("InitStatus", true);
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        mIntent.putExtra("GazeViewStatus", GazeViewStatus);
        mIntent.putExtra("TranslateStatus", TranslateStatus);
        mIntent.putExtra("InitStatus", InitStatus);

        setResult(Activity.RESULT_OK, mIntent);
        finish();
    }

    private void showToast(final String msg, final boolean isShort) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SettingActivity.this, msg, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
            }
        });
    }

    public void InitializeSettingData() {
        SettingDataList = new ArrayList<ListItem>();
        SettingDataList.add(new ListItem("동체스캔 실행(정확도 향상)", OnClickType.START_CALIBRATION, false));
        SettingDataList.add(new ListItem("포인터 활성화", OnClickType.GAZE_VIEW_STATUS, true));
        SettingDataList.add(new ListItem("번역 활성화", OnClickType.TRANSLATE_STATUS, true));
        SettingDataList.add(new ListItem("시작 시 동체스캔 활성화", OnClickType.INIT_STATUS, true));
        SettingDataList.add(new ListItem("설정 초기화", OnClickType.DELETE_SETTING, false));
    }

    public class ListItem {
        private String itemName;
        public boolean isSwitch;
        public OnClickType onClickType;

        private ListItem(String itemName, OnClickType onClickType, boolean isSwitch) {
            this.itemName = itemName;
            this.onClickType = onClickType;
            this.isSwitch = isSwitch;
        }

        public String getItemName() {
            return this.itemName;
        }
        public OnClickType getOnClickType() {
            return this.onClickType;
        }
    }

    public class SettingAdapter extends BaseAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<SettingActivity.ListItem> mData;

        public SettingAdapter(Context context, ArrayList<SettingActivity.ListItem> data) {
            mContext = context;
            mData = data;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mData.get(position).isSwitch) {
                convertView = mLayoutInflater.inflate(R.layout.switch_item, null);
                Switch switchView = convertView.findViewById(R.id.itemSwitch);
                switchView.setText(mData.get(position).getItemName());

                OnClickType onClickType = mData.get(position).getOnClickType();
                switch (onClickType) {
                    case GAZE_VIEW_STATUS:
                        switchView.setChecked(GazeViewStatus);
                        break;
                    case TRANSLATE_STATUS:
                        switchView.setChecked(TranslateStatus);
                        break;
                    case INIT_STATUS:
                        switchView.setChecked(InitStatus);
                        break;
                    default:
                        break;
                }

                switchView.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        switch (onClickType) {
                            case GAZE_VIEW_STATUS:
                                GazeViewStatus = !GazeViewStatus;
                                showToast("포인터 활성화 " + ((GazeViewStatus) ? "설정" : "해제"), true);
                                break;
                            case TRANSLATE_STATUS:
                                TranslateStatus = !TranslateStatus;
                                showToast("번역 활성화 " + ((TranslateStatus) ? "설정" : "해제"), true);
                                break;
                            case INIT_STATUS:
                                InitStatus = !InitStatus;
                                showToast("시작 시 동체스캔 " + ((InitStatus) ? "설정" : "해제"), true);
                                break;
                            default:
                                break;
                        }
                    }
                });
            } else {
                convertView = mLayoutInflater.inflate(R.layout.text_item, null);
                TextView textView = convertView.findViewById(R.id.itemText);
                textView.setText(mData.get(position).getItemName());

                textView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        OnClickType onClickType = mData.get(position).getOnClickType();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        switch (onClickType) {
                            case START_CALIBRATION:
                                intent.putExtra("ActiveCalibration", true);
                                intent.putExtra("GazeViewStatus", GazeViewStatus);
                                intent.putExtra("TranslateStatus", TranslateStatus);
                                intent.putExtra("InitStatus", InitStatus);
                                intent.putExtra("Clicked", true);
                                startActivity(intent);
                                finish();
                                break;
                            case DELETE_SETTING:
                                showToast("설정 초기화 완료", true);
                                intent.putExtra("GazeViewStatus", false);
                                intent.putExtra("TranslateStatus", true);
                                intent.putExtra("InitStatus", true);
                                intent.putExtra("Clicked", true);
                                Log.i(TAG + "onActivityResult", String.valueOf(GazeViewStatus) + " / " + TranslateStatus + " / " + InitStatus);
                                startActivity(intent);
                                finish();
                                break;
                            default:
                                break;
                        }
                    }
                });
            }

            return convertView;
        }
    }
}
