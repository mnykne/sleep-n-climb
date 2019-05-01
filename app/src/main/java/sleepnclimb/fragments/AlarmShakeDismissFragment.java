package sleepnclimb.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import sleepnclimb.R;
import sleepnclimb.activities.WakeUpActivity;

public class AlarmShakeDismissFragment extends Fragment implements SensorListener {

    SensorManager mSensorManager;
    long mLastUpdate;
    float mLastX;
    float mLastY;
    float mLastZ;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_step_dismiss,
                container, false);
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mSensorManager.registerListener(
            this,
            Sensor.TYPE_LINEAR_ACCELERATION,
            SensorManager.SENSOR_DELAY_GAME
        );
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        // Following:
        // https://stackoverflow.com/questions/5271448/how-to-detect-shake-event-with-android
        long curTime = System.currentTimeMillis();
        if ((curTime - mLastUpdate) > 300) {
            long diffTime = (curTime - mLastUpdate);
            float x0 = mLastX;
            float y0 = mLastY;
            float z0 = mLastZ;
            float x1 = values[SensorManager.DATA_X];
            float y1 = values[SensorManager.DATA_Y];
            float z1 = values[SensorManager.DATA_Z];
            if (mLastUpdate != 0) {
                float magnitude = Math.abs(
                        (x1 - x0)
                                + (y1 - y0)
                                + (z1 - z0)
                ) / diffTime * 10000;

                if (magnitude > 2000) {
                    ((WakeUpActivity) getActivity()).dismissAndFinalize();
                }
            }
            mLastX = x1;
            mLastY = y1;
            mLastZ = z1;
            mLastUpdate = curTime;
        }
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }

    @Override
    public void onStop() {
        mSensorManager.unregisterListener(this);
        super.onStop();
    }
}
