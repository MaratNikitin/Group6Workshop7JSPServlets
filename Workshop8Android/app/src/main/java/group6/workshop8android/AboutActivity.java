/*
Author: Marat Nikitin;
Co-Author: , contribution: ;
PROJ-207 Threaded Project, Stage 3, Group 6, Workshop #8 (Android),
OOSD program, SAIT, March-May 2022;
This Android app allows doing a full range of CRUD operations with the 'packages' table
    of the 'travelexperts' MySQL database.
This class is responsible for all actions in the "activity_about.xml" window.
*/

package group6.workshop8android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    // defining the 'Back' button
    Button btnBackAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about); // the appropriate layout activity file is defined here

        // finding the 'Back' button
        btnBackAbout = findViewById(R.id.btnBackAbout);

        // when the 'Back' button' is clicked, a user is redirected to the Main page:
        btnBackAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
