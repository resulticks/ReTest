package io.mob.resu.reandroidsdk;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.ArrayList;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;

class ShakeDetector implements SensorEventListener {

    /*
     * The gForce that is necessary to register as shake.
     * Must be greater than 1G (one earth gravity unit).
     * You can install "G-Force", by Blake La Pierre
     * from the Google Play Store and run it to see how
     *  many G's it takes to register a shake
     */



    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    private OnShakeListener mListener;
    private ArrayList<Float> detectionCount = new ArrayList<>();
    private long mShakeTimestamp;

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (mListener != null) {
                if (event.values[0] > 3) {
                    detectionCount.add(event.values[0]);

                    final long now = System.currentTimeMillis();
                    if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                        return;
                    }
                    // reset the shake count after 3 seconds of no shakes
                    if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                        detectionCount.clear();
                    }

                    Log.e("Shake count", "" + detectionCount.size());
                    mShakeTimestamp = now;
                    if (detectionCount.size() > 4) {
                        mListener.onShake(detectionCount.size());
                        detectionCount.clear();
                        detectionCount = new ArrayList<>();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Exception", "" + e.getMessage());
            ExceptionTracker.track(e);
        }

    }


    public interface OnShakeListener {

        void onShake(int count);
    }
}
