package com.ombre901.samuelhibbard.byui_events_app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyEventsActivity extends Activity implements ActivityObserver{
    /*
     * MEMBER VARIABLES
     */
    private static ExpandableListViewAdapter listAdapter = null;
    private static ExpandableListView expListView;
    private static TextView dateView;
    private Database database = Database.getInstance();

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

        expListView = (ExpandableListView) findViewById(R.id.myEventsList);
        dateView = (TextView) findViewById(R.id.myEventsDate);

        setAdapter();
    }

    /**
     * SETADAPTER
     */
    private void setAdapter() {
        //create the lists!
        List<String> headerList = new ArrayList<String>();
        Map<String, String> childList = new HashMap<String, String>();
        List<byte[]> images = new ArrayList<byte[]>();
        Map<String, String[]> dateList = new HashMap<String, String[]>();

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
            listAdapter = new ExpandableListViewAdapter(this, headerList, childList, images, dateList, "MYEVENTS", this);
        } else {
            listAdapter.setLists(headerList, childList, images, dateList);
        }

        //now set it to the screen!
        expListView.setAdapter(listAdapter);
    }

    /**
     * ONSTART
     *  This will start the activity!
     */
    @Override
    protected void onStart() {
        super.onStart();

        //now create a listener for the list!
        //this will only allow one thing to be selected!
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if (previousItem != groupPosition) {
                    expListView.collapseGroup(previousItem);
                    previousItem = groupPosition;
                }
            }
        });
    }

    /**
     * ONRESUME
     */
    @Override
    protected void onResume() {
        super.onResume();
        //check to see if there are any new events saved!
        setAdapter();
    }

    @Override
    public void update() {
        setAdapter();
    }
}

