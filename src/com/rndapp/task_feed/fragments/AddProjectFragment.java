package com.rndapp.task_feed.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.rndapp.task_feed.R;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 3:21 PM
 *
 */
public class AddProjectFragment extends SherlockFragment{
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
