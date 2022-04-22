/*
Author: Marat Nikitin;
Co-Author: , contribution: ;
PROJ-207 Threaded Project, Stage 3, Group 6, Workshop #8 (Android),
OOSD program, SAIT, March-May 2022;
This Android app allows doing a full range of CRUD operations with the 'packages' table
    of the 'travelexperts' MySQL database.
This class is responsible for all actions in the "activity_detail.xml" window.
*/

package group6.workshop8android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.android.volley.toolbox.StringRequest;
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

public class DetailActivity extends AppCompatActivity {
    // defining objects at a class level:
    Button btnSave, btnDelete, btnBack;
    EditText etPackageID, etPkgName, etStartDate, etEndDate, etDescription, etBasePrice, etCommission;
    RequestQueue requestQueue;
    ListViewPackage listViewPackage;

    // this object definition is needed for user input validation:
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // creating a RequestQueue instance accessible at the class level:
        requestQueue = Volley.newRequestQueue(this);

        // assigning all the numerous defined objects of the form:
        btnSave= findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack= findViewById(R.id.btnBack);
        etPackageID = findViewById(R.id.etPackageID);
        etPkgName = findViewById(R.id.etPkgName);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etDescription = findViewById(R.id.etDescription);
        etBasePrice = findViewById(R.id.etBasePrice);
        etCommission = findViewById(R.id.etCommission);

        // initializing validation style:
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        // Setting RegEx for validating 'YYYY-MM-DD' dates' format:
        String dateFormatRegEx = "\\d{4}-\\d{2}-\\d{2}";

        // adding validation for EditText fields:
        awesomeValidation.addValidation(this, R.id.etPkgName,
                RegexTemplate.NOT_EMPTY, R.string.pkgName2); // enforcing required Package Name
        awesomeValidation.addValidation(this, R.id.etStartDate,
                RegexTemplate.NOT_EMPTY, R.string.pkgStartDate2); // enforcing required Start Date
        awesomeValidation.addValidation(this, R.id.etEndDate,
                RegexTemplate.NOT_EMPTY, R.string.pkgEndDate2); // enforcing required End Date
        awesomeValidation.addValidation(this, R.id.etDescription,
                RegexTemplate.NOT_EMPTY, R.string.pkgDescription2); // enforcing required Description
        awesomeValidation.addValidation(this, R.id.etBasePrice,
                RegexTemplate.NOT_EMPTY, R.string.pkgBasePrice2); // enforcing required Base Price
        awesomeValidation.addValidation(this, R.id.etCommission,
                RegexTemplate.NOT_EMPTY, R.string.pkgCommission2); // enforcing required Commission
        awesomeValidation.addValidation(this, R.id.etStartDate,
                dateFormatRegEx, R.string.pkgStartDate2); // enforcing 'YYYY-MM-DD' Start Date format
        awesomeValidation.addValidation(this, R.id.etEndDate,
                dateFormatRegEx, R.string.pkgStartDate2); // enforcing 'YYYY-MM-DD' End Date format

        Intent intent = getIntent();

        // retrieving data from ListView of the main window using packageId of the selected package:
        listViewPackage = (ListViewPackage) intent.getSerializableExtra("listviewpackages");
        Executors.newSingleThreadExecutor().execute(new GetPackage(listViewPackage.getId()));

        // when 'Save' button is clicked (selected package is updated):
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) // code below will be executed only if all validation passes
                {
                    // creating a 'Package' class instance from data entered in the text fields:
                    Package pkg = new Package(Integer.parseInt(etPackageID.getText().toString()),
                            etPkgName.getText().toString(),
                            etStartDate.getText().toString(), // dates work great as String type
                            etEndDate.getText().toString(), // dates work great as String type
                            etDescription.getText().toString(),
                            Double.parseDouble(etBasePrice.getText().toString()), // converting to Double
                            Double.parseDouble(etCommission.getText().toString()) // converting to Double
                    );
                    // methods of the PostPackage class (defined below) are executed:
                    Executors.newSingleThreadExecutor().execute(new PostPackage(pkg));
                    Toast.makeText(getApplicationContext(), "Update operation was successfull!",
                            Toast.LENGTH_LONG).show(); // feedback to a user

                    // returning to the main page by reloading it (i.e. refreshing data from the DB):
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        // when the 'Delete' button is clicked (selected package is deleted):
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Asking for confirmation in a dialog window before executing the Delete operation:
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setCancelable(true); // a user can still cancel the Delete operation
                builder.setTitle("CONFIRM DELETE"); // meaningful instructions to a user
                builder.setMessage("Please confirm the delete operation"); // meaningful instructions to a user

                // when Delete operation was confirmed:
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // executing run method of the DeletePackage class (defined below):
                        Executors.newSingleThreadExecutor().execute(new DeletePackage(listViewPackage.getId()));
                        // returning to the main page by reloading it (i.e. refreshing data from the DB):
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

                // when Delete operation was not confirmed:
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // some friendly feedback to a user would not hurt:
                        Toast.makeText(getApplicationContext(), "Delete operation was cancelled.",
                                Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog dialog = builder.create(); // building an alert
                dialog.show(); // displaying the dialog window asking for Delete confirmation
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // returning to the main page by reloading it (i.e. refreshing data from the DB):
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent); // opening the main window ('activity_main.xml')
            }
        });

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
                ; // do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    // this class helps getting a selected package:
    class GetPackage implements Runnable {
        private int id; // it represents a packageId

        public GetPackage(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            //retrieve JSON data from REST service into StringBuffer
            StringBuffer buffer = new StringBuffer();

            // comment-out the line(s) with not your IP(s) and insert a line with your IP instead:
            String url = "http://192.168.1.68:8080/api/getpackage/" + id; // Marat's IP
            // creating a request:
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    VolleyLog.wtf(response, "utf-8");

                    //convert JSON data from response string into a Package
                    JSONObject pkg = null;
                    try {
                        pkg = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //update ListView with the adapter of Packages
                    final JSONObject finalPkg = pkg;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // populating text fields by data of the selected package
                                etPackageID.setText(finalPkg.getInt("id") + "");
                                etPkgName.setText(finalPkg.getString("pkgName"));
                                etStartDate.setText(finalPkg.getString("pkgStartDate").substring(0,10));
                                etEndDate.setText(finalPkg.getString("pkgEndDate").substring(0,10));
                                etDescription.setText(finalPkg.getString("pkgDesc"));
                                etBasePrice.setText(finalPkg.getString("pkgBasePrice"));
                                etCommission.setText(finalPkg.getString("pkgAgencyCommission"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.wtf(error.getMessage(), "utf-8");
                }
            });
            requestQueue.add(stringRequest); // request is added to the queue
        }
    }

    // class with a run() method helping to update a DB in the DB through REST API
    class PostPackage implements Runnable {
        private Package pkg; // represents a Package instance to be updated

        // constructor method:
        public PostPackage(Package pkg) {
            this.pkg = pkg;
        }

        @Override
        public void run() {
            //sending JSON data to REST service

            // comment-out the line(s) with not your IP(s) and insert a line with your IP instead:
            String url = "http://192.168.1.68:8080/api/updatepackage";
            JSONObject obj = new JSONObject();
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
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            VolleyLog.wtf(response.toString(), "utf-8");
                            //display result message
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // feedback to a user in a toast:
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
                            VolleyLog.wtf(error.getMessage(), "utf-8");
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } // end of run() method
    } // end of PostPackage class

    // class with a run() method helping to delete a selected package:
    class DeletePackage implements Runnable {
        private int id;

        // constructor method:
        public DeletePackage(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            //retrieving JSON data from REST service into StringBuffer

            // comment-out the line(s) with not your IP(s) and insert a line with your IP instead:
            String url = "http://192.168.1.68:8080/api/deletepackage/" + id;

            // creating a request:
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
                    VolleyLog.wtf(response, "utf-8");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
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

    } // end of DeletePackage class

} // end of DetailActivity class