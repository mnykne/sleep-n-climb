package sleepnclimb.fragments;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sleepnclimb.R;
import sleepnclimb.activities.AlarmSetupActivity;
import sleepnclimb.daos.AlarmDao;
import sleepnclimb.databases.AppDatabase;
import sleepnclimb.models.Alarm;
import sleepnclimb.utils.AlarmUtils;

public class AlarmListFragment extends Fragment {

    AlarmAdapter mAlarmAdapter;
    View mFab;
    public AlarmViewModel mAlarmViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_alarm_list,
                container, false);
        mAlarmAdapter = new AlarmAdapter(this);
        mAlarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);
        mAlarmViewModel.getAll().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(@Nullable List<Alarm> newData) {
                mAlarmAdapter.setData(newData);
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAlarmAdapter);
        mFab = view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AlarmSetupActivity.class);
                intent.putExtra("cid", 0);
                startActivity(intent);
            }
        });
        return view;
    }

    public static class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {
        private List<Alarm> mData;
        private AlarmListFragment mParent;

        AlarmAdapter(AlarmListFragment parent) {
            mParent = parent;
            mData = new ArrayList<>();
        }

        static class MyViewHolder extends RecyclerView.ViewHolder {
            View view;
            TextView timeView;
            TextView periodView;
            SwitchCompat toggleButton;
            MyViewHolder(View v) {
                super(v);
                view = v;
                timeView = v.findViewById(R.id.alarm_time);
                periodView = v.findViewById(R.id.alarm_period);
                toggleButton = v.findViewById(R.id.alarm_toggle);
            }
        }

        public Alarm getItem(int position) {
            return mData.get(position);
        }

        void setData(List<Alarm> newData) {
            mData = new ArrayList<>();
            for (Alarm a : newData) {
                if (a.cid != 1) {
                    mData.add(a);
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public AlarmAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_alarm_item, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Alarm alarm = mData.get(position);
            holder.view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            mParent.mAlarmViewModel.delete(alarm);
                            return true;
                        }
                    });
                }
            });
            holder.toggleButton.setChecked(AlarmUtils.active(mParent.getContext(), alarm));
            holder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        AlarmUtils.schedule(mParent.getContext(), alarm);
                    } else {
                        AlarmUtils.cancel(mParent.getContext(), alarm);
                    }
                }
            });
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(alarm.alarmTime);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            holder.timeView.setText(sdf.format(calendar.getTimeInMillis()));
            int am_pm = calendar.get(Calendar.AM_PM);
            if (am_pm == Calendar.AM) {
                holder.periodView.setText("AM");
            } else if (am_pm == Calendar.PM) {
                holder.periodView.setText("PM");
            }
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AlarmSetupActivity.class);
                    intent.putExtra("cid", alarm.cid);
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    public static class AlarmViewModel extends AndroidViewModel {
        private AlarmDao mDao;
        private ExecutorService mExecutorService;

        public AlarmViewModel(@NonNull Application application) {
            super(application);
            mDao = AppDatabase.getInstance(application).alarmDao();
            mExecutorService = Executors.newSingleThreadExecutor();
        }

        LiveData<List<Alarm>> getAll() {
            return mDao.getAll();
        }

        void delete(final Alarm alarm) {
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    mDao.delete(alarm);
                }
            });
        }
    }
}
