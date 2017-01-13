package edu.buffalo.cse.cse486586.groupmessenger1;


import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Mathurs on 2/16/16.
 */
public class OnSendClickListner implements OnClickListener
{
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";


    private static final String TAG = OnSendClickListner.class.getName();
    private final EditText edit_Text ;
    private final TextView text_view  ;
    private final String myPort;

    public OnSendClickListner(EditText editText1 , TextView textView1, String myPort)
    {
        this.edit_Text = editText1;
        this.text_view = textView1;
        this.myPort = myPort;
    }
    @Override
    public void onClick(View v)
    {
        String msg = edit_Text.getText().toString() + "\n";
        Log.e(TAG, "in OnClick with message :: " + msg);
        edit_Text.setText(""); // This is one way to reset the input box.
//        text_view.append(msg);// This is one way to display a string.

        Log.e(TAG, "Calling Client Task ");
        new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
        return;
    }

    public class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

            Log.e(TAG, "In client :: This port ::   "+ msgs[1]);
            String[] ports = {REMOTE_PORT0, REMOTE_PORT1, REMOTE_PORT2, REMOTE_PORT3, REMOTE_PORT4};
            String msgToSend = msgs[0];
            Log.e(TAG, "writing this message ::  "+ msgs[0]);
//            OutputStreamWriter out;
//            PrintWriter write;
            try {
                for(String port  : ports)
                {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(port));
                    Log.e(TAG, "In client :: Created socket ::   s"+ socket);
                    OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
                    PrintWriter write = new PrintWriter(out);
                    Log.e(TAG, "In client :: writing message to socket ::   "+ msgToSend);
                    write.print(msgToSend);
                    out.flush();
                }
            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException:: " + e.toString());
            }


            return null;
        }
    }

}