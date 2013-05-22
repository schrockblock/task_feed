package com.rndapp.task_feed.models;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:32 PM
 *
 */
public class Task implements Serializable{
    private int serverId;
    private int localId;
    private int projectId;
    private String text;
    private boolean completed = false;
    private int points = 1;
    public int position;

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
}
