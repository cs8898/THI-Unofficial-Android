package ml.raketeufo.thiunofficial.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.threeten.bp.LocalDateTime;

import java.util.Locale;
import java.util.stream.Collectors;

import ml.raketeufo.thirestbridge.api.model.Grade;
import ml.raketeufo.thirestbridge.api.model.PersDataResponse;
import ml.raketeufo.thirestbridge.api.model.Pruefungsordnung;
import ml.raketeufo.thirestbridge.api.model.UserInformation;
import ml.raketeufo.thiunofficial.R;
import ml.raketeufo.thiunofficial.helpers.LocalDateTimeHelper;
import ml.raketeufo.thiunofficial.ui.grades.GradesListAdapter;
import ml.raketeufo.thiunofficial.ui.home.upcoming.UpcomingListAdapter;
import ml.raketeufo.thiunofficial.ui.timetable.TimetableEvent;
import ml.raketeufo.thiunofficial.ui.timetable.TimetableViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TimetableViewModel timetableViewModel;
    private UpcomingListAdapter mUpcomingAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        timetableViewModel =
                ViewModelProviders.of(this).get(TimetableViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView userVorname = root.findViewById(R.id.user_firstName);
        final TextView userName = root.findViewById(R.id.user_name);
        final TextView userMatr = root.findViewById(R.id.user_matr);
        final TextView userStdGr = root.findViewById(R.id.user_studiengruppe);
        final TextView userSpo = root.findViewById(R.id.user_spo);

        final TextView printerBalance = root.findViewById(R.id.user_printerBalance);
        final TextView bibNr = root.findViewById(R.id.user_bibNr);

        final RecyclerView recyclerView = root.findViewById(R.id.upcomingEvents_recyclerView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mUpcomingAdapter = new UpcomingListAdapter(getContext(), new TimetableEvent[0]);
        recyclerView.setAdapter(mUpcomingAdapter);

        homeViewModel.getUserInformation().observe(getViewLifecycleOwner(), new Observer<UserInformation>() {
            @Override
            public void onChanged(@Nullable UserInformation user) {
                userVorname.setText(user.getVorname());
                userName.setText(user.getName());
                userMatr.setText(user.getMatrikelNummer());
                userStdGr.setText(user.getStudiengang() + " " + user.getStudiengruppe());
                Pruefungsordnung po = user.getPruefungsordnung();

                String spoString = "SPO: <a href=\"" + po.getUrl() + "\">" + po.getVersion() + "</a>";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    userSpo.setText(Html.fromHtml(spoString, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    userSpo.setText(Html.fromHtml(spoString));
                }
                userSpo.setMovementMethod(LinkMovementMethod.getInstance());

                printerBalance.setText(String.format(Locale.GERMANY, "%.2f €", user.getPrinterCredit()));

                bibNr.setText(user.getBibliotheksNummer());
            }
        });

        timetableViewModel.getEvents().observe(getViewLifecycleOwner(), timetableEvents -> {
            LocalDateTime now = LocalDateTime.now();
            TimetableEvent[] upcomingEvents = timetableEvents.stream()
                    .filter(e -> e.getEventType() == TimetableEvent.EventType.COURSE || e.getEventType() == TimetableEvent.EventType.EXAM)
                    .filter(e -> e.getStartTime().isAfter(now) || e.getEndTime().isAfter(now))
                    .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                    .limit(25)
                    .toArray(TimetableEvent[]::new);
            mUpcomingAdapter.setDataset(upcomingEvents);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.fetchData();
        timetableViewModel.fetchData();
    }
}