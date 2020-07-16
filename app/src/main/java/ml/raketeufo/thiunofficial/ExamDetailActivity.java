package ml.raketeufo.thiunofficial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import org.threeten.bp.format.DateTimeFormatter;

import ml.raketeufo.thirestbridge.api.model.Exam;
import ml.raketeufo.thirestbridge.api.model.Pruefer;
import ml.raketeufo.thiunofficial.helpers.ExamHelper;

public class ExamDetailActivity extends AppCompatActivity {

    public static final String ACTION_SHOW_DETAILS = "ExamDetail.Action_SHOW_DETAILS";
    public static final String EXTRA_EXAM = "ExamDetail.Extra_EXAM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        if (!ACTION_SHOW_DETAILS.equals(intent.getAction())) {
            finish();
            return;
        }
        setContentView(R.layout.activity_exam_detail);
        Exam exam = (Exam) intent.getSerializableExtra(EXTRA_EXAM);
        TextView examTitleView = findViewById(R.id.examDetail_title);
        TextView examDateView = findViewById(R.id.examDetail_date);
        TextView examTypeMark = findViewById(R.id.examDetail_typeMark);
        TextView examTypeTextView = findViewById(R.id.examDetail_typeString);
        TextView examPrueferText = findViewById(R.id.examDetail_pruefer);
        TextView examToolsText = findViewById(R.id.examDetail_tools);
        TextView examSeatText = findViewById(R.id.examDetail_seat);
        TextView examRoomsText = findViewById(R.id.examDetail_roomList);

        examTitleView.setText(exam.getTitel());
        if (exam.getZeit() != null)
            examDateView.setText(exam.getZeit().format(DateTimeFormatter.ISO_DATE_TIME));
        else{
            examDateView.setText(getString(R.string.exam_during_semester));
        }

        if (exam.isAusserhalbZeitraum()) {
            examTypeMark.setBackgroundColor(getColor(R.color.thigrey));
        } else {
            examTypeMark.setBackgroundColor(getColor(R.color.thiorange));
        }

        examTypeTextView.setText(exam.getArt());

        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder();
        for (int i = 0; i < exam.getPruefer().size(); i++) {
            Pruefer pruefer = exam.getPruefer().get(i);
            spannableBuilder.append(pruefer.getVorname());
            spannableBuilder.append(" ").append(pruefer.getName(), new StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (i < exam.getPruefer().size() - 1) {
                spannableBuilder.append(", ");
            }
        }
        examPrueferText.setText(spannableBuilder);

        examToolsText.setText(TextUtils.join("\n", exam.getHilfsmittel()));

        if (exam.getSeat() != null && !exam.getSeat().isEmpty()) {
            String locationText = ExamHelper.getLocationText(exam);
            examSeatText.setText(locationText);
            examSeatText.setVisibility(View.VISIBLE);
        } else {
            examSeatText.setVisibility(View.GONE);
        }

        examRoomsText.setText(TextUtils.join("\n", exam.getRooms()));

    }
}