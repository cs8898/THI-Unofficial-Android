package ml.raketeufo.thiunofficial.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Calendars;
import android.util.Log;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import ml.raketeufo.thirestbridge.api.model.Exam;
import ml.raketeufo.thirestbridge.api.model.ExamsResponse;
import ml.raketeufo.thirestbridge.api.model.Timetable;
import ml.raketeufo.thirestbridge.api.model.TimetableResponse;
import ml.raketeufo.thiunofficial.R;
import ml.raketeufo.thiunofficial.helpers.AccountManagerHelper;
import ml.raketeufo.thiunofficial.helpers.ApiHelper;
import ml.raketeufo.thiunofficial.ui.timetable.TimetableEvent;
import retrofit2.Response;

public class CalendarSyncLogic {
    private static final String CALENDAR_NAME = "THI-Unofficial-Calendar";
    private final String CALENDAR_DISPLAY_NAME;
    private final Context context;
    private final ContentResolver contentResolver;
    private final AccountManagerHelper accountHelper;

    public static final String[] CALENDAR_PROJECTION = new String[]{
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    public static final String[] EVENT_PROJECTION = new String[]{
            Events._ID,
            Events.ACCOUNT_NAME,
            Events.TITLE,
            Events.DTSTART,
            Events.DTEND,
            Events.ALL_DAY,
            Events.EVENT_COLOR
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    private static final int PROJECTION_TITLE = 2;
    private static final int PROJECTION_DT_START = 3;
    private static final int PROJECTION_DT_END = 4;
    private static final int PROJECTION_ALL_DAY = 5;
    private static final int PROJECTION_EVENT_COLOR = 6;

    private final Account account;
    private final ApiHelper apiHelper;


    public CalendarSyncLogic(Context context, ContentResolver contentResolver, Account account) {
        this.context = context;
        this.contentResolver = contentResolver;
        this.account = account;
        this.accountHelper = AccountManagerHelper.get(context);
        this.apiHelper = ApiHelper.get(context);

        this.CALENDAR_DISPLAY_NAME = context.getString(R.string.calendar_display_name);
    }

    public void sync() {
        long calId = createCalendar();
        if (calId == -1) {
            Log.e("CalendarSync", "No Calendar ID Found");
            return;
        }
        List<TimetableEvent> events = fetchEvents();
        for (TimetableEvent event : events) {
            addToCalendar(event, calId);
        }
    }

    public List<TimetableEvent> fetchEvents() {
        List<TimetableEvent> events = new ArrayList<>();
        try {
            Response<TimetableResponse> timetableResponse = apiHelper.getApi().userTimetableGet("0").execute();
            TimetableResponse timetableRes = timetableResponse.body();
            if (timetableRes.isOk()) {
                Timetable timetable = timetableRes.getTimetable();
                events.addAll(TimetableEvent.eventsFrom(context, timetable));
            }

            Response<ExamsResponse> examsResponse = apiHelper.getApi().userExamsGet().execute();
            ExamsResponse examsRes = examsResponse.body();
            if (examsRes.isOk()) {
                List<Exam> exams = examsRes.getExams();
                events.addAll(TimetableEvent.eventsFrom(context.getApplicationContext(), exams));
            }
        } catch (Exception ignore) {

        }
        return events;
    }

    private long createCalendar() {
        if (accountHelper.hasAccount()) {
            String accout_name = account.name;
            String account_type = account.type;
            Uri uri = asSyncService(Calendars.CONTENT_URI);
            List<CalendarHolder> calendars = listCalendars(uri, accout_name, account_type);
            if (calendars.size() > 0) {
                updateCalendar(calendars.get(0).id);
                return calendars.get(0).id;
            }

            ContentValues cv = generateCalendarContentValues();
            Uri response = contentResolver.insert(uri, cv);

            calendars = listCalendars(uri, accout_name, account_type);
            if (calendars.size() > 0)
                return calendars.get(0).id;

        }
        return -1;
    }

    private void updateCalendar(long id) {
        Uri uri = ContentUris.withAppendedId(asSyncService(Calendars.CONTENT_URI), id);
        ContentValues values = generateCalendarContentValues();
        contentResolver.update(uri, values, null, null);
    }

    private ContentValues generateCalendarContentValues() {
        ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, account.name);
        values.put(Calendars.ACCOUNT_TYPE, account.type);
        values.put(Calendars.NAME, CALENDAR_NAME);
        values.put(Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_DISPLAY_NAME);
        values.put(Calendars.CALENDAR_COLOR, context.getColor(R.color.thiblue));
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_ROOT);
        values.put(Calendars.OWNER_ACCOUNT, account.name);
        values.put(Calendars.VISIBLE, 1);
        values.put(Calendars.SYNC_EVENTS, 1);
        values.put(Calendars.CALENDAR_TIME_ZONE, "Europe/Berlin");
        values.put(Calendars.CAN_PARTIALLY_UPDATE, 1);
        return values;
    }

    private Uri asSyncService(Uri uri) {
        return uri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, account.name)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, account.type).build();
    }

    private void addToCalendar(TimetableEvent event, long calId) {
        Uri uri = asSyncService(Events.CONTENT_URI);
        deleteEvent(event, calId);
        ContentValues cv = generateEventContentValues(event, calId);
        contentResolver.insert(uri, cv);
    }


    private ContentValues generateEventContentValues(TimetableEvent event, long calId) {
        ZoneId zoneId = ZoneId.of("Europe/Berlin");
        ContentValues values = new ContentValues();
        values.put(Events.TITLE, event.getTitle());
        values.put(Events.CALENDAR_ID, calId);
        values.put(Events.EVENT_LOCATION, event.getLocation());
        values.put(Events.ALL_DAY, event.isAllDay());
        values.put(Events.EVENT_COLOR, event.getColor());
        values.put(Events.ORIGINAL_SYNC_ID, event.getId());
        values.put(Events.DESCRIPTION, event.getNote());
        values.put(Events.DTSTART, event.getStartTime().atZone(zoneId).toEpochSecond() * 1_000L);
        values.put(Events.DTEND, event.getEndTime().atZone(zoneId).toEpochSecond() * 1_000L);
        return values;
    }

    private boolean hasEvent(TimetableEvent event, long calId) {
        Uri uri = asSyncService(Events.CONTENT_URI);
        String selection = "((" + Events.ACCOUNT_NAME + " = ?) AND ("
                + Events.ACCOUNT_TYPE + " = ?) AND ("
                + Events.OWNER_ACCOUNT + " = ?) AND ("
                + Events.CALENDAR_ID + " = ?) AND ("
                + Events.ORIGINAL_SYNC_ID + " = ?))";
        String[] selectionArgs = new String[]{account.name, account.type, account.name, String.valueOf(calId), String.valueOf(event.getId())};

        Cursor cur = contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        boolean found = false;
        if (cur != null) {
            found = cur.getCount() > 0;
            cur.close();
        }
        return found;
    }

    private int deleteEvent(TimetableEvent event, long calId) {
        Uri uri = asSyncService(Events.CONTENT_URI);
        String selection = "((" + Events.ACCOUNT_NAME + " = ?) AND ("
                + Events.ACCOUNT_TYPE + " = ?) AND ("
                + Events.OWNER_ACCOUNT + " = ?) AND ("
                + Events.CALENDAR_ID + " = ?) AND ("
                + Events.ORIGINAL_SYNC_ID + " = ?))";
        String[] selectionArgs = new String[]{account.name, account.type, account.name, String.valueOf(calId), String.valueOf(event.getId())};

        return contentResolver.delete(uri, selection, selectionArgs);
    }

    private List<CalendarHolder> listCalendars(Uri uri, String account_name, String account_type) {
        String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{account_name, account_type, account_name};

// Submit the query and get a Cursor object back.
        Cursor cur = contentResolver.query(uri, CALENDAR_PROJECTION, selection, selectionArgs, null);
        List<CalendarHolder> list = new ArrayList<>();
        while (cur != null && cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            list.add(new CalendarHolder(calID, displayName));
        }
        cur.close();
        return list;
    }

    private static class CalendarHolder {
        private final long id;
        private final String displayName;

        public CalendarHolder(long id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }
    }
}