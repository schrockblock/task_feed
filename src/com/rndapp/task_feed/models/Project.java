package com.rndapp.task_feed.models;

import android.content.Context;
import com.google.gson.Gson;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.data.TaskDataSource;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

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
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private int color;

    public String toString(){
        return title;
    }

    public String getFirstTaskText(){
        String output = null;
        if (tasks.size() != 0){
            output = tasks.get(0).getText();
        }
        return output;
    }

    public void addTask(Context context, Task task){
        TaskDataSource source = new TaskDataSource(context);
        source.open();
        task = source.createTask(task.getText(),
                this.getServerId(),
                task.getServerId(),
                0,
                task.getPoints(),
                task.isCompleted());
        source.close();
        task.setPosition(0);
        tasks.add(0,task);
        updatePositions(context);
    }

    public void removeFirstTask(Context context){
        if (tasks.size() != 0){
            deleteTask(context, 0);
        }
    }

    public void deleteTask(Context context, int position){
        TaskDataSource source = new TaskDataSource(context);
        source.open();
        source.deleteTask(tasks.get(position));
        source.close();
        tasks.remove(position);
        updatePositions(context);
    }

    public void updateTask(Context context, Task task){
        if (task.getPosition() != tasks.indexOf(task)){
            tasks.remove(task);
            if (task.getPosition() == tasks.size()-1){
                tasks.add(task);
            }else {
                tasks.add(task.getPosition(), task);
            }
        }
        Task.updateTask(context, task);
        updatePositions(context);
    }

    private void updatePositions(Context context){
        for (Task task : tasks){
            if (task.getPosition() != tasks.indexOf(task)){
                task.setPosition(tasks.indexOf(task));
                Task.updateTask(context, task);
            }
        }
    }

    public void sortTasks(){
        Collections.sort(tasks);
    }

    public Task getTask(int position){
        return tasks.get(position);
    }

    public static void updateProject(Context context, Project project){
        ProjectDataSource source = new ProjectDataSource(context);
        source.open();
        source.updateProject(project);
        source.close();
    }

    public boolean isEmpty(){
        return tasks.size() == 0;
    }

    public void syncProject(){

    }

    public static Project uploadProjectToServer(Context context, Project project){
        ServerCommunicator server = new ServerCommunicator(context);
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("project",project);
        Project newProject = new Project();
        newProject.setColor(project.color);
        newProject.setTitle(project.title);
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(hash));
            String json = server.postToEndpointAuthed("projects",jsonObject, true);
            newProject = new Gson().fromJson(json, Project.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        for (Task task : project.getTasks()){
             newProject.getTasks().add(Task.uploadTaskToServer(context, task));
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (localId != project.localId) return false;
        if (serverId != 0 && project.serverId != 0 && serverId != project.serverId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serverId;
        result = 31 * result + localId;
        return result;
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

    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
