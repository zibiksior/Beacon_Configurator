package com.zbigniew.beaconconfigurator;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.EddystoneScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.IBeaconScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.BeaconRegion;
import com.kontakt.sdk.android.ble.device.EddystoneNamespace;
import com.kontakt.sdk.android.ble.discovery.AbstractBluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.DistanceSort;
import com.kontakt.sdk.android.ble.discovery.EventType;
import com.kontakt.sdk.android.ble.discovery.eddystone.EddystoneDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.ibeacon.IBeaconAdvertisingPacket;
import com.kontakt.sdk.android.ble.discovery.ibeacon.IBeaconDeviceEvent;
import com.kontakt.sdk.android.ble.filter.eddystone.EddystoneFilters;
import com.kontakt.sdk.android.ble.filter.ibeacon.IBeaconFilter;
import com.kontakt.sdk.android.ble.filter.ibeacon.IBeaconFilters;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.rssi.RssiCalculators;
import com.kontakt.sdk.android.ble.util.BluetoothUtils;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.Proximity;
import com.kontakt.sdk.android.common.log.LogLevel;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements OnClickListener, ProximityManager.ProximityListener {

    private TextView myAwesomeTextView;
    private Button button1,scanStop;
    private FloatingActionButton fab;
    private List<IEddystoneDevice> deviceList;
    private IEddystoneNamespace namespace;


    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);

        scanStop = (Button) findViewById(R.id.scanStop);
        scanStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                proximityManager.finishScan();
                proximityManager.disconnect();
            }
        });

        KontaktSDK.initialize(this)
                .setDebugLoggingEnabled(BuildConfig.DEBUG)
                .setLogLevelEnabled(LogLevel.DEBUG, true)
                .setCrashlyticsLoggingEnabled(true);

        myAwesomeTextView = (TextView) findViewById(R.id.textView1);
        scanContext = new ScanContext.Builder()
                .setIBeaconScanContext(IBeaconScanContext.DEFAULT)
                .setEddystoneScanContext(EddystoneScanContext.DEFAULT)
                .setScanPeriod(new ScanPeriod(TimeUnit.SECONDS.toMillis(10), TimeUnit.SECONDS.toMillis(3)))
                .build();

        //button1.setOnClickListener(new OnClickListener() {
            //                       @Override
          //                         public void onClick(View view) {
          //                             myAwesomeTextView.setText("Scanning stopped");
          //                             proximityManager.disconnect();
        //                     });

        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        proximityManager = new ProximityManager(getApplicationContext());
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                proximityManager.initializeScan(scanContext, new OnServiceReadyListener() {
                    @Override
                    public void onServiceReady() {
                        proximityManager.attachListener(new ProximityManager.ProximityListener() {
                            @Override
                            public void onScanStart() {
                                myAwesomeTextView.setText("Scanning started");
                                //showToast("Scanning started");
                                //Log.w("Main Activity", "Scanning started");
                            }

                            @Override
                            public void onScanStop() {
                                myAwesomeTextView.setText("Scanning stopped");


                            }

                            @Override
                            public void onEvent(BluetoothDeviceEvent var1) {
                                myAwesomeTextView.setText(var1.getDeviceProfile().toString()+" \n"+ var1.getEventType().toString());
                                //proximityManager.
                            }
                        });
                    }

                    @Override
                    public void onConnectionFailure() {
                        //Utils.showToast(BaseBeaconRangeActivity.this, getString(R.string.unexpected_error_connection));
                    }
                });
            }
        });

    }*/

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;

    private ProximityManager proximityManager;

    private ScanContext scanContext;

    private ListView beaconListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        proximityManager = new ProximityManager(this);


        scanContext = new ScanContext.Builder()
                .setIBeaconScanContext(IBeaconScanContext.DEFAULT)
                .setEddystoneScanContext(EddystoneScanContext.DEFAULT)
                .setScanPeriod(new ScanPeriod(TimeUnit.SECONDS.toMillis(10), TimeUnit.SECONDS.toMillis(3)))
                .build();

        beaconListView = (ListView) findViewById(R.id.beaconList);

        scanStop = (Button) findViewById(R.id.scanStop);
        scanStop.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);

        myAwesomeTextView = (TextView) findViewById(R.id.textView1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!BluetoothUtils.isBluetoothEnabled()) {
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
        } else {
            initializeScan();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        proximityManager.finishScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {

            case REQUEST_CODE_ENABLE_BLUETOOTH:

                if(resultCode == RESULT_OK) {
                    initializeScan();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onScanStart() {
        myAwesomeTextView.setText("Scanning started");
    }

    @Override
    public void onEvent(BluetoothDeviceEvent event) {
        myAwesomeTextView.setText(event.getDeviceProfile().toString() + "\n" + event.getEventType().toString());
        switch (event.getDeviceProfile()) {

            case IBEACON:
                final IBeaconDeviceEvent iBeaconDeviceEvent = (IBeaconDeviceEvent) event;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        IBeaconRegion region = iBeaconDeviceEvent.getRegion();
                        List<IBeaconDevice> deviceList = iBeaconDeviceEvent.getDeviceList();
                    }
                });
                break;

            case EDDYSTONE:
                final EddystoneDeviceEvent eddystoneDeviceEvent = (EddystoneDeviceEvent) event;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        namespace = eddystoneDeviceEvent.getNamespace();
                        deviceList = eddystoneDeviceEvent.getDeviceList();
                    }
                });
                break;

            default:

        }
    }

    @Override
    public void onScanStop() {
        myAwesomeTextView.setText("Scanning stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proximityManager.disconnect();
        proximityManager = null;
    }

    private void initializeScan() {
        proximityManager.initializeScan(scanContext, new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.attachListener(MainActivity.this);
            }

            @Override
            public void onConnectionFailure() {
                //Utils.showToast(MainActivity.this, getString(R.string.unexpected_error_connection));
            }
        });
    }

    @Override
    public void onClick(View v) {
                switch(v.getId()){
                    case R.id.fab:
                        //DO something
                        initializeScan();
                        //myAwesomeTextView.setText("Click");
                        break;
                    case R.id.scanStop:
                        //DO something
                        BeaconAdapter adapter = new BeaconAdapter(this,
                                R.layout.listview_item_row, deviceList);


                        beaconListView = (ListView)findViewById(R.id.beaconList);

                        View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
                        beaconListView.addHeaderView(header);

                        beaconListView.setAdapter(adapter);
                        myAwesomeTextView.setText("Scanning stopped");
                        proximityManager.finishScan();
                        //proximityManager.disconnect();
                        break;
                    case R.id.button1:
                        //DO something
                        Log.w("Beacon info: ", deviceList.get(0).getUrl());
                        String pass="Hf6n";
                        deviceList.get(0).setPassword(pass.getBytes());
                        Intent intent = new Intent(this, EddystoneManagementActivity.class);
                        intent.putExtra(EddystoneManagementActivity.EDDYSTONE_DEVICE,deviceList.get(0));
                        startActivity(intent);
                        break;
                }
    }
}
