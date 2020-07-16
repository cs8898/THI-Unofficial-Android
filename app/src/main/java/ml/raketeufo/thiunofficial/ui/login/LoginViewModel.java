package ml.raketeufo.thiunofficial.ui.login;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ml.raketeufo.thiunofficial.data.LoginRepository;
import ml.raketeufo.thiunofficial.data.Result;
import ml.raketeufo.thiunofficial.data.model.LoggedInUser;
import ml.raketeufo.thiunofficial.R;

public class LoginViewModel extends ViewModel {

    private final Handler mHandler;
    private final LoginRepository loginRepository;
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        mHandler = new Handler();
    }

    public void handleAutoLogin() {
        Thread loginThread = new Thread(() -> {
            Result<LoggedInUser> result = loginRepository.autoLogin();
            if (result instanceof Result.Success) {
                mHandler.post(() -> {
                    LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                    loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
                });
            }else{
                mHandler.post(() -> {
                    loginResult.setValue(new LoginResult(R.string.login_failed));
                });
            }
        });
        loginThread.start();
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Thread loginThread = new Thread(() -> {
            Result<LoggedInUser> result = loginRepository.login(username, password);

            mHandler.post(() -> {
                if (result instanceof Result.Success) {
                    LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                    loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
                } else {
                    loginResult.setValue(new LoginResult(R.string.login_failed));
                }
            });
        });
        loginThread.start();
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        return username != null && !username.trim().isEmpty();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && !password.trim().isEmpty();
    }
}