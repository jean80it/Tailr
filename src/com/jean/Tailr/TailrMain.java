package com.jean.Tailr;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.*;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.jean.Tailr.R;

public class TailrMain extends Activity implements LocationListener {
	private static final String TAG = "Tailr";
	private static final String[] S = { "Out of Service", "Temporarily Unavailable", "Available" };
        private final static int REQUEST_ENABLE_BT = 1;
        
	private TextView output;
	private LocationManager _locationManager = null;
	private String bestProvider;

        Location _location = null;
                
        BluetoothAdapter _bluetoothAdapter = null;
        
        // Create a BroadcastReceiver for ACTION_FOUND
        private final BroadcastReceiver _receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    println(device.getName() + " (" + device.getAddress()+")"); // TODO: should sync
                }
            }
        };
        
        public BluetoothAdapter getUsableBTAdapter()
        {
            if (_bluetoothAdapter == null) {
                _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            
            if (_bluetoothAdapter == null) {
                return null;
            }
            
            // polls user for BT activation
            if (!_bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            
            if (!_bluetoothAdapter.isEnabled()) {
                return null;
            }
            
            return _bluetoothAdapter;
        }
        
	@Override
	public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            output = (TextView) findViewById(R.id.output);

            // trigger BT init
            BluetoothAdapter bta = getUsableBTAdapter(); // TODO: check for sanity
            if (bta!=null)
            {
                bta.startDiscovery();
            }
            else
            {
                Toast.makeText(this, "Houston, ...", Toast.LENGTH_LONG).show();
            }
              
            // Register the BroadcastReceiver
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(_receiver, filter); // Don't forget to unregister during onDestroy

            // location init
            _locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (_locationManager == null) {
                // no so neanche se pol suceder :D
            }
            getBestProvider();

	}

	@Override
	protected void onResume() {
            super.onResume();
            _locationManager.requestLocationUpdates(bestProvider, 20000, 1, this);
	}

	@Override
	protected void onPause() {
            super.onPause();
//          _locationManager.removeUpdates(this);
	}

	public void onLocationChanged(Location location) {
//		printLocation(location);
            _location = location;
	}

	public void onProviderDisabled(String provider) {
            getBestProvider();
            _locationManager.requestLocationUpdates(bestProvider, 20000, 1, this);
	}

	public void onProviderEnabled(String provider) {
            getBestProvider();
            _locationManager.requestLocationUpdates(bestProvider, 20000, 1, this);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
//		output.append("\n\nProvider Status Changed: " + provider + ", Status="
//				+ S[status] + ", Extras=" + extras);
	}

        private void println(String s)
        {
            print(s+"\n");
        }
        
        private void print(String s)
        {
            output.append(s);
        }
        
	private void printLocation(Location location) {
            try {
                if (location == null)
                        output.append("\nLocation[unknown]\n\n");
                else
                        output.append("\n\n" + location.toString());
            } catch (Exception ex) {
                //Logger.getLogger(TailrMain.class.getName()).log(Level.SEVERE, null, ex);
            }
	}

    private void getBestProvider() {
        Criteria criteria = new Criteria(); 
        // ...
        // TODO: set some criteria
        // ...
        bestProvider = _locationManager.getBestProvider(criteria, false);
    }
}
