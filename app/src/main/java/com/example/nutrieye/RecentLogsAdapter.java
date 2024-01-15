package com.example.nutrieye;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecentLogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RecentLogs> logItemList;

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    public RecentLogsAdapter(List<RecentLogs> logItemList) {
        this.logItemList = logItemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_NORMAL) {
            view = inflater.inflate(R.layout.recent_logs_item, parent, false);
            return new LogsViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.empty_view, parent, false);
            return new EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LogsViewHolder) {
            ((LogsViewHolder) holder).bind(logItemList.get(position));
        } else if (holder instanceof RecentLogsAdapter.EmptyViewHolder) {
            ((RecentLogsAdapter.EmptyViewHolder) holder).bindEmpty();
        }
    }

    @Override
    public int getItemCount() {
        return logItemList.isEmpty() ? 1 : logItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return logItemList.isEmpty() ? VIEW_TYPE_EMPTY : VIEW_TYPE_NORMAL;
    }

    public void setData(List<RecentLogs> logItemList) {
        this.logItemList = logItemList;
        notifyDataSetChanged();
    }

    public class LogsViewHolder extends RecyclerView.ViewHolder {
        private ImageView logsIcon;
        private TextView actionLabel;
        private TextView actionCategory;
        private TextView logsTimestamp;

        public LogsViewHolder(@NonNull View itemView) {
            super(itemView);
            logsIcon = itemView.findViewById(R.id.logsIcon);
            actionLabel = itemView.findViewById(R.id.actionLabel);
            actionCategory = itemView.findViewById(R.id.actionCategory);
            logsTimestamp = itemView.findViewById(R.id.logsTimestamp);
        }

        public void bind(RecentLogs logItem) {
            Picasso.get().load(logItem.getLogsIcon()).into(logsIcon);
            actionLabel.setText(logItem.getActionLabel());
            actionCategory.setText(logItem.getActionCategory());
            logsTimestamp.setText(logItem.getLogsTimestamp());
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        private ImageView emptyImg;
        private TextView emptyText;

        public EmptyViewHolder(View view) {
            super(view);
            emptyImg = view.findViewById(R.id.empty);
            emptyText = view.findViewById(R.id.emptyText);
        }

        public void bindEmpty() {
            // Set the image and text for the empty state
            emptyImg.setImageResource(R.drawable.logs_empty);
            emptyImg.setAlpha(0.4F);
            emptyText.setText("No activity logs found for today. \nAdd some activities to your log.");
            emptyText.setAlpha(0.8F);
        }
    }
}
