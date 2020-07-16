package ml.raketeufo.thiunofficial.ui.grades;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ml.raketeufo.thirestbridge.api.model.Exam;
import ml.raketeufo.thirestbridge.api.model.Grade;
import ml.raketeufo.thiunofficial.R;
import ml.raketeufo.thiunofficial.helpers.GradeGroup;

public class GradesListAdapter extends RecyclerView.Adapter<GradesListAdapter.GradesViewHolder> {
    private Grade[] mDataset;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class GradesViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public TextView noteView;
        public TextView groupMark;
        public TextView gradeMark;
        public TextView gradeView;

        public GradesViewHolder(View v) {
            super(v);
            this.titleView = v.findViewById(R.id.grades_title);
            this.groupMark = v.findViewById(R.id.grades_groupMark);
            this.gradeMark = v.findViewById(R.id.grades_gradeMark);
            this.gradeView = v.findViewById(R.id.grades_grade);
            this.noteView = v.findViewById(R.id.grades_note);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GradesListAdapter(Context context, Grade[] myDataset) {
        mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GradesViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grades_list_item, parent, false);
        //...
        GradesViewHolder vh = new GradesViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GradesViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Grade grade = mDataset[position];
        holder.titleView.setText(grade.getTitel());
        holder.gradeView.setText(String.valueOf(grade.getNote()));
        holder.noteView.setText(grade.getFristSemester());
        if (grade.isGroup() != null) {
            holder.groupMark.setBackgroundColor(context.getColor(R.color.thiblue));
        } else {
            holder.groupMark.setBackgroundColor(Color.TRANSPARENT);
        }
        if (grade.getNote() != null)
            holder.gradeMark.setBackgroundColor(GradeGroup.find(grade.getNote()).getColor(this.context));
        else
            holder.gradeMark.setBackgroundColor(Color.TRANSPARENT);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public void setDataset(Grade[] exams) {
        this.mDataset = exams;
        this.notifyDataSetChanged();
    }
}
