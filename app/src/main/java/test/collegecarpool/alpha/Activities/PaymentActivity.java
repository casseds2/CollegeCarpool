package test.collegecarpool.alpha.Activities;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import test.collegecarpool.alpha.R;

import static android.nfc.NdefRecord.createMime;

public class PaymentActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    public NfcAdapter nfcAdapter;
    private TextView textView;
    private final String TAG = "PaymentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        textView = (TextView) findViewById(R.id.payloadText);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(null != nfcAdapter && nfcAdapter.isEnabled()){
            Toast.makeText(this, "Nfc Available", Toast.LENGTH_SHORT).show();
            nfcAdapter.setNdefPushMessageCallback(this, this); //Called when NFC device in range
            nfcAdapter.setOnNdefPushCompleteCallback(this, this); //Called when nDef message delivered
        }
        else{
            Toast.makeText(this, "Nfc Not Available", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "NFC NOT AVAILABLE");
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String message = ("Beam me up Scotty \n Beam Time: " + System.currentTimeMillis());
        return new NdefMessage(new NdefRecord[]{ createMime("application/test.collegecarpool.alpha", message.getBytes())});
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        Toast.makeText(this, "Payload Delivered", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Payload Delivered");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if(null != intent && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(null != rawMessages){
                NdefMessage ndefMessage = (NdefMessage) rawMessages[0];
                Log.d(TAG, "NDEF is " + String.valueOf(ndefMessage));
                String message = new String(ndefMessage.getRecords()[0].getPayload());
                textView.setText(message);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
           onNewIntent(getIntent());
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            onNewIntent(getIntent());
        }
    }
}
