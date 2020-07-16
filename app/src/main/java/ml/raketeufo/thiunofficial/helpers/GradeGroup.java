package ml.raketeufo.thiunofficial.helpers;

import android.content.Context;

import ml.raketeufo.thiunofficial.R;

public enum GradeGroup {
    SUPERB(0.7, 0.7, R.color.grade_superb, R.string.grade_superb),
    SUPER(1.0, 1.3, R.color.grade_super, R.string.grade_super),
    GOOD(1.7, 2.3, R.color.grade_good, R.string.grade_good),
    OK(2.7, 3.3, R.color.grade_ok, R.string.grade_ok),
    ENOUGH(3.7, 4.0, R.color.grade_enough, R.string.grade_enough),
    FAILED(4.3, 5, R.color.grade_failed, R.string.grade_failed),
    UNKNOWN(0,0, R.color.grade_unknown, R.string.grade_unknown);

    private final double start;
    private final double end;
    private final int color;
    private final int title;

    GradeGroup(double start, double end, int color, int title) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.title = title;
    }

    public static GradeGroup find(double grade) {
        for (GradeGroup gg : GradeGroup.values()) {
            if (gg.start <= grade && gg.end >= grade) {
                return gg;
            }
        }
        return null;
    }

    public String getTitle(Context context) {
        return context.getString(this.title);
    }

    public int getColor(Context context) {
        return context.getColor(this.color);
    }
}
