package com.rndapp.task_feed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.models.Project;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM
 *
 */
public class ProjectListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Project> projects;

    public ProjectListAdapter(Context context, ArrayList<Project> projects){
        this.context = context;
        this.projects = (ArrayList<Project>) projects.clone();

        removeEmptyProjects();
    }

    public void removeEmptyProjects(){
        for (Project project : projects){
            if (project.isEmpty() || project.isHidden()){
                projects.remove(project);
                removeEmptyProjects();
                break;
            }
        }
    }

    @Override
    public int getCount() {
        return projects.size();
    }

    @Override
    public Project getItem(int position) {
        return projects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_list_item, null);
        }

        Project project = getItem(position);
        TextView tv = (TextView)convertView.findViewById(R.id.tv_task);
        tv.setText(project.getName() + ": " + project.getFirstTaskText());
        tv.setBackgroundColor(project.getColor());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        removeEmptyProjects();
    }
}
