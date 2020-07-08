package com.example.uber;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
//import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
//import com.parse.SignUpCallback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    Switch riderOrDriverSwitch;

    public void getStarted(View view) {

        String riderOrDriver = "rider";

        if (riderOrDriverSwitch.isChecked()) {
            riderOrDriver = "driver";
            Log.i("AppInfo", "Driver gewählt");
        }

        ParseUser.getCurrentUser().put("riderOrDriver", riderOrDriver);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("AppInfo", "Benutzer eingeloggt.");
                    redirectUser();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Actionbar verschwinden lassen
        getSupportActionBar().hide();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
        // Fixme
        ParseUser.getCurrentUser().put("riderOrDriver", "rider");

        if (ParseUser.getCurrentUser() == null) {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e != null) {
                        Log.d("MyApp", "Anonymous login failed.");
                    } else {
                        Log.d("MyApp", "Anonymous user logged in.");
                    }
                }
            });

        } else {

            if (ParseUser.getCurrentUser().get("riderOrDriver") != null) {

                Log.i("MyApp", "Weitergeleitet...");
                redirectUser();

            }

        }

        riderOrDriverSwitch = (Switch) findViewById(R.id.riderOrDriverSwitch);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);

        /*ParseObject testObject = new ParseObject("TestObjekt");
        testObject.put("test", "hallo");
        testObject.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("Parse", "Save Succeeded");
                } else {
                    Log.i("Parse", "Save Failed");
                }
            }
        });*/

        /*ParseUser user = new ParseUser();
        user.setUsername("Heike");
        user.setPassword("meinPass");

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    Log.i("signUp", "Erfolgreich");
                } else {
                    Log.i("signUp", "Fehlgeschlagen");
                    e.printStackTrace();
                }

            }
        });*/

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void redirectUser() {

        if(ParseUser.getCurrentUser().get("riderOrDriver").equals("rider")) {
            Intent i = new Intent(getApplicationContext(), YourLocation.class);
            startActivity(i);
        } else {

            Intent i = new Intent(getApplicationContext(), ViewRequest.class);
            startActivity(i);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
