package ml.raketeufo.thiunofficial.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ml.raketeufo.thiunofficial.helpers.AccountManagerHelper;
import ml.raketeufo.thiunofficial.ui.login.LoginActivity;

public class Authenticator extends AbstractAccountAuthenticator {
    private final Context mContext;

    public Authenticator(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, LoginActivity.class);

        // This key can be anything. Try to use your domain/package
        intent.putExtra(AccountManagerHelper.ACCOUNT_TYPE, accountType);

        // This key can be anything too. It's just a way of identifying the token's type (used when there are multiple permissions)
        intent.putExtra(AccountManagerHelper.TOKEN_TYPE, authTokenType);

        // This key can be anything too. Used for your reference. Can skip it too.
        intent.putExtra("is_adding_new_account", true);

        // Copy this exactly from the line below.
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();

    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        AccountManagerHelper amHelper = AccountManagerHelper.get(mContext);
        String token = amHelper.getToken(authTokenType);
        if (token == null || token.isEmpty()) {
            //Fetch New Token
        }

        if (token != null && !token.isEmpty()) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
            return result;
        }

        // If you reach here, person needs to login again. or sign up

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity which is the AccountsActivity in my case.
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountManagerHelper.ACCOUNT_TYPE, account.type);
        intent.putExtra(AccountManagerHelper.TOKEN_TYPE, authTokenType);

        Bundle retBundle = new Bundle();
        retBundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return retBundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
