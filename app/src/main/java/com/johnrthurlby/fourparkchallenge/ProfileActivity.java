package com.johnrthurlby.fourparkchallenge;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    TextView textViewUsername;
    TextView textViewEmail;
    TextView textViewCert;
    TextView textViewPassword;

    String userNamePrefix;

    String deleteDate;

    String pattern = "yyyy-MM-dd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewCert = (TextView) findViewById(R.id.textViewCert);
        textViewPassword = (TextView) findViewById(R.id.textViewPassword);

        textViewPassword.setVisibility(View.INVISIBLE);

        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();

        //setting the values to the textviews
        textViewUsername.setText(user.getUsername());
        textViewEmail.setText(user.getEmail());
        textViewCert.setText(user.getCert());
        textViewPassword.setText(user.getPassword());

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userNamePrefix = textViewUsername.getText().toString();

                Intent intent = new Intent(getApplicationContext(), locationActivity.class);

                intent.putExtra("USERNAME_PREFIX", userNamePrefix );

                startActivity(intent);

            }
        });

        findViewById(R.id.buttonDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userDelete();

            }
        });

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

            new AlertDialog.Builder(ProfileActivity.this)

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("4 Park Challenge Rules.")
                    .setMessage("Rules: Visit all 4 parks in the same day. Take a picture of an icon in each park. Do one ride in each park. Buy something that has the name of the park on the receipt. Use the app to take the pics. You will get a certification of completion sent to the email you signed up with. Hit Yes to continue")

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and go back to the main activity

                            SharedPrefManager.getInstance(getApplicationContext()).logout();

                            //starting the location activity
                            finish();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            startActivity(intent);

                        }
                    })
                    .show();

            return true;
        }

        if (item.getItemId() == R.id.logout) {


            // if this button is clicked, just close
            // the dialog box and go back to the main activity

            SharedPrefManager.getInstance(getApplicationContext()).logout();

            //starting the location activity
            finish();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(intent);

        }

        if (item.getItemId() == R.id.contact) {


            // if this button is clicked, just close
            // the dialog box and go back to the main activity


            Intent intent = new Intent(getApplicationContext(), ContactActivity.class);

            intent.putExtra("USERNAME_PREFIX", userNamePrefix );

            startActivity(intent);

        }

        if (item.getItemId() == R.id.help) {


            // if this button is clicked, just close
            // the dialog box and go back to the main activity


            Intent intent = new Intent(getApplicationContext(), HelpActivity.class);

            intent.putExtra("USERNAME_PREFIX", userNamePrefix );

            startActivity(intent);

        }

        return true;

    }

    private void userDelete() {
        //first getting the values
        final String username = textViewUsername.getText().toString();
        final String password = textViewPassword.getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        deleteDate = simpleDateFormat.format(new Date());

        class UserDelete extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                //SharedPrefManager.getInstance(getApplicationContext()).logout();

                //starting the location activity
                //finish();
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        SharedPrefManager.getInstance(getApplicationContext()).logout();

                        //starting the location activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("deletedate", deleteDate);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_DELETE, params);

            }
        }

        UserDelete ud = new UserDelete();
        ud.execute();
    }
}


