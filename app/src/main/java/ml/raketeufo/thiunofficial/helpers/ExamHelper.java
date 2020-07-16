package ml.raketeufo.thiunofficial.helpers;

import android.text.TextUtils;

import org.threeten.bp.LocalDateTime;

import java.util.List;

import ml.raketeufo.thirestbridge.api.model.Exam;

public class ExamHelper {
    public static String getLocationText(Exam exam) {
        List<String> rooms = exam.getRooms();
        String seat = exam.getSeat();
        boolean seatContainsRoom = false;
        if (rooms != null && seat != null && !seat.isEmpty()) {
            for (String room : rooms) {
                if (seat.contains(room)) {
                    seatContainsRoom = true;
                    break;
                }
            }
        }

        String locationText = "";
        if (seatContainsRoom) {
            locationText = seat;
        } else if (rooms != null && seat != null && !seat.isEmpty()) {
            locationText = TextUtils.join(", ", rooms);
            locationText += " (" + seat + ")";
        } else if (rooms != null) {
            locationText = TextUtils.join(", ", rooms);
        }
        return locationText;
    }

    public static LocalDateTime getEndZeit(Exam exam) {
        String art = exam.getArt();
        //TODO Move to Rest Bridge
        if (art.contains("schrP90")) {
            return exam.getZeit().plusMinutes(90);
        }
        return exam.getZeit();
    }
}
