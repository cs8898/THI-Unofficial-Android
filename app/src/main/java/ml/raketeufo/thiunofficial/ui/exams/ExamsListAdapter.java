package ml.raketeufo.thiunofficial.ui.exams;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ml.raketeufo.thirestbridge.api.model.Exam;
import ml.raketeufo.thirestbridge.api.model.Pruefer;
import ml.raketeufo.thiunofficial.ExamDetailActivity;
import ml.raketeufo.thiunofficial.R;
import ml.raketeufo.thiunofficial.helpers.ExamHelper;

public class ExamsListAdapter extends RecyclerView.Adapter<ExamsListAdapter.GradesViewHolder> {
    private Exam[] mDataset;
    private Context context;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class GradesViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public TextView locationView;
        public TextView dateView;
        public TextView prueferView;
        public TextView typeMark;
        public CardView holderView;

        public GradesViewHolder(View v) {
            super(v);
            this.holderView = v.findViewById(R.id.exams_card);
            this.titleView = v.findViewById(R.id.exams_title);
            this.locationView = v.findViewById(R.id.exams_locationView);
            this.dateView = v.findViewById(R.id.exams_dateView);
            this.prueferView = v.findViewById(R.id.exams_prueferView);
            this.typeMark = v.findViewById(R.id.exams_typeMark);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ExamsListAdapter(Context context, Exam[] myDataset) {
        mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GradesViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exams_list_item, parent, false);
        //...
        GradesViewHolder vh = new GradesViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GradesViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Exam exam = mDataset[position];
        holder.titleView.setText(exam.getTitel());
        if (exam.getZeit() == null) {
            holder.dateView.setText("");
            holder.dateView.setVisibility(View.GONE);
        } else {
            holder.dateView.setText(exam.getZeit().format(formatter));
            holder.dateView.setVisibility(View.VISIBLE);
        }
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder();
        for (int i = 0; i < exam.getPruefer().size(); i++) {
            Pruefer pruefer = exam.getPruefer().get(i);
            spannableBuilder.append(pruefer.getVorname());
            spannableBuilder.append(" ").append(pruefer.getName(), new StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (i < exam.getPruefer().size() - 1) {
                spannableBuilder.append(", ");
            }
        }
        holder.prueferView.setText(spannableBuilder);

        String locationText = ExamHelper.getLocationText(exam);

        holder.locationView.setText(locationText);
        if (locationText.isEmpty()) {
            holder.locationView.setVisibility(View.GONE);
        } else {
            holder.locationView.setVisibility(View.VISIBLE);
        }

        if (exam.isAusserhalbZeitraum()) {
            holder.typeMark.setBackgroundColor(context.getColor(R.color.thigrey));
        } else {
            holder.typeMark.setBackgroundColor(context.getColor(R.color.thiorange));
        }

        holder.titleView.setOnClickListener((View v) -> {
            Log.d("ExamsList", "Somebody Clicked on Exam " + exam.getTitel());
            Intent intent = new Intent(context, ExamDetailActivity.class)
                    .setAction(ExamDetailActivity.ACTION_SHOW_DETAILS)
                    .putExtra(ExamDetailActivity.EXTRA_EXAM, exam);
            context.startActivity(intent);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public void setDataset(Exam[] exams) {
        this.mDataset = exams;
        this.notifyDataSetChanged();
    }
}
