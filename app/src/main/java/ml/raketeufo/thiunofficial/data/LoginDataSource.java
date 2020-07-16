package ml.raketeufo.thiunofficial.data;

import android.content.Context;
import android.util.Log;

import ml.raketeufo.thirestbridge.api.model.PersDataResponse;
import ml.raketeufo.thirestbridge.api.model.UserInformation;
import ml.raketeufo.thiunofficial.data.model.LoggedInUser;
import ml.raketeufo.thiunofficial.helpers.AccountManagerHelper;
import ml.raketeufo.thiunofficial.helpers.ApiHelper;
import ml.raketeufo.thiunofficial.helpers.PreferencesHelper;
import retrofit2.Response;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private final Context context;

    public LoginDataSource(Context context) {
        this.context = context;
    }

    public Result<LoggedInUser> login(String username, String password) {

        ApiHelper apiHelper = ApiHelper.get(this.context.getApplicationContext());
        apiHelper.setCredentials(username, password);
        // TODO: handle loggedInUser authentication
        LoggedInUser user = getLoggedInUser(apiHelper);
        if (user != null)
            return new Result.Success<>(user);

        return new Result.Error(new IOException("Error logging in"));
    }

    public void logout() {
        // TODO: revoke authentication
    }

    public LoggedInUser getLoggedInUser(ApiHelper apiHelper) {
        Response<PersDataResponse> response;
        try {
            response = apiHelper.getApi().userGet().execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        PersDataResponse persDataResponse = response.body();
        if (persDataResponse != null && persDataResponse.isOk()) {
            UserInformation userInformation = persDataResponse.getInfo();
            return new LoggedInUser(userInformation.getUser(), userInformation.getVorname(), apiHelper.getToken());
        }
        Log.e("LoginDataSource", response.toString());
        return null;
    }

    public Result<LoggedInUser> autoLogin(AccountManagerHelper accountManagerHelper) {
        if (accountManagerHelper.hasAccount()) {
            ApiHelper apiHelper = ApiHelper.get(this.context.getApplicationContext());

            if (PreferencesHelper.get(this.context).useAutoLogin() && accountManagerHelper.hasPassword()) {
                apiHelper.setCredentials(accountManagerHelper.getUsername(), accountManagerHelper.getPassword());
            }
            if (accountManagerHelper.hasToken()) {
                apiHelper.setToken(accountManagerHelper.getToken());
                LoggedInUser loggedInUser = this.getLoggedInUser(apiHelper);
                if (loggedInUser != null) {
                    return new Result.Success<LoggedInUser>(loggedInUser);
                }
            }
        }
        return null;
    }
}