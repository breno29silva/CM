package com.example.cm.controller;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cm.BuildConfig;
import com.example.cm.R;
import com.example.cm.activity.MainActivity;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.snapshot.DetectedActivityResponse;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainController {

    private String TAG = "MainController";

    protected MainActivity mContext;
    private static Vibrator sVibrator;
    private static final int VIBRATOR_TIMER = 1500;
    private static final String FENCE_RECEIVER_ACTION = BuildConfig.APPLICATION_ID +
            "FENCE_RECEIVER_ACTION";
    private static final String FENCE_KEY = "walkingFenceKey";
    private FenceReceiver mFenceReceiver;
    private PendingIntent mPendingIntent;

    public MainController(MainActivity context) {
        mContext = context;
    }

    public void fenceEnabled() {
        AwarenessFence walkingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);

        mPendingIntent = PendingIntent.getBroadcast(mContext, 0,
                new Intent(FENCE_RECEIVER_ACTION), 0);
        mContext.registerReceiver(mFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));

        Awareness.getFenceClient(mContext).updateFences(new FenceUpdateRequest.Builder()
                .addFence(FENCE_KEY, walkingFence, mPendingIntent)
                .build())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mContext.showText(mContext.getString(R.string.fence_enabled));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Fence could not be registered: " + e);
                    }
                });
    }

    public void snapshot() {
        Awareness.getSnapshotClient(mContext).getDetectedActivity()
                .addOnSuccessListener(new OnSuccessListener<DetectedActivityResponse>() {
                                          @Override
                                          public void onSuccess(DetectedActivityResponse dar) {
                                              ActivityRecognitionResult arr = dar.getActivityRecognitionResult();
                                              DetectedActivity probableActivity = arr.getMostProbableActivity();

                                              String activity = defineActivity(probableActivity.getType());
                                              mContext.showText("Snapshot: " + activity);
                                          }
                                      }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Snapshot FAIL " + e);
                    }
                });
    }

    private void vibrate() {
        if (sVibrator == null) {
            sVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            assert sVibrator != null;
        }
        sVibrator.vibrate(VibrationEffect.createOneShot(VIBRATOR_TIMER, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    private String defineActivity(int activityValue) {
        switch (activityValue) {
            case 0:
                return mContext.getString(R.string.IN_VEHICLE);
            case 1:
                return mContext.getString(R.string.ON_BICYCLE);
            case 2:
                return mContext.getString(R.string.ON_FOOT);
            case 3:
                return mContext.getString(R.string.STILL);
            case 5:
                return mContext.getString(R.string.TILTING);
            case 7:
                return mContext.getString(R.string.WALKING);
            case 8:
                return mContext.getString(R.string.RUNNING);
            default:
                return mContext.getString(R.string.UNKNOWN);
        }
    }

    public class FenceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context contextInternal, Intent intent) {

            FenceState fenceState = FenceState.extract(intent);

            if (TextUtils.equals(fenceState.getFenceKey(), FENCE_KEY)) {
                if (fenceState.getCurrentState() == FenceState.TRUE) {
                    mContext.showText(mContext.getString(R.string.YOU_WALKING));
                    vibrate();
                }
            }
        }
    }
}