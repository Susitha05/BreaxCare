package com.example.breaxcare.model.treatments;

public class TreatmentPlan {

    private String title;
    private String time;
    private String description;
    private String taskType;

    public TreatmentPlan(String title, String time, String description, String taskType) {
        this.title = title;
        this.time = time;
        this.description = description;
        this.taskType = taskType;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getTaskType() {
        return taskType;
    }
}
