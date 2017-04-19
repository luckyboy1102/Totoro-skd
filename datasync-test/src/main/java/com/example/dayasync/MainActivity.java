package com.example.dayasync;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "insert sql:" + BPMeasureSyncUtil.insertConfigSQL("123"));
        Log.d(TAG, "fetch sql:" + BPMeasureSyncUtil.fetchSQL("createTime > %d AND deleteFlag = %s", new Object[] {12883, "1"}));
        Log.d(TAG, "insert sql:" + PersonSyncUtil.insertConfigSQL("123"));
        Log.d(TAG, "fetch sql:" + PersonSyncUtil.fetchSQL("createTime > %s AND deleteFlag = '%s'", new String[] {"12883", "1"}));

        JSONObject obj = new JSONObject();
        try {
            obj.put("SBP", 120);
            obj.put("DBP", 80);
            obj.put("PR", 60);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "insert sql: " + BPMeasureSyncUtil.insertSQL(obj));
    }
}
