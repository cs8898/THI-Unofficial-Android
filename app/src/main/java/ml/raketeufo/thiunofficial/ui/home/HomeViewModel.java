package ml.raketeufo.thiunofficial.ui.home;

import android.app.Application;
import android.os.Handler;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

import ml.raketeufo.thirestbridge.api.model.PersDataResponse;
import ml.raketeufo.thirestbridge.api.model.UserInformation;
import ml.raketeufo.thiunofficial.helpers.ApiHelper;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {

    private final Handler mHandler;
    private ApiHelper apiHelper;
    private boolean running = false;

    private MutableLiveData<UserInformation> mUserInformation;

    public HomeViewModel(Application application) {
        super(application);
        apiHelper = ApiHelper.get(getApplication().getApplicationContext());
        mUserInformation = new MutableLiveData<>();
        mHandler = new Handler();
    }

    public LiveData<UserInformation> getUserInformation() {
        return mUserInformation;
    }

    public void fetchData() {
        if (running) {
            return;
        }
        running = true;
        Thread userInformationThread = new Thread(() -> {
            try {
                Response<PersDataResponse> persDataResponse = apiHelper.getApi().userGet().execute();
                PersDataResponse persDataRes = persDataResponse.body();
                if (persDataRes != null && persDataRes.isOk()) {
                    mHandler.post(() -> mUserInformation.setValue(persDataRes.getInfo()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            running = false;
        });
        userInformationThread.start();
    }
}