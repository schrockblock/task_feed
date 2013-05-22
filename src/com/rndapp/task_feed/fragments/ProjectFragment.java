package com.rndapp.task_feed.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.models.Project;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:33 PM
 *
 */
public class ProjectFragment extends SherlockFragment {
    private Project project;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_PROJECT = "project";

    public ProjectFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed,
                container, false);
        project = (Project)getArguments().getSerializable(ARG_PROJECT);
        TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
        dummyTextView.setText(project.getTitle());
        return rootView;
    }
}
