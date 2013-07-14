package com.rndapp.task_feed.models;

import android.content.Context;
import com.rndapp.task_feed.data.TaskDataSource;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:32 PM
 *
 */
public class Task implements Serializable, Comparable<Task>{
    private int serverId;
    private int localId;
    private int projectId;
    private String text;
    private boolean completed = false;
    private int points = 1;
    private int position;

    public static void updateTask(Context context, Task task){
        TaskDataSource source = new TaskDataSource(context);
        source.open();
        source.updateTask(task);
        source.close();
    }

    @Override
    public int compareTo(Task task){
        return this.position - task.position;
    }

    public static Task uploadTaskToServer(Context context, Task task){

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (projectId != task.projectId) return false;
        if (serverId != 0 && task.serverId != 0 && serverId != task.serverId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serverId;
        result = 31 * result + localId;
        result = 31 * result + projectId;
        result = 31 * result + text.hashCode();
        return result;
    }

    public String toString(){
        return text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
