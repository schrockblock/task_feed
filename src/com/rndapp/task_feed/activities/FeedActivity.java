package com.rndapp.task_feed.activities;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.data.TaskDataSource;
import com.rndapp.task_feed.fragments.FeedFragment;
import com.rndapp.task_feed.fragments.ProjectFragment;
import com.rndapp.task_feed.models.Project;
import com.rndapp.task_feed.models.Task;

import java.util.Vector;

public class FeedActivity extends SherlockFragmentActivity implements
		ActionBar.OnNavigationListener {
    private Vector<Project> projects;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        //load projects
        loadProjects();

        //get titles
        String[] dropdownTitles = new String[projects.size()+2];

        dropdownTitles[0] = "Feed";
        for (int i = 0; i < projects.size(); i++){
            dropdownTitles[i+1] = projects.get(i).getTitle();
        }
        dropdownTitles[dropdownTitles.length - 1] = "New Project";

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
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.feed, menu);
//		return true;
//	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
        if (position == 0){
            //TODO: feed
            Fragment fragment1 = new FeedFragment();
            Bundle args = new Bundle();
            args.putSerializable(FeedFragment.ARG_PROJECTS, projects);
            fragment1.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment1).commit();
        }else if (position == projects.size()+1){
            //TODO: add project
            Fragment fragment2 = new AddProjectFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment2).commit();
        }else{
                //TODO: display project
                Fragment fragment3 = new ProjectFragment();
                Bundle args2 = new Bundle();
                args2.putSerializable(ProjectFragment.ARG_PROJECT, projects.get(position));
                fragment3.setArguments(args2);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment3).commit();
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
        Vector<Task> tasks = taskDataSource.getAllTasks();
        taskDataSource.close();

        //associate
        for (Project project : projects){
            for (Task task : tasks){
                if (task.getProjectId() == project.getServerId()){
                    project.addTask(task);
                }
            }
        }
    }

    public class AddProjectFragment extends SherlockFragment {
        public AddProjectFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_feed,
                    container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText("Hi");
            return rootView;
        }
    }
}
