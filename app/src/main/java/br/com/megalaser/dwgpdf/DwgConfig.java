package br.com.megalaser.dwgpdf;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by rodrigo on 31/01/18.
 */

public class DwgConfig extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    public static final String config = "config";
    public static final String server = "server";
    public static final String port = "port";
    public static final String database = "database";
    public static final String user = "user";
    public static final String password = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dwgconfig);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final TextView textServer = (TextView) findViewById(R.id.editTextServer);
        final TextView textPort = (TextView) findViewById(R.id.editTextPort);
        final TextView textDatabase = (TextView) findViewById(R.id.editTextDatabase);
        final TextView textUser = (TextView) findViewById(R.id.editTextUser);
        final TextView textPassword = (TextView) findViewById(R.id.editTextPassword);

        sharedPreferences = getSharedPreferences(config,  Context.MODE_PRIVATE);

        if (sharedPreferences.contains(server)) {
            textServer.setText(sharedPreferences.getString(server, ""));
        }

        if (sharedPreferences.contains(port)) {
            textPort.setText(sharedPreferences.getString(port, ""));
        }

        if (sharedPreferences.contains(database)) {
            textDatabase.setText(sharedPreferences.getString(database, ""));
        }

        if (sharedPreferences.contains(user)) {
            textUser.setText(sharedPreferences.getString(user, ""));
        }

        if (sharedPreferences.contains(password)) {
            textPassword.setText(sharedPreferences.getString(password, ""));
        }


        Button save = findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverValue = textServer.getText().toString();
                String portValue = textPort.getText().toString();
                String databaseValue = textDatabase.getText().toString();
                String userValue = textUser.getText().toString();
                String passwordValue = textPassword.getText().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(server, serverValue);
                editor.putString(port, portValue);
                editor.putString(database, databaseValue);
                editor.putString(user, userValue);
                editor.putString(password, passwordValue);
                editor.commit();
            }
        });


    }
}
