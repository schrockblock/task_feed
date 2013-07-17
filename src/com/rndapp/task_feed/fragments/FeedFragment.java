package com.rndapp.task_feed.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.activities.LoginActivity;
import com.rndapp.task_feed.adapters.ProjectListAdapter;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.interfaces.ProjectDisplayer;
import com.rndapp.task_feed.listeners.SwipeDismissListViewTouchListener;
import com.rndapp.task_feed.models.ActivityUtils;
import com.rndapp.task_feed.models.Project;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:32 PM
 *
 */
public class FeedFragment extends SherlockFragment {
    public ProjectDisplayer delegate;
    public ArrayList<Project> projects;
    private ProjectListAdapter adapter;

    public FeedFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        ListView lv = (ListView)rootView.findViewById(R.id.project_list_view);

        adapter = new ProjectListAdapter(getActivity(), projects);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    new RemoveItemTask().execute(adjustPosition(position));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        lv.setOnTouchListener(touchListener);
        lv.setOnScrollListener(touchListener.makeScrollListener());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(
                        adjustPosition(position) + 1);
            }
        });

        lv.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private int adjustPosition(int position){
        int result = position;
        for (int i = 0; i < result+1; i++){
            if (projects.get(i).isEmpty() || projects.get(i).isHidden()){
                result++;
            }
        }
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.feed, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add_project:
                newProject();
                break;
            case R.id.action_logout:
                ActivityUtils.logout(getActivity());
                break;
        }
        return true;
    }

    public void removeItemFromProject(int position){
        projects.get(position).removeFirstTask(getActivity());
    }

    private class RemoveItemTask extends AsyncTask<Integer, String, Object> {

        //param[0] = int position
        @Override
        protected String doInBackground(Integer... params) {
            removeItemFromProject(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Object o){
            adapter.removeEmptyProjects();
            adapter.notifyDataSetChanged();
        }
    }

    int swatchColor;
    public void newProject(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set title
        alertDialogBuilder.setTitle("New Project");

        View layout = getSherlockActivity().getLayoutInflater().inflate(R.layout.new_project, null);

        final EditText taskTitle = (EditText)layout.findViewById(R.id.projectName);

        final View swatch = layout.findViewById(R.id.color_swatch);

        Button btnRed = (Button)layout.findViewById(R.id.btn_red);
        Button btnBlue = (Button)layout.findViewById(R.id.btn_blue);
        Button btnPlum = (Button)layout.findViewById(R.id.btn_plum);
        Button btnGold = (Button)layout.findViewById(R.id.btn_yellow);
        Button btnOrange = (Button)layout.findViewById(R.id.btn_orange);
        Button btnGreen = (Button)layout.findViewById(R.id.btn_green);
        Button btnTurquoise = (Button)layout.findViewById(R.id.btn_turquoise);

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_blue:
                        swatchColor = getResources().getColor(R.color.blue);
                        swatch.setBackgroundColor(getResources().getColor(R.color.blue));
                        break;
                    case R.id.btn_green:
                        swatchColor = getResources().getColor(R.color.green);
                        swatch.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                    case R.id.btn_orange:
                        swatchColor = getResources().getColor(R.color.orange);
                        swatch.setBackgroundColor(getResources().getColor(R.color.orange));
                        break;
                    case R.id.btn_plum:
                        swatchColor = getResources().getColor(R.color.plum);
                        swatch.setBackgroundColor(getResources().getColor(R.color.plum));
                        break;
                    case R.id.btn_red:
                        swatchColor = getResources().getColor(R.color.red);
                        swatch.setBackgroundColor(getResources().getColor(R.color.red));
                        break;
                    case R.id.btn_yellow:
                        swatchColor = getResources().getColor(R.color.yellow);
                        swatch.setBackgroundColor(getResources().getColor(R.color.yellow));
                        break;
                    case R.id.btn_turquoise:
                        swatchColor = getResources().getColor(R.color.turquoise);
                        swatch.setBackgroundColor(getResources().getColor(R.color.turquoise));
                        break;
                }
            }
        };

        btnRed.setOnClickListener(listener);
        btnBlue.setOnClickListener(listener);
        btnOrange.setOnClickListener(listener);
        btnGreen.setOnClickListener(listener);
        btnGold.setOnClickListener(listener);
        btnPlum.setOnClickListener(listener);
        btnTurquoise.setOnClickListener(listener);

        swatchColor = getResources().getColor(R.color.goldenrod);

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new UploadNewProjectTask().execute(taskTitle.getText().toString());
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {}
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class UploadNewProjectTask extends AsyncTask<String, String, Project> {

        //param[0] = project name
        @Override
        protected Project doInBackground(String... params) {
            Project project = new Project(params[0],swatchColor);
            project = Project.uploadProjectToServer(getActivity(), project);
            project = Project.addProjectToDatabase(getActivity(), project);
            projects.add(project);
            return project;
        }

        @Override
        protected void onPostExecute(Project project){
            delegate.setupNav(null);
            getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(
                    projects.indexOf(project) + 1);
        }
    }
}
