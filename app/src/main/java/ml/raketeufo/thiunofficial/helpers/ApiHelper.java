package ml.raketeufo.thiunofficial.helpers;

import android.content.Context;
import android.util.Log;

import org.apache.oltu.oauth2.common.token.BasicOAuthToken;

import ml.raketeufo.thirestbridge.api.ApiClient;
import ml.raketeufo.thirestbridge.api.api.DefaultApi;
import ml.raketeufo.thirestbridge.api.auth.OAuth;
import ml.raketeufo.thiunofficial.R;
import okhttp3.Interceptor;

public class ApiHelper implements OAuth.AccessTokenListener {
    private static volatile ApiHelper instance;
    private static volatile DefaultApi apiService;

    private final ApiClient apiClient;
    private final String baseURL;
    private final AccountManagerHelper accountManagerHelper;

    private ApiHelper(Context context) {
        this.baseURL = context.getString(R.string.api_base_url);
        this.apiClient = new ApiClient(baseURL, ApiClient.AuthNames.JWT_AuthToken);
        this.accountManagerHelper = AccountManagerHelper.get(context);
        init(context);
    }

    private void init(Context context) {
        //fill Credentials from Account Manager
        if (accountManagerHelper.hasAccount()) {
            String username = accountManagerHelper.getUsername();
            String password = accountManagerHelper.getPassword();
            String token = accountManagerHelper.getToken();
            setCredentials(username, password);
            setToken(token);
        }
        apiClient.registerAccessTokenListener(this);
    }

    public static ApiHelper get(Context context) {
        if (instance == null) {
            instance = new ApiHelper(context);
        }
        return instance;
    }

    public void setCredentials(String username, String password) {
        this.apiClient.setCredentials(username, password);
    }

    public void setToken(String token) {
        this.apiClient.setAccessToken(token);
    }

    public String getToken() {
        Interceptor interceptor = this.apiClient.getApiAuthorizations().getOrDefault(ApiClient.AuthNames.JWT_AuthToken.getTitle(), null);
        if (interceptor instanceof OAuth) {
            OAuth oAuthInterceptor = (OAuth) interceptor;
            return oAuthInterceptor.getAccessToken();
        }
        return "";
    }

    public DefaultApi getApi() {
        if (apiService == null) {
            apiService = this.apiClient.createService(DefaultApi.class);
        }
        return apiService;
    }

    @Override
    public void notify(BasicOAuthToken basicOAuthToken) {
        Log.d("ApiHelper", "Recieved new Auth Token...");
        accountManagerHelper.updateToken(
                basicOAuthToken.getAccessToken()
        );
    }
}
