package com.ombre901.samuelhibbard.byui_events_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Grant on 12/13/2014.
 */
public class SearchActivity  {
    Activity activity;
    TextView textView;
    ExpandableListView expandableListView;
    ExpandableListViewAdapter listAdapter;
    Database database;
    String queryText;

    /**
     * SEARCHACTIVITY
     *  This will search for events!
     * @param activity
     * @param queryText
     */
    public SearchActivity(Activity activity, String queryText) {
        this.activity = activity;
        database = Database.getInstance();
        this.queryText = queryText;

        final Dialog dialog = new Dialog(activity);

        //tell the Dialog to use the dialog.xml as it's layout description
        dialog.setContentView(R.layout.activity_search);
        dialog.setTitle("Searching for: " + queryText);

        textView = (TextView) dialog.findViewById(R.id.searchDate);
        expandableListView = (ExpandableListView)dialog.findViewById(R.id.searchList);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        setAdapter();

        dialog.show();
    }

    /**
     * SETADPATER
     */
    private void setAdapter() {
        //create the lists!
        List<String> headerList = new ArrayList<String>();
        Map<String, String> childList = new HashMap<String, String>();
        List<byte[]> images = new ArrayList<byte[]>();
        Map<String, String[]> dateList = new HashMap<String, String[]>();

        //now grab from the database!
        database.searchEvents(queryText, headerList, childList, images, dateList);

        //now to put it on the screen!
        if (listAdapter == null) {
            listAdapter = new ExpandableListViewAdapter(activity, headerList, childList, images, dateList, "SEARCH", null);
        } else {
            listAdapter.setLists(headerList, childList, images, dateList);
        }

        //now set it to the screen!
        expandableListView.setAdapter(listAdapter);
    }
}
