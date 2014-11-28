package timex.android.com.gpslog;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;


public class GPSActivity extends Activity {
    Criteria hdCrit;
    String mlocProvider;
    Location currentLocation;

    int accuracyDelta;
    double crntLat, crntLon, crntAlt, crntFix, crntBear, crntSpeed, crntTime;

    TextView GLat, GLon, GAlt,  GFix, GBear, GSpeed, GTime;
    EditText wayPt;

    //***************************************************************** file
    String wayPoint;
    String fullyCompiledString = "";
    String currentString = "";
    File reportDirectoryName;
    File myFile;
    String confidential = "******** This information may be confidential and/or privileged."+
            " Use of this information by anyone other than the intended recipient is prohibited."+
            " If you received this in error, please inform the sender and remove any record of this message. **********";
    BufferedWriter myOutWriter;

    String GPSLogFileName = "GPSLOG.txt";
    //Button upGPS, writeGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        GLat = (TextView) findViewById(R.id.GPSLat);
        GLon = (TextView) findViewById(R.id.GPSLon);
        GAlt = (TextView) findViewById(R.id.GPSAlt);
        wayPt = (EditText) findViewById(R.id.waypointName);
        GFix = (TextView) findViewById(R.id.fixVal);
        GBear = (TextView) findViewById(R.id.bearingVal);
        GSpeed = (TextView) findViewById(R.id.speedVal);
        GTime = (TextView) findViewById(R.id.timeVal);
        //upGPS = (Button) findViewById(R.id.updateButton);
        //writeGPS = (Button) findViewById(R.id.writeFileButton);
        try
        {
            GPSLogFileName = "GPSLog.txt";
            reportDirectoryName = new File(Environment.getExternalStorageDirectory(), "/GPSLog_Files/");
            if(!reportDirectoryName.exists()){
                reportDirectoryName.mkdirs();
            }
            myFile = new File(reportDirectoryName, GPSLogFileName);
            myFile.createNewFile();
            myOutWriter = new BufferedWriter(new FileWriter(myFile));
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error opening GPS File",
                    Toast.LENGTH_SHORT).show();
        }
        aquireGPS();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.g, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void aquireGPS ()
    {
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);            //*** bring back in when running on phone
/*
        LocationManagerHelper lmh = new LocationManagerHelper();


        mlocProvider = ;


*/

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {                                                          //*** bring back in when running on phone
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        hdCrit = new Criteria();
        hdCrit.setAccuracy(Criteria.NO_REQUIREMENT);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3, 1, locationListener);

        mlocProvider = locationManager.getBestProvider(hdCrit, true);
        currentLocation = locationManager.getLastKnownLocation(mlocProvider);
        /*
        Timer updateTimer = new Timer("updateTimer",true);
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                //update screen
            }
        };
        updateTimer.scheduleAtFixedRate(updateTask, 1000, 1000);
*/
    }

    void printLocation(Location loc)
    {
        crntLat = loc.getLatitude();   //
        crntLon = loc.getLongitude();   //
        crntAlt = loc.getAltitude();   //
        crntFix = loc.getAccuracy();
        crntSpeed = loc.getSpeed();
        crntTime = loc.getTime();

        Log.d("System.out", "\nlocation accuracy: " + accuracyDelta + "\nlatitude: " +
                crntLat + "\nlongitude: " + crntLon + "\naltitude: " + crntAlt +
                "\nFix: " + crntFix + "\nSpeed: " + crntSpeed + "\nTime: " + crntTime);
        GLat.setText("" + crntLat);   //crntLat
        GLon.setText("" + crntLon);   //crntLon
        GAlt.setText("" + crntAlt); //crntAlt
        GFix.setText("" + crntFix);
        GSpeed.setText("" + crntSpeed);
        GTime.setText("" + crntTime);
    }

    public void addCoord(View view)
    {
        Log.d("Devon Test", "logging...");
        wayPoint = wayPt.getText().toString();
        fullyCompiledString = wayPoint + ": " + crntLat + ", " + crntLon + ", " + crntAlt + ", " + crntFix +
                ", " + crntSpeed + ", " + crntTime; //
        if(myOutWriter==null)
        {
            try
            {
                myOutWriter = new BufferedWriter(new FileWriter(myFile));
                Toast.makeText(getBaseContext(), "Started new buffered writer",
                        Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                Toast.makeText(getBaseContext(), "Error initializing GPS File write buffer",
                        Toast.LENGTH_SHORT).show();
            }
        }
        try
        {
            myOutWriter.write(fullyCompiledString);
            myOutWriter.newLine();
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error writing to GPS File",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void update(View view)
    {
        Log.d("Devon Test", "updating...");
        printLocation(currentLocation);
    }

    public void saveFile(View view)
    {
        Log.d("Devon Test", "write to File: " + fullyCompiledString);
        try {

            Toast.makeText(getBaseContext(),
                    "Saving GPS File...",
                    Toast.LENGTH_SHORT).show();

            myOutWriter.close();

            Toast.makeText(getBaseContext(),
                    "GPS File successfully saved",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error saving GPS File",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
