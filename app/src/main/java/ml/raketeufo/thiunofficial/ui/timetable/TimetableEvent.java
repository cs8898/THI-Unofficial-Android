package ml.raketeufo.thiunofficial.ui.timetable;

import android.content.Context;
import android.graphics.Color;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ml.raketeufo.thirestbridge.api.model.CourseEvent;
import ml.raketeufo.thirestbridge.api.model.Exam;
import ml.raketeufo.thirestbridge.api.model.IntervalEvent;
import ml.raketeufo.thirestbridge.api.model.SpecialEvent;
import ml.raketeufo.thirestbridge.api.model.Timetable;
import ml.raketeufo.thiunofficial.R;
import ml.raketeufo.thiunofficial.helpers.ExamHelper;
import ml.raketeufo.thiunofficial.helpers.LocalDateTimeHelper;

public class TimetableEvent implements WeekViewDisplayable<TimetableEvent> {
    //long id;
    String title = "";
    String note = "";
    LocalDateTime startTime;
    LocalDateTime endTime;
    String location = "";
    int color = Color.GREEN;
    int textcolor = Color.BLACK;
    boolean isAllDay = false;
    boolean isCanceled = false;

    EventType eventType = EventType.UNKNOWN;
    Object eventPayload;

    public boolean isAllDay() {
        return isAllDay;
    }

    public enum EventType {
        UNKNOWN(Object.class),
        SPECIAL(SpecialEvent.class),
        EXAM(Exam.class),
        INTERVAL(IntervalEvent.class),
        COURSE(CourseEvent.class);

        final Class<?> objClass;

        EventType(Class<?> objClass) {
            this.objClass = objClass;
        }

        public Class<?> getObjClass() {
            return objClass;
        }
    }

    public TimetableEvent(Context context, Exam exam) {
        this.title = exam.getTitel();
        this.note = exam.getArt();
        this.location = ExamHelper.getLocationText(exam);
        this.startTime = exam.getZeit();
        this.endTime = ExamHelper.getEndZeit(exam);
        this.color = context.getColor(R.color.thiorange);
        this.textcolor = context.getColor(R.color.white);
        this.eventType = EventType.EXAM;
        this.eventPayload = exam;
    }

    public TimetableEvent(Context context, CourseEvent courseEvent) {
        this.note = courseEvent.getDozent();
        this.title = courseEvent.getTitle();
        this.location = courseEvent.getRoom();
        this.startTime = courseEvent.getStart();
        this.endTime = courseEvent.getEnd();
        this.color = context.getColor(R.color.thiblue);
        this.textcolor = context.getColor(R.color.white);
        this.eventType = EventType.COURSE;
        this.eventPayload = courseEvent;
    }

    public TimetableEvent(Context context, IntervalEvent intervalEvent) {
        this.title = intervalEvent.getTitle();
        this.startTime = intervalEvent.getStart();
        this.endTime = intervalEvent.getEnd();
        this.color = context.getColor(R.color.thigrey);
        this.textcolor = context.getColor(R.color.white);
        this.isAllDay = true;
        this.eventType = EventType.INTERVAL;
        this.eventPayload = intervalEvent;
    }

    public TimetableEvent(Context context, SpecialEvent specialEvent) {
        this.title = specialEvent.getTitle();
        this.startTime = specialEvent.getStart();
        this.endTime = specialEvent.getEnd();
        this.color = context.getColor(R.color.thiburgundy);
        this.textcolor = context.getColor(R.color.white);
        this.isAllDay = true;
        this.eventType = EventType.SPECIAL;
        this.eventPayload = specialEvent;
    }

    public static List<TimetableEvent> eventsFrom(Context context, Timetable timetable) {
        List<TimetableEvent> events = new ArrayList<>();

        List<CourseEvent> courses = timetable.getCourses();
        for (CourseEvent courseEvent : courses) {
            events.add(new TimetableEvent(context, courseEvent));
        }

        List<IntervalEvent> intervals = timetable.getIntervals();
        for (IntervalEvent intervalEvent : intervals) {
            events.add(new TimetableEvent(context, intervalEvent));
        }

        List<SpecialEvent> specials = timetable.getSpecials();
        for (SpecialEvent specialEvent : specials) {
            events.add(new TimetableEvent(context, specialEvent));
        }

        return events;
    }

    public static List<TimetableEvent> eventsFrom(Context context, List<Exam> exams) {
        List<TimetableEvent> events = new ArrayList<>();

        for (Exam exam : exams) {
            if (exam.getZeit() != null)
                events.add(new TimetableEvent(context, exam));
        }

        return events;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public int getColor() {
        return color;
    }

    public int getTextcolor() {
        return textcolor;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Object getEventPayload() {
        return eventPayload;
    }

    @NotNull
    @Override
    public WeekViewEvent<TimetableEvent> toWeekViewEvent() {
        WeekViewEvent.Style style = new WeekViewEvent.Style.Builder()
                .setBackgroundColor(color)
                .setTextColor(textcolor)
                .setTextStrikeThrough(isCanceled)
                .build();

        WeekViewEvent.Builder<TimetableEvent> builder = new WeekViewEvent.Builder<TimetableEvent>(this);
        builder
                .setId(this.getId())
                .setTitle(title)
                .setStartTime(LocalDateTimeHelper.localDateTimeToCalendar(startTime, ZoneId.of("Europe/Berlin")))
                .setEndTime(LocalDateTimeHelper.localDateTimeToCalendar(endTime, ZoneId.of("Europe/Berlin")))
                .setAllDay(isAllDay)
                .setStyle(style);
        if (location != null && !location.isEmpty()) {
            builder.setLocation(location);
        }

        return builder.build();
    }

    public long getId() {
        return 99277L * title.hashCode() + 42787L * startTime.toEpochSecond(ZoneOffset.UTC) + (isAllDay ? 43L : 61L);
    }
}
