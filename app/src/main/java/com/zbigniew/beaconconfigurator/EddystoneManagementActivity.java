package com.zbigniew.beaconconfigurator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.connection.eddystone.EddystoneBeaconConnection;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

public class EddystoneManagementActivity extends AppCompatActivity implements EddystoneBeaconConnection.ConnectionListener {

    public static final String EXTRA_FAILURE_MESSAGE = "extra_failure_message";

    public static final String EDDYSTONE_DEVICE = "eddystone_device";

    private EddystoneBeaconConnection eddystoneBeaconConnection;
    private IEddystoneDevice eddystoneDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eddystone_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtDescription = (EditText) findViewById(R.id.urlValueEditText);
                String string = txtDescription.getText().toString();
                updateURL(string);
            }
        });
        getEddystone();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearConnection();
    }

    private void getEddystone() {
        eddystoneDevice = getIntent().getParcelableExtra(EDDYSTONE_DEVICE);
        eddystoneBeaconConnection = new EddystoneBeaconConnection(this, eddystoneDevice, this);
    }


    private void connect() {
        if (eddystoneBeaconConnection != null && !eddystoneBeaconConnection.isConnected()) {
            eddystoneBeaconConnection.connect();
        }
    }

    private void clearConnection() {
        if (eddystoneBeaconConnection != null && eddystoneBeaconConnection.isConnected()) {
            eddystoneBeaconConnection.close();
            eddystoneBeaconConnection = null;
        }
    }

    @Override
    public void onConnected() {
        showToast("Connected");
    }


    @Override
    public void onAuthenticationSuccess(IEddystoneDevice.Characteristics characteristics) {
        showToast("Authentication success");
        fillEntries(characteristics);
    }

    @Override
    public void onAuthenticationFailure(final int failureCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Intent intent = getIntent();
                switch (failureCode) {
                    case EddystoneBeaconConnection.FAILURE_UNKNOWN_BEACON:
                        intent.putExtra(EXTRA_FAILURE_MESSAGE, String.format("Unknown beacon: %s", eddystoneDevice.getAddress()));
                        break;
                    case EddystoneBeaconConnection.FAILURE_WRONG_PASSWORD:
                        intent.putExtra(EXTRA_FAILURE_MESSAGE, "Wrong password. Beacon will be disabled for about 20 mins.");
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
    public void onCharacteristicsUpdated(IEddystoneDevice.Characteristics characteristics) {
        fillEntries(characteristics);
    }

    @Override
    public void onErrorOccured(int errorCode) {
        switch (errorCode) {
            case EddystoneBeaconConnection.ERROR_OVERWRITE_REQUEST:
                showToast("Overwrite request error");
                break;

            case EddystoneBeaconConnection.ERROR_SERVICES_DISCOVERY:
                showToast("Services discovery error");
                break;

            case EddystoneBeaconConnection.ERROR_AUTHENTICATION:
                showToast("Authentication error");
                break;

            default:
                throw new IllegalStateException("Unexpected connection error occured: " + errorCode);
        }
    }

    @Override
    public void onDisconnected() {
        showToast("Disconnected");
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EddystoneManagementActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillEntries(final IEddystoneDevice.Characteristics characteristics) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fillUI(characteristics);
            }
        });
    }

    private void fillUI(IEddystoneDevice.Characteristics characteristics) {
        //fill UI
    }

    private void updateURL(String url){

        eddystoneBeaconConnection.overwriteUrl(url, new EddystoneBeaconConnection.WriteListener() {
            @Override
            public void onWriteSuccess() {
                showToast("Overwrite url success");
            }

            @Override
            public void onWriteFailure() {
                showToast("Overwrite url failure");
            }
        });
    }

}
