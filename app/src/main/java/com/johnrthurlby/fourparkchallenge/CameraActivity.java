package com.johnrthurlby.fourparkchallenge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.HttpURLConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.os.AsyncTask;
import android.widget.EditText;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedWriter;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.io.BufferedReader;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import android.util.Base64;

public class CameraActivity extends AppCompatActivity {

    Button UploadImageToServer;
    Button CaptureIconImageFromCamera;
    Button CaptureRideImageFromCamera;
    Button CaptureReceiptImageFromCamera;

    ImageView ImageViewHolder;

    //EditText imageName;
    String imageName;

    /*static String epPic = "NNN";
    static String mkPic = "NNN";
    static String akPic = "NNN";
    static String hsPic = "NNN";*/

    final int type = 0;

    TextView parkText;

    RelativeLayout backGround;

    Intent intent ;

    public  static final int RequestPermissionCode  = 1 ;

    Bitmap imageBitmap;

    boolean check = true;

    String GetImageNameFromEditText;

    String ImageNameFieldOnServer = "image_name" ;

    String ImagePathFieldOnServer = "image_path" ;

    String ImageUploadPathOnSever ="http://ec2-3-227-58-34.compute-1.amazonaws.com/capture_img_upload_to_server.php" ;

    String parkName;

    String imagePrefix;

    String userNamePrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        CaptureIconImageFromCamera = (Button)    findViewById(R.id.buttonIcon);
        CaptureRideImageFromCamera = (Button)    findViewById(R.id.buttonRide);
        CaptureReceiptImageFromCamera = (Button)    findViewById(R.id.buttonReceipt);

        ImageViewHolder        = (ImageView) findViewById(R.id.imageView);
        UploadImageToServer    = (Button)    findViewById(R.id.button2);
        parkText               = (TextView)  findViewById(R.id.parkTextView);

        CaptureIconImageFromCamera.setVisibility(View.VISIBLE);
        CaptureReceiptImageFromCamera.setVisibility(View.VISIBLE);
        CaptureRideImageFromCamera.setVisibility(View.VISIBLE);
        UploadImageToServer.setVisibility(View.INVISIBLE);

        backGround             = (RelativeLayout) findViewById(R.id.RelativeLayout1);

        intent = getIntent();

        parkName = intent.getStringExtra("PARK_NAME");
        userNamePrefix = intent.getStringExtra("USERNAME_PREFIX");

        imagePrefix = userNamePrefix;

        parkText.setText("You are in " + parkName);

        switch (parkName){
            case "Epcot":
                imagePrefix += "ep";
                break;
            case "Magic Kingdom":
                imagePrefix += "mk";
                break;
            case "Animal Kingdom":
                imagePrefix += "ak";
                break;
            case "Hollywood Studios":
                imagePrefix += "hs";
                break;
            default:
                break;
        }

        EnableRuntimePermissionToAccessCamera();

        CaptureIconImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CaptureIconImageFromCamera.setVisibility(View.INVISIBLE);
                CaptureReceiptImageFromCamera.setVisibility(View.INVISIBLE);
                CaptureRideImageFromCamera.setVisibility(View.INVISIBLE);

                UploadImageToServer.setVisibility(View.VISIBLE);

                imageName = "icon";

                //replacePicStr(parkName, 0);

                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, 7);

            }
        });

        CaptureReceiptImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CaptureIconImageFromCamera.setVisibility(View.INVISIBLE);
                CaptureReceiptImageFromCamera.setVisibility(View.INVISIBLE);
                CaptureRideImageFromCamera.setVisibility(View.INVISIBLE);

                UploadImageToServer.setVisibility(View.VISIBLE);

                imageName = "receipt";

                //replacePicStr(parkName, 1);

                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, 7);

            }
        });

        CaptureRideImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CaptureIconImageFromCamera.setVisibility(View.INVISIBLE);
                CaptureReceiptImageFromCamera.setVisibility(View.INVISIBLE);
                CaptureRideImageFromCamera.setVisibility(View.INVISIBLE);

                UploadImageToServer.setVisibility(View.VISIBLE);

                imageName = "ride";

                //replacePicStr(parkName, 2);

                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, 7);

            }
        });

        UploadImageToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageNameFromEditText = imagePrefix + imageName;

                ImageUploadToServerFunction();

            }
        });
    }

    // Star activity for result method to Set captured image on image view after click.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 7 && resultCode == RESULT_OK ) {

            try {

                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                ImageViewHolder.setImageBitmap(imageBitmap);

            } catch (Exception e) {

                e.printStackTrace();
            }
        } else {

            Log.i("onActivityResult: ", "Error convert image to  bitmap");

        }

    }

    // Requesting runtime permission to access camera.
    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(CameraActivity.this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(CameraActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(CameraActivity.this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }

    // Upload captured image online on server function.
    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        // Converting bitmap image to jpeg format, so by default image will upload in jpeg format.
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                //pgsBar.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(CameraActivity.this,string1,Toast.LENGTH_LONG).show();

                // Setting image as transparent after done uploading.
                ImageViewHolder.setImageResource(android.R.color.transparent);

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageNameFieldOnServer, GetImageNameFromEditText);

                HashMapParams.put(ImagePathFieldOnServer, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(ImageUploadPathOnSever, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();

        finish();

        Intent intent = new Intent(getApplicationContext(), ParkProgressActivity.class );

        intent.putExtra("PARK_NAME", parkName);

        //intent.putExtra("EPPARK_NAME", epPic);
        //intent.putExtra("MKPARK_NAME", mkPic);
        //intent.putExtra("AKPARK_NAME", akPic);
        //intent.putExtra("HSPARK_NAME", hsPic);

        intent.putExtra("USERNAME_PREFIX", userNamePrefix );

        startActivity(intent);
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(CameraActivity.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(CameraActivity.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }
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

            new AlertDialog.Builder(CameraActivity.this)

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

        Intent intent = new Intent(getApplicationContext(), locationActivity.class );

        intent.putExtra("USERNAME_PREFIX", userNamePrefix );

        startActivity(intent);

        super.onBackPressed();
    }

    /*public static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }

    public void replacePicStr(String name, int type) {

        switch (name){
            case "Epcot":
                epPic =  replaceCharAt(epPic, type, 'Y');
                break;
            case "Magic Kingdom":
                mkPic =  replaceCharAt(mkPic, type, 'Y');
                break;
            case "Animal Kingdom":
                akPic =  replaceCharAt(akPic, type, 'Y');
                break;
            case "Hollywood Studios":
                hsPic =  replaceCharAt(hsPic, type, 'Y');
                break;
            default:
                break;
        }

    }*/

}

