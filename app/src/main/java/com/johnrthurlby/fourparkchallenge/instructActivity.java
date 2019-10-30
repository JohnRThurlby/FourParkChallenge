package com.johnrthurlby.fourparkchallenge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import android.provider.Settings.Secure;

public class instructActivity extends AppCompatActivity {

    Button   buttonRegister;
    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextPassword2;
    EditText editTextEmail;
    EditText editCertName;
    TextView textViewLogin;

    String userdevice;
    String deleteDate = " ";
    String userNamePrefix;

    String strRegEx = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*](?=\\S+$).{8,15}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruct);

        userdevice = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);


        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
            return;
        }

        buttonRegister    = (Button) findViewById(R.id.buttonRegister);
        editTextUsername  = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword  = (EditText) findViewById(R.id.editTextPassword);
        editTextPassword2 = (EditText) findViewById(R.id.editTextPassword2);
        editTextEmail     = (EditText) findViewById(R.id.editTextEmail);
        editCertName      = (EditText) findViewById(R.id.editCertName);
        textViewLogin     = (TextView)findViewById(R.id.textViewLogin);

        editTextUsername.setVisibility(View.INVISIBLE);
        editTextPassword.setVisibility(View.INVISIBLE);
        editTextPassword2.setVisibility(View.INVISIBLE);
        editTextEmail.setVisibility(View.INVISIBLE);
        editCertName.setVisibility(View.INVISIBLE);
        buttonRegister.setVisibility(View.INVISIBLE);
        textViewLogin.setVisibility(View.INVISIBLE);


        new AlertDialog.Builder(instructActivity.this)

            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("4 Park Challenge Rules.")
            .setMessage("Rules: Visit all 4 parks in the same day. Take a picture of an icon in each park. Do one ride in each park. Buy something that has the name of the park on the receipt. Use the app to take the pics. You will get a certification of completion sent to the email you signed up with. Hit Yes to continue")

            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                buttonRegister.setVisibility(View.VISIBLE);
                editTextUsername.setVisibility(View.VISIBLE);
                editTextPassword.setVisibility(View.VISIBLE);
                editTextPassword2.setVisibility(View.VISIBLE);
                editTextEmail.setVisibility(View.VISIBLE);
                editCertName.setVisibility(View.VISIBLE);
                textViewLogin.setVisibility(View.VISIBLE);

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

        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on button register
                //here we will register the user to server
                registerUser();
            }
        });

        findViewById(R.id.textViewLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on login
                //we will open the login screen
                finish();
                startActivity(new Intent(instructActivity.this, LoginActivity.class));
            }
        });

    }


    private void registerUser() {
        final String username = editTextUsername.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String password2 = editTextPassword2.getText().toString().trim();
        final String cert = editCertName.getText().toString().trim();

        //first we will do the validations

        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter username");
            editTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Enter a password");
            editTextPassword.requestFocus();
            return;
        }

        if (!password.matches(strRegEx)) {
            editTextPassword.setError("Invalid password");
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password2)) {
            editTextPassword2.setError("Enter a confirm password");
            editTextPassword2.requestFocus();
            return;
        }

        if (!password.matches(password2)){
            editTextPassword.setError("Passwords do not match");
            editTextPassword.requestFocus();
            return;

        }

        if (TextUtils.isEmpty(cert)) {
            editCertName.setError("Enter a name");
            editCertName.requestFocus();
            return;
        }

        //if it passes all the validations

        class RegisterUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("cert", cert);
                params.put("userdevice", userdevice);
                params.put("deletedate", deleteDate);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

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

                        userNamePrefix = userJson.getString("username");

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        finish();

                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class );

                        intent.putExtra("USERNAME_PREFIX", userNamePrefix );

                        startActivity(intent);

                    }
                    else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();
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

            new AlertDialog.Builder(instructActivity.this)

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("4 Park Challenge Rules.")
                    .setMessage("Rules: Visit all 4 parks in the same day. Take a picture of an icon in each park. Do one ride in each park. Buy something that has the name of the park on the receipt. Use the app to take the pics. You will get a certification of completion sent to the email you signed up with. Hit Yes to continue")

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            buttonRegister.setVisibility(View.VISIBLE);
                            editTextUsername.setVisibility(View.VISIBLE);
                            editTextPassword.setVisibility(View.VISIBLE);
                            editTextEmail.setVisibility(View.VISIBLE);
                            editCertName.setVisibility(View.VISIBLE);
                            textViewLogin.setVisibility(View.VISIBLE);

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

}
