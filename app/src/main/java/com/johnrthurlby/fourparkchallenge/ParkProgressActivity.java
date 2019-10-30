package com.johnrthurlby.fourparkchallenge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class ParkProgressActivity extends AppCompatActivity {

    TextView textViewEPPic1;
    TextView textViewEPPic2;
    TextView textViewEPPic3;
    TextView textViewMKPic1;
    TextView textViewMKPic2;
    TextView textViewMKPic3;
    TextView textViewAKPic1;
    TextView textViewAKPic2;
    TextView textViewAKPic3;
    TextView textViewHSPic1;
    TextView textViewHSPic2;
    TextView textViewHSPic3;
    TextView textViewEPComp;
    TextView textViewMKComp;
    TextView textViewAKComp;
    TextView textViewHSComp;
    TextView textViewAllDone;

    Intent intent;

    String userNamePrefix;
    String countPrefix = " ";
    String parkName;

    String selectDate;

    String pattern = "yyyy-MM-dd";

    String parks[] = {"ep", "mk", "ak", "hs"};

    int countEPIcon;
    int countEPReceipt;
    int countEPRide;
    int countMKIcon;
    int countMKReceipt;
    int countMKRide;
    int countAKIcon;
    int countAKReceipt;
    int countAKRide;
    int countHSIcon;
    int countHSReceipt;
    int countHSRide;

    int parksCompleted = 0;

    /*String epPic;
    String mkPic;
    String akPic;
    String hsPic = "NNN";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_progress);

        textViewEPPic1 = findViewById(R.id.textViewEPPic1);
        textViewEPPic2 = findViewById(R.id.textViewEPPic2);
        textViewEPPic3 = findViewById(R.id.textViewEPPic3);
        textViewMKPic1 = findViewById(R.id.textViewMKPic1);
        textViewMKPic2 = findViewById(R.id.textViewMKPic2);
        textViewMKPic3 = findViewById(R.id.textViewMKPic3);
        textViewAKPic1 = findViewById(R.id.textViewAKPic1);
        textViewAKPic2 = findViewById(R.id.textViewAKPic2);
        textViewAKPic3 = findViewById(R.id.textViewAKPic3);
        textViewHSPic1 = findViewById(R.id.textViewHSPic1);
        textViewHSPic2 = findViewById(R.id.textViewHSPic2);
        textViewHSPic3 = findViewById(R.id.textViewHSPic3);
        textViewEPComp = findViewById(R.id.textViewEPComp);
        textViewMKComp = findViewById(R.id.textViewMKComp);
        textViewAKComp = findViewById(R.id.textViewAKComp);
        textViewHSComp = findViewById(R.id.textViewHSComp);
        textViewAllDone = findViewById(R.id.textViewAllDone);


        textViewEPComp.setVisibility(View.INVISIBLE);
        textViewMKComp.setVisibility(View.INVISIBLE);
        textViewAKComp.setVisibility(View.INVISIBLE);
        textViewHSComp.setVisibility(View.INVISIBLE);
        textViewAllDone.setVisibility(View.INVISIBLE);

        intent = getIntent();

        userNamePrefix = intent.getStringExtra("USERNAME_PREFIX");
        parkName       = intent.getStringExtra("PARK_NAME");
        /*epPic          = intent.getStringExtra("EPPARK_NAME");
        mkPic          = intent.getStringExtra("MKPARK_NAME");
        akPic          = intent.getStringExtra("AKPARK_NAME");
        hsPic          = intent.getStringExtra("HSPARK_NAME");*/


        findViewById(R.id.textViewAllDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on login
                //we will open the login screen
                finish();
                Intent intent = new Intent(getApplicationContext(), RequestCertActivity.class );

                intent.putExtra("USERNAME_PREFIX", userNamePrefix );

                startActivity(intent);
            }
        });

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        selectDate = simpleDateFormat.format(new Date()) + "%";

        progressCountEP();

        progressCountMK();

        progressCountAK();

        progressCountHS();

        if (parksCompleted == 4) {

            textViewAllDone.setVisibility(View.VISIBLE);

        }

    }

    private void progressCountEP() {

        class ProgressCountEP extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object

                countPrefix = userNamePrefix + parks[0] + "%";

                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("countprefix", countPrefix);
                params.put("selectdate", selectDate);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_PROGRESS, params);

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {

                countEPIcon = 0;
                countEPReceipt = 0;
                countEPRide = 0;

                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                try {

                    JSONArray jsonArray = new JSONArray(s);

                    String[] imagenames = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject obj = jsonArray.getJSONObject(i);

                        imagenames[i] = obj.getString("imagenames");

                        if (imagenames[i].contains("icon")) {

                            textViewEPPic1.setBackgroundResource(R.color.colorGreen);
                            countEPIcon++;

                        }

                        if (imagenames[i].contains("receipt")) {

                            textViewEPPic2.setBackgroundResource(R.color.colorGreen);
                            countEPReceipt++;

                        }

                        if (imagenames[i].contains("ride")) {

                            textViewEPPic3.setBackgroundResource(R.color.colorGreen);
                            countEPRide++;

                        }

                    }

                    if (countEPIcon > 0 && countEPReceipt > 0 && countEPRide > 0) {

                        textViewEPComp.setVisibility(View.VISIBLE);

                        parksCompleted = parksCompleted + 1;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                /*try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject imagecountJson = obj.getJSONObject("imagecount");



                        rowcountEP = imagecountJson.getInt("rowcount");

                        switch (rowcountEP){
                            case 0:
                                break;
                            case 1:
                                textViewEPPic1.setBackgroundResource(R.color.colorGreen);
                                break;
                            case 2:
                                textViewEPPic1.setBackgroundResource(R.color.colorGreen);
                                textViewEPPic2.setBackgroundResource(R.color.colorGreen);
                                break;
                            default:
                                textViewEPPic1.setBackgroundResource(R.color.colorGreen);
                                textViewEPPic2.setBackgroundResource(R.color.colorGreen);
                                textViewEPPic3.setBackgroundResource(R.color.colorGreen);
                                textViewEPComp.setVisibility(View.VISIBLE);
                                parksCompleted++;
                                break;                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }

        }

        ProgressCountEP pgcnt = new ProgressCountEP();
        pgcnt.execute();
    }

    private void progressCountMK() {

       /* if (mkPic.charAt(0) == 'Y'){

            textViewMKPic1.setBackgroundResource(R.color.colorGreen);

        }

        if (mkPic.charAt(1) == 'Y'){

            textViewMKPic2.setBackgroundResource(R.color.colorGreen);

        }

        if (mkPic.charAt(2) == 'Y'){

            textViewMKPic3.setBackgroundResource(R.color.colorGreen);

        }

        if (mkPic.matches("YYY")) {

            textViewMKComp.setVisibility(View.VISIBLE);

            parksCompleted = parksCompleted + 1;

        }*/

        class ProgressCountMK extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object

                countPrefix = userNamePrefix + parks[1] + '%';

                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("countprefix", countPrefix);
                params.put("selectdate", selectDate);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_PROGRESS, params);

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {

                countMKIcon = 0;
                countMKReceipt = 0;
                countMKRide = 0;

                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                try {

                    JSONArray jsonArray = new JSONArray(s);

                    String[] imagenames = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject obj = jsonArray.getJSONObject(i);

                        imagenames[i] = obj.getString("imagenames");

                        if (imagenames[i].contains("icon")) {

                            textViewMKPic1.setBackgroundResource(R.color.colorGreen);
                            countMKIcon++;
                        }

                        if (imagenames[i].contains("receipt")) {

                            textViewMKPic2.setBackgroundResource(R.color.colorGreen);
                            countMKReceipt++;

                        }

                        if (imagenames[i].contains("ride")) {

                            textViewMKPic3.setBackgroundResource(R.color.colorGreen);
                            countMKRide++;

                        }

                    }

                    if (countMKIcon > 0 && countMKReceipt > 0 && countMKRide > 0 ) {

                        textViewMKComp.setVisibility(View.VISIBLE);

                        parksCompleted = parksCompleted + 1;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                /*try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject imagecountJson = obj.getJSONObject("imagecount");

                        //creating a new imagecount object
                        ImageCount imagecount = new ImageCount(
                                imagecountJson.getString("userprefix"),
                                imagecountJson.getString("selectdate"),
                                imagecountJson.getInt("rowcount")

                        );

                        rowcountMK = imagecountJson.getInt("rowcount");

                        switch (rowcountMK){
                            case 0:
                                break;
                            case 1:
                                textViewMKPic1.setBackgroundResource(R.color.colorGreen);
                                break;
                            case 2:
                                textViewMKPic1.setBackgroundResource(R.color.colorGreen);
                                textViewMKPic2.setBackgroundResource(R.color.colorGreen);
                                break;
                            default:
                                textViewMKPic1.setBackgroundResource(R.color.colorGreen);
                                textViewMKPic2.setBackgroundResource(R.color.colorGreen);
                                textViewMKPic3.setBackgroundResource(R.color.colorGreen);
                                textViewMKComp.setVisibility(View.VISIBLE);
                                parksCompleted = parksCompleted + 1;
                                break;

                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }

        }

        ProgressCountMK pgcnt = new ProgressCountMK();
        pgcnt.execute();
    }

    private void progressCountAK() {

        /*if (akPic.charAt(0) == 'Y'){

            textViewAKPic1.setBackgroundResource(R.color.colorGreen);

        }

        if (akPic.charAt(1) == 'Y'){

            textViewAKPic2.setBackgroundResource(R.color.colorGreen);

        }

        if (akPic.charAt(2) == 'Y'){

            textViewAKPic3.setBackgroundResource(R.color.colorGreen);

        }

        if (akPic.matches("YYY")) {

            textViewAKComp.setVisibility(View.VISIBLE);

            parksCompleted = parksCompleted + 1;

        }*/

        class ProgressCountAK extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object

                countPrefix = userNamePrefix + parks[2] + '%';

                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("countprefix", countPrefix);
                params.put("selectdate", selectDate);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_PROGRESS, params);

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {

                countAKIcon = 0;
                countAKReceipt = 0;
                countAKRide = 0;


                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                try {

                    JSONArray jsonArray = new JSONArray(s);

                    String[] imagenames = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject obj = jsonArray.getJSONObject(i);

                        imagenames[i] = obj.getString("imagenames");

                        if (imagenames[i].contains("icon")) {

                            textViewAKPic1.setBackgroundResource(R.color.colorGreen);
                            countAKIcon++;

                        }

                        if (imagenames[i].contains("receipt")) {

                            textViewAKPic2.setBackgroundResource(R.color.colorGreen);
                            countAKReceipt++;

                        }

                        if (imagenames[i].contains("ride")) {

                            textViewAKPic3.setBackgroundResource(R.color.colorGreen);
                            countAKRide++;

                        }

                    }

                    if (countAKIcon > 0 && countAKReceipt > 0 && countAKRide > 0) {

                        textViewAKComp.setVisibility(View.VISIBLE);

                        parksCompleted = parksCompleted + 1;

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }


                /*try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject imagecountJson = obj.getJSONObject("imagecount");

                        //creating a new imagecount object
                        ImageCount imagecount = new ImageCount(
                                imagecountJson.getString("userprefix"),
                                imagecountJson.getString("selectdate"),
                                imagecountJson.getInt("rowcount")

                        );

                        rowcountAK = imagecountJson.getInt("rowcount");

                        switch (rowcountAK){
                            case 0:
                                break;
                            case 1:
                                textViewAKPic1.setBackgroundResource(R.color.colorGreen);
                                break;
                            case 2:
                                textViewAKPic1.setBackgroundResource(R.color.colorGreen);
                                textViewAKPic2.setBackgroundResource(R.color.colorGreen);
                                break;
                            default:
                                textViewAKPic1.setBackgroundResource(R.color.colorGreen);
                                textViewAKPic2.setBackgroundResource(R.color.colorGreen);
                                textViewAKPic3.setBackgroundResource(R.color.colorGreen);
                                textViewAKComp.setVisibility(View.VISIBLE);
                                parksCompleted = parksCompleted + 1;
                                break;

                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }

        }

        ProgressCountAK pgcnt = new ProgressCountAK();
        pgcnt.execute();
    }

    private void progressCountHS() {



        class ProgressCountHS extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object

                countPrefix = userNamePrefix + parks[3] + '%';

                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("countprefix", countPrefix);
                params.put("selectdate", selectDate);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_PROGRESS, params);

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);

                countHSIcon = 0;
                countHSReceipt = 0;
                countHSRide = 0;


                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                try {

                    JSONArray jsonArray = new JSONArray(s);

                    String[] imagenames = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject obj = jsonArray.getJSONObject(i);

                        imagenames[i] = obj.getString("imagenames");

                        if (imagenames[i].contains("icon")) {

                            textViewHSPic1.setBackgroundResource(R.color.colorGreen);
                            countHSIcon++;

                        }

                        if (imagenames[i].contains("receipt")) {

                            textViewHSPic2.setBackgroundResource(R.color.colorGreen);
                            countHSReceipt++;

                        }

                        if (imagenames[i].contains("ride")) {

                            textViewHSPic3.setBackgroundResource(R.color.colorGreen);
                            countHSRide++;

                        }

                    }

                    if (countHSIcon > 0 && countHSReceipt > 0 && countHSRide > 0) {

                        textViewHSComp.setVisibility(View.VISIBLE);

                        parksCompleted = parksCompleted + 1;

                    }

                    if (parksCompleted == 4) {

                        textViewAllDone.setVisibility(View.VISIBLE);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject imagecountJson = obj.getJSONObject("imagecount");

                        //creating a new imagecount object
                        ImageCount imagecount = new ImageCount(
                                imagecountJson.getString("userprefix"),
                                imagecountJson.getString("selectdate"),
                                imagecountJson.getInt("rowcount")

                        );

                        rowcountHS = imagecountJson.getInt("rowcount");

                        switch (rowcountHS){
                            case 0:
                                break;
                            case 1:
                                textViewHSPic1.setBackgroundResource(R.color.colorGreen);
                                break;
                            case 2:
                                textViewHSPic1.setBackgroundResource(R.color.colorGreen);
                                textViewHSPic2.setBackgroundResource(R.color.colorGreen);
                                break;
                            default:
                                textViewHSPic1.setBackgroundResource(R.color.colorGreen);
                                textViewHSPic2.setBackgroundResource(R.color.colorGreen);
                                textViewHSPic3.setBackgroundResource(R.color.colorGreen);
                                textViewHSComp.setVisibility(View.VISIBLE);

                                parksCompleted = parksCompleted + 1;

                                if (parksCompleted == 4) {

                                    textViewAllDone.setVisibility(View.VISIBLE);

                                }
                                break;

                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }

        }

        ProgressCountHS pgcnt = new ProgressCountHS();
        pgcnt.execute();
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

            new AlertDialog.Builder(ParkProgressActivity.this)

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

            Intent intent = new Intent(getApplicationContext(), ContactActivity.class );

            intent.putExtra("USERNAME_PREFIX", userNamePrefix );

            startActivity(intent);

        }

        if (item.getItemId() == R.id.help) {

            Intent intent = new Intent(getApplicationContext(), HelpActivity.class );

            intent.putExtra("USERNAME_PREFIX", userNamePrefix );

            startActivity(intent);

        }

        if (item.getItemId() == R.id.logout) {

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

        finish();

        Intent intent = new Intent(getApplicationContext(), CameraActivity.class );

        intent.putExtra("USERNAME_PREFIX", userNamePrefix );

        intent.putExtra("PARK_NAME", parkName);

        startActivity(intent);

        super.onBackPressed();
    }

}
