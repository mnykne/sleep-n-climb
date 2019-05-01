package sleepnclimb.fragments;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import sleepnclimb.R;
import sleepnclimb.daos.CheckpointDao;
import sleepnclimb.databases.AppDatabase;
import sleepnclimb.models.Checkpoint;

public class StatusFragment extends Fragment {

    TextView mAltitudeIndicator;
    TextView mOxygenIndicator;
    LineChart mAltitudeHistory;
    LineChart mOxygenHistory;
    CheckpointViewModel mCheckpointViewModel;
    Observer<List<Checkpoint>> mCheckpointObserver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status,
                container, false);
        mAltitudeIndicator = view.findViewById(R.id.altitude_indicator);
        mOxygenIndicator = view.findViewById(R.id.oxygen_indicator);
        mAltitudeHistory = view.findViewById(R.id.altitude_history);
        mOxygenHistory = view.findViewById(R.id.oxygen_history);
        styleChart(mAltitudeHistory);
        styleChart(mOxygenHistory);
        mCheckpointViewModel = ViewModelProviders.of(this).get(CheckpointViewModel.class);
        mCheckpointObserver = new Observer<List<Checkpoint>>() {
            @Override
            public void onChanged(@Nullable List<Checkpoint> checkpoints) {
                updateAltitudeHistory(checkpoints);
                updateOxygenHistory(checkpoints);
            }
        };
        mCheckpointViewModel.getAll().observe(StatusFragment.this, mCheckpointObserver);
        return view;
    }

    private void styleChart(LineChart chart) {
        Resources res = getResources();
        chart.setNoDataTextColor(res.getColor(R.color.colorWhite));
        chart.getPaint(Chart.PAINT_INFO).setTextSize(32.0f);
        chart.getXAxis().setGridColor(res.getColor(R.color.overlay));
        chart.getAxisRight().setGridColor(res.getColor(R.color.overlay));
        chart.getAxisLeft().setGridColor(res.getColor(R.color.overlay));
        chart.getXAxis().setTextColor(res.getColor(R.color.colorWhite));
        chart.getAxisRight().setTextColor(res.getColor(R.color.colorWhite));
        chart.getAxisLeft().setTextColor(res.getColor(R.color.colorWhite));
        chart.getAxisLeft().setDrawLabels(true);
        chart.getAxisRight().setDrawLabels(false);
        chart.getXAxis().setDrawLabels(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.setDrawBorders(false);
        chart.invalidate();
    }

    private void updateAltitudeHistory(final List<Checkpoint> checkpoints) {
        LineChart chart = mAltitudeHistory;
        List<Entry> entries = new ArrayList<>();
        int altitude = 0;
        entries.add(new Entry(0, altitude));
        for (int i = 0; i < checkpoints.size(); i++) {
            altitude += checkpoints.get(i).altitude;
            entries.add(new Entry(i+1, altitude));
        }
        LineDataSet set = new LineDataSet(entries, "");
        set.setCircleRadius(0);
        set.setColor(getResources().getColor(R.color.colorAccent));
        LineData data = new LineData(set);
        data.setDrawValues(false);
        chart.getLegend().setEnabled(false);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
        chart.notifyDataSetChanged();
        chart.invalidate();
        mAltitudeIndicator.setText(String.valueOf(altitude));
    }

    private void updateOxygenHistory(final List<Checkpoint> checkpoints) {
        LineChart chart = mOxygenHistory;
        List<Entry> entries = new ArrayList<>();
        int oxygen = 50;
        entries.add(new Entry(0, oxygen));
        for (int i = 0; i < checkpoints.size(); i++) {
            oxygen += checkpoints.get(i).oxygen;
            entries.add(new Entry(i+1, oxygen));
        }
        LineDataSet set = new LineDataSet(entries, "");
        LineData data = new LineData(set);
        data.setDrawValues(false);
        chart.getLegend().setEnabled(false);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
        mOxygenIndicator.setText(String.valueOf(oxygen));
    }

    public static class CheckpointViewModel extends AndroidViewModel {
        private CheckpointDao mDao;

        public CheckpointViewModel(@NonNull Application application) {
            super(application);
            mDao = AppDatabase.getInstance(application).checkpointDao();
        }

        LiveData<List<Checkpoint>> getAll() {
            return mDao.getAll();
        }
    }
}
