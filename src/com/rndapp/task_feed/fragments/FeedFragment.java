package com.rndapp.task_feed.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.models.Project;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:32 PM
 *
 */
public class FeedFragment extends SherlockFragment {
    private Vector<Project> projects;
    public static final String ARG_PROJECTS = "projects";

    public FeedFragment() {
        Log.wtf("Feed","called");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.wtf("Feed", "created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.wtf("Feed","view called");
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        projects = (Vector<Project>)getArguments().getSerializable(ARG_PROJECTS);
        TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
        dummyTextView.setText("Feed");
        return rootView;
    }
}
