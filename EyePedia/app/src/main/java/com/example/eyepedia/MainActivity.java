package com.example.eyepedia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.eyepedia.view.GazePathView;
import com.example.eyepedia.view.PointView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import camp.visual.gazetracker.*;
import camp.visual.gazetracker.callback.CalibrationCallback;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.callback.InitializationCallback;
import camp.visual.gazetracker.callback.StatusCallback;
import camp.visual.gazetracker.constant.InitializationErrorType;
import camp.visual.gazetracker.constant.StatusErrorType;
import camp.visual.gazetracker.device.GazeDevice;
import camp.visual.gazetracker.filter.OneEuroFilterManager;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.ScreenState;
import camp.visual.gazetracker.state.TrackingState;
import camp.visual.gazetracker.util.ViewLayoutChecker;



import android.os.Environment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = new String[]
            {Manifest.permission.CAMERA};
    private static final int REQ_PERMISSION = 1000;

    private final ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
    private GazePathView gazePathView;
    private GazeTrackerManager gazeTrackerManager;
    private final OneEuroFilterManager oneEuroFilterManager = new OneEuroFilterManager(
            2, 30, 0.5F, 0.001F, 1.0F);

    //private GazeTracker gazeTracker;

    private TextView textView;
    private ConstraintLayout constraintLayout;
    private Button btn;
    final static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "GazeTracker Version: " + GazeTracker.getVersionName());
        initView();
        checkPermission();
        initHandler();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = findViewById(R.id.textView);
        constraintLayout = findViewById((R.id.layout));
        Spannable spannable = (Spannable) textView.getText();
        String content = textView.getText().toString(); Log.i(TAG, content);
        String[] strArray = content.split("\\."); Log.i(TAG, Arrays.toString(strArray));
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
                    Log.i(TAG, content.substring(indArray.get(finalI), indArray.get(finalI + 1)));
                }
            }, indArray.get(finalI), indArray.get(finalI + 1) - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        btn = (Button) findViewById(R.id.button);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       switch (id) {
           case R.id.action_settings:
               Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
               startActivity(intent);
       }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        gazeTrackerManager.setGazeTrackerCallbacks(gazeCallback);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gazeTrackerManager.startGazeTracking();
        setOffsetOfView();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        gazeTrackerManager.stopGazeTracking();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        gazeTrackerManager.removeCallbacks(gazeCallback);
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, REQ_PERMISSION);
            } else {
                checkPermission(true);
            }
        } else {
            checkPermission(true);
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private boolean hasPermissions(String[] permissions) {
        int result;
        // Check permission status in string array
        for (String perms : permissions) {
            if (perms.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (!Settings.canDrawOverlays(this)) {
                    return false;
                }
            }
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                // When if unauthorized permission found
                return false;
            }
        }
        // When if all permission allowed
        return true;
    }

    private void checkPermission(boolean isGranted) {
        if (isGranted) {
            permissionGranted();
        } else {
            showToast("not granted permissions", true);
            finish();
        }
    }

    // permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraPermissionAccepted) {
                        checkPermission(true);
                    } else {
                        checkPermission(false);
                    }
                }
                break;
        }
    }

    private void permissionGranted() {
        initGaze();
    }
    // permission end

//    private void initFail(InitializationErrorType error) {
//        String err = "";
//        if (error == InitializationErrorType.ERROR_INIT) {
//            // When initialization is failed
//            err = "Initialization failed";
//        } else if (error == InitializationErrorType.ERROR_CAMERA_PERMISSION) {
//            // When camera permission doesn not exists
//            err = "Required permission not granted";
//        } else if (error == InitializationErrorType.AUTH_INVALID_KEY) {
//            // Authentication failure (Invalid License Key)
//            err = "Invalid License Key";
//        } else if (error == InitializationErrorType.AUTH_INVALID_ENV_USED_DEV_IN_PROD) {
//            // Using Dev Key in Prod Environment
//            err = "Authentication failed (Dev Key in Prod Environment)";
//        } else if (error == InitializationErrorType.AUTH_INVALID_ENV_USED_PROD_IN_DEV) {
//            // Using Prod Key in Dev Environment
//            err = "Authentication failed (Prod Key in Dev Environment)";
//        } else if (error == InitializationErrorType.AUTH_INVALID_PACKAGE_NAME) {
//            // Invalide Package Name
//            err = "Invalid Package Name";
//        } else if (error == InitializationErrorType.AUTH_INVALID_APP_SIGNATURE) {
//            // Invalid App Signature
//            err = "Invalid App Signature";
//        } else if (error == InitializationErrorType.AUTH_EXCEEDED_FREE_TIER) {
//            // Free Tier limit is exceeded
//            err = "Free Tier limit is exceeded";
//        } else if (error == InitializationErrorType.AUTH_DEACTIVATED_KEY) {
//            // Deactivated License Key
//            err = "Authentication failed (Deactivated License Key)";
//        } else if (error == InitializationErrorType.AUTH_INVALID_ACCESS) {
//            // Invalid Access Method
//            err = "Invalid Access Method";
//        } else if (error == InitializationErrorType.AUTH_UNKNOWN_ERROR) {
//            // Unknown Error
//            err = "Unknown Error";
//        } else if (error == InitializationErrorType.AUTH_SERVER_ERROR) {
//            // Server Authentication Error
//            err = "Server Authentication failed";
//        } else if (error == InitializationErrorType.AUTH_CANNOT_FIND_HOST) {
//            // Host Server Connection failed
//            err = "Host Server Connection failed";
//        } else if (error == InitializationErrorType.AUTH_WRONG_LOCAL_TIME) {
//            // Local and server time does not match
//            err = "Local device time does not match with Server";
//        } else if (error == InitializationErrorType.AUTH_INVALID_KEY_FORMAT) {
//            // Invalide License Key Format
//            err = "Invalid License Key Format";
//        } else {
//            // Gaze library initialization failure
//            // It can ba caused by several reasons(i.e. Out of memory).
//            err = "init gaze library fail";
//        }
//        Log.w(TAG, "error description: " + err);
//    }

    private void initGaze() {
        showProgress();
        GazeDevice gazeDevice = new GazeDevice();
        if (gazeDevice.isCurrentDeviceFound()) {
            // 돌린 기기의 device info가 있는지확인
            Log.d(TAG, "이 디바이스는 gazeinfo 설정이 필요 없습니다.");
        } else {
            // 예시입니다. SM-T720은 갤럭시탭 s5e 모델명
            gazeDevice.addDeviceInfo("SM-T720", -72f, -4f);
        }

        String licenseKey = "dev_6223o6dqrnnywdbl55htd5mr26jv9fi08qskthlp";
        GazeTracker.initGazeTracker(getApplicationContext(), gazeDevice, licenseKey, initializationCallback);
    }