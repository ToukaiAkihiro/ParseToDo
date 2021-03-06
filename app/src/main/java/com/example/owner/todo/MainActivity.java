package com.example.owner.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mTaskListView;
    private TaskAdapter mAdapter;
    private EditText mTitleEditText;

    private String delete = getString(R.string.delete);
    private String add_title = getString(R.string.add_title);
    private String add = getString(R.string.add);
    private String failed_add =getString(R.string.failed_add);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTaskListView = (ListView) findViewById(R.id.listView);
        mAdapter = new TaskAdapter(getApplicationContext(),
                R.layout.listview_item_task,
                new ArrayList<Task>());
        mTaskListView.setAdapter(mAdapter);
        parse();
    }

    public void parse() {
        ParseQuery<Task> parseQuery = new ParseQuery<>(Task.class);
        //parseQuery.whereEqualTo("check", "true");
        parseQuery.findInBackground(new FindCallback<Task>() {
            @Override
            public void done(List<Task> list, ParseException e) {
                if (e == null) {
                    mAdapter.addAll(list);
                } else {
                    e.printStackTrace();
                }
            }
        });

        Button deleteButton = new Button(getApplicationContext());
        deleteButton.setText(delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteTask();
            }
        });
        mTaskListView.addFooterView(deleteButton);

        Button addButton = new Button(getApplicationContext());
        addButton.setText("追加する");
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog();
            }
        });
        mTaskListView.addFooterView(addButton);

    }


    private void showDialog() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_new_task, null);
        mTitleEditText = (EditText) view.findViewById(R.id.editTextTitle);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("新しいタスクを追加");
        builder.setPositiveButton(add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                addNewTask(mTitleEditText.getText().toString());
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void addNewTask(String title) {

        final Task task = new Task();
        task.setTitle(title);
        task.setCheck(false);
        task.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    mAdapter.add(task);
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "追加に失敗しました",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void deleteTask() {

        final Task task = new Task();
        task.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    mAdapter.remove(task);
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "削除に失敗しました",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, Menu.FIRST, Menu.NONE, "更新");
        // Inflate the menu; this adds items to the action bar if it is present
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == 0) {
            parse();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        showDialog();
    }
}
