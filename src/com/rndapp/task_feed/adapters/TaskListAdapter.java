package com.rndapp.task_feed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.models.Task;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM
 *
 */
public class TaskListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Task> tasks;

    public TaskListAdapter(Context context, ArrayList<Task> tasks){
        this.context = context;
        this.tasks = tasks;
        removeFinishedTasks();
    }

    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getOrder();
    }

    @Override
    public int getCount(){
        return tasks.size();
    }

    private void removeFinishedTasks(){
        for (Task task : tasks){
            if (task.isFinished()){
                tasks.remove(task);
                removeFinishedTasks();
                break;
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_list_item, null);
        }

        Task task = getItem(position);
        ((TextView)convertView.findViewById(R.id.tv_task)).setText(task.getName());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        removeFinishedTasks();
    }
}
