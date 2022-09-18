package com.vamsim.vnscollegeattendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vamsim.vnscollegeattendance.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class myAdapter extends FirebaseRecyclerAdapter<model,myAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public myAdapter(@NonNull FirebaseRecyclerOptions<model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull model model) {
        holder.attendanceDate.setText(model.getDate());
        holder.attendanceTime.setText(model.getTime());
        holder.attendanceStatus.setText(model.getStatus());

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView attendanceDate,attendanceTime,attendanceStatus;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            attendanceDate = itemView.findViewById(R.id.tvDate);
            attendanceTime = itemView.findViewById(R.id.tvTime);
            attendanceStatus = itemView.findViewById(R.id.textPresent);
        }
    }
}
