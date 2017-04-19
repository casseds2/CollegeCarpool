package test.collegecarpool.alpha.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private TextView balance, amountPaid;
    private final String TAG = "PaymentActivity";
    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private int euro, cent;
    private boolean acceptRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        balance = (TextView) findViewById(R.id.balance);

        initFirebaseAuth();
        initNumWheels();
        displayBalance();


        /*Test Code to Check Euro and Cents are Changing*/
        amountPaid = (TextView) findViewById(R.id.amountPaid);

        Button button = (Button) findViewById(R.id.refreshTest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountPaid.setText(euro + "." + cent);
            }
        });
        /*End  of the test code*/


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

    /*Offer a Choice to Accept NFC*/
    private boolean initAlertDialog(){
        new AlertDialog.Builder(getApplicationContext())
                .setTitle("NFC Detected")
                .setMessage("Do you want to accept this Request")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        acceptRequest = true;
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        acceptRequest = false;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return acceptRequest;
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
                euro = newVal;
            }
        });
        Log.d(TAG, "NEW EURO: " + String.valueOf(euro));
    }

    private void initNumPickerTwo(){
        NumberPicker numPickerTwo = (NumberPicker) findViewById(R.id.numPickerTwo);
        numPickerTwo.setMinValue(0);
        numPickerTwo.setMaxValue(99);
        numPickerTwo.setWrapSelectorWheel(true);
        numPickerTwo.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, oldVal + " changed to " + newVal);
                cent = newVal;
            }
        });
        Log.d(TAG, "NEW CENT: " + cent);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //String message = ("Beam me up Scotty \n Beam Time: " + System.currentTimeMillis());
        String message = String.valueOf(euro) + "." + String.valueOf(cent);
        return new NdefMessage(new NdefRecord[]{ createMime("application/test.collegecarpool.alpha", message.getBytes())});
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        //Toast.makeText(this, "Payload Delivered", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Payload Delivered: " + String.valueOf(euro) + "." + String.valueOf(cent));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if(null != intent && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            initAlertDialog();
            if(acceptRequest) {
                Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (null != rawMessages) {
                    NdefMessage ndefMessage = (NdefMessage) rawMessages[0];
                    Log.d(TAG, "NDEF is " + String.valueOf(ndefMessage));
                    String message = new String(ndefMessage.getRecords()[0].getPayload());
                    //balance.setText(message);
                    Toast.makeText(this, "Amount to be Paid: " + message, Toast.LENGTH_SHORT).show();
                }
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
