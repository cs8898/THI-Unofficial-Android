package ml.raketeufo.thiunofficial.ui.timetable;

import android.app.Application;
import android.os.Handler;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ml.raketeufo.thirestbridge.api.model.Exam;
import ml.raketeufo.thirestbridge.api.model.ExamsResponse;
import ml.raketeufo.thirestbridge.api.model.Timetable;
import ml.raketeufo.thirestbridge.api.model.TimetableResponse;
import ml.raketeufo.thiunofficial.helpers.ApiHelper;
import retrofit2.Response;

public class TimetableViewModel extends AndroidViewModel {

    private final Handler mHandler;
    private ApiHelper apiHelper;
    private boolean running = false;
    private MutableLiveData<List<TimetableEvent>> mEvents;

    public TimetableViewModel(Application application) {
        super(application);
        apiHelper = ApiHelper.get(getApplication().getApplicationContext());
        mEvents = new MutableLiveData<>();
        mHandler = new Handler();
    }

    public LiveData<List<TimetableEvent>> getEvents() {
        return mEvents;
    }

    public void fetchData() {
        if (running) {
            return;
        }
        running = true;
        Thread userTimetableThread = new Thread(() -> {
            try {
                List<TimetableEvent> events = new ArrayList<>();
                Response<TimetableResponse> timetableResponse = apiHelper.getApi().userTimetableGet("0").execute();
                TimetableResponse timetableRes = timetableResponse.body();
                if (timetableRes.isOk()) {
                    Timetable timetable = timetableRes.getTimetable();
                    events.addAll(TimetableEvent.eventsFrom(getApplication().getApplicationContext(), timetable));
                }

                Response<ExamsResponse> examsResponse = apiHelper.getApi().userExamsGet().execute();
                ExamsResponse examsRes = examsResponse.body();
                if (examsRes.isOk()) {
                    List<Exam> exams = examsRes.getExams();
                    events.addAll(TimetableEvent.eventsFrom(getApplication().getApplicationContext(), exams));
                }

                mHandler.post(() -> {
                    mEvents.setValue(events);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            running = false;
        });
        userTimetableThread.start();
    }
}