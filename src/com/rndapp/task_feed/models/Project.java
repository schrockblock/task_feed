package com.rndapp.task_feed.models;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:39 PM
 *
 */
public class Project implements Serializable{
    private int serverId;
    private int localId;
    private String title;
    private Vector<Task> tasks = new Vector<Task>();
    private int color;

    public String getFirstTask(){
        return tasks.firstElement().getText();
    }

    public void addTask(Task task){
        tasks.add(task);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getServerId() {
        return localId;
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
}
