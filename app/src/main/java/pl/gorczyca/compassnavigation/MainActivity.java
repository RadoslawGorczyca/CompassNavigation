package pl.gorczyca.compassnavigation;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements LocationListener {
    @BindView(R.id.button_latitude)
    Button buttonLatitude;
    @BindView(R.id.button_longitude)
    Button buttonLongitude;
    @BindView(R.id.image_needle)
    ImageView imageNeedle;
    @BindView(R.id.image_compass)
    ImageView imageCompass;
    @BindView(R.id.text_provideLatLng)
    TextView textProvideLatLng;
    @BindView(R.id.label_latitude)
    TextView labelLatitude;
    @BindView(R.id.label_longitude)
    TextView labelLongitude;

    private Compass compass;
    protected LocationManager locationManager;

    private float currentAzimuth;
    private Location currentLocation;
    private float destinationLat = -200;
    private float destinationLong = -200;
    private float bearingTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Compass compass = new Compass(this);

        if (checkLocationPermission()) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            setupCompass();
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (compass != null) {
            compass.stop();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (compass != null) {
            compass.start();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        }
    }


    private void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = getCompassListener();
        compass.setListener(cl);
    }

    private void adjustCompass(float azimuth) {
        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        imageCompass.startAnimation(an);

        imageNeedle.setVisibility(View.GONE);
        textProvideLatLng.setVisibility(View.VISIBLE);
        if (currentLocation != null && destinationLat != -200 && destinationLong != -200) {

            imageNeedle.setVisibility(View.VISIBLE);
            textProvideLatLng.setVisibility(View.GONE);

            Location targetLocation = new Location("");
            targetLocation.setLatitude(destinationLat);
            targetLocation.setLongitude(destinationLong);

            float oldBearingTo = bearingTo;

            bearingTo = currentLocation.bearingTo(targetLocation);

            Animation an2 = new RotateAnimation(oldBearingTo, bearingTo,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);

            an2.setDuration(500);
            an2.setRepeatCount(0);
            an2.setFillAfter(true);

            imageNeedle.startAnimation(an2);
        }


    }

    private Compass.CompassListener getCompassListener() {
        return new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(final float azimuth) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adjustCompass(azimuth);
                    }
                });
            }
        };
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    setupCompass();
                }

            } else {
                finish();
            }
        }
    }

    @OnClick(R.id.button_longitude)
    public void onButtonLongitudeClick() {
        handleInput(EditTextDialog.INPUT_LONGITUDE);
    }

    @OnClick(R.id.button_latitude)
    public void onButtonLatitudeClick() {
        handleInput(EditTextDialog.INPUT_LATITUDE);
    }

    private void handleInput(final int whichInput) {
        EditTextDialog dialogHelper = new EditTextDialog();
        final AlertDialog dialog = dialogHelper.getInputDialog(this, whichInput);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float inputToCheck;
                boolean valueCorrect = true;

                EditText editText = dialog.findViewById(R.id.editTextDialog);
                final TextInputLayout inputLayout = dialog.findViewById(R.id.editTextDialogLayout);


                if (editText != null && inputLayout != null) {
                    inputToCheck = Float.valueOf(editText.getText().toString());
                    editText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            inputLayout.setErrorEnabled(false);
                        }
                    });

                    switch (whichInput) {
                        case EditTextDialog.INPUT_LATITUDE:
                            if (inputToCheck < -90.0 || inputToCheck > 90.0) {

                                inputLayout.setError(getText(R.string.input_error_latitude_out_of_bound));
                                inputLayout.setErrorEnabled(true);
                                valueCorrect = false;

                            }

                            if (valueCorrect) {
                                destinationLat = inputToCheck;
                                labelLatitude.setText(String.valueOf(destinationLat));
                                dialog.dismiss();
                            }
                            break;
                        case EditTextDialog.INPUT_LONGITUDE:
                            if (inputToCheck < -180.0 || inputToCheck > 180.0) {

                                inputLayout.setError(getText(R.string.input_error_longitude_out_of_bound));
                                inputLayout.setErrorEnabled(true);
                                valueCorrect = false;

                            }

                            if (valueCorrect) {
                                destinationLong = inputToCheck;
                                labelLongitude.setText(String.valueOf(destinationLong));
                                dialog.dismiss();
                            }
                            break;
                    }


                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


}
