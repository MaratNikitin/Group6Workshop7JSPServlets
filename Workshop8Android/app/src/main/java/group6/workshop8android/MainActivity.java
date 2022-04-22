/*
Author: Marat Nikitin;
Co-Author: , contribution: ;
PROJ-207 Threaded Project, Stage 3, Group 6, Workshop #8 (Android),
OOSD program, SAIT, March-May 2022;
This Android app allows doing a full range of CRUD operations with the 'packages' table
    of the 'travelexperts' MySQL database.
This class is responsible for all actions in the "activity_main.xml" window (Main page of the app).
*/

package group6.workshop8android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // defining objects at a class level:
    ListView lvPackages;
    Button btnAdd;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // defining the appropriate layout activity

        // creating a RequestQueue instance accessible at the class level:
        requestQueue = Volley.newRequestQueue(this);

        // assigning all the defined objects of the form:
        lvPackages = findViewById(R.id.lvPackages);
        btnAdd = findViewById(R.id.btnAdd);

        // GetPackages() class with its run() method is called to get a list of packages into the ListView:
        Executors.newSingleThreadExecutor().execute(new GetPackages());

        // when a package is selected from a listview, a new window opens with package details:
        lvPackages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // determining the selected package
                ListViewPackage pkg = (ListViewPackage) lvPackages.getAdapter().getItem(position);

                // opening the activity_detail.xml window passing a selected package instance there:
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("listviewpackages", pkg);
                startActivity(intent);
            }
        });

        // when the 'Add' button is clicked, a new window with an appropriate form is opened:
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // activity_detail.xml window is opened:
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(intent); // nothing is passed to the new window
            }
        });

    } // end of onCreate()

    // making sure that menu bar is properly displayed:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // menu options (the only one that is there):
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.miAbout:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                ;
        }
        return super.onOptionsItemSelected(item);
    }

    // this class with its run() method helps getting packages' data into the ListView:
    class GetPackages implements Runnable {
        @Override
        public void run() {
            //retrieve JSON data from REST service into StringBuffer
            StringBuffer buffer = new StringBuffer();

            // comment-out the line(s) with not your IP(s) and insert a line with your IP instead:
            String url = "http://192.168.1.68:8080/api/packages"; // Marat's IP is in the URL

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    VolleyLog.wtf(response, "utf-8");

                    //convert JSON data from response string into an ArrayAdapter of Agents
                    ArrayAdapter<ListViewPackage> adapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.listview_styles); // using custom styling for the ListView
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i=0; i<jsonArray.length(); i++) // iterating through all JSON objects:
                        {
                            JSONObject pkg = jsonArray.getJSONObject(i); // retrieving a single package
                            // instead of a larger Package instances, lighter ListViewPackage instances are retrieved:
                            ListViewPackage myPackage = new ListViewPackage(pkg.getInt("id"),
                                    pkg.getString("pkgName"));
                            adapter.add(myPackage); // adding a package to the ListView
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //updating ListView with the adapter of Packages (lighter ListViewPackage objects):
                    final ArrayAdapter<ListViewPackage> finalAdapter = adapter;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lvPackages.setAdapter(finalAdapter);
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.wtf(error.getMessage(), "utf-8");
                }
            });

            requestQueue.add(stringRequest); // adding the request to the queue
        } // end of run() method

    } // end of GetPackages class

} // end of MainActivity class