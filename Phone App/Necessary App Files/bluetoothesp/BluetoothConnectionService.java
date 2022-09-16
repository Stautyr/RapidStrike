package com.example.bluetooth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "MYAPP";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private AcceptThread mInsecureAcceptThread;

    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;

    private ConnectedThread mConnectedThread;

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    public BluetoothConnectionService(Context mContext) {
        this.mContext = mContext;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        @SuppressLint("MissingPermission")
        public AcceptThread(){
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            }catch (IOException e){}
            mmServerSocket = tmp;
        }
        //Automatically executes in a thread. No call needed
        public void run(){
            Log.d(TAG, "run: AcceptThread Running");
            BluetoothSocket socket = null;

            try {
                //This is a blocking call and will only return  on a successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start");
                socket = mmServerSocket.accept();

                //Will not progress to this line unless a connection is made
                Log.d(TAG, "run: RFCOM server socket accepted connection");
            } catch (IOException e) {
                Log.d(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            if(socket != null){
                connected(socket, mmDevice);
            }
            Log.i(TAG, "End mAcceptThread");
        }
        public void cancel(){
            Log.d(TAG, "cancel: Canceling AcceptThread");
            try{
                mmServerSocket.close();
            }catch (IOException e){
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }

    /**This thread runs while attempting to make an outgoing connection
     * with a device. There is no breakpoints. The connection will succeed or fail*/
    private class ConnectThread extends Thread{
        private BluetoothSocket mmSocket;
        public ConnectThread(BluetoothDevice device, UUID uuid){
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }
        @SuppressLint("MissingPermission")
        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN m ConnectThread");
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: " + MY_UUID_INSECURE);
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.d(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }
            mmSocket = tmp;

            mBluetoothAdapter.cancelDiscovery();

            try {
                //This only returns on exception or successful connection
                mmSocket.connect();
                Log.d(TAG, "run: ConnectThread connected.");
            } catch (IOException e) {
                //close socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket");
                } catch (IOException ex) {
                    Log.d(TAG, "mConnectThread: run: Unable to close connection in socket " + ex.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE);
            }
            connected(mmSocket, mmDevice);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket");
                mmSocket.close();
            } catch (IOException e){
                Log.e(TAG, "cancel: close() of mmSocket in ConnectThread failed." + e.getMessage());
            }
        }
    }

    public synchronized  void start(){
            Log.d(TAG, "start");
            // Cancel any thread attempting to make connection
            if (mConnectThread != null){
                mConnectThread.cancel();
                mConnectThread = null;
            }
            if(mInsecureAcceptThread == null){
                mInsecureAcceptThread = new AcceptThread();
                //Note this start is not the same as the function it's currently in. It's the thread start function
                mInsecureAcceptThread.start();
            }
        }

        public void startClient(BluetoothDevice device, UUID uuid){
            Log.d(TAG, "startClient: Started");

            //initiate progress dialog
            mProgressDialog = ProgressDialog.show(mContext, "Connecting Bluetooth", "Please Wait...", true);
            mConnectThread = new ConnectThread(device, uuid);
            mConnectThread.start();
        }
        private class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;

            public ConnectedThread(BluetoothSocket socket) {
                Log.d(TAG, "ConnectedThread: Starting");
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                //dismiss the progressdialog when connection is established
                mProgressDialog.dismiss();

                try {
                    tmpIn = mmSocket.getInputStream();
                    tmpOut = mmSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }

            public void run() {
                byte[] buffer = new byte[1024]; // buffer store for the stream
                int bytes; // bytes returned from read()

                while (true) {
                    //read from InputStream
                    try {
                        bytes = mmInStream.read(buffer);
                        String incomingMessage = new String(buffer, 0, bytes);
                        Log.d(TAG, "InputStream: " + incomingMessage);
                    } catch (IOException e) {
                        Log.d(TAG, "write: Error reading inputstream. " + e.getMessage());
                        break;
                    }
                }
            }

            public void write(byte[] bytes) {
                String text = new String(bytes, Charset.defaultCharset());
                Log.d(TAG, "write: Writing to output stream: " + text);
                try {
                    mmOutStream.write(bytes);
                } catch (IOException e) {
                    Log.d(TAG, "write: Error writing to outputstream. " + e.getMessage());
                }
            }

            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {}
            }
    }



        private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting");

        //Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

        }
        public void write(byte[] out){
            //temp object
            ConnectedThread r;
            //Sync copy of Connect thread
            Log.d(TAG, "write: Write Called");
            mConnectedThread.write(out);
        }
    }

