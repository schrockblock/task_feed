package com.rndapp.task_feed.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.rndapp.task_feed.adapters.TaskListAdapter;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.interfaces.ProjectDisplayer;
import com.rndapp.task_feed.listeners.SwipeDismissListViewTouchListener;
import com.rndapp.task_feed.models.Project;
import com.rndapp.task_feed.models.Task;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:33 PM
 *
 */
public class ProjectFragment extends SherlockFragment {
    public ProjectDisplayer delegate;
    private Project project;
    private TaskListAdapter adapter;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_PROJECT = "project";

    public ProjectFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        project = (Project)getArguments().getSerializable(ARG_PROJECT);

        View rootView = inflater.inflate(R.layout.fragment_project, container, false);
        rootView.setBackgroundColor(project.getColor());

        adapter = new TaskListAdapter(getActivity(), project.getTasks());

        ListView lv = (ListView)rootView.findViewById(R.id.task_list_view);
        lv.setAdapter(adapter);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    project.deleteTask(getActivity(), position);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        lv.setOnTouchListener(touchListener);
        lv.setOnScrollListener(touchListener.makeScrollListener());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editTask(project.getTask(position));
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.project, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getTitle().equals(getString(R.string.action_add_task))){
            //new task
            createNewTask();
        } else{
            //edit project
            editProject();
        }
        return true;
    }

    private void createNewTask(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set title
        alertDialogBuilder.setTitle("New Task");

        View layout = getSherlockActivity().getLayoutInflater().inflate(R.layout.new_task, null);

        final EditText taskTitle = (EditText)layout.findViewById(R.id.task);

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Task task = new Task();
                                task.setText(taskTitle.getText().toString());
                                project.addTask(getActivity(), task);
                                adapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {}
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void editTask(final Task task){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set title
        alertDialogBuilder.setTitle(getString(R.string.edit_task));

        View layout = getSherlockActivity().getLayoutInflater().inflate(R.layout.new_task, null);

        final EditText taskTitle = (EditText)layout.findViewById(R.id.task);
        final EditText taskPos = (EditText)layout.findViewById(R.id.position);

        //populate text fields
        taskTitle.setText(task.getText());
        taskPos.setText(String.valueOf(task.getPosition()));

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                task.setText(taskTitle.getText().toString());
                                task.setPosition(Integer.parseInt(taskPos.getText().toString()));
                                project.updateTask(getActivity(), task);
                                adapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {}
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    int swatchColor;
    private void editProject(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set title
        alertDialogBuilder.setTitle(getString(R.string.action_edit_project));

        View layout = getSherlockActivity().getLayoutInflater().inflate(R.layout.new_project, null);

        final EditText projectTitle = (EditText)layout.findViewById(R.id.projectName);
        projectTitle.setText(project.getTitle());

        final View swatch = layout.findViewById(R.id.color_swatch);

        Button btnRed = (Button)layout.findViewById(R.id.btn_red);
        Button btnBlue = (Button)layout.findViewById(R.id.btn_blue);
        Button btnPlum = (Button)layout.findViewById(R.id.btn_plum);
        Button btnGold = (Button)layout.findViewById(R.id.btn_yellow);
        Button btnOrange = (Button)layout.findViewById(R.id.btn_orange);
        Button btnGreen = (Button)layout.findViewById(R.id.btn_green);

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_blue:
                        swatchColor = getResources().getColor(R.color.pastel_blue);
                        swatch.setBackgroundColor(getResources().getColor(R.color.pastel_blue));
                        break;
                    case R.id.btn_green:
                        swatchColor = getResources().getColor(R.color.pastel_green);
                        swatch.setBackgroundColor(getResources().getColor(R.color.pastel_green));
                        break;
                    case R.id.btn_orange:
                        swatchColor = getResources().getColor(R.color.pastel_orange);
                        swatch.setBackgroundColor(getResources().getColor(R.color.pastel_orange));
                        break;
                    case R.id.btn_plum:
                        swatchColor = getResources().getColor(R.color.pastel_plum);
                        swatch.setBackgroundColor(getResources().getColor(R.color.pastel_plum));
                        break;
                    case R.id.btn_red:
                        swatchColor = getResources().getColor(R.color.pastel_red);
                        swatch.setBackgroundColor(getResources().getColor(R.color.pastel_red));
                        break;
                    case R.id.btn_yellow:
                        swatchColor = getResources().getColor(R.color.pastel_yellow);
                        swatch.setBackgroundColor(getResources().getColor(R.color.pastel_yellow));
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

        swatchColor = getResources().getColor(R.color.goldenrod);

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                project.setTitle(projectTitle.getText().toString());
                                project.setColor(swatchColor);
                                Project.updateProject(getActivity(), project);
                                delegate.setupNav();
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {}
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
