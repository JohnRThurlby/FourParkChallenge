package com.johnrthurlby.fourparkchallenge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RequestCertActivity extends AppCompatActivity {

    Button buttonRequest;
    EditText editCertName;

    Intent intent;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0 ;

    String phoneNo;
    String message;

    String userNamePrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_cert);

        buttonRequest   = (Button) findViewById(R.id.buttonRequest);
        editCertName    = (EditText) findViewById(R.id.editCertName);

        intent = getIntent();

        userNamePrefix = intent.getStringExtra("USERNAME_PREFIX");

        findViewById(R.id.buttonRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on button request
                //here we will send an SMS
                sendSMS();
            }
        });
    }

    @Override
    public void onBackPressed() {

        // Put your own code here which you want to run on back button click.

        Intent intent = new Intent(getApplicationContext(), ParkProgressActivity.class );

        intent.putExtra("USERNAME_PREFIX", userNamePrefix );

        startActivity(intent);

        super.onBackPressed();
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

            new AlertDialog.Builder(RequestCertActivity.this)

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
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class );

                            startActivity(intent);

                        }})
                    .show();

            return true;
        }

        if (item.getItemId() == R.id.contact) {

            // if this button is clicked, just close
            // the dialog box and go back to the main activity

            Intent intent = new Intent(getApplicationContext(), ContactActivity.class );

            intent.putExtra("USERNAME_PREFIX", userNamePrefix );

            startActivity(intent);

        }

        if (item.getItemId() == R.id.help) {

            // if this button is clicked, just close
            // the dialog box and go back to the main activity

            Intent intent = new Intent(getApplicationContext(), HelpActivity.class );

            intent.putExtra("USERNAME_PREFIX", userNamePrefix );

            startActivity(intent);

        }

        if (item.getItemId() == R.id.logout) {

            //storing the user in shared preferences
            SharedPrefManager.getInstance(getApplicationContext()).logout();

            //starting the profile activity
            //finish();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class );

            startActivity(intent);

        }

        return true;

    }

    private void sendSMS() {
        final String certname = editCertName.getText().toString().trim();

        //first we will do the validations

        if (TextUtils.isEmpty(certname)) {
            editCertName.setError("Please enter Certificate name");
            editCertName.requestFocus();
            return;
        }

        //if it passes the validation

        phoneNo = "xxxxxxxx";
        message = "User " + userNamePrefix + " has completed the challenge. Please put the name " + editCertName.getText().toString() + " on the certificate";

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }
}
