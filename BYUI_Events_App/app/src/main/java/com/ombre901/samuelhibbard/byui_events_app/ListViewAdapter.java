package com.ombre901.samuelhibbard.byui_events_app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Iceman on 3/7/2015.
 */
public class ListViewAdapter extends BaseAdapter {

    private List<String> titleList;
    private Map<String, String> childList;
    private List<byte[]> images;
    private Map<String, String[]> dateList;
    private LayoutInflater inflater;
    private String activity;
    private ActivityObserver act;

    /**
     * CONSTRUCTOR
     * @param activity
     * @param list
     * @param childList
     */
    public ListViewAdapter(Activity activity, List<String> list,
                                     Map<String, String> childList,
                                     List<byte[]> images,
                                     Map<String, String[]> dateList,
                                     String type, ActivityObserver act) {
        this.titleList = list;
        this.childList = childList;
        this.images = images;
        this.dateList = dateList;
        inflater = activity.getLayoutInflater();
        this.activity = type;
        this.act = act;
    }

    /**
     * SETLISTS
     *  Set the lists!
     * @param list
     * @param childList
     * @param images
     * @param dateList
     */
    public void setLists(List<String> list, Map<String, String> childList, List<byte[]> images,
                         Map<String, String[]> dateList) {
        this.titleList = list;
        this.childList = childList;
        this.images = images;
        this.dateList = dateList;
    }



    @Override
    public int getCount() {
        return titleList.size();
    }

    @Override
    public Object getItem(int position) {
        return titleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * GETDATE
     *  Grab the date!
     * @param groupPosition
     * @return
     */
    public String[] getDate(int groupPosition) {
        return dateList.get(titleList.get(groupPosition));
    }

    /**
     * GETIMAGE
     *  Grab the image!
     * @param groupPosition
     * @return
     */
    public byte[] getImage(int groupPosition) {
        if (images.size() > 0) {
            return images.get(groupPosition);
        }

        return null;
    }

    /**
     * SETLISTS
     *  Set the header
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //grab the title!
        String title = (String) getItem(position);
        String textDate;

        //inflate the view if null!
        if (view == null) {
            //now convert it!
            view = inflater.inflate(R.layout.list_view, null);
        }

        TextView titleView = (TextView) view.findViewById(R.id.list_view);

        //and parse string correctly!
        String[] split = title.split("~");

        if (split.length > 1) {
            title = split[1];
            //now grab the date!
            String[] date = getDate(position);

            //grab the date!
            if (activity.equals("DAY")) {
                textDate = date[1];

            } else {
                //split the date into just month and day!
                String[] splitDate = date[0].split("-");
                textDate = splitDate[1] + "/" + splitDate[2] + " " + date[1];
            }

            //check to make sure there is something there!
            byte[] image = getImage(position);

            ImageView imageView = (ImageView) view.findViewById(R.id.image_view);

            //convert the bytes to an image!
            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

                //now set the image!
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.no_image);
            }

            //now add the title text!
            titleView.setText(Html.fromHtml("<b>" + title + "</b><br />" + textDate));
        } else {
            titleView.setText("No events");
        }

        //Log.d("Inside this special function", "Here i am");
        return view;
    }
}
