package ml.raketeufo.thiunofficial;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.aboutlibraries.LibsBuilder;

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ml.raketeufo.thiunofficial.data.LoginDataSource;
import ml.raketeufo.thiunofficial.data.LoginRepository;
import ml.raketeufo.thiunofficial.helpers.AccountManagerHelper;
import ml.raketeufo.thiunofficial.ui.login.LoginViewModel;
import ml.raketeufo.thiunofficial.ui.login.LoginViewModelFactory;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private LoginRepository loginRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginRepository = LoginRepository.getInstance(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_timetable, R.id.nav_rooms, R.id.nav_grades, R.id.nav_exams, R.id.nav_food)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        TextView nameView = headerView.findViewById(R.id.nav_header_firstname);
        nameView.setText(loginRepository.getDisplayName());
        TextView usernameView = headerView.findViewById(R.id.nav_header_username);
        usernameView.setText(loginRepository.getUsername());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                new LibsBuilder()
                        .withFields(R.string.class.getFields())
                        .withAutoDetect(true)
                        .withLicenseShown(true)
                        .withVersionShown(true)
                        .start(this);
                return true;
            case R.id.action_logout:
                AccountManagerHelper.get(this).logout();
                Intent launcherIntent = new Intent(this, LauncherActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(launcherIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}