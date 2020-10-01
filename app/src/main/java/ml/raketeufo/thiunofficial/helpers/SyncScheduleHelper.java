package ml.raketeufo.thiunofficial.helpers;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.PeriodicSync;
import android.os.Bundle;

import java.util.List;

import ml.raketeufo.thiunofficial.R;

public class SyncScheduleHelper {
    public static final long SYNC_INTERVAL = 60L * 60L * 2L; //Sync every 2 Hours

    public static void schedule(Context context) {
        if (AccountManagerHelper.get(context).hasAccount()) {
            Account mAccount = AccountManagerHelper.get(context).getAccount();
            String authority = context.getString(R.string.stub_provider_authorities);
            ContentResolver mResolver = context.getContentResolver();
            List<PeriodicSync> addedSyncs = ContentResolver.getPeriodicSyncs(mAccount, authority);
            if (addedSyncs.isEmpty()) {
                ContentResolver.addPeriodicSync(
                        mAccount,
                        authority,
                        Bundle.EMPTY,
                        SYNC_INTERVAL);
            }
        }
    }

    public static void sync(Context context) {
        if (AccountManagerHelper.get(context).hasAccount()) {
            Account mAccount = AccountManagerHelper.get(context).getAccount();
            String authority = context.getString(R.string.stub_provider_authorities);
            ContentResolver mResolver = context.getContentResolver();
            ContentResolver.requestSync(mAccount,
                    authority,
                    Bundle.EMPTY);
        }
    }
}
