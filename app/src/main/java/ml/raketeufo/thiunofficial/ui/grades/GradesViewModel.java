package ml.raketeufo.thiunofficial.ui.grades;

import android.app.Application;
import android.os.Handler;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ml.raketeufo.thirestbridge.api.model.Exam;
import ml.raketeufo.thirestbridge.api.model.Grade;
import ml.raketeufo.thirestbridge.api.model.GradesResponse;
import ml.raketeufo.thiunofficial.helpers.ApiHelper;
import ml.raketeufo.thiunofficial.helpers.GradeGroup;
import retrofit2.Response;

public class GradesViewModel extends AndroidViewModel {

    private final Handler mHandler;
    private MutableLiveData<Map<GradeGroup, Integer>> mGradesMap;
    private ApiHelper apiHelper;
    private boolean running = false;
    private MutableLiveData<List<Grade>> mSuccessExams;
    private MutableLiveData<List<Grade>> mMissingExams;
    private MutableLiveData<List<Grade>> mDeadlineExams;

    public GradesViewModel(Application application) {
        super(application);
        apiHelper = ApiHelper.get(getApplication().getApplicationContext());
        mGradesMap = new MutableLiveData<>();

        mSuccessExams = new MutableLiveData<>();
        mMissingExams = new MutableLiveData<>();
        mDeadlineExams = new MutableLiveData<>();

        mHandler = new Handler();
    }

    public MutableLiveData<Map<GradeGroup, Integer>> getGradesMap() {
        return mGradesMap;
    }

    public MutableLiveData<List<Grade>> getSuccessExams() {
        return mSuccessExams;
    }

    public MutableLiveData<List<Grade>> getMissingExams() {
        return mMissingExams;
    }

    public MutableLiveData<List<Grade>> getDeadlineExams() {
        return mDeadlineExams;
    }

    public synchronized void fetchData() {
        if (running) {
            return;
        }
        running = true;
        Thread userGradesThread = new Thread(() -> {
            try {
                Response<GradesResponse> gradesResponse = apiHelper.getApi().userGradesGet().execute();
                GradesResponse gradesRes = gradesResponse.body();
                if (gradesRes.isOk()) {
                    Map<GradeGroup, Integer> gradesHashmap = new HashMap<>();
                    List<Grade> successExams = new ArrayList<>();
                    List<Grade> deadlineExams = new ArrayList<>();
                    List<Grade> missingExams = new ArrayList<>();
                    for (Grade grade : gradesRes.getGrades()) {
                        Double note = grade.getNote();
                        GradeGroup gradeGroup = GradeGroup.find(note);
                        if (note > 0) {
                            Integer count = gradesHashmap.getOrDefault(gradeGroup, 0);
                            gradesHashmap.put(gradeGroup, count + 1);
                            if (note <= 4) {
                                successExams.add(grade);
                            } else {
                                deadlineExams.add(grade);
                            }
                        } else {
                            missingExams.add(grade);
                        }
                    }

                    mHandler.post(() -> {
                        mGradesMap.setValue(gradesHashmap);
                        mSuccessExams.setValue(successExams);
                        mMissingExams.setValue(missingExams);
                        mDeadlineExams.setValue(deadlineExams);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            running = false;
        });
        userGradesThread.start();
    }
}