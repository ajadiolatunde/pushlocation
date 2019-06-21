package com.physson.getphys;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.UUID;

import static java.security.AccessController.getContext;

/**
 * Created by olatunde on 7/18/2017.
 */

public class Addlocation extends AppCompatActivity implements LocationListener,ActivityCompat.OnRequestPermissionsResultCallback{
    LocationManager locationManager;
    Boolean chIn=false;
    Boolean locationchange =false;
    String  latLong;
    private File mPhotoFile;
    private Singleton1 singleton1;
    private ImageButton picButton;
    private Button savebtn;
    private ProgressBar progressBar;
    private ImageView imgView;
    private static final int ARG_PHOTO_REQUEST =2;
    Long tsLong = System.currentTimeMillis();
    String ts;
    String uiid =UUID.randomUUID().toString();
    private EditText desc;
    PhyLocation phyLocation;
    final int REQUEST_LOCATION=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addlocation1);
        phyLocation =new PhyLocation();
        phyLocation.setId(uiid);
        phyLocation.setPhoto("No");
        ts=tsLong.toString();
        phyLocation.setTstamp(ts);
        progressBar=(ProgressBar)findViewById(R.id.getLocationBar);

        imgView = (ImageView)findViewById(R.id.imgView);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        mPhotoFile = Singleton1.getInstance(this).getPhotoFile(uiid);
        PackageManager packageManager = this.getPackageManager();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePicture =mPhotoFile != null && captureImage.resolveActivity(packageManager)!=null;
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),uiid,Toast.LENGTH_SHORT).show();
            }
        });
        picButton = (ImageButton)findViewById(R.id.snapPic);
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage,ARG_PHOTO_REQUEST);
            }
        });
        picButton.setEnabled(canTakePicture);

        if(canTakePicture){
            Uri uri=Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }

        desc = (EditText) findViewById(R.id.descEV);
        savebtn = (Button)findViewById(R.id.saveBtn);
        savebtn.setEnabled(false);
        savebtn.setText("Getting location...");
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean len =(desc.getText().toString().length()<5)? false:true;
                if(len){
                    phyLocation.setDescription(desc.getText().toString());
                    new Jsonparse(getBaseContext()).addLocation(phyLocation,Constants.LOCATION);
                    Intent intent =new Intent(Addlocation.this,Home.class);
                    startActivity(intent);
                }else {
                        desc.setError("Required");
                }
            }
        });
    }
    //https://developers.google.com/android/guides/permissions
    public static void checkPermission(Context context,Activity activity){
       int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(context,"we need this ",Toast.LENGTH_SHORT).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

      //https://stackoverflow.com/questions/33327984/call-requires-permissions-that-may-be-rejected-by-user
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == ARG_PHOTO_REQUEST ){
            PictureUtils.resize_compress(this,uiid);
            phyLocation.setPhoto("Yes");
            upDatePhotoView();
        }
    }

    private  void upDatePhotoView(){

        if (mPhotoFile == null || !mPhotoFile.exists()){
            int id = this.getResources().getIdentifier("drawable/dr", null,this.getPackageName());
            imgView.setImageResource(id);

        }else {
            //Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            //mimageView.setImageBitmap(bitmap);


            Glide.with(this)
                    .load(mPhotoFile+".jpg")
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    //.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(imgView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        gpsTart();

    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    public void onPause() {
        super.onPause();
        gpsTop();

    }

    @Override
    public void onLocationChanged(Location location) {
        phyLocation.setLat(String.valueOf(location.getLatitude()));
        phyLocation.setLon(String.valueOf(location.getLongitude()));
        savebtn.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        savebtn.setText("Save");
//        if (!locationchange) {
//            Toast.makeText(getApplicationContext(),"PhyLocation service \n available",Toast.LENGTH_SHORT).show();
//            locationchange = true;
//
//        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(), "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(),"Please enable location service",Toast.LENGTH_LONG).show();
        savebtn.setEnabled(false);
        savebtn.setText("Please enable location sevice");
        //new AlertDialog.Builder(Addlocation.this).setMessage("Some text").show();

        showSettingsAlert();
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Addlocation.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS  settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }

        });

        // Showing Alert Message
        alertDialog.show();
    }
    private  void gpsTart(){
        PackageManager pmm = this.getPackageManager();
        if (pmm.checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, this.getPackageName()) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1,this);


            // Toast.makeText(getBaseContext(), "GPS Access Granted", Toast.LENGTH_SHORT).show();
        }
        else {

            Toast.makeText(this, "Please permit app to use location service", Toast.LENGTH_LONG).show();

        }
    }
    private  void gpsTop() {
        PackageManager pmm = this.getPackageManager();
        if (pmm.checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, this.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);

            // Toast.makeText(getBaseContext(), "GPS Access Granted", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "Please permit app to use location service", Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public  void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
            if (requestCode == REQUEST_LOCATION) {
                if(grantResults.length == 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // We can now safely use the API we requested access to

                } else {
                    // Permission was denied or request was cancelled
                    Toast.makeText(this, "Location service required", Toast.LENGTH_LONG).show();
                    savebtn.setEnabled(false);
                    savebtn.setText("Please enable location sevice");
                }
            }
    }


}
