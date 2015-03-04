package com.ombre901.samuelhibbard.byui_events_app;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends TabActivity {
    /*
     * MEMBER VARIABLES
     */
    private Menu menu; //save the menu
    private SQLDatabase dataBaseHome;
    private Database database = null;
    private List<ActivityObserver> activities = new ArrayList<ActivityObserver>();

    /*
     * MEMBER METHODS
     */

    /**
     * ONCREATE
     *  This is when the application is first started.
     *      It will also create the tabs.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Database.newInstance(this);

        DatabaseAsyncTask asyncTask = new DatabaseAsyncTask(this);

        asyncTask.execute(dataBaseHome);
    }

    /**
     * ADDOBSERVER
     * @param observers
     */
    public void addObserver(List<ActivityObserver> observers) {
        activities = observers;
    }

    public ActivityObserver getObserver(int loc) {
        return activities.get(loc);
    }

    /**
     * ONCREATEOPTIONSMENU
     *  Create the options menu!
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu = menu;
        final Activity mainActivity = this;
        final MenuItem searchBar = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView)searchBar.getActionView();

        //now for the look on the searchview!
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);

        if (searchPlate!=null) {
            searchPlate.setBackgroundColor(getResources().getColor(R.color.darkgray));

            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView searchText = (TextView) searchPlate.findViewById(searchTextId);

            if (searchText!=null) {
                searchText.setTextColor(getResources().getColor(R.color.white));
                searchText.setHintTextColor(getResources().getColor(R.color.white));
                searchText.setTextSize(15);
            }
        }

        //also create a listener to see when the person is editing it!
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Search", "onQueryTextChange");
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Search", "onQueryTextSubmit");

                new SearchActivity(mainActivity, query);
                searchView.setIconified(true);
                searchView.onActionViewCollapsed();
                return true;
            }
        });

        return true;
    }

    /**
     * ONOPTIONSITEMSELECTED
     *  What item was selected in the menu!
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_search:
                return true;
            case R.id.filter_highlighted_events:
            case R.id.filter_activities_life_skills:
            case R.id.filter_academic_soical:
            case R.id.filter_activities_outdoor:
            case R.id.filter_activities_service:
            case R.id.filter_activities_awarness:
            case R.id.filter_dept_music:
            case R.id.filter_employee_events:
            case R.id.filter_employee_training:
            case R.id.filter_forum:
            case R.id.filter_free_play:
            case R.id.filter_meeting:
            case R.id.filter_presentations:
            case R.id.filter_recital_faculty:
            case R.id.filter_recital_student:
            case R.id.filter_student_assc:
            case R.id.filter_student_highlighted:
            case R.id.filter_student_spirit:
            case R.id.filter_activities_social:
            case R.id.filter_activities_sports:
            case R.id.filter_activities_talent:
            case R.id.filter_activities_wellness:
            case R.id.filter_alumni_or_reunion:
            case R.id.filter_broadcast_conference:
            case R.id.filter_concert:
            case R.id.filter_conference_workshop:
            case R.id.filter_devo_speeches:
            case R.id.filter_get_connected:
            case R.id.filter_graduation:
            case R.id.filter_theatre: {
                if (item.isChecked()) {
                    item.setChecked(false);
                    database.deleteFromFilter(item.toString());
                } else {
                    item.setChecked(true);
                    database.addToFilter(item.toString());
                }

                //now tell everyone!
                for (ActivityObserver ob : activities) {
                    ob.update();
                }

                Log.d("ASYNC: ", activities.get(0).toString());

                return true;
            }
            case R.id.action_clear_filters: {
                //uncheck everyone!
                unCheckBoxes();
                //and delete from the filter list
                database.deleteAllFromFilter();
                //now tell everyone!
                for (ActivityObserver ob : activities) {
                    ob.update();
                }

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * UNCHECKBOXES
     *  Uncheck all the boxes.
     */
    public void unCheckBoxes() {
        //now go thru all the items in the menu!
        MenuItem item = menu.getItem(1);
        SubMenu sub = item.getSubMenu();
        int num = sub.size();
        //now loop thru each item!
        for (int i = 0; i < num - 1; i++) {
            //now grab it!
            MenuItem grabItem = sub.getItem(i);
            //now check it...
            if (grabItem.isChecked()) {
                //and change it back to false!
                grabItem.setChecked(false);
            }
        }
    }
}

