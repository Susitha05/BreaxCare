package com.example.breaxcare.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breaxcare.R;
import com.example.breaxcare.model.MedicalHistory;

import java.util.List;

public class MedicalHistoryAdapter extends RecyclerView.Adapter<MedicalHistoryAdapter.ViewHolder> {

    private Context context;
    private List<MedicalHistory> historyList;
    private OnItemClickListener listener;

    public MedicalHistoryAdapter(Context context, List<MedicalHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicalhistory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicalHistory history = historyList.get(position);

        holder.tvDate.setText("Date: " + history.getDate());
        holder.tvResult.setText("Result: " + history.getResult());
        holder.tvRiskLevel.setText("Risk: " + history.getRiskLevel());
        holder.tvRoundnessColor.setText("Color: " + history.getRoundnessColor());
        holder.tvWhiteDotsCount.setText("Dots: " + history.getWhiteDotsCount());
        holder.tvWhiteDotsPercentage.setText(history.getWhiteDotsPercentage() + "%");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(history);  // Notify the activity on item click
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvResult, tvRiskLevel, tvRoundnessColor, tvWhiteDotsCount, tvWhiteDotsPercentage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvResult = itemView.findViewById(R.id.tv_result);
            tvRiskLevel = itemView.findViewById(R.id.tv_risk_level);
            tvRoundnessColor = itemView.findViewById(R.id.tv_roundness_color);
            tvWhiteDotsCount = itemView.findViewById(R.id.tv_white_dots_count);
            tvWhiteDotsPercentage = itemView.findViewById(R.id.tv_white_dots_percentage);
        }
    }

    // Interface to handle item click events
    public interface OnItemClickListener {
        void onItemClick(MedicalHistory history);
    }
}
