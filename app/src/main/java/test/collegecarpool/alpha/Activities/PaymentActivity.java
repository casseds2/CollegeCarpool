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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.UserProfile;

import static android.nfc.NdefRecord.createMime;

public class PaymentActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    public NfcAdapter nfcAdapter;
    private TextView balance;
    private final String TAG = "PaymentActivity";
    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        balance = (TextView) findViewById(R.id.balance);

        initFirebaseAuth();
        initNumWheels();
        displayBalance();

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


    private void initNumWheels(){
        initNumPickerOne();
        initNumPickerTwo();
    }

    private void initNumPickerOne(){
        NumberPicker numPickerOne = (NumberPicker) findViewById(R.id.numPickerOne);
        numPickerOne.setMinValue(0);
        numPickerOne.setMaxValue(50);
        numPickerOne.setWrapSelectorWheel(true);
        numPickerOne.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, oldVal + " changed to " + newVal);
            }
        });
    }

    private void initNumPickerTwo(){
        NumberPicker numPickerTwo = (NumberPicker) findViewById(R.id.numPickerOne);
        numPickerTwo.setMinValue(0);
        numPickerTwo.setMaxValue(99);
        numPickerTwo.setWrapSelectorWheel(true);
        numPickerTwo.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, oldVal + " changed to " + newVal);
            }
        });
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
                balance.setText(message);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
           Toast.makeText(this, "PHONE SEEN", Toast.LENGTH_SHORT).show();
           onNewIntent(getIntent());
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            Toast.makeText(this, "PHONE SEEN", Toast.LENGTH_SHORT).show();
            onNewIntent(getIntent());
        }
    }

    private void initFirebaseAuth(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(null != auth.getCurrentUser())
            currentUser = auth.getCurrentUser();
    }

    private void displayBalance(){
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot dataSnapshot1 : dataSnapshots){
                    UserProfile userProfile = dataSnapshot1.getValue(UserProfile.class);
                    if(userProfile.getEmail().equals(currentUser.getEmail())){
                        double wallet = userProfile.getWallet();
                        balance.setText(String.valueOf(wallet));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private double getBalance(){
        return Double.parseDouble((String)balance.getText());
    }

    private boolean checkEnoughCredit(double expense){
        return getBalance() > expense;
    }

    private void removeCostFromWallet(double cost){
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        double newBalance = getBalance();
        if(checkEnoughCredit(cost)){
            newBalance = getBalance() - cost;
        }
        else{
            Toast.makeText(this, "Not Enough in Wallet", Toast.LENGTH_SHORT).show();
        }
        if(null != currentUser){
            userRef.child(currentUser.getUid()).child("wallet").setValue(newBalance);
            displayBalance();
        }
    }
}
