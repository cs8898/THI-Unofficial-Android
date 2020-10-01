package ml.raketeufo.thiunofficial.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.widget.Toast;

import ml.raketeufo.thiunofficial.R;

public class AccountManagerHelper {
    public final String ACCOUNT_TYPE;
    public final String TOKEN_TYPE;

    private final AccountManager accountManager;
    private final Context context;

    private AccountManagerHelper(Context context) {
        this.context = context;
        this.ACCOUNT_TYPE = context.getString(R.string.account_type);
        this.TOKEN_TYPE = context.getString(R.string.token_type);
        this.accountManager = AccountManager.get(context);
    }

    public static AccountManagerHelper get(Context context) {
        return new AccountManagerHelper(context);
    }

    private Account getStoredAccount() {
        Account[] accounts = this.accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length == 1) {
            return accounts[0];
        } else if (accounts.length > 1) {
            Toast.makeText(this.context, "Whoops Found Too Many Accounts...", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public String getType() {
        Account account = getStoredAccount();
        if (account != null) {
            return account.type;
        }
        return null;
    }

    public String getPassword() {
        Account account = getStoredAccount();
        if (account != null) {
            return accountManager.getPassword(account);
        }
        return null;
    }

    public boolean hasPassword() {
        String password = getPassword();
        return password != null && !password.isEmpty();
    }

    public String getToken() {
        return getToken(TOKEN_TYPE);
    }

    public String getToken(String tokenType) {
        Account account = getStoredAccount();
        if (account != null) {
            return accountManager.peekAuthToken(account, tokenType);
        }
        return null;
    }

    public boolean hasToken() {
        String accessToken = getToken();
        return accessToken != null && !accessToken.isEmpty();
    }

    public boolean hasAccount() {
        return getStoredAccount() != null;
    }

    public String getUsername() {
        Account account = getStoredAccount();
        if (account != null) {
            return account.name;
        }
        return null;
    }

    public void updateAccount(String username, String password, String accessToken) {
        Account account = getStoredAccount();
        if (account == null) {
            account = createAccount(username, password);
        }
        if (password != null && !password.equals(getPassword()))
            accountManager.setPassword(account, password);
        accountManager.setAuthToken(account, TOKEN_TYPE, accessToken);
    }

    public void updateToken(String token) {
        Account account = getStoredAccount();
        if (account != null) {
            accountManager.setAuthToken(account, TOKEN_TYPE, token);
        }
    }

    private Account createAccount(String username, String password) {
        Account account = new Account(username, ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, password, null);
        return account;
    }

    public void logout() {
        Account account = getStoredAccount();
        if (account != null) {
            accountManager.removeAccountExplicitly(account);
        }
    }

    public Account getAccount() {
        return getStoredAccount();
    }
}
