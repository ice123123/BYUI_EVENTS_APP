package com.ombre901.samuelhibbard.byui_events_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Used to access the byui_events database and perform didn't MySQL operations on it.
 * Created by SamuelHibbard on 12/5/14.
 */
public class Database extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "BYUIEvents.db";
    private final static int VERSION = 1;
    private static Database database = null;
    private List<String> filters = new ArrayList<String>();

    private static final String SQL_CREATE_EVENT_TABLE =
            "CREATE TABLE IF NOT EXISTS event"
                    + "( event_id    INTEGER  NOT NULL"
                    + ", event_name  TEXT     NOT NULL"
                    + ", date        TEXT"
                    + ", start_time  TEXT"
                    + ", end_time    TEXT"
                    + ", description TEXT"
                    + ", category    TEXT"
                    + ", location    TEXT"
                    + ", picture     BLOB)";

    private static final String SQL_CREATE_MY_EVENTS_TABLE =
            "CREATE TABLE IF NOT EXISTS my_events"
                    + "( event_id    INTEGER  NOT NULL"
                    + ", event_name  TEXT     NOT NULL"
                    + ", date        TEXT"
                    + ", start_time  TEXT"
                    + ", end_time    TEXT"
                    + ", description TEXT"
                    + ", category    TEXT"
                    + ", location    TEXT"
                    + ", picture     BLOB)";

    private static final String SQL_CREATE_COMMON_LOOKUP_TABLE =
            "CREATE TABLE IF NOT EXISTS common_lookup"
            + "( event_id      INTEGER"
            + ", calendar_name TEXT)";

    private static final String SQL_CREATE_CALENDAR_TABLE =
            "CREATE TABLE IF NOT EXISTS calendar"
            + "( name  Text)";

    /**
     *  Creates the database!
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table!
        db.execSQL(SQL_CREATE_EVENT_TABLE);
        db.execSQL(SQL_CREATE_MY_EVENTS_TABLE);
        db.execSQL(SQL_CREATE_CALENDAR_TABLE);
        db.execSQL(SQL_CREATE_COMMON_LOOKUP_TABLE);
    }

    /**
     *  Upgrades the database!
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * ADDTOFILTER
     *  Add to the list of strings!
     * @param text
     */
    public void addToFilter(String text) {
        filters.add(text);
    }

    /**
     * DELETEFROMFILTER
     * @param text
     */
    public void deleteFromFilter(String text) {
        filters.remove(text);
    }

    /**
     * DELETEALLFROMFILTER
     */
    public void deleteAllFromFilter() {
        filters.clear();
    }

    /**
     * Constructs a Database object
     * @param context
     */
    private Database(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     *  Creates a new instance and returns it!
     * @param context
     * @return
     */
    public static Database newInstance(Context context) {
        // See if it is null!
        if (database == null) {
            // Create the database!
            database = new Database(context);
        }

        return database;
    }

    /**
     * Gets the instance of the Database
     * @return
     */
    public static Database getInstance() {
        return database;
    }


    public void deleteFromMy_Events(String header) {
        //grab database!
        SQLiteDatabase write = this.getWritableDatabase();

        //now grab the right things!
        String [] split = header.split("~");
        String event_id = split[0];

        write.delete("my_events", "event_id = " + event_id, null);

        write.close();
    }

    /**
     * INSERTMYEVENTS
     *  Insert the event into the myevents table!
     * @param header
     */
    public void insertMyEvents(String header) {
        // Grab the read database!
        SQLiteDatabase read = this.getReadableDatabase();

        // Now grab the right things to select the data!
        String [] splitHeader = header.split("~");
        String event_id = splitHeader[0];

        // Grab the event!
        Cursor cursor = read.rawQuery("SELECT * FROM event WHERE event_id = '"
                                     + event_id + "'", null);

        //make sure that it is not the same event in myevents!
        Cursor check = read.rawQuery("SELECT * FROM my_events WHERE event_id = '"
                                     + event_id + "'", null);

        Log.d("Selecting event", event_id);

        // Grab the data... should be able to...
        if (cursor != null && cursor.getCount() > 0 && check.getCount() == 0) {
            //go to the first element in the list!
            cursor.moveToFirst();

            // Grab everything!
            String eventId = cursor.getString(cursor.getColumnIndex("event_id"));
            String name = cursor.getString(cursor.getColumnIndex("event_name"));
            String dateText = cursor.getString(cursor.getColumnIndex("date"));
            String start_time = cursor.getString(cursor.getColumnIndex("start_time"));
            String end_time = cursor.getString(cursor.getColumnIndex("end_time"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            String category = cursor.getString(cursor.getColumnIndex("category"));
            String location = cursor.getString(cursor.getColumnIndex("location"));
            byte [] image = cursor.getBlob(cursor.getColumnIndex("picture"));

            // Now insert into the database!
            SQLiteDatabase write = this.getWritableDatabase();

            // Put the values into the content!
            ContentValues values = new ContentValues();
            values.put("event_id", eventId);
            values.put("event_name", name);
            values.put("date", dateText);
            values.put("start_time", start_time);
            values.put("end_time", end_time);
            values.put("description", description);
            values.put("category", category);
            values.put("location", location);
            values.put("picture", image);

            // Now insert it!
            Log.d("Inserting event", eventId);
            write.insert("my_events", null, values);
            write.close();
        }

        read.close();
    }

    /**
     * SELECTALLMY_EVENTS
     *  This will select all the events from My_Events table.
     * @param header
     * @param childs
     * @param images
     * @param dateList
     */
    public void selectAllMy_Events(List<String> header, Map<String, String> childs,
                                   List<byte[]> images, Map<String, String[]> dateList) {
        // Now grab all the events!
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM my_events",null);
        Log.d("Selecting events", "All");

        gatherDataFromCursor(cursor, header, childs, images, dateList);
        db.close();
    }

    /**
     * INSERTEVENT
     *  Add an event to the table!
     * @param textDate
     * @param pic
     */
    public void insertEvent(String [] textDate, byte [] pic) {
        //check to see if the event is already there!
        SQLiteDatabase read = this.getReadableDatabase();
        boolean insert = false;

        Cursor cursor = read.rawQuery("SELECT event_id FROM event WHERE event_id = '" +
                                      textDate[0] + "'", null);

        if (cursor.getCount() != 1) {
            Log.d("DATABASE: ", "Inserting into event");
            insert = true;
        }

        read.close();

        if (insert) {
            // Grab the database!
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();


            // Now put the values into the table!
            values.put("event_id", Integer.parseInt(textDate[0]));
            values.put("event_name", textDate[1]);
            values.put("date", textDate[2]);
            values.put("start_time", textDate[3]);
            values.put("end_time", textDate[4]);
            values.put("description", textDate[5]);
            values.put("category", textDate[6]);
            values.put("location", textDate[7]);
            values.put("picture", pic);

            // Now insert it!
            db.insert("event", null, values);

            // Remember to close it!
            db.close();
        }
    }

    /**
     * INSERTCOMMONLOOKUP
     *  This will insert the date into these tables:
     *                  COMMON_LOOKUP
     * @param clookup
     */
    public void insertCommonLookup(String [] clookup) {

        SQLiteDatabase read = this.getReadableDatabase();
        boolean insert = false;


        Cursor cursor = read.rawQuery("SELECT * FROM common_lookup WHERE event_id = '" +
                                    clookup[0] + "' AND calendar_name = '" + clookup[1] + "'", null);



        if (cursor.getCount() == 0) {
            Log.d("DATABASE: ", clookup[0]);
            read.close();

            //grab the database!
            SQLiteDatabase db = this.getWritableDatabase();

            //create a values variable
            ContentValues values = new ContentValues();

            //now put the data into the values!
            values.put("event_id", Integer.parseInt(clookup[0]));
            values.put("calendar_name", clookup[1]);

            //now insert it!
            db.insert("common_lookup", null, values);

            db.close();
        }
    }

    /**
     * INSERTCALENDAR
     *  Insert the names into the calendar table!
     * @param name
     */
    public void insertCalendar(String name) {

        SQLiteDatabase read = this.getReadableDatabase();
        boolean insert = false;

        Cursor cursor = read.rawQuery("SELECT name FROM calendar WHERE name = '" +
                                    name + "'", null);

        if (cursor.getCount() == 0) {
            Log.d("DATABASE: ", "Inserting into calendar");
            insert = true;
        }

        read.close();

        if (insert) {
            //grab the database
            SQLiteDatabase db = this.getWritableDatabase();

            //insert into the table!
            ContentValues value = new ContentValues();
            value.put("name", name);

            db.insert("calendar", null, value);

            db.close();
        }
    }

    /**
     * GRABQUERY
     *  This will create the query!
     * @param startDate
     * @param endDate
     * @param db
     * @return
     */
    public Cursor grabQuery(String startDate, String endDate, SQLiteDatabase db) {
        //create a new cursor!
        Cursor cursor = null;
        String query;

        //only filter by day!
        if (filters.size() == 0) {
            if (startDate.equals(endDate)) {
                query = "SELECT * FROM event WHERE date = '"
                        + startDate + "'";
            } else {
                query = "SELECT * FROM event WHERE date BETWEEN '"
                        + startDate + "' AND '" + endDate
                        + "' ORDER BY date(date), start_time";
            }
        } else {
            //else filter by the other things!
            query = "SELECT * FROM event AS e " +
                    "JOIN common_lookup AS co ON co.event_id = e.event_id " +
                    "JOIN calendar AS ca ON ca.name = co.calendar_name " +
                    "WHERE ";

            String filterQuery = new String();

            //grab the filter!
            int count = 0;
            for (String filter : filters) {
                if (count == 0) {
                    filterQuery = "ca.name = '" + filter + "'";
                    count++;
                } else {
                    filterQuery = filterQuery + " OR ca.name = '" + filter + "'";
                }
            }

            //now put it together!
            query = query + filterQuery;

            //now put the dates on too!
            if (startDate.equals(endDate)) {
                query = query + " AND e.date = '" + startDate + "'";
            } else {
                query = query + " AND e.date >= '" + startDate + "' AND e.date <= '" + endDate + "'" +
                        " ORDER BY date(date), start_time";
            }
        }

        Log.d("DATABASE: ", query);

        cursor = db.rawQuery(query, null);

        return cursor;
    }

    /**
     * SELECTEVENTS
     *  Select the events based on the day!
     * @param startDate
     * @param endDate
     * @param header
     * @param childs
     * @param images
     * @param dateList
     */
    public void selectEvents(String startDate, String endDate, List<String> header, Map<String, String> childs,
                             List<byte[]> images, Map<String, String[]> dateList) {
        // Grab the database!
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        cursor = grabQuery(startDate, endDate, db);

        gatherDataFromCursor(cursor, header, childs, images, dateList);
        db.close();
    }

    /**
     * SEARCHEVENTS
     *  Search for the event!
     * @param queryTitle
     * @param header
     * @param childs
     * @param images
     * @param dateList
     */
    public void searchEvents(String queryTitle, List<String> header, Map<String, String> childs,
                             List<byte[]> images, Map<String, String[]> dateList) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Create select statement! Which one we want depends if startDate and endDate are the same or not.
        cursor = db.rawQuery("SELECT * FROM event WHERE event_name LIKE '%" + queryTitle + "%'", null);

        gatherDataFromCursor(cursor, header, childs, images, dateList);
    }

    /**
     * GATHERDATAFROMCURSOR
     *  If the cursor isn't null then we need to get stuff from it and store it in the parameters provided.
     * @param cursor
     * @param header
     * @param childs
     * @param images
     * @param dateList
     */
    private void gatherDataFromCursor(Cursor cursor, List<String> header, Map<String, String> childs,
                                      List<byte[]> images, Map<String, String[]> dateList) {
        // Now check to make sure there is something there!
        if (cursor != null && cursor.getCount() > 0) {
            // Go to the first element in the list!
            cursor.moveToFirst();

            // And make some variables...
            String child;
            int index = 0;

            // Now loop through all the events and grab them!
            for (int i = 0; i < cursor.getCount(); i++) {
                String event_id = cursor.getString(cursor.getColumnIndex("event_id"));
                String name = cursor.getString(cursor.getColumnIndex("event_name"));
                String dateText = cursor.getString(cursor.getColumnIndex("date"));
                String start_time = cursor.getString(cursor.getColumnIndex("start_time"));
                String end_time = cursor.getString(cursor.getColumnIndex("end_time"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String category = cursor.getString(cursor.getColumnIndex("category"));
                String location = cursor.getString(cursor.getColumnIndex("location"));
                byte [] image = cursor.getBlob(cursor.getColumnIndex("picture"));

                // Now combine them!
                String [] date = {dateText, (timeFormat(start_time) + "-" + timeFormat(end_time))};

                // This variable is only to make sure the maps have a unique key.
                String event = event_id + "~" + name;
                Log.d("DATABASE: ", event);

                child = "Location: " + location + "\n" + category + "\n\n" + description + "\n";

                // Now insert them into the lists and map!
                header.add(index, event);
                childs.put(event, child);
                images.add(index++, image);
                dateList.put(event, date);

                // Now move forward by one..
                cursor.moveToNext();
            }
        } else {
            // Display that no events are happening on this date!
            header.add(0, "No events today");
        }

        // Remember to close it!
        cursor.close();
    }

    /**
     *  Change the format for the time!
     * @param textTime
     * @return
     */
    public String timeFormat(String textTime) {
        // Make a new time!
        String[] splitTime = textTime.split(":");
        String time = splitTime[0] + splitTime[1];

        try {
            Date date = new SimpleDateFormat("hhmm").parse(time);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            time = sdf.format(date);
        } catch (ParseException pex) {
            pex.printStackTrace();
        }

        return time;
    }

    public String formatDate(String dateText) {
        //split the date!

        //change the format the DATETEXT
        String date = null;

        return date;
    }

    /**
     * DELETEEVENTS
     *  Delete from the table!
     */
    public void deleteEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("event", null, null);
        db.delete("my_events", null, null);
        db.delete("calendar", null, null);
        db.delete("common_lookup", null, null);
        db.close();
    }
}
