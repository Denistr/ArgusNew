package com.example.denis.argusnew;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.internal.CreateFileIntentSenderRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private Button btnStart, btnFix=null;

    public boolean btnStartEnabled, btnStartClick=false;
    private boolean btnFixEnabled=true;

    ArrayList<LocationData> dataLoc = null;
    DataTreatment myTask=null;


    TextView tView=null;
    private MyLocationListener  myListener=null;
    private PolygonOptions rectOptions=null;
    Polygon polygon=null;
    private int It=1;
    private Marker[] marker=new Marker[1000];


    private int numberofMarker=0;

    static boolean first=true;

    private PolygonOptions getRectOptions() {
        if (rectOptions == null) {
            rectOptions = new PolygonOptions()
                    .strokeColor(Color.RED)
                    .fillColor(Color.TRANSPARENT);
        }
        return rectOptions;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Enable location service in settings", Toast.LENGTH_SHORT);
            toast.show();
        }

        myListener = new MyLocationListener();
        myListener.SetUpLocationListener(this);

        btnStart = (Button)findViewById(R.id.btnStart);
        btnFix = (Button)findViewById(R.id.btnFix);

        tView = (TextView)findViewById(R.id.textView);

        btnStart.setEnabled(false);
        btnFix.setEnabled(true);



        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    public void compare(int ad){
    tView.setText(" ");
        if (ad==1)
            tView.setText("You are in the area");
        else  tView.setText("You are outside the area");
    }

    private  void drawPolygon() {
       polygon = mMap.addPolygon(getRectOptions());
        rectOptions=null;
    }



    private void setUpMap() {

        try {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(MyLocationListener.imHere.getLatitude(), MyLocationListener.imHere.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
            mMap.setMyLocationEnabled(true);
        } catch (Exception e)
        {
            int z=0;
        }
    }


    public void onClickStart(View view) throws IOException {
        if (!btnStartClick) {
            btnStartClick = true;
            if (btnStartEnabled) {
                btnFix.setEnabled(false);
            }
            first=true;
            myTask = new DataTreatment(this);
            try {
                 myTask.execute();
            } catch (Exception e) {
                 int z = 0;
            }

            drawPolygon();//рисуем область
            for (int j=0; j<numberofMarker;j++)
                marker[j].remove();

            numberofMarker=0;

            btnStart.setText("Stop");
        } else {

            btnStartClick=false;
            btnStart.setText("Start");
            btnStart.setEnabled(false);
            btnFix.setEnabled(true);

            dataLoc.clear(); //очищаем лист
            It=0;
            polygon.remove();

            tView.setText("");

                myTask.disconnect();

            myTask=null;
        }
    }


    public void onClickFix(View view) throws IOException{
        if (btnFixEnabled)
            btnStart.setEnabled(false);

            try {
                if (dataLoc == null) {
                    dataLoc = new ArrayList<LocationData>();
                }
                dataLoc.add(new LocationData(MyLocationListener.imHere.getLatitude(), MyLocationListener.imHere.getLongitude()));
                //tView.setText(" ");
                //tView.setText(Double.toString(MyLocationListener.imHere.getLatitude())+" "+Double.toString(MyLocationListener.imHere.getLongitude()));

                getRectOptions().add(new LatLng(MyLocationListener.imHere.getLatitude(), MyLocationListener.imHere.getLongitude()));


                marker[numberofMarker]=mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(MyLocationListener.imHere.getLatitude(), MyLocationListener.imHere.getLongitude()))
                        .title(Integer.toString(It)));

                numberofMarker++;
            } catch (Exception e) {
                int z = 0;
            }
            if (dataLoc.size() >= 3) {
                btnStartEnabled = true;
                btnStart.setEnabled(true);
            }
            It++;
    }
}
