package com.example.todoapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import com.example.todoapp.adapter.TodoAdapter;
import com.example.todoapp.database.TodoDbHelper;
import com.example.todoapp.model.TodoTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TodoDbHelper dbHelper;
    private TodoAdapter adapter;
    private boolean showAllTasks = false;
    private EditText editText;
    private Button addButton;
    private Button toggleViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TodoDbHelper(this);
        editText = findViewById(R.id.etTodo);
        addButton = findViewById(R.id.btnAddTask);
        toggleViewButton = findViewById(R.id.btnToggleView);
        RecyclerView recyclerView = findViewById(R.id.rvTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodoAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        refreshTaskList();

        addButton.setOnClickListener(v -> handleAddTask());
        toggleViewButton.setOnClickListener(v -> toggleTaskView());
    }

    private void handleAddTask() {
        String taskDescription = editText.getText().toString();
        if (!taskDescription.isEmpty()) {
            TodoTask newTask = new TodoTask(0, taskDescription, false);
            dbHelper.addTask(newTask);
            refreshTaskList();
            editText.setText("");
        } else {
            Toast.makeText(this, "Please enter a task description", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateViewVisibility() {
        int visibility = showAllTasks ? View.GONE : View.VISIBLE;
        editText.setVisibility(visibility);
        addButton.setVisibility(visibility);
    }

    private void toggleTaskView() {
        showAllTasks = !showAllTasks;
        toggleViewButton.setText(showAllTasks ? "Show Active Tasks" : "Show All Tasks");
        refreshTaskList();
        updateViewVisibility();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshTaskList() {
        List<TodoTask> tasks = showAllTasks ? dbHelper.getAllTasks() : dbHelper.getActiveTasks();
        adapter.setTasks(tasks);
        adapter.notifyDataSetChanged();
    }

    public void showDeleteConfirmationDialog(TodoTask task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage(String.format("Are you sure you want to delete task '%s'?", task.getTaskDescription()));
        builder.setPositiveButton("Delete", (dialog, id) -> {
            dbHelper.deleteTask(task.getId());
            refreshTaskList();
        });
        builder.setNegativeButton("Cancel", (dialog, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void updateDatabase(TodoTask task) {
        dbHelper.updateTask(task);
        refreshTaskList();
    }
}