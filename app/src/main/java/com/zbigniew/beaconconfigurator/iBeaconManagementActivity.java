package com.zbigniew.beaconconfigurator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.connection.ibeacon.IBeaconConnection;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.connection.SyncableIBeaconConnection;
import com.kontakt.sdk.android.http.exception.ClientException;

public class iBeaconManagementActivity extends AppCompatActivity implements IBeaconConnection.ConnectionListener {

    public static final String EXTRA_BEACON_DEVICE = "extra_beacon_device";

    public static final String EXTRA_FAILURE_MESSAGE = "extra_failure_message";

    public static final int REQUEST_CODE_OBTAIN_CONFIG = 1;

    public static final int REQUEST_CODE_OBTAIN_PROFILE = 2;

    private IBeaconDevice beacon;

    private SyncableIBeaconConnection syncableIBeaconConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_beacon_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        beacon = getIntent().getParcelableExtra(EXTRA_BEACON_DEVICE);

        syncableIBeaconConnection = new SyncableIBeaconConnection(this, beacon, this);
        Button updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int viewId = view.getId();
                updateMajorValue(R.id.majorEditText);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!syncableIBeaconConnection.isConnectedToDevice()) {
            syncableIBeaconConnection.connectToDevice();
        }
    }

    @Override
    protected void onDestroy() {
        clearConnection();
        super.onDestroy();
    }

    @Override
    public void onConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Utils.showToast(SyncableBeaconManagementActivity.this, "Connected");
                Toast toast = Toast.makeText(
                        iBeaconManagementActivity.this,
                        "Connected",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void onAuthenticationSuccess(final IBeaconDevice.Characteristics characteristics) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Utils.showToast(SyncableBeaconManagementActivity.this, "Authentication Success");
                Toast toast= Toast.makeText(
                        iBeaconManagementActivity.this,
                        "Authentication Success",
                        Toast.LENGTH_LONG);
                toast.show();
                fillUI(characteristics);
            }
        });
    }

    @Override
    public void onAuthenticationFailure(final int failureCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Intent intent = getIntent();
                switch (failureCode) {
                    case IBeaconConnection.FAILURE_UNKNOWN_BEACON:
                        intent.putExtra(EXTRA_FAILURE_MESSAGE, String.format("Unknown beacon: %s", beacon.getName()));
                        break;
                    case IBeaconConnection.FAILURE_WRONG_PASSWORD:
                        intent.putExtra(EXTRA_FAILURE_MESSAGE, String.format("Wrong password. Beacon will be disabled for about 20 mins."));
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Unknown beacon connection failure code: %d", failureCode));
                }
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    @Override
    public void onCharacteristicsUpdated(final IBeaconDevice.Characteristics characteristics) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fillUI(characteristics);
            }
        });
    }

    @Override
    public void onErrorOccured(final int errorCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast= Toast.makeText(
                        iBeaconManagementActivity.this,
                        "Toast",
                        Toast.LENGTH_SHORT);
                switch (errorCode) {

                    case IBeaconConnection.ERROR_OVERWRITE_REQUEST:
                        //Utils.showToast(SyncableBeaconManagementActivity.this, "Overwrite request error");
                        toast.setText("Overwrite request error");
                        toast.show();
                        break;

                    case IBeaconConnection.ERROR_SERVICES_DISCOVERY:
                        //Utils.showToast(SyncableBeaconManagementActivity.this, "Services discovery error");
                        toast.setText("Services discovery error");
                        toast.show();
                        break;

                    case IBeaconConnection.ERROR_AUTHENTICATION:
                        //Utils.showToast(SyncableBeaconManagementActivity.this, "Authentication error");
                        toast.setText("Authentication error");
                        toast.show();
                        break;

                    default:
                        throw new IllegalStateException("Unexpected connection error occured: " + errorCode);
                }
            }
        });
    }

    @Override
    public void onDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Utils.showToast(SyncableBeaconManagementActivity.this, "Disconnected");
                Toast toast = Toast.makeText(
                        iBeaconManagementActivity.this,
                        "Disconnected",
                        Toast.LENGTH_SHORT);
                toast.show();
            }

        });
    }


    private void fillUI(IBeaconDevice.Characteristics characteristics) {
        //Update UI parameters
    }

    private void clearConnection() {
        syncableIBeaconConnection.close();
        syncableIBeaconConnection = null;
    }

    private void updateMajorValue(final int newMajor){
        syncableIBeaconConnection.overwriteMajor(newMajor, new SyncableIBeaconConnection.SyncWriteListener() {
            @Override
            public void onWriteFailed() {
                Toast toast = Toast.makeText(
                        iBeaconManagementActivity.this,
                        "Write fail",
                        Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onSyncFailed(ClientException e) {
                e.printStackTrace();
            }

            @Override
            public void onSuccess() {
                Toast toast = Toast.makeText(
                        iBeaconManagementActivity.this,
                        "Major updated with value: "+newMajor,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
