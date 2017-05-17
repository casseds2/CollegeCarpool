package test.collegecarpool.alpha.LoginAndRegistrationActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import test.collegecarpool.alpha.Activities.HomeScreenActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class SigninActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private EditText inputEmail, inputPassword;

    private ProgressBar progressBar;

    private FirebaseAuth auth;

    private GoogleApiClient googleApiClient;

    private FirebaseAuth.AuthStateListener authStateListener;

    String firstName, secondName, email;

    SharedPreferences settings;

    private static final int RC_SIGN_IN = 9001;

    private final String TAG = "SIGNIN  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        auth = FirebaseAuth.getInstance();

        settings = this.getSharedPreferences("Login", 0);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        Button btn_forgot_password = (Button) findViewById(R.id.btn_forgotPassword);
        Button btn_signup = (Button) findViewById(R.id.btn_Signup);
        Button google_login = (Button) findViewById(R.id.google_login);

        if(getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(SigninActivity.this, HomeScreenActivity.class));
        }

        if(returnSaved("Email") != null && returnSaved("Password") != null){
            String savedEmail = returnSaved("Email");
            String savedPassword = returnSaved("Password");
            inputEmail.setText(savedEmail, TextView.BufferType.EDITABLE);
            inputPassword.setText(savedPassword, TextView.BufferType.EDITABLE);
        }

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this, SignupActivity.class));
            }
        });

        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this, ForgotPasswordActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter an e-mail", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                /*Authenticate User With Credentials*/
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);

                            if(!task.isSuccessful()){
                                if(password.length() < 6){
                                    inputPassword.setError(getString(R.string.minimum_password));
                                }
                                else{
                                    Toast.makeText(SigninActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                saveLogin(email, password);
                                startActivity(new Intent(SigninActivity.this, HomeScreenActivity.class));
                                finish();
                            }
                        }
                    });
            }
        });

        setUpGoogle();

        google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setUpGoogle(){
        /*Configure Signin*/
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        /*If Login Successful, Push Profile*/
        authListener();

        Log.d(TAG, "SET UP GOOGLE COMPLETED");
    }

    private void authListener(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "AUTH LISTENER HIT");
                FirebaseUser user = auth.getCurrentUser();
                if(user != null){
                    HashMap<String, Object> userMap = new HashMap<>();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserProfile/");
                    Log.d(TAG, firstName);
                    Log.d(TAG, secondName);
                    Log.d(TAG, email);
                    UserProfile userProfile = new UserProfile(firstName, secondName, email, 0, 0, 10);
                    userMap.put(auth.getCurrentUser().getUid(), userProfile.toMap());
                    databaseReference.updateChildren(userMap);
                    Log.d(TAG, userProfile.toString());
                }
            }
        };
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            if(auth.getCurrentUser() != null) {
                                startActivity(new Intent(SigninActivity.this, HomeScreenActivity.class));
                                HashMap<String, Object> userMap = new HashMap<>();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserProfile/");
                                if(databaseReference.child(auth.getCurrentUser().getUid()) == null) {
                                    UserProfile userProfile = new UserProfile(firstName, secondName, email, 0, 0, 10);
                                    userMap.put(auth.getCurrentUser().getUid(), userProfile.toMap());
                                    databaseReference.updateChildren(userMap);
                                }
                            }
                            else
                                Log.d(TAG,"CURRENT USER WAS NULL");
                        }
                        else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                ArrayList<String> name = new ArrayList<>();
                if(account != null) {
                    if (account.getDisplayName() != null)
                        name = new ArrayList<>(Arrays.asList(account.getDisplayName().split(" ")));
                    firstName = name.get(0);
                    Log.d(TAG, firstName);
                    secondName = name.get(name.size()-1);
                    Log.d(TAG, secondName);
                    email = account.getEmail();
                    Log.d(TAG, email);
                    Log.d(TAG, "ACCOUNT CREATED");
                }
                else{
                    Log.d(TAG, "ERROR READING DETAILS");
                }
            }
            else {
                Log.d(TAG, "SIGNING FAILED");
            }
        }
    }

    private void saveLogin(String email, String password){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.apply();
    }

    private String returnSaved(String key){
        String saved = settings.getString(key, "");
        return saved;
    }

    public void onStart(){
        super.onStart();

    }

    public void onStop(){
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
