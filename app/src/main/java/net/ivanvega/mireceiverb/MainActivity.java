package net.ivanvega.mireceiverb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.ivanvega.mireceiverb.receiver.FileManager;
import net.ivanvega.mireceiverb.receiver.MiBroadcastReceiverB;
import net.ivanvega.mireceiverb.receiver.MiReceiverTelefonia;

public class MainActivity extends AppCompatActivity {
    MiBroadcastReceiverB myBroadcastReceiver=
            new MiBroadcastReceiverB();

    MiReceiverTelefonia miReceiverTelefonia = new MiReceiverTelefonia();

    EditText txtTel, txtMessage;
    String message;
    String num;
    TextView lblMensaje;
    TextView lblNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblMensaje = findViewById(R.id.lblMensaje);
        num = "";
        txtTel = findViewById(R.id.txtNum);
        txtMessage = findViewById(R.id.txtMen);
        lblNum = findViewById(R.id.lblNumero);
        String leido = FileManager.readFromFile(getApplicationContext());
        if(leido.contains("%!%")){
            String result[] = leido.replace("\n","").replace("\r","").split("%!%");
            num = result[0];
            message = result[1];
        }
        lblNum.setText(num);
        lblMensaje.setText(message);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(getString(R.string.action_broadcast));
        this.registerReceiver(myBroadcastReceiver, filter);

        //Telephony.Sms .Intents.SMS_RECEIVED_ACTION

        IntentFilter intentFilterTel = new IntentFilter(Telephony.Sms .Intents.SMS_RECEIVED_ACTION);

        getApplicationContext().registerReceiver(miReceiverTelefonia,
                intentFilterTel
        );
    }

    private void enviarSMS(String tel, String msj) {
        SmsManager smsManager =  SmsManager.getDefault();

        smsManager.sendTextMessage(tel,null, msj,
                null, null);



        Toast.makeText(
                this, "Mensaje enviado",
                Toast.LENGTH_LONG
        ).show();
    }

    public void btnProMensaje(View v) {
        String cadAgregar = txtTel.getText().toString() + "%!%" + txtMessage.getText().toString();
        FileManager.writeToFile(cadAgregar,getApplicationContext());
        lblNum.setText(txtTel.getText());
        lblMensaje.setText(txtMessage.getText());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(myBroadcastReceiver);
    }

    public void btnMenDif(View v){
        enviarSMS(txtTel.getText().toString(), txtMessage.getText().toString());
    }
}