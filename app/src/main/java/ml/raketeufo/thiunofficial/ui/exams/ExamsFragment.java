package ml.raketeufo.thiunofficial.ui.exams;

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

import ml.raketeufo.thirestbridge.api.model.Exam;
import ml.raketeufo.thirestbridge.api.model.Grade;
import ml.raketeufo.thiunofficial.R;
import ml.raketeufo.thiunofficial.helpers.GradeGroup;

public class ExamsFragment extends Fragment {

    private ExamsViewModel examsViewModel;
    private RecyclerView recyclerView;
    private ExamsListAdapter mAdapter;
    private Exam[] examList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        examsViewModel =
                ViewModelProviders.of(this).get(ExamsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_exams, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ExamsListAdapter(getContext(), new Exam[0]);
        recyclerView.setAdapter(mAdapter);

        //currentTab = "Success";

        examsViewModel.getExams().observe(getViewLifecycleOwner(), exams -> {
            this.examList = exams.toArray(new Exam[0]);
            this.mAdapter.setDataset(this.examList);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        examsViewModel.fetchData();
    }
}