package test.collegecarpool.alpha.LoginAndRegistrationActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import test.collegecarpool.alpha.Activities.HomeScreenActivity;
import test.collegecarpool.alpha.R;

public class SigninActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private SharedPreferences settings;

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

        if(getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(SigninActivity.this, HomeScreenActivity.class));
        }

        String savedEmail = returnSaved("Email");
        String savedPassword = returnSaved("Password");
        inputEmail.setText(savedEmail, TextView.BufferType.EDITABLE);
        inputPassword.setText(savedPassword, TextView.BufferType.EDITABLE);

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
                signin(email, password);
            }
        });
    }

    public void signin(final String email, final String password){
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

    private void saveLogin(String email, String password){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.apply();
    }

    private String returnSaved(String key){
        return settings.getString(key, "");
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
