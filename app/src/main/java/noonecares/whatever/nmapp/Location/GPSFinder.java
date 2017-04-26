package noonecares.whatever.nmapp.Location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import noonecares.whatever.nmapp.NMAPPconstants;

/**
 * Created by justdial on 4/17/17.
 */

public class GPSFinder extends Activity {

    private Activity mActivity;
    private Context mcontext;
    private LocationManager locationManager;
    private boolean isGPSEnabled, isNetworkEnabled;
    private LocationListener locationListener;
    private Location gpsLocation = null, networkLocaion = null ,passiveLocation= null;
    private Location tempLocation = null;
    private double latitude, longitude;
    // Location Update request params
    public static int LOCATION_UPDATE_TIME = 5* 60 *1000 ; //5 minute
    public static int LOCATION_UPDATE_DISTANCE = 5; //1 meter

    public GPSFinder(Activity mActivity, Context mcontext, LocationListener locationListener){
        this.mActivity = mActivity;
        this.mcontext = mcontext;
        this.locationListener = locationListener;
    }

    /* getLocation Logic
    *   check if the providers are enabled (GPS, Network and passive)
    *   if GPS and Network are enabled -> start both GPS and network LocationUpdates.
    *                                     Get GPS last known location. If the location is not null and difference bwt. curr time and time it was taken is less than 15 sec set it to tempLocation.
    *                                     Now get Network lat known Location also and if the location is not null  and time taken is greater than the time taken by gpsLocation set it to tempLocation
    *   else if GPS only enabled       -> Same as GPS logic in above step (Do not follow network logic)
    *   else if network only enabled   -> Get network last known location. If the location is not null and difference bwt. curr time and time it was taken is less than 15 sec set it to tempLocation.
    *   else if passive only enabled   -> Same logic
    *   else                           -> tempLocation == null
    *
    * */
    public Location getLocation() {
        if(ContextCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) mcontext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Boolean provider = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(isGPSEnabled && isNetworkEnabled){



                Log.i("TINTIN", "both GPS and Network enabled");

                    //request GPS updates
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_TIME, LOCATION_UPDATE_DISTANCE, locationListener);
                    gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.i("TINTIN", gpsLocation.toString());

                    if(gpsLocation != null && System.currentTimeMillis()-gpsLocation.getTime() < 15000){
                        tempLocation = gpsLocation;
                        latitude = gpsLocation.getLatitude();
                        longitude = gpsLocation.getLongitude();
                        Log.i("TINTIN","GPS Location");
                        Log.i("TINTIN", latitude + "," + longitude);
                    }

                    // get Last known location from network provider
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_TIME, LOCATION_UPDATE_DISTANCE, locationListener);
                    networkLocaion = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.i("TINTIN", gpsLocation.toString());

                    if( networkLocaion != null && networkLocaion.getTime() > gpsLocation.getTime()){
                        tempLocation = networkLocaion;
                        latitude = gpsLocation.getLatitude();
                        longitude = gpsLocation.getLongitude();
                        Log.i("TINTIN","Network Location");
                        Log.i("TINTIN", latitude + "," + longitude);
                    }

            } else if (isGPSEnabled){

                Log.i("TINTIN", "GPS enabled");

                //try getting the last known location using GPS provider
                    //request GPS updates
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_TIME, LOCATION_UPDATE_DISTANCE, locationListener);
                    gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.i("TINTIN", gpsLocation.toString());
                    tempLocation = gpsLocation;
                    if(gpsLocation != null && System.currentTimeMillis()-gpsLocation.getTime() < 15000){
                        latitude = gpsLocation.getLatitude();
                        longitude = gpsLocation.getLongitude();
                        Log.i("TINTIN","GPS Location");
                        Log.i("TINTIN", latitude + "," + longitude);
                    }


            } else if (isNetworkEnabled){

                Log.i("TINTIN", "Network enabled");

                    // get Last known location from network provider
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_TIME, LOCATION_UPDATE_DISTANCE, locationListener);
                    networkLocaion = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.i("TINTIN", gpsLocation.toString());
                    tempLocation = networkLocaion;
                    if( networkLocaion != null && System.currentTimeMillis()-networkLocaion.getTime() <15000){
                        latitude = gpsLocation.getLatitude();
                        longitude = gpsLocation.getLongitude();
                        Log.i("TINTIN","Network Location");
                        Log.i("TINTIN", latitude + "," + longitude);
                    }


            } else if (provider) {

                Log.i("TINTIN", "using Passive provider");

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_TIME, LOCATION_UPDATE_DISTANCE, locationListener);

                    passiveLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    tempLocation = passiveLocation;
                    if (passiveLocation != null && System.currentTimeMillis() - passiveLocation.getTime() < 150000) {
                        latitude = passiveLocation.getLatitude();
                        longitude = passiveLocation.getLongitude();
                        Log.i("TINTIN","Passive Location");
                        Log.i("TINTIN", latitude + "," + longitude);
                    }


            } else {
                tempLocation = null;
            }

//            if(gpsLocation != null && networkLocaion != null && networkLocaion.getTime() > gpsLocation.getTime()){
//                tempLocation = networkLocaion;
//                latitude = tempLocation.getLatitude();
//                longitude = tempLocation.getLongitude();
//                Log.i("TINTIN", "networkTime > GPSTime" );
//                Log.i("TINTIN", latitude + "," + longitude);
//
//            }else if ( gpsLocation != null && networkLocaion != null && networkLocaion.getTime() < gpsLocation.getTime()){
//                tempLocation = gpsLocation;
//                latitude = tempLocation.getLatitude();
//                longitude = tempLocation.getLongitude();
//                Log.i("TINTIN", "networkTime < GPSTime" );
//                Log.i("TINTIN", latitude + "," + longitude);
//            }
        }
        return tempLocation;
    }

    /*Stop using GPS listener Calling this function will stop using GPS in your app*/
    public void stopUsingGPS() {
        if (locationManager != null) {
            try {
                //	AndroidMPermissionSupport.isStoragePermissionGranted(GPSFinder.this, Manifest.permission.ACCESS_FINE_LOCATION);
                locationManager.removeUpdates(locationListener);
            }catch(SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /* Function to get latitude*/
    public double getLatitude() {
        if (tempLocation != null) {
            latitude = tempLocation.getLatitude();
        }
        return latitude;
    }

    /*Function to get longitude*/
    public double getLongitude() {
        if (tempLocation != null) {
            longitude = tempLocation.getLongitude();
        }
        return longitude;
    }
}
