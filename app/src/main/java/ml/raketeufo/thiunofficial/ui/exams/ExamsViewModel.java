package ml.raketeufo.thiunofficial.ui.exams;

import android.app.Application;
import android.os.Handler;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.threeten.bp.LocalDateTime;

import java.io.IOException;
import java.util.List;

import ml.raketeufo.thirestbridge.api.model.Exam;
import ml.raketeufo.thirestbridge.api.model.ExamsResponse;
import ml.raketeufo.thiunofficial.helpers.ApiHelper;
import retrofit2.Response;

public class ExamsViewModel extends AndroidViewModel {

    private final Handler mHandler;
    private ApiHelper apiHelper;
    private boolean running = false;
    private MutableLiveData<List<Exam>> mExams;

    public ExamsViewModel(Application application) {
        super(application);
        apiHelper = ApiHelper.get(getApplication().getApplicationContext());

        mExams = new MutableLiveData<>();

        mHandler = new Handler();
    }

    public MutableLiveData<List<Exam>> getExams() {
        return mExams;
    }

    public synchronized void fetchData() {
        if (running) {
            return;
        }
        running = true;
        Thread userGradesThread = new Thread(() -> {
            try {
                Response<ExamsResponse> examsResponse = apiHelper.getApi().userExamsGet().execute();
                ExamsResponse examsRes = examsResponse.body();
                if (examsRes.isOk()) {
                    List<Exam> exams = examsRes.getExams();
                    exams.sort((a, b) -> {
                        LocalDateTime aDate = a.getZeit();
                        LocalDateTime bDate = b.getZeit();
                        if (aDate == null && bDate != null) {
                            return 1;
                        } else if (aDate != null && bDate == null) {
                            return -1;
                        } else if (aDate == null && bDate == null) {
                            return 0;
                        } else {
                            return aDate.compareTo(bDate);
                        }
                    });
                    mHandler.post(() -> {
                        mExams.setValue(exams);
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