package ml.raketeufo.thiunofficial.ui.home.upcoming;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import ml.raketeufo.thirestbridge.api.model.Grade;
import ml.raketeufo.thiunofficial.R;
import ml.raketeufo.thiunofficial.helpers.GradeGroup;
import ml.raketeufo.thiunofficial.ui.timetable.TimetableEvent;

public class UpcomingListAdapter extends RecyclerView.Adapter<UpcomingListAdapter.UpcomingViewHolder> {
    private TimetableEvent[] mDataset;
    private Context context;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class UpcomingViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public TextView locationView;
        public TextView typeMark;
        public TextView dateView;

        public UpcomingViewHolder(View v) {
            super(v);
            this.titleView = v.findViewById(R.id.upcoming_title);
            this.locationView = v.findViewById(R.id.upcoming_locationView);
            this.typeMark = v.findViewById(R.id.upcoming_typeMark);
            this.dateView = v.findViewById(R.id.upcoming_dateView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public UpcomingListAdapter(Context context, TimetableEvent[] myDataset) {
        mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UpcomingViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_list_item, parent, false);
        //...
        UpcomingViewHolder vh = new UpcomingViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(UpcomingViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TimetableEvent event = mDataset[position];
        holder.titleView.setText(event.getTitle());
        holder.dateView.setText(event.getStartTime().format(formatter));
        holder.locationView.setText(event.getLocation());
        holder.typeMark.setBackgroundColor(event.getColor());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public void setDataset(TimetableEvent[] exams) {
        this.mDataset = exams;
        this.notifyDataSetChanged();
    }
}
