package ml.raketeufo.thiunofficial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ml.raketeufo.thiunofficial.helpers.AccountManagerHelper;
import ml.raketeufo.thiunofficial.helpers.PreferencesHelper;
import ml.raketeufo.thiunofficial.ui.login.LoginActivity;
import ml.raketeufo.thiunofficial.ui.login.LoginResult;
import ml.raketeufo.thiunofficial.ui.login.LoginViewModel;
import ml.raketeufo.thiunofficial.ui.login.LoginViewModelFactory;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    final List<Integer> availableColors = new ArrayList<>();
    final Random random = new Random();
    private ImageView logoView;
    private int lastColor = -1;
    private boolean wasClicked;
    private boolean ready;
    private View launcherLayout;
    private boolean needsLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        logoView = findViewById(R.id.logoView);

        availableColors.clear();
        //availableColors.add(R.color.thiblue);
        availableColors.add(R.color.thiorange);
        availableColors.add(R.color.thired);
        availableColors.add(R.color.thigreen);
        availableColors.add(R.color.thiyellow);
        availableColors.add(R.color.thiyellowit);

        launcherLayout = findViewById(R.id.launcherLayout);
        launcherLayout.setOnClickListener(this);
        launcherLayout.setOnLongClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        wasClicked = false;
        ready = false;
        needsLogin = false;
        init();
    }

    private void init() {
        PreferencesHelper preferencesHelper = PreferencesHelper.get(this);
        AccountManagerHelper accountManagerHelper = AccountManagerHelper.get(this);
        if (preferencesHelper.useAutoLogin() || accountManagerHelper.hasToken()) {
            LoginViewModel loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(this.getApplicationContext()))
                    .get(LoginViewModel.class);

            loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
                @Override
                public void onChanged(@Nullable LoginResult loginResult) {
                    needsLogin = loginResult == null || loginResult.getSuccess() == null;
                    ready = true;
                    new Handler().postDelayed(LauncherActivity.this::start, 300);
                }
            });
            loginViewModel.handleAutoLogin();
        }
    }

    private void start() {
        if (wasClicked || !ready) {
            return; // Do Nothing when Clicked or not ready
        }
        if (needsLogin) {
            startLogin();
        } else {
            startMain();
        }
        finish();
    }

    private void startMain() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityIntent);
    }

    private void startLogin() {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginActivityIntent);
    }

    @Override
    public void onClick(View v) {
        if (!wasClicked)
            Snackbar.make(launcherLayout, R.string.party_easter_egg, BaseTransientBottomBar.LENGTH_LONG).show();
        wasClicked = true;

        logoView.setImageResource(R.drawable.ic_thi_applogo_white);
        int newColor = lastColor;
        while (newColor == lastColor) {
            newColor = random.nextInt(availableColors.size());
        }
        logoView.setColorFilter(getColor(availableColors.get(newColor)));
        lastColor = newColor;

    }

    @Override
    public boolean onLongClick(View v) {
        wasClicked = false;
        start();
        return false;
    }
}
