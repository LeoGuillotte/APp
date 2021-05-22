package Enstavigation.fr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapsActivity1 extends FragmentActivity implements OnMapReadyCallback, RoutingListener, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SearchView searchView;
    protected LatLng start=null;
    protected LatLng end=null;
    int nb_markers=1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    LocationManager locationManager;
    private List<Polyline> polylines=new ArrayList<>();

    AbstractRouting.TravelMode[] travel_mode={AbstractRouting.TravelMode.DRIVING,AbstractRouting.TravelMode.TRANSIT,AbstractRouting.TravelMode.BIKING};
    List<Integer> colors = Arrays.asList(R.color.black, R.color.teal_200, R.color.purple_200);
    int current_travel_mode=1;

    private final int ACCESS_LOCATION_REQUEST_CODE=1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Button back_button = (Button) findViewById(R.id.button3);
        back_button.setOnClickListener(v -> {
            Intent otherActivity = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(otherActivity);
            finish();
        });

        searchView=findViewById(R.id.sv_location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location=searchView.getQuery().toString();
                List<Address> addressList=null;
                if(location!=null||!location.equals("")){
                    Geocoder geocoder=new Geocoder(MapsActivity1.this);
                    try{
                        addressList=geocoder.getFromLocationName(location,1);

                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    assert addressList != null;
                    Address address=addressList.get(0);
                    LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
                    if (nb_markers<2 && latLng!=start){
                        nb_markers=nb_markers+1;
                    }
                    else{
                        mMap.clear();
                        MarkerOptions markerOptions = new MarkerOptions().position(start).title("I am here");
                        mMap.addMarker(markerOptions);
                    }
                    end=latLng;
                    LatLngBounds latLngBounds = new LatLngBounds.Builder().include(start).include(end).build();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 300));

                    Findroutes(start,end,travel_mode[current_travel_mode]);


                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getPackageManager();
        mMap=googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableuserlocation();
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_LOCATION_REQUEST_CODE);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    start = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(start).title("I am here");
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(start));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 12));
                    googleMap.addMarker(markerOptions);
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng pos) {
                if (nb_markers<2 && pos!=start){
                    nb_markers=nb_markers+1;
                }
                else{
                    mMap.clear();
                    MarkerOptions markerOptions = new MarkerOptions().position(start).title("I am here");
                    googleMap.addMarker(markerOptions);
                }
                end=pos;
                LatLngBounds latLngBounds = new LatLngBounds.Builder().include(start).include(end).build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 300));

                Findroutes(start,end,travel_mode[current_travel_mode]);
            }
        });

        googleMap.setOnPolylineClickListener(this);

    }
    private void enableuserlocation(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==ACCESS_LOCATION_REQUEST_CODE){
            getPackageManager();
            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                enableuserlocation();
            }
            //We can show the dailog permission not granted

        }
    }

    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End,AbstractRouting.TravelMode TRAVEL_MODE)
    {
        if(Start==null || End==null) {
            Toast.makeText(MapsActivity1.this,"Unable to get location",Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(TRAVEL_MODE)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyBPy9ccrDafESAucXKiMq7nLh9XT-b2lU0")  //also define your api key here.
                    .build();
            routing.execute();

        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
//    Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(MapsActivity1.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        PolylineOptions polyOptions = new PolylineOptions();

        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;

        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(colors.get(current_travel_mode)));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylines.add(polyline);
                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(end);
                markerOptions.snippet("Duration :" + "");
                mMap.addMarker(markerOptions);

                polyline.setClickable(true);

            }

        }
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(start,end,travel_mode[current_travel_mode]);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start,end,travel_mode[current_travel_mode]);
    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {

    }
}