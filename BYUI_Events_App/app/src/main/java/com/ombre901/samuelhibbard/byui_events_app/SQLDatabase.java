package com.ombre901.samuelhibbard.byui_events_app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.lang.Object;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SQLDATABASE
 * Created by SamuelHibbard on 11/18/14.
 */
public class SQLDatabase {
    //  Database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://184.99.79.3:3306/byui_events";
    private static final String LINK_PHP = "http://157.201.194.254/~gshawm/JAVA/databaseSelect.php";

    //  Database credentials
    private static final String USER = "byui";
    private static final String PASS = "byui";

    //grab the database!
    private Database database = Database.getInstance();

    /**
     * GETDATA
     *  This will grab from the database!
     */
    public void run() {
        //create the variables!
        Connection conn = null;
        Statement stmt = null;
        //now to grab from the database!
        Log.d("SQL: ", "Entered into the getDATA()");
        try {
            Log.d("SQL: ", "Entered into TRY");
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER).newInstance();

            //STEP 3: Open a connection
            Log.d("SQL: ", "Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            Log.d("SQL: ","Creating statement...");
            stmt = conn.createStatement();

            //grab from all tables!
            String events;
            String common_lookup;
            String calendar;

            events = "SELECT event_id, name, description, date, start_time, end_time, category, location, picture FROM event";
            common_lookup = "SELECT * FROM common_lookup";
            calendar = "SELECT * FROM calendar";

            ResultSet rs = stmt.executeQuery(events);

            Log.d("Made a change", "yes");

            //STEP 5: Extract data from result set
            while(rs.next()) {
                //Retrieve by column name
                String [] data = new String[8];

                /****************************************************************
                 * NOTE these are in a certain order. If you change this then make
                 * sure to change the order in the DATABASE in the function INSERTEVENT.
                 ****************************************************************/
                data[0] = rs.getString("event_id");
                data[1] = rs.getString("name");
                data[2] = rs.getString("date");
                data[3] = rs.getString("start_time");
                data[4] = rs.getString("end_time");
                data[5] = rs.getString("description");
                data[6] = rs.getString("category");
                data[7] = rs.getString("location");
                byte [] pic = rs.getBytes("picture");

                //now insert it into the table!
                database.insertEvent(data, pic);
            }

            ResultSet rs2 = stmt.executeQuery(calendar);

            // Grab from the calendar table!
            while (rs2.next()) {
                //retrieve by column name
                String data = new String();

                //now grab from it!
                data = rs2.getString("name");

                //now insert it into the database!
                database.insertCalendar(data);
            }

            ResultSet rs1 = stmt.executeQuery(common_lookup);

            // and from the common_lookup!
            while (rs1.next()) {
                //retrieve by column name
                String [] data = new String[2];

                //now grab it!
                data[0] = rs1.getString("event_id");
                data[1] = rs1.getString("calendar_name");

                //now insert it into the database!
                database.insertCommonLookup(data);
            }

            //STEP 6: Clean-up environment
            rs.close();
            rs1.close();
            rs2.close();
            stmt.close();
            conn.close();
        } catch(SQLException se){
            //Handle errors for JDBC
            Log.d("SQL: ", "SQLException");
            se.printStackTrace();
        } catch(Exception e){
            //Handle errors for Class.forName
            Log.d("SQL: ", "Exception");
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            Log.d("SQL: ", "Entered into FINALLY");
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        Log.d("SQL: ", "Goodbye!");
    }

    public void getServerData() {
        InputStream inputStream = null;
        String result = "";
        //ArrayList<NameValuePair>nameValuePairs = new ArrayList<NameValuePair>();
        //nameValuePairs.add(new BasicNameValuePair("year","1970"));

        try {
            Log.d("WebService", " Accessing PHP file to get data from main database");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(LINK_PHP);
            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection "+e.toString());
        }

        try {
            Log.d("WebService", " Reading data");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStream.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        try {
            JSONArray jArray = new JSONArray(result);

            for(int i = 0; i < jArray.length(); ++i) {
                String[] data = new String[8];

                JSONObject json_data = jArray.getJSONObject(i);

                Log.d("EVENT", Integer.toString(i));
                Log.d("Data", "data found: " + json_data.getString("event_id"));
                Log.d("Data", "data found: " + json_data.getString("name"));
                Log.d("Data", "data found: " + json_data.getString("date"));
                Log.d("Data", "data found: " + json_data.getString("start_time"));
                Log.d("Data", "data found: " + json_data.getString("end_time"));
                Log.d("Data", "data found: " + json_data.getString("description"));
                Log.d("Data", "data found: " + json_data.getString("category"));
                Log.d("Data", "data found: " + json_data.getString("location"));

                data[0] = json_data.getString("event_id");
                data[1] = json_data.getString("name");
                data[2] = json_data.getString("date");
                data[3] = json_data.getString("start_time");
                data[4] = json_data.getString("end_time");
                data[5] = json_data.getString("description");
                data[6] = json_data.getString("category");
                data[7] = json_data.getString("location");
                String picAsString = json_data.get("picture").toString();
                byte[] picBytes = Base64.decode(picAsString, Base64.DEFAULT);

                //now insert it into the table!
                database.insertEvent(data, picBytes);
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data "+e.toString());
        }
    }
}
