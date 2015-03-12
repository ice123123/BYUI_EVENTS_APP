package com.ombre901.samuelhibbard.byui_events_app;


import android.app.Activity;
import android.widget.ExpandableListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SamuelHibbard on 12/16/14.
 */
public abstract class TemplateActivity extends Activity {
    protected Database database = Database.getInstance();
    protected String stringDate;

    protected List<String> headerList;
    protected Map<String, String> childList;
    protected List<byte[]> imageList;
    protected Map<String, String[]> dateList;

    protected void setAdapter() {
        // Create the lists!
        headerList = new ArrayList<String>();
        childList  = new HashMap<String, String>();
        imageList  = new ArrayList<byte[]>();
        dateList   = new HashMap<String, String[]>();

        // Grab from the database and set the title as well
        grabFromDatabase();
        setTitle();

        // Now put it on the screen
        setUpListViewAdapter();
    }

    protected String dateFormat(String textDate) {
        // Create the variables!
        String date = "";
        String[] splitDate = textDate.split("-");
        String[] month = {
                "none", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "July", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        date = month[Integer.parseInt(splitDate[1])] + " " + splitDate[2] + " " + splitDate[0];

        return date;
    }

    protected abstract void grabFromDatabase();
    protected abstract void setTitle();
    protected abstract void setUpListViewAdapter();
}
