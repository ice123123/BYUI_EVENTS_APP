package com.ombre901.samuelhibbard.byui_events_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyEventsActivity extends Activity implements ActivityObserver{
    /*
     * MEMBER VARIABLES
     */
    private static ListViewAdapter listAdapter = null;
    private static ListView listView;
    private static TextView dateView;
    private Database database = Database.getInstance();
    private Button deleteButton;
    private List<String> headerList;
    private Map<String, String> childList;
    private List<byte[]> images;
    private Map<String, String[]> dateList;

    /*
     * MEMBER METHODS
     */

    /**
     * ONCREATE
     *  Create the event!
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        listView = (ListView) findViewById(R.id.myEventsList);
        dateView = (TextView) findViewById(R.id.myEventsDate);

        setAdapter();
    }

    /**
     * SETADAPTER
     */
    private void setAdapter() {
        //create the lists!
        headerList = new ArrayList<String>();
        childList = new HashMap<String, String>();
        images = new ArrayList<byte[]>();
        dateList = new HashMap<String, String[]>();

        //now grab from the database!
        database.selectAllMy_Events(headerList, childList, images, dateList);

        //check the lists!
        if (childList.size() == 0) {
            dateView.setText("No events saved");
        } else {
            String title;

            if (childList.size() == 1) {
                title = " Event saved";
            } else {
                title = " Events saved";
            }

            dateView.setText(Integer.toString(headerList.size()) + title);
        }

        //now to put it on the screen!
        if (listAdapter == null) {
            Log.d("My_Events: ", "create list adapter");
            listAdapter = new ListViewAdapter(this, headerList, childList, images, dateList, "MYEVENTS", this);
        } else {
            listAdapter.setLists(headerList, childList, images, dateList);
        }

        //now set it to the screen!
        listView.setAdapter(listAdapter);
    }

    /**
     * ONRESUME
     */
    @Override
    protected void onResume() {
        super.onResume();
        //check to see if there are any new events saved!
        //now create a listener for the list!
        //this will only allow one thing to be selected!
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Activity eventActivity = new EventActivity(childList.get(headerList.get(position)));
                Intent intent = new Intent(MyEventsActivity.this, EventActivity.class);
                intent.putExtra("details", childList.get(headerList.get(position)));
                intent.putExtra("date", dateList.get(headerList.get(position)));
                intent.putExtra("title", headerList.get(position));
                intent.putExtra("image",images.get(position));
                intent.putExtra("activity","MyEvents");
                MyEventsActivity.this.startActivity(intent);
            }
        });


        setAdapter();
        update();
    }

    @Override
    public void update() {
        setAdapter();
    }
}

