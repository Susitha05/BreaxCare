package com.example.breaxcare.model.treatments;

public class Task {

    private long id;
    private String title;
    private String date;
    private String time;
    private String taskType;  // Added taskType field
    private String description;  // Added description field
    private long userId;  // Added userId field
    private boolean isSelected;  // To track whether the task is selected

    // Constructor to include taskType, description, and userId
    public Task(long id, String title, String date, String time, String taskType, String description, long userId) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.taskType = taskType;
        this.description = description;
        this.userId = userId;
        this.isSelected = false;  // Default value for selection
    }

    // Getter and Setter methods
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
