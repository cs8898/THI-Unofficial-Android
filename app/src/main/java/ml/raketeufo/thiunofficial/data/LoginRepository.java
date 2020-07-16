package ml.raketeufo.thiunofficial.data;

import android.content.Context;

import ml.raketeufo.thirestbridge.api.model.UserInformation;
import ml.raketeufo.thiunofficial.data.model.LoggedInUser;
import ml.raketeufo.thiunofficial.helpers.AccountManagerHelper;
import ml.raketeufo.thiunofficial.helpers.ApiHelper;
import ml.raketeufo.thiunofficial.helpers.PreferencesHelper;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    private static volatile AccountManagerHelper accountManagerHelper;
    private final Context context;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource, Context context) {
        this.dataSource = dataSource;
        this.context = context;
    }

    public static LoginRepository getInstance(Context context) {
        if (instance == null) {
            accountManagerHelper = AccountManagerHelper.get(context);
            LoginDataSource dataSource = new LoginDataSource(context);
            instance = new LoginRepository(dataSource, context);
        }
        return instance;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource, Context context) {
        if (instance == null) {
            accountManagerHelper = AccountManagerHelper.get(context);
            instance = new LoginRepository(dataSource, context);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public String getUsername() {
        if (isLoggedIn())
            return user.getUserId();
        return null;
    }

    public String getDisplayName() {
        if (isLoggedIn())
            return user.getDisplayName();
        return null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user, String username, String password) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        accountManagerHelper.updateAccount(username, password, user.getAccessToken());
    }

    public Result<LoggedInUser> login(String username, String password) {
        // handle login
        Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData(), username, password);
        }
        return result;
    }

    public Result<LoggedInUser> autoLogin() {
        Result<LoggedInUser> result = dataSource.autoLogin(accountManagerHelper);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData(), accountManagerHelper.getUsername(), accountManagerHelper.getPassword());
        }
        return result;
    }
}