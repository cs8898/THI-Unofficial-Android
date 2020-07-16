package ml.raketeufo.thiunofficial.ui.grades;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ml.raketeufo.thirestbridge.api.model.Grade;
import ml.raketeufo.thiunofficial.R;
import ml.raketeufo.thiunofficial.helpers.GradeGroup;

public class GradesFragment extends Fragment {

    private GradesViewModel gradesViewModel;
    private RecyclerView recyclerView;
    private GradesListAdapter mAdapter;
    private Grade[] successDataSet;
    private Grade[] deadlineDataSet;
    private Grade[] missingDataSet;
    private String currentTab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        gradesViewModel =
                ViewModelProviders.of(this).get(GradesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_grades, container, false);

        PieChart chart = root.findViewById(R.id.chart);

        gradesViewModel.getGradesMap().observe(getViewLifecycleOwner(), gradesMap -> {
            List<PieEntry> entryList = new ArrayList<>();
            List<Integer> colorList = new ArrayList<>();
            List<Map.Entry<GradeGroup, Integer>> gradesList = new ArrayList<>(gradesMap.entrySet());
            gradesList.sort((a, b) -> Integer.compare(a.getKey().ordinal(), b.getKey().ordinal()));


            for (Map.Entry<GradeGroup, Integer> grade : gradesList) {
                PieEntry entry = new PieEntry(grade.getValue(), grade.getKey().getTitle(getContext()));
                entryList.add(entry);
                colorList.add(grade.getKey().getColor(getContext()));
            }

            PieDataSet dataSet = new PieDataSet(entryList, "Noten");
            dataSet.setColors(colorList);
            PieData data = new PieData(dataSet);
            chart.setData(data);
            chart.setHoleColor(Color.TRANSPARENT);
            chart.invalidate();
        });

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new GradesListAdapter(getContext(), new Grade[0]);
        recyclerView.setAdapter(mAdapter);

        currentTab = "Success";

        gradesViewModel.getSuccessExams().observe(getViewLifecycleOwner(), exams -> {
            this.successDataSet = exams.toArray(new Grade[0]);
            update("Success");
        });
        gradesViewModel.getMissingExams().observe(getViewLifecycleOwner(), exams -> {
            this.missingDataSet = exams.toArray(new Grade[0]);
            update("Missing");

        });
        gradesViewModel.getDeadlineExams().observe(getViewLifecycleOwner(), exams -> {
            this.deadlineDataSet = exams.toArray(new Grade[0]);
            update("Deadline");
        });

        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "Ausstehend":
                        currentTab = "Missing";
                        update("Missing");
                        break;
                    case "Frist":
                        currentTab = "Deadline";
                        update("Deadline");
                        break;
                    case "Bestanden":
                    default:
                        currentTab = "Success";
                        update("Success");
                }

                Log.d("SelectedTab", "name: " + tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return root;
    }

    public void update(String datasetName) {
        if (currentTab.equals(datasetName)) {
            switch (currentTab) {
                case "Success":
                    mAdapter.setDataset(successDataSet);
                    break;
                case "Missing":
                    mAdapter.setDataset(missingDataSet);
                    break;
                case "Deadline":
                    mAdapter.setDataset(deadlineDataSet);
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        gradesViewModel.fetchData();
    }
}