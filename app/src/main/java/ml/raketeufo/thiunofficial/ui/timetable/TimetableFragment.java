package ml.raketeufo.thiunofficial.ui.timetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alamkanak.weekview.WeekView;

import java.util.List;

import ml.raketeufo.thiunofficial.R;

public class TimetableFragment extends Fragment {

    private TimetableViewModel timetableViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        timetableViewModel =
                ViewModelProviders.of(this).get(TimetableViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timetable, container, false);
        final WeekView weekView = root.findViewById(R.id.weekView);
        timetableViewModel.getEvents().observe(getViewLifecycleOwner(), new Observer<List<TimetableEvent>>() {
            @Override
            public void onChanged(@Nullable List<TimetableEvent> events) {
                weekView.submit(events);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        timetableViewModel.fetchData();
    }
}