/**
*pj92insgh
*Prabhjit Singh
*
* testing android bluetooth class

*android manifest will use the following permissions
<manifest ... >
  <uses-permission android:name="android.permission.BLUETOOTH" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
  ...
</manifest>

**/


/*
NOTES
-first use the adapter to get it
BluetoothAdapter.getDefaultAdapter()
val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

-next using that we will use the listner/profile
-using the BluetoothProfile.ServiceListener and BlueetoothProfile

*/

package com.example.bttest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

/*pj92singh
* Prabhjit Singh
* Bluetooth test */
public class MainActivity extends AppCompatActivity {
    /* GLOBAL variables for bluetooth class members*/
    private static int GET_BLUETOOTH_ON = 100;
    private boolean per_Granted = false;
    public  static int REQUEST_ENABLE_BT = 1;
    BluetoothHeadset bluetoothHeadset;
    BluetoothAdapter bluetoothAdapter;
    BluetoothProfile.ServiceListener profileListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        * first get the permission for bluetooth
        * ** if nothing was found, then start discovery
        * ** else
        * ** check which type of device it is and then next part is use of media
        * */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case GET_BLUETOOTH_ON;
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    per_Granted = true;
                }else {
                    per_Granted = false;
                    Toast.makeText(getApplicationContext(), "App needs bluetooth premission!!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void getBt(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_LONG);

        }
    }
    /*
    * after Bluetooth permission is granted we can connect the 2 devices
    * then proceed to stream data/media
    * */
    public void PairDevices(){
        bluetoothAdapter.getDefaultAdapter();
        try {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                if (bluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                    Log.i("DEBUG", "bluetooth not connected/device not supported");
                    Toast.makeText(getApplicationContext(), "device not supported", Toast.LENGTH_LONG);

                } else {
                    //bluetooth is enabled on device
                    //find paried devices
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                    if (pairedDevices.size() > 0) {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device : pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress(); // MAC address
                            Log.i("DEBUG BT", "BT list: " + deviceName + " hardwareAddress: " + deviceHardwareAddress);
                            //we have a paried device
                            //lets connect
                            profileListener = new BluetoothProfile.ServiceListener() {
                                @Override
                                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                                    //connected
                                    //use global bluetoothHeadset
                                    if (profile == BluetoothProfile.HEADSET) {
                                        bluetoothHeadset = (BluetoothHeadset) proxy;
                                        try {
                                            // Establish connection to the proxy.
                                            bluetoothAdapter.getProfileProxy(getApplicationContext(), profileListener, BluetoothProfile.HEADSET);
                                            //TODO check here for media and connection
                                            //check the connection health
                                            /*
                                            *In order to create a connection between two devices,
                                            * you must implement both the server-side and client-side
                                            * mechanisms because one device must open a server socket,
                                            * the other one must initiate the connection using the
                                            * server device's MAC address.
                                            * The server device and the client device each obtain the
                                            * required BluetoothSocket in different ways.
                                             */
                                            BluetoothServerSocket BtSocket =  new BluetoothServerSocket();

                                        } catch (Exception e) {
                                            Log.i("DEBUG catch SC", "ServiceConnected getProflie Connection error: " + e);
                                        }

                                    }
                                }

                                @Override
                                public void onServiceDisconnected(int profile) {
                                    if (profile == BluetoothProfile.HEADSET) {
                                        bluetoothHeadset = null;
                                        // Close proxy connection after use.
                                        // bluetoothAdapter.closeProfileProxy(bluetoothHeadset);
                                    }
                                }
                            };
                        }
                    } else if (pairedDevices.size() < 0) {
                        //no devices, need to discover
                        //scan for devices
                        bluetoothAdapter.startDiscovery();
                    }
                }
            }else if(bluetoothAdapter.isEnabled() == false){
                //please turn on bluetooth
                Log.i("DEBUG", "turn on bluetooth");
            }
        }catch(Exception e){
            Log.i("DEBUG catch PD", "Pair Devices error: " +e);
        }

    }
}
