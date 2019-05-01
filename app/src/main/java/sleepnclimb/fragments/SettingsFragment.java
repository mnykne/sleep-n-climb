package sleepnclimb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import sleepnclimb.R;
import sleepnclimb.utils.CheckpointUtils;

public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,
                container, false);
        Button resetHistoryButton = view.findViewById(R.id.reset_history);
        resetHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckpointUtils.reset(getContext());
            }
        });
//        resetHistoryButton.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                AppDatabase.populateDemo(getContext());
//                Toast.makeText(
//                        getContext(),
//                        "Populated DB with demo entries",
//                        Toast.LENGTH_SHORT
//                ).show();
//                return false;
//            }
//        });
        return view;
    }
}
