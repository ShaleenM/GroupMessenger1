package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final int SERVER_PORT = 10000;
    public int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        Log.e(TAG, "In OnCreate...");


        try {

                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                Log.e(TAG, "Server Socket Created :: " + SERVER_PORT);
                new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
            } catch (IOException e) {
                Log.e(TAG, "Can't create a ServerSocket" + e.toString());
            }




        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());

        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));

        final Button send = (Button) findViewById(R.id.button4);
        final TextView text_view = (TextView) findViewById(R.id.textView1);
        final EditText edit_text = (EditText) findViewById(R.id.editText1);
        Log.v(TAG, "Before onClick Listner  ");

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        send.setOnClickListener(new OnSendClickListner(edit_text, text_view, myPort));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }

    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        final TextView text_view = (TextView) findViewById(R.id.textView1);
        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            Log.e(TAG, "Socket  :: " + sockets[0]);
            ServerSocket serverSocket = sockets[0];
            InputStreamReader br;
            BufferedReader in;


            try {
                while(1==1) {
                    Log.e(TAG, "I am before socket.accept");
                    Socket clientSocket = serverSocket.accept();
                    Log.e(TAG, "Server accepted socket");
                    br = new InputStreamReader(clientSocket.getInputStream());
                    in = new BufferedReader(br);
                    String msg = in.readLine();
                    clientSocket.close();
                    Log.e(TAG, "Going To publish" + msg);
                    publishProgress(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Server vala exception!!!");
            }
            return null;
        }

        protected void onProgressUpdate(String... strings) {

            Log.e(TAG, "in onProgressUpdate :: strng passed :: " + strings[0]);
            String msg = strings[0].trim();

            final Uri uri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger1.provider");
            Log.e(TAG, "in onProgressUpdate :: URI Built :: " + uri);


            String key = Integer.toString(count);
            Log.e(TAG, "Count / Key " + key);
            Log.e(TAG, "Values before insert ::  " + msg);

            ContentValues values = new ContentValues();

            values.put("key", key);
            values.put("value", msg);
            try {
                getContentResolver().insert(uri, values);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            count++;

            //Writing to screen
            text_view.append(msg);
            return;
        }
    }
}