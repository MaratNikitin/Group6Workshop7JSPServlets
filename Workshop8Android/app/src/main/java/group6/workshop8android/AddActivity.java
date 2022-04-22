/*
Author: Marat Nikitin;
Co-Author: , contribution: ;
PROJ-207 Threaded Project, Stage 3, Group 6, Workshop #8 (Android),
OOSD program, SAIT, March-May 2022;
This Android app allows doing a full range of CRUD operations with the 'packages' table
    of the 'travelexperts' MySQL database.
This class is responsible for all actions in the "activity_add.xml" window.
*/

package group6.workshop8android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

public class AddActivity extends AppCompatActivity {
    // defining objects at a class level:
    Button btnSave2, btnBack2;
    EditText etPkgName2, etStartDate2, etEndDate2, etDescription2, etBasePrice2, etCommission2;
    RequestQueue requestQueue;

    // this object definition is needed for user input validation:
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add); // defining the appropriate layout activity

        // creating a RequestQueue instance accessible at the class level:
        requestQueue = Volley.newRequestQueue(this);

        // assigning all the numerous defined objects of the form:
        btnSave2 = findViewById(R.id.btnSave2);
        btnBack2 = findViewById(R.id.btnBack2);
        etPkgName2 = findViewById(R.id.etPkgName2);
        etStartDate2 = findViewById(R.id.etStartDate2);
        etEndDate2 = findViewById(R.id.etEndDate2);
        etDescription2 = findViewById(R.id.etDescription2);
        etBasePrice2 = findViewById(R.id.etBasePrice2);
        etCommission2 = findViewById(R.id.etCommission2);

        // initializing validation style:
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        // Setting RegEx for validating 'YYYY-MM-DD' dates' format:
        String dateFormatRegEx = "\\d{4}-\\d{2}-\\d{2}";

        // adding validation for EditText fields:
        awesomeValidation.addValidation(this, R.id.etPkgName2,
                RegexTemplate.NOT_EMPTY, R.string.pkgName2); // enforcing required Package Name
        awesomeValidation.addValidation(this, R.id.etStartDate2,
                RegexTemplate.NOT_EMPTY, R.string.pkgStartDate2); // enforcing required Start Date
        awesomeValidation.addValidation(this, R.id.etEndDate2,
                RegexTemplate.NOT_EMPTY, R.string.pkgEndDate2); // enforcing required End Date
        awesomeValidation.addValidation(this, R.id.etDescription2,
                RegexTemplate.NOT_EMPTY, R.string.pkgDescription2); // enforcing required Description
        awesomeValidation.addValidation(this, R.id.etBasePrice2,
                RegexTemplate.NOT_EMPTY, R.string.pkgBasePrice2); // enforcing required Base Price
        awesomeValidation.addValidation(this, R.id.etCommission2,
                RegexTemplate.NOT_EMPTY, R.string.pkgCommission2); // enforcing required Commission
        awesomeValidation.addValidation(this, R.id.etStartDate2,
                dateFormatRegEx, R.string.pkgStartDate2); // enforcing 'YYYY-MM-DD' Start Date format
        awesomeValidation.addValidation(this, R.id.etEndDate2,
                dateFormatRegEx, R.string.pkgStartDate2); // enforcing 'YYYY-MM-DD' End Date format

        btnSave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) // code below will be executed only if all validation passes
                {
                    // creating a 'Package' class instance from data entered in the text fields:
                    Package pkg = new Package(0, // packageId is auto-generated
                            etPkgName2.getText().toString(),
                            etStartDate2.getText().toString(),
                            etEndDate2.getText().toString(), // dates work great as String type
                            etDescription2.getText().toString(), // dates work great as String type
                            Double.parseDouble(etBasePrice2.getText().toString()), // converting to Double
                            Double.parseDouble(etCommission2.getText().toString()) // converting to Double
                    );
                    // executing the insert operation in a PutPackage class having run() method:
                    Executors.newSingleThreadExecutor().execute(new PutPackage(pkg));
                    Toast.makeText(getApplicationContext(), "Insert operation was successfull!",
                            Toast.LENGTH_LONG).show(); // feedback to a user

                    // returning to the main page by reloading it (i.e. refreshing data from the DB):
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        }); // end of btnSave2.setOnClickListener()

        // when the 'Back' button is clicked, user returns to the main page:
        btnBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // returning to the main page
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent); // opening the main window ('activity_main.xml')
            }
        }); // end of btnBack2.setOnClickListener()

    } // end of onCreate() method

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

    // adding this class here (not a separate class file) for convenience:
    class PutPackage implements Runnable {
        private Package pkg;

        // constructor method
        public PutPackage(Package pkg) {
            this.pkg = pkg;
        }

        @Override
        public void run() {
            //send JSON data to REST service

            // comment-out the line(s) with not your IP(s) and insert a line with your IP instead:
            String url = "http://192.168.1.68:8080/api/addpackage"; // Marat's IP

            // creating an empty JSON object:
            JSONObject obj = new JSONObject();
            // filling up the empty JSON object with data:
            try {
                obj.put("id", pkg.getId() + "");
                obj.put("pkgName", pkg.getPkgName() + "");
                obj.put("pkgStartDate", pkg.getPkgStartDate() + "");
                obj.put("pkgEndDate", pkg.getPkgEndDate() + "");
                obj.put("pkgDesc", pkg.getPkgDesc() + "");
                obj.put("pkgBasePrice", pkg.getPkgBasePrice() + "");
                obj.put("pkgAgencyCommission", pkg.getPkgAgencyCommission() + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            VolleyLog.wtf(response.toString(), "utf-8"); // creating log data
                            // display result message if any returns:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // displaying a toast message:
                                        Toast.makeText(getApplicationContext(), response.getString("message"),
                                                Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.wtf(error.getMessage(), "utf-8"); // creating log data
                        }
                    });
            requestQueue.add(jsonObjectRequest); // adding the request to the queue
        }
    }
}
