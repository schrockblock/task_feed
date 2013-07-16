package com.rndapp.task_feed.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.broadcast_receivers.ListWidgetProvider;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.data.TaskDataSource;
import com.rndapp.task_feed.fragments.FeedFragment;
import com.rndapp.task_feed.fragments.ProjectFragment;
import com.rndapp.task_feed.interfaces.ProjectDisplayer;
import com.rndapp.task_feed.models.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedActivity extends SherlockFragmentActivity implements
		ActionBar.OnNavigationListener, ProjectDisplayer {
    private ArrayList<Project> projects;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);

        //load projects
        loadProjects();

        setupNav();

        new DownloadProjectsTask().execute("");
	}

    public void setupNav(){
        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        //get titles
        String[] dropdownTitles = new String[projects.size()+1];

        dropdownTitles[0] = "Feed";
        for (int i = 0; i < projects.size(); i++){
            dropdownTitles[i+1] = projects.get(i).getName();
        }

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1, dropdownTitles), this);
    }

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
                .getSelectedNavigationIndex());
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
        if (position == 0){
            Fragment fragment1 = new FeedFragment();
            ((FeedFragment)fragment1).delegate = this;
            ((FeedFragment)fragment1).projects = projects;
            switchFragBack(fragment1);
        } else {
            Fragment fragment3 = new ProjectFragment();
            ((ProjectFragment)fragment3).delegate = this;
            Bundle args2 = new Bundle();
            args2.putSerializable(ProjectFragment.ARG_PROJECT, projects.get(position - 1));
            fragment3.setArguments(args2);
            switchFragBack(fragment3);
        }
        return true;
    }

    private void loadProjects(){
        //load projects
        ProjectDataSource projectDataSource = new ProjectDataSource(this);
        projectDataSource.open();
        projects = projectDataSource.getAllProjects();
        projectDataSource.close();

        //load tasks
        TaskDataSource taskDataSource = new TaskDataSource(this);
        taskDataSource.open();
        ArrayList<Task> tasks = taskDataSource.getAllTasks();
        taskDataSource.close();

        //associate
        for (Project project : projects){
            for (Task task : tasks){
                if (task.getProject_id() == project.getId()){
                    project.getTasks().add(task);
                }
            }
        }

        //sort
        for (final Project project : projects){
            project.sortTasks();
        }
    }

    private class DownloadProjectsTask extends AsyncTask<String, String, Object> {

        @Override
        protected Object doInBackground(String... params) {
            downloadProjectsFromServer();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Object o){
            setupNav();
        }
    }

    private void downloadProjectsFromServer(){
        ArrayList<Project> serverProjects;
        ServerCommunicator server = new ServerCommunicator(this);
        try {
            SharedPreferences sp = getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE);
            String json = server.getEndpointAuthed("users/" + sp.getInt("user_id", 0) + "/projects");
            Type listOfProjects = new TypeToken<List<Project>>(){}.getType();
            serverProjects = new Gson().fromJson(json, listOfProjects);

            syncProjectsWithServer(serverProjects);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void syncProjectsWithServer(List<Project> serverProjects){
        for (Project project : projects){
            boolean isOnServer = false;
            if (serverProjects != null){
                for (Project serverProject : serverProjects){
                    if (project.equals(serverProject)) {
                        isOnServer = true;
                    }
                }
            }
            if (!isOnServer) Project.uploadProjectToServer(this, project);
        }

        if (serverProjects != null){
            for (Project serverProject : serverProjects) {
                boolean isInDatabase = false;
                int indexOfProject = 0;
                Project syncedProject = null;
                for (Project project : projects){
                    if (project.equals(serverProject)){
                        isInDatabase = true;
                        indexOfProject = projects.indexOf(project);
                        syncedProject = syncProjects(project, serverProject);
                    }
                }
                if (!isInDatabase) {
                    Project project = Project.addProjectToDatabase(this, serverProject);
                    for (Task task : serverProject.getTasks()){
                        project.addTaskRespectingOrder(this, task);
                    }
                    projects.add(project);
                }else if (syncedProject != null){
                    projects.set(indexOfProject, syncedProject);
                }
            }
        }
    }

    private Project syncProjects(Project localProject, Project remoteProject){
        Project syncedProject = null;
        if (remoteProject == null){
            return localProject;
        }else {
            syncedProject =
                    localProject.getUpdated_at().before(remoteProject.getUpdated_at()) ? remoteProject : localProject;
        }

        syncedProject.setTasks(syncTasks(localProject.getTasks(), remoteProject.getTasks()));
        return syncedProject;
    }

    private ArrayList<Task> syncTasks(ArrayList<Task> localTasks, ArrayList<Task> remoteTasks){
        ArrayList<Task> syncedTasks = new ArrayList<Task>();
        for (Task task : localTasks){
            boolean isOnServer = false;
            for (Task serverTask : remoteTasks){
                if (serverTask.equals(task)){
                    isOnServer = true;
                    if (task.getUpdated_at().before(serverTask.getUpdated_at())){
                        //take the server version
                        syncedTasks.add(serverTask);
                    }
                }
            }
            if (!isOnServer){
                syncedTasks.add(Task.uploadTaskToServer(this, task));
            }
        }
        return syncedTasks;
    }

    private void switchFragBack(Fragment frag){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, frag)
                .addToBackStack(frag.getClass().getCanonicalName()).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent updateWidget = new Intent(this, ListWidgetProvider.class);
        updateWidget.setAction("update_widget");
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pending.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
