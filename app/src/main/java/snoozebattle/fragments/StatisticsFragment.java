package snoozebattle.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import snoozebattle.R;

public class StatisticsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics,
                container, false);
        // TODO CLEANUP
        prepareDismissFrequency(view);
        // TODO CLEANUP
        prepareExperience(view);
        return view;
    }

    private void prepareDismissFrequency(View view) {
        // TODO CLEANUP
        BarChart chart = view.findViewById(R.id.dismiss_frequency);
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 1));
        entries.add(new BarEntry(1, 2));
        entries.add(new BarEntry(2, 1));
        entries.add(new BarEntry(3, 2));
        entries.add(new BarEntry(4, 3));
        entries.add(new BarEntry(5, 3));
        entries.add(new BarEntry(6, 3));
        BarDataSet set = new BarDataSet(entries, "");
        BarData data = new BarData(set);
        data.setDrawValues(false);
        data.setBarWidth(1);
        chart.getLegend().setEnabled(false);
        chart.setData(data);
        chart.setFitBars(true);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String [] {
                "Mon",
                "Tue",
                "Wed",
                "Thu",
                "Fri",
                "Sat",
                "Sun"
        }));
    }

    private void prepareExperience(View view) {
        // TODO CLEANUP
        LineChart chart = view.findViewById(R.id.experience);
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 11));
        entries.add(new Entry(1, 22));
        entries.add(new Entry(2, 53));
        entries.add(new Entry(3, 65));
        entries.add(new Entry(4, 65));
        entries.add(new Entry(5, 70));
        entries.add(new Entry(6, 75));
        LineDataSet set = new LineDataSet(entries, "");
        LineData data = new LineData(set);
        data.setDrawValues(false);
        chart.getLegend().setEnabled(false);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String [] {
                "Mon",
                "Tue",
                "Wed",
                "Thu",
                "Fri",
                "Sat",
                "Sun"
        }));
    }
}
