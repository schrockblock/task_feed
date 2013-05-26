package com.rndapp.task_feed.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.rndapp.task_feed.models.Project;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 2:28 PM
 * 
 */
public class ProjectDataSource {
    // Database fields
    private SQLiteDatabase database;
    private ProjectOpenHelper dbHelper;
    private String[] allColumns = { ProjectOpenHelper.COLUMN_ID,
            ProjectOpenHelper.COLUMN_SERVER_ID,
            ProjectOpenHelper.COLUMN_COLOR,
            ProjectOpenHelper.COLUMN_TITLE};

    public ProjectDataSource(Context context) {
        dbHelper = new ProjectOpenHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Project createProject(String title, int color, int serverId) {
        ContentValues values = new ContentValues();
        values.put(ProjectOpenHelper.COLUMN_SERVER_ID, serverId);
        values.put(ProjectOpenHelper.COLUMN_COLOR, color);
        values.put(ProjectOpenHelper.COLUMN_TITLE, title);
        long insertId = database.insert(ProjectOpenHelper.TABLE_PROJECTS, null,
                values);
        Cursor cursor = database.query(ProjectOpenHelper.TABLE_PROJECTS,
                allColumns, ProjectOpenHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Project newProject = cursorToProject(cursor);
        cursor.close();
        return newProject;
    }

    public void updateProject(Project project){
        ContentValues values = new ContentValues();
        values.put(ProjectOpenHelper.COLUMN_SERVER_ID, project.getServerId());
        values.put(ProjectOpenHelper.COLUMN_COLOR, project.getColor());
        values.put(ProjectOpenHelper.COLUMN_TITLE, project.getTitle());

        database.update(ProjectOpenHelper.TABLE_PROJECTS,
                values,
                ProjectOpenHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(project.getLocalId())});
    }

    //DOES NOT DELETE TASKS ASSOCIATED WITH PROJECT!!
    public void deleteProject(Project project) {
        long id = project.getLocalId();
        System.out.println("Project deleted with id: " + id);
        database.delete(ProjectOpenHelper.TABLE_PROJECTS, ProjectOpenHelper.COLUMN_ID
                + " = " + id, null);
    }

    public ArrayList<Project> getAllProjects() {
        ArrayList<Project> projects = new ArrayList<Project>();

        Cursor cursor = database.query(ProjectOpenHelper.TABLE_PROJECTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Project project = cursorToProject(cursor);
            projects.add(project);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return projects;
    }

    private Project cursorToProject(Cursor cursor) {
        Project project = new Project();
        project.setLocalId(cursor.getInt(cursor.getColumnIndex(ProjectOpenHelper.COLUMN_ID)));
        project.setServerId(cursor.getInt(cursor.getColumnIndex(ProjectOpenHelper.COLUMN_SERVER_ID)));
        project.setColor(cursor.getInt(cursor.getColumnIndex(ProjectOpenHelper.COLUMN_COLOR)));
        project.setTitle(cursor.getString(cursor.getColumnIndex(ProjectOpenHelper.COLUMN_TITLE)));
        return project;
    }
}
