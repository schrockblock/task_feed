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
import com.rndapp.task_feed.adapters.ProjectListAdapter;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.interfaces.ProjectDisplayer;
import com.rndapp.task_feed.listeners.SwipeDismissListViewTouchListener;
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
                                    adapter.removeItemFromProject(position);
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
                        position + 1);
            }
        });

        lv.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.feed, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        newProject();
        return true;
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
                                ProjectDataSource source = new ProjectDataSource(getActivity());
                                source.open();
                                Project project = source.createProject(taskTitle.getText().toString(), swatchColor, 0);
                                source.close();
                                projects.add(project);
                                delegate.setupNav();
                                getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(
                                        projects.indexOf(project) + 1);
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {}
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
