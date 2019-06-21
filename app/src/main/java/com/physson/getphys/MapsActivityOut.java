package com.physson.getphys;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MapsActivityOut extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    private Singleton1 singleton1;
    private LatLng currentLatLon, clientLatLon;
    private ImageButton carView,tView;
    private ImageView imageView;
    private TextView dText;
    private Chronometer chronometer;
    LocationManager locationManager;
    ProgressBar progressBar;
    Button poiBut;
    Double lat,lon;
    String id,latlon;
    List<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        list = new ArrayList<>();
        poiBut= (Button) findViewById(R.id.poiBtn);
        poiBut.setVisibility(View.GONE);
        poiBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivityOut.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.custompoi, null);
                alertDialog.setView(convertView);
                alertDialog.setTitle("Places ofinterest");
                ListView lv = (ListView) convertView.findViewById(R.id.lv);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapsActivityOut.this,android.R.layout.simple_list_item_1,list);
                lv.setAdapter(adapter);
                alertDialog.show();
            }
        });
        singleton1 = Singleton1.getInstance(MapsActivityOut.this);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        Intent i = getIntent();
        latlon = i.getStringExtra("lat")+","+i.getStringExtra("lon");
        lat = Double.parseDouble(i.getStringExtra("lat"));
        lon = Double.parseDouble(i.getStringExtra("lon"));
        id = i.getStringExtra("id");
        //System.out.println("Tunde ..ll..."+lat+" "+lon);
        clientLatLon = new LatLng(lat, lon);
        progressBar =(ProgressBar)findViewById(R.id.pb);
        tView = (ImageButton)findViewById(R.id.mapTrek);
        tView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tView.setEnabled(false);
                tView.setBackground(getBaseContext().getResources().getDrawable(R.drawable.roundcornergreen));
                carView.setBackground(getBaseContext().getResources().getDrawable(R.drawable.transparent));
                carView.setEnabled(true);
                addMap("w");
            }
        });

        carView = (ImageButton)findViewById(R.id.mapTv);
        carView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(),"Mode change to driving",Toast.LENGTH_SHORT).show();
                carView.setEnabled(false);
                carView.setBackground(getBaseContext().getResources().getDrawable(R.drawable.roundcornergreen));
                tView.setBackground(getBaseContext().getResources().getDrawable(R.drawable.transparent));
                tView.setEnabled(true);
                addMap("d");
            }
        });
        carView.setEnabled(false);
        dText =(TextView)findViewById(R.id.distance);
        chronometer =(Chronometer)findViewById(R.id.simpleChronometer);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {

            }
        });
        carView.setBackground(getBaseContext().getResources().getDrawable(R.drawable.roundcornergreen));
        tView.setVisibility(View.GONE);
        carView.setVisibility(View.GONE);
        imageView=(ImageView)findViewById(R.id.imgMap);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog settingsDialog = new Dialog(MapsActivityOut.this);
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(LayoutInflater.from(MapsActivityOut.this).inflate(R.layout.imagedialog, null));
                ImageView imgView=(ImageView)settingsDialog.findViewById(R.id.imgViewDialog);
                //Button cancel = (Button)settingsDialog.findViewById((R.id.okDialogBtn));

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        settingsDialog.dismiss();
                    }
                });
                Glide.with(MapsActivityOut.this)
                        .load(Constants.HTTPURL+Constants.GET_IMAGE+id+".jpg")
                        .placeholder(R.drawable.ic_home_black_24dp)
                        .centerCrop()
                        .into(imgView);
                settingsDialog.show();

            }

        });

        Glide.with(this)
                .load(Constants.HTTPURL+Constants.GET_IMAGE+id+".jpg")
                .centerCrop()
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                //.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(imageView);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new TrackAsyncpoi( latlon,MapsActivityOut.this, new AsynTaskCallback() {
            @Override
            public void processFinish(String str) {

                String count=new Jsonparse(getBaseContext()).getPoi(str);
                list = new Jsonparse(getBaseContext()).getPoiArray(str,lat,lon);

                if (Integer.parseInt(count)>0) {
                    poiBut.setVisibility(View.VISIBLE);
                    poiBut.setText("Destination have "+count+" places of interest");
                }else {
                    poiBut.setVisibility(View.GONE);
                }
                Toast.makeText(getBaseContext(),count+" places of interest",Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(4);


    }
    private void addMap(String mode){
        mMap.clear();

//        Double lat = Double.parseDouble(latlon[0]);
//        Double lon = Double.parseDouble(latlon[1]);
        // Add a marker in Sydney and move the camera

        //currentLatLon = new LatLng(lat, lon);

        mMap.addMarker(new MarkerOptions()
                .position(currentLatLon)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Me"));
        mMap.addMarker(new MarkerOptions()
                .position(clientLatLon)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("Target"));
        updateMap(currentLatLon, clientLatLon,mode);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLon, 15));
    }


    private void updateMap(LatLng start, LatLng end,String mode) {
        String url = (mode.equals("d"))?getUrl(start, end): getTrekUrl(start, end);;
        FetchUrl fetchUrl = new FetchUrl(mMap);
        fetchUrl.execute(url);
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String dir = "https://maps.googleapis.com/maps/api/directions/" + "json" + "?&mode=driving&" + (str_origin + "&" + ("destination=" + dest.latitude + "," + dest.longitude) + "&" + "sensor=false");

        return dir;
    }
    private String getTrekUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        return "https://maps.googleapis.com/maps/api/directions/" + "json" + "?mode=walking&" + (str_origin + "&" + ("destination=" + dest.latitude + "," + dest.longitude) + "&" + "sensor=false");
    }

    @Override
    public void onResume() {
        super.onResume();
        tView.setVisibility(View.GONE);
        carView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        chronometer.start();

        gpsTart();

    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    public void onPause() {
        super.onPause();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        gpsTop();

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatLon = new LatLng(location.getLatitude(),location.getLongitude());
        //textView.setText("Location obtained");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        addMap("d");
        gpsTop();
        progressBar.setVisibility(View.GONE);
        tView.setVisibility(View.VISIBLE);
        carView.setVisibility(View.VISIBLE);
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                lat, lon, results);
        float distanceb = results[0];
        int distance = (int)distanceb;
        String res = (distance)<1000?String.valueOf(Math.round(distanceb))+" m":String.valueOf(Math.round(distanceb/1000))+" km";
        dText.setText(res);

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
        showSettingsAlert();
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());

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
    private  void gpsTop(){
        PackageManager pmm = this.getPackageManager();
        if (pmm.checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, this.getPackageName()) == PackageManager.PERMISSION_GRANTED){
            locationManager.removeUpdates(this);

            // Toast.makeText(getBaseContext(), "GPS Access Granted", Toast.LENGTH_SHORT).show();
        }
        else {

            Toast.makeText(this, "Please permit app to use location service", Toast.LENGTH_LONG).show();

        }
    }
}
