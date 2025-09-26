package com.tasknotesapp.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tasknotesapp.R;
import com.tasknotesapp.models.Task;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskClickListener onTaskClickListener;
    private OnTaskLongClickListener onTaskLongClickListener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener onTaskClickListener, 
                      OnTaskLongClickListener onTaskLongClickListener) {
        this.taskList = taskList;
        this.onTaskClickListener = onTaskClickListener;
        this.onTaskLongClickListener = onTaskLongClickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBoxCompleted;
        private TextView textViewTitle, textViewDescription, textViewPriority, textViewDueDate;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPriority = itemView.findViewById(R.id.textViewPriority);
            textViewDueDate = itemView.findViewById(R.id.textViewDueDate);
        }

        public void bind(Task task) {
            textViewTitle.setText(task.getTitle());
            textViewDescription.setText(task.getDescription());
            textViewPriority.setText(task.getPriority());

            checkBoxCompleted.setChecked(task.isCompleted());

            // Strike through completed tasks
            if (task.isCompleted()) {
                textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textViewDescription.setPaintFlags(textViewDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                textViewDescription.setPaintFlags(textViewDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            // Format due date
            if (task.getDueDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                textViewDueDate.setText("Due: " + dateFormat.format(task.getDueDate()));
                textViewDueDate.setVisibility(View.VISIBLE);
            } else {
                textViewDueDate.setVisibility(View.GONE);
            }

            // Set priority color
            switch (task.getPriority().toLowerCase()) {
                case "high":
                    textViewPriority.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
                    break;
                case "medium":
                    textViewPriority.setTextColor(itemView.getContext().getColor(android.R.color.holo_orange_dark));
                    break;
                case "low":
                    textViewPriority.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
                    break;
            }

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (onTaskClickListener != null) {
                    onTaskClickListener.onTaskClick(task);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (onTaskLongClickListener != null) {
                    onTaskLongClickListener.onTaskLongClick(task);
                }
                return true;
            });

            // Checkbox listener
            checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setCompleted(isChecked);
                // Update task completion status
                updateTaskCompletion(task, isChecked);
                notifyItemChanged(getAdapterPosition());
            });
        }

        private void updateTaskCompletion(Task task, boolean isCompleted) {
            // Update task in Firestore
            FirebaseFirestore.getInstance()
                    .collection("tasks")
                    .document(task.getId())
                    .update("completed", isCompleted)
                    .addOnFailureListener(e -> {
                        // Revert checkbox state on failure
                        checkBoxCompleted.setChecked(!isCompleted);
                    });
        }
    }
}