package com.johnrthurlby.fourparkchallenge;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import android.provider.Settings.Secure;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;

    String userdeviceId;

    String deviceId;

    String deleteDate;

    String userNamePrefix;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        deviceId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        //if user presses on login
        //calling the method login
        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        //if user presses on not registered
        findViewById(R.id.textViewRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register screen
                finish();
                startActivity(new Intent(getApplicationContext(), instructActivity.class));
            }
        });
    }

    private void userLogin() {
        //first getting the values
        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter your username");
            editTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }

        //if everything is fine

        class UserLogin extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("password"),
                                userJson.getString("cert"),
                                userJson.getString("userdevice"),
                                userJson.getString("deletedate")
                        );

                        deleteDate = userJson.getString("deletedate");

                        userNamePrefix = userJson.getString("username");

                        userdeviceId = userJson.getString("userdevice");

                        if (userdeviceId.matches(deviceId) && deleteDate.matches(" ")) {


                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();


                            //storing the user in shared preferences
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                            //starting the location activity
                            finish();

                            Intent intent = new Intent(getApplicationContext(), locationActivity.class);

                            intent.putExtra("USERNAME_PREFIX", userNamePrefix);

                            startActivity(intent);
                        } else if (!deleteDate.matches(" ")){

                            Toast.makeText(getApplicationContext(), "Account marked for deletion. Unavailable.", Toast.LENGTH_LONG).show();

                            SharedPrefManager.getInstance(getApplicationContext()).logout();

                            finish();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class );

                            startActivity(intent);

                        } else{

                            Toast.makeText(getApplicationContext(), "User account is registered to a different device", Toast.LENGTH_LONG).show();

                            SharedPrefManager.getInstance(getApplicationContext()).logout();

                            finish();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class );

                            startActivity(intent);
                        }



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

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
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

                            SharedPrefManager.getInstance(getApplicationContext()).logout();

                            finish();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class );

                            startActivity(intent);

                        }})
                    .show();

            return true;
        }

        if (item.getItemId() == R.id.contact) {

            Intent intent = new Intent(getApplicationContext(), ContactActivity.class );

            startActivity(intent);

        }

        if (item.getItemId() == R.id.help) {

            Intent intent = new Intent(getApplicationContext(), HelpActivity.class );

            startActivity(intent);

        }

        if (item.getItemId() == R.id.logout) {

            SharedPrefManager.getInstance(getApplicationContext()).logout();

            finish();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class );

            startActivity(intent);

        }

        return true;

    }

}
