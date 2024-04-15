package com.example.todoapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.MainActivity;
import com.example.todoapp.R;
import com.example.todoapp.model.TodoTask;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TaskViewHolder> {
    private List<TodoTask> mTaskList;
    private final LayoutInflater mInflater;
    private final MainActivity mainActivity;

    public TodoAdapter(Context context, List<TodoTask> taskList) {
        mInflater = LayoutInflater.from(context);
        this.mTaskList = taskList;
        this.mainActivity = (MainActivity) context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        TodoTask mCurrent = mTaskList.get(position);
        holder.taskItemView.setText(mCurrent.getTaskDescription());
        holder.completedCheckbox.setChecked(mCurrent.isCompleted());

        holder.completedCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (holder.completedCheckbox.isPressed()) {
                mCurrent.setCompleted(isChecked);
                mainActivity.updateDatabase(mCurrent);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            mainActivity.showDeleteConfirmationDialog(mCurrent);
            return true;
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(List<TodoTask> tasks) {
        this.mTaskList = tasks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        public final TextView taskItemView;
        public final CheckBox completedCheckbox;
        final TodoAdapter mAdapter;

        public TaskViewHolder(View itemView, TodoAdapter adapter) {
            super(itemView);
            taskItemView = itemView.findViewById(R.id.task);
            completedCheckbox = itemView.findViewById(R.id.completed);
            this.mAdapter = adapter;
        }
    }
}