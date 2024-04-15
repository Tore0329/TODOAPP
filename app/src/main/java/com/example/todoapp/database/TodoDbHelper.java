package com.example.todoapp.database;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todoapp.model.TodoTask;

import java.util.ArrayList;
import java.util.List;

public class TodoDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todoapp.db";
    private static final int DATABASE_VERSION = 1;

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<TodoTask> getActiveTasks() {
        List<TodoTask> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("tasks", new String[]{"id", "description", "completed"},
                "completed = 0", null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") TodoTask task = new TodoTask(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getInt(cursor.getColumnIndex("completed")) == 1
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TODO_TABLE = "CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "description TEXT NOT NULL," +
                "completed INTEGER NOT NULL);";
        db.execSQL(SQL_CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }

    public void addTask(TodoTask task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("description", task.getTaskDescription());
        values.put("completed", task.isCompleted() ? 1 : 0);

        db.insert("tasks", null, values);
        db.close();
    }

    public void updateTask(TodoTask task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("completed", task.isCompleted() ? 1 : 0);

        db.update("tasks", values, "id = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tasks", "id = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    public List<TodoTask> getAllTasks() {
        List<TodoTask> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM tasks";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") TodoTask task = new TodoTask(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getInt(cursor.getColumnIndex("completed")) == 1
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    public List<TodoTask> getCompletedTasks() {
        List<TodoTask> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM tasks WHERE completed = 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") TodoTask task = new TodoTask(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        true
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }
}