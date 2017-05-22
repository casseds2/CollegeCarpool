package test.collegecarpool.alpha.Activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

import test.collegecarpool.alpha.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.MessagingActivities.ChatRoomActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.UserProfile;

import static android.nfc.NdefRecord.createApplicationRecord;
import static android.nfc.NdefRecord.createMime;

public class PaymentActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    public NfcAdapter nfcAdapter;
    private boolean tapDetected;
    private TextView balance;
    private final String TAG = "PaymentActivity";
    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private int euro, cent;
    private FirebaseAuth auth;
    private double cost = 0;
    private TextView paymentAmount;
    private double wallet;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initFirebaseAuth();

        tapDetected = true;
        auth = FirebaseAuth.getInstance();
        balance = (TextView) findViewById(R.id.balance);
        euro = 0;
        cent = 0;

        displayBalance();
        initNumWheels();
        initPaymentAmount();
        initDrawer();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(null != nfcAdapter && nfcAdapter.isEnabled()){
            //Toast.makeText(this, "Nfc Available", Toast.LENGTH_SHORT).show();
            nfcAdapter.setNdefPushMessageCallback(this, this); //Called when NFC device in range
            nfcAdapter.setOnNdefPushCompleteCallback(this, this); //Called when nDef message delivered
        }
        else{
            Toast.makeText(this, "Nfc Not Available", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "NFC NOT AVAILABLE");
        }
    }

    private void initPaymentAmount(){
        paymentAmount = (TextView) findViewById(R.id.payment_amount);
        paymentAmount.setText("Donation: €" + String.valueOf(euro) + "." + String.valueOf(cent));
    }

    private void initNumWheels(){
        initNumPickerOne();
        initNumPickerTwo();
    }

    private void initNumPickerOne(){
        NumberPicker numPickerOne = (NumberPicker) findViewById(R.id.numPickerOne);
        numPickerOne.setMinValue(0);
        numPickerOne.setMaxValue(20);
        numPickerOne.setWrapSelectorWheel(true);
        numPickerOne.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, oldVal + " changed to " + newVal);
                euro = newVal;
                if(cent < 10)
                    paymentAmount.setText("Donation: €" + String.valueOf(euro) + ".0" + String.valueOf(cent));
                else
                    paymentAmount.setText("Donation: €" + String.valueOf(euro) + "." + String.valueOf(cent));

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
                if(cent < 10)
                    paymentAmount.setText("Donation: €" + String.valueOf(euro) + ".0" + String.valueOf(cent));
                else
                    paymentAmount.setText("Donation: €" + String.valueOf(euro) + "." + String.valueOf(cent));
            }
        });
        Log.d(TAG, "NEW CENT: " + cent);
    }

    /*Called when NFC Tag is detected in Range*/
    //Issue With If User (To be Paid)
    //Has Num Wheel Set To Something, The money will be taken out of their account
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String message = String.valueOf(euro) + "." + String.valueOf(cent);
        cost = Double.parseDouble(message);
        /*This incorrect loop cause money to be taken away irregardless of message being beamed*/
        Log.d(TAG, "Personal Balance is " + String.valueOf(getPersonalBalance()) + " and Cost is " + String.valueOf(cost));
        if (userHasEnoughCredit(cost)) { //This is the point where its going wrong
            if(tapDetected) {
                tapDetected = false;
                //removeCostFromWallet(cost);
                //displayBalance();
                return new NdefMessage(new NdefRecord[]{
                        createMime("application/test.collegecarpool.alpha", message.getBytes()), //Inserts the Mime Message
                        createApplicationRecord("test.collegecarpool.alpha") //Embeds Android Application Record into Ndef Message to Start Correct Package
                });
            }
            else {
                String tapWarning = "Restart Activity";
                return new NdefMessage(new NdefRecord[]{
                        createMime("application/test.collegecarpool.alpha", tapWarning.getBytes()),
                        createApplicationRecord("test.collegecarpool.alpha")
                });
            }
        } else {
            Log.d(TAG, "ENTERED ELSE NDEF STATE");
            String ErrorMessage = "Not Enough Credit";
            return new NdefMessage(new NdefRecord[]{
                    createMime("application/test.collegecarpool.alpha", ErrorMessage.getBytes()),
                    createApplicationRecord("test.collegecarpool.alpha")
            });
        }
    }

    /*Retrieve the User Balance From the Wallet TextView*/
    private double getPersonalBalance(){
        Log.d(TAG, "getPersonalBalance() is " + wallet);
        return wallet;
    }

    /*Check if the Cost Parameter is Less than the User Balance*/
    private boolean userHasEnoughCredit(double cost){
        return getPersonalBalance() > cost;
    }

    /*Called When The message was successfully sent*/
    @Override
    public void onNdefPushComplete(NfcEvent event) {
        //Toast.makeText(this, "Payload Delivered", Toast.LENGTH_SHORT).show();
        removeCostFromWallet(cost);
        displayBalance();
        tapDetected = true;
        Log.d(TAG, "Payload Delivered: " + String.valueOf(euro) + "." + String.valueOf(cent));
    }

    /*Called When Ndef Message Received*/
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if(null != intent && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (null != rawMessages) {
                NdefMessage ndefMessage = (NdefMessage) rawMessages[0];
                String message = new String(ndefMessage.getRecords()[0].getPayload());
                Log.d(TAG, "NDEF MESSAGE!!! " + ndefMessage.toString());
                Log.d(TAG, "NFC MESSAGE!!! " + message);
                if (!message.equals("Not Enough Credit") && !message.equals("User Must Restart Activity")) {
                    cost = Double.parseDouble(message);
                    Log.d(TAG, "RECEIVED COST IS" + cost);
                    addCostToWallet(cost);
                }
                else
                    if(message.equals("Not Enough Credit"))
                        Toast.makeText(PaymentActivity.this, "User Hasn't Enough Credit", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(PaymentActivity.this, "User Must Restart Payment Activity", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
           onNewIntent(getIntent());
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    public void onStart(){
        super.onStart();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            //Toast.makeText(this, "PHONE SEEN", Toast.LENGTH_SHORT).show();
            onNewIntent(getIntent());
        }
    }

    private void initFirebaseAuth(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(null != auth.getCurrentUser())
            currentUser = auth.getCurrentUser();
    }

    private void displayBalance(){
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(currentUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                wallet = userProfile.getWallet();
                Log.d(TAG, "Wallet: " + String.valueOf(wallet));
                balance.setText("Wallet: €" + String.valueOf(wallet));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*Function that when Given Incoming Amount, Will Update User's Wallet*/
    private void removeCostFromWallet(double cost){
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        double newBalance = 0;
        if(userHasEnoughCredit(cost)){
            newBalance = getPersonalBalance() - cost;
        }
        else{
            Toast.makeText(this, "Not Enough in Wallet", Toast.LENGTH_SHORT).show();
        }
        if(null != currentUser){
            userRef.child(currentUser.getUid()).child("wallet").setValue(newBalance);
            displayBalance();
        }
    }

    private void addCostToWallet(double cost){
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        double newBalance = getPersonalBalance();
        newBalance = newBalance + cost;
        if(null != currentUser){
            userRef.child(currentUser.getUid()).child("wallet").setValue(newBalance);
            displayBalance();
            Toast.makeText(PaymentActivity.this, "Transaction Complete", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void initDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(PaymentActivity.this, HomeScreenActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_journey:
                        startActivity(new Intent(PaymentActivity.this, PlanJourneyActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_messages:
                        startActivity(new Intent(PaymentActivity.this, ChatRoomActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_payment:
                        return true;
                    case R.id.nav_friends:
                        startActivity(new Intent(PaymentActivity.this, FriendActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_logout:
                        auth.signOut();
                        startActivity(new Intent(PaymentActivity.this, SigninActivity.class));
                        onStop();
                        return true;
                }
                return false;
            }
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
    }
}
