package com.ombre901.samuelhibbard.byui_events_app;

/**
 * Created by Iceman on 3/7/2015.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventActivity extends Activity {

    private String details;
    private String [] date;
    private String title;
    private byte [] image;
    private ImageButton backButton;
    //private Button saveButton;
    private String activity;
    private ImageButton saveButton;
    private TextView viewButton1;
    private TextView viewButton2;

    public EventActivity() {
        //details = "This was copied from the default constructor";

    }

    public EventActivity(String details) {
        this.details = details;
    }

     /*
     * MEMBER METHODS
     */

    /**
     * ONCREATE
     * Create the list and display it!
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            details = extras.getString("details");
            date = extras.getStringArray("date");
            title = extras.getString("title");
            image = extras.getByteArray("image");
            activity = extras.getString("activity");
        }


        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(details);
        textView.setMovementMethod(new ScrollingMovementMethod());

        TextView textView2 = (TextView) findViewById(R.id.textView2);
        //textView2.setText(date[0]);

        //and parse string correctly!
        String[] split = title.split("~");

        if (split.length > 1) {
            textView2.setText(split[1]);
        }

        TextView textView3 = (TextView) findViewById(R.id.textView3);
        //Log.d("Date: ", date);
        String[] splitDate = date[0].split("-");
        textView3.setText(splitDate[1] + "/" + splitDate[2] + " " + date[1]);


        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        //set the image properly
        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

            //now set the image!
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.no_image);
        }

        backButton = (ImageButton) findViewById(R.id.button2);

        saveButton = (ImageButton) findViewById(R.id.button3);

        viewButton1 = (TextView) findViewById(R.id.textView4);
        viewButton1.setText("Back");

        viewButton2 = (TextView) findViewById(R.id.textView5);

        if(activity.equals("MyEvents")){
            viewButton2.setText("Delete");
        } else {
            viewButton2.setText("Save");
        }

    }

    /**
     * ONRESUME
     * This will start the activity!
     */
    @Override
    protected void onResume() {
        super.onResume();
        saveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activity.equals("MyEvents")) {
                    Log.d("TOUCH EVENT: ", "TOUCHED!");

                    //now grab the database!
                    Database database = Database.getInstance();

                    //now insert it!
                    database.insertMyEvents(title);

                    //close the event after save
                    finish();
                } else {
                    //grab database
                    Database database = Database.getInstance();

                    //now delete it!
                    database.deleteFromMy_Events(title);

                    finish();
                }
            }
        });

        backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
