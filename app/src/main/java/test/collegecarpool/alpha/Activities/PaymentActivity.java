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

public class PaymentActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

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
            nfcAdapter.setNdefPushMessageCallback(this, this);
        }
        else{
            Toast.makeText(this, "Nfc Not Available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(null != intent && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(null != rawMessages){
                NdefMessage ndefMessage = (NdefMessage) rawMessages[0];
                Log.d(TAG, "NDEF is " + String.valueOf(ndefMessage));
                textView.setText(new String(ndefMessage.getRecords()[0].getPayload()));
                Toast.makeText(this, new String(ndefMessage.getRecords()[0].getPayload()), Toast.LENGTH_SHORT).show();
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

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String message = "Beam Me Up Scotty!";
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{
                createMime("application/test.collegecarpool.alpha.android.beam", message.getBytes())
        });
        Log.d(TAG, "NDEF Message Created");
        return  ndefMessage;
    }
}
