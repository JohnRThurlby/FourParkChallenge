package com.johnrthurlby.fourparkchallenge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class HelpActivity extends AppCompatActivity {

    Intent intent ;

    String userNamePrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        intent = getIntent();

        userNamePrefix = intent.getStringExtra("USERNAME_PREFIX");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.rules_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.show_rules) {

            new AlertDialog.Builder(getApplicationContext())

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("4 Park Challenge Rules.")
                    .setMessage("Rules: Visit all 4 parks in the same day. Take a picture of an icon in each park. Do one ride in each park. Buy something that has the name of the park on the receipt. Use the app to take the pics. You will get a certification of completion sent to the email you signed up with. Hit Yes to continue")

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }

                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and go back to the main activity

                            SharedPrefManager.getInstance(getApplicationContext()).logout();

                            //starting the location activity
                            finish();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class );

                            startActivity(intent);

                        }})
                    .show();

            return true;
        }

        if (item.getItemId() == R.id.help) {

            Intent intent = new Intent(getApplicationContext(), HelpActivity.class );

            startActivity(intent);

        }

        if (item.getItemId() == R.id.logout) {


            // if this button is clicked, just close
            // the dialog box and go back to the main activity

            SharedPrefManager.getInstance(getApplicationContext()).logout();

            //starting the location activity
            finish();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class );

            startActivity(intent);

        }

        return true;

    }

    @Override
    public void onBackPressed() {

        // Put your own code here which you want to run on back button click.

        Intent intent = new Intent(getApplicationContext(), locationActivity.class );

        intent.putExtra("USERNAME_PREFIX", userNamePrefix );

        startActivity(intent);

        super.onBackPressed();
    }

}
