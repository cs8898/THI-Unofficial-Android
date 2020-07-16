package ml.raketeufo.thiunofficial.helpers;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import java.util.Calendar;

public class LocalDateTimeHelper {

    public static Calendar localTimeToCalendar(LocalTime localTime, int year, int month, int day,
                                               ZoneOffset zoneOffset) {
        LocalDateTime dateTime = localTime.atDate(LocalDate.of(year, month, day));
        return localDateTimeToCalendar(dateTime, zoneOffset);
    }

    public static Calendar localTimeToCalendar(LocalTime localTime, int year, int month, int day,
                                               ZoneId zoneId) {
        LocalDateTime dateTime = localTime.atDate(LocalDate.of(year, month, day));
        return localDateTimeToCalendar(dateTime, zoneId);
    }

    public static Calendar localTimeToCalendar(LocalDate localDate, ZoneOffset zoneOffset) {
        LocalDateTime dateTime = localDate.atStartOfDay();//assuming start of day
        return localDateTimeToCalendar(dateTime, zoneOffset);
    }

    public static Calendar localTimeToCalendar(LocalDate localDate, ZoneId zoneId) {
        LocalDateTime dateTime = localDate.atStartOfDay();//assuming start of day
        return localDateTimeToCalendar(dateTime, zoneId);
    }

    public static Calendar localDateTimeToCalendar(LocalDateTime localDateTime, ZoneId zoneId) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zoneDateTimeToDate(zonedDateTime);
    }

    public static Calendar localTimeToCalendar(LocalDateTime localDateTime, ZoneOffset zoneOffset) {
        OffsetDateTime offsetDateTime = localDateTime.atOffset(zoneOffset);
        return offsetDateTimeToCalendar(offsetDateTime);
    }

    public static Calendar offsetDateTimeToCalendar(OffsetDateTime offsetDateTime) {
        return zoneDateTimeToDate(offsetDateTime.toZonedDateTime());
    }

    public static Calendar zoneDateTimeToDate(ZonedDateTime zonedDateTime) {
        return DateTimeUtils.toGregorianCalendar(zonedDateTime);
    }
}
