package com.rndapp.task_feed.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.data.TaskDataSource;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:39 PM
 *
 */
public class Project implements Serializable{
    private int id;
    private int localId;
    private String name;
    private Date created_at;
    private Date updated_at;
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private int color;

    public Project(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public Project() {}

    public String toString(){
        return name;
    }

    public String getFirstTaskText(){
        String output = null;
        if (tasks.size() != 0){
            for (Task task : tasks){
                if (!task.isFinished()) {
                    output = task.getName();
                    break;
                }
            }
        }
        return output;
    }

    public void addTaskToBeginning(Context context, Task task){
        TaskDataSource source = new TaskDataSource(context);
        source.open();
        task = source.createTask(task.getName(),
                this.getId(),
                task.getId(),
                task.getOrder(),
                task.getPoints(),
                task.isFinished());
        source.close();
        tasks.add(0,task);
        updatePositions(context);
    }

    public void addTaskRespectingOrder(Context context, Task task){
        TaskDataSource source = new TaskDataSource(context);
        source.open();
        task = source.createTask(task.getName(),
                this.getId(),
                task.getId(),
                task.getOrder(),
                task.getPoints(),
                task.isFinished());
        source.close();
        tasks.add(0,task);
        sortTasks();
    }

    public void removeFirstTask(Context context){
        if (tasks.size() != 0){
            int indexOfTask = -1;
            for (Task task : tasks){
                if (!task.isFinished()) {
                    indexOfTask = tasks.indexOf(task);
                    break;
                }
            }
            if (indexOfTask != -1) markTaskAtPositionAsFinished(context, indexOfTask);
        }
    }

    public void markTaskAtPositionAsFinished(Context context, int position){
        tasks.set(position, Task.markAsFinished(context, tasks.get(position)));
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
        if (task.getOrder() != tasks.indexOf(task)){
            tasks.remove(task);
            if (task.getOrder() == tasks.size()-1){
                tasks.add(task);
            }else {
                tasks.add(task.getOrder(), task);
            }
        }
        Task.updateTask(context, task);
        updatePositions(context);
    }

    private void updatePositions(Context context){
        for (Task task : tasks){
            if (task.getOrder() != tasks.indexOf(task)){
                task.setOrder(tasks.indexOf(task));
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

    public static Project uploadProjectToServer(Context context, Project project){
        ServerCommunicator server = new ServerCommunicator(context);
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("project",project);
        Project newProject = new Project();
        newProject.setColor(project.color);
        newProject.setName(project.name);
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(hash));
            SharedPreferences sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE);
            String json = server.postToEndpointAuthed("users/"+sp.getInt("user_id", 0)+"/projects",jsonObject);
            newProject = new Gson().fromJson(json, Project.class);

            for (Task task : project.getTasks()){
                task.setProject_id(newProject.getId());
                newProject.getTasks().add(Task.uploadTaskToServer(context, task));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return newProject;
    }

    public static Project updateProjectOnServer(Context context, Project project){
        ServerCommunicator server = new ServerCommunicator(context);
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("project",project);
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(hash));
            SharedPreferences sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE);
            server.postToEndpointAuthed(
                    "users/"+sp.getInt("user_id", 0)+"/projects/"+project.getId(), jsonObject);
        }catch (Exception e){
            e.printStackTrace();
        }
        return project;
    }

    public static Project addProjectToDatabase(Context context, Project project){
        return Project.addProjectToDatabase(context,
                project.getName(),
                project.getColor(),
                project.getId(),
                project.getCreated_at(),
                project.getUpdated_at());
    }

    public static Project addProjectToDatabase(Context context, String name, int color, int serverId, Date created, Date updated){
        ProjectDataSource source = new ProjectDataSource(context);
        source.open();
        Project project = source.createProject(name, color, serverId, created, updated);
        source.close();
        return project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (id != project.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + localId;
        return result;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
