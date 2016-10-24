package com.example.collegecarpool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    /**My User Interface**/
    private Button btnSignin, btnSignup, btnForgotPassword;
    private EditText mEmail, mPassword;
    private ProgressDialog Progress;

    /**Firebase Auth Class**/
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**Map UI Elements**/
        btnSignin = (Button)findViewById(R.id.signin);
        btnSignup = (Button)findViewById(R.id.signup);
        btnForgotPassword = (Button)findViewById(R.id.forgot);
        mEmail = (EditText)findViewById(R.id.email);
        mPassword = (EditText)findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();
        Progress = new ProgressDialog(this);

        btnSignin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Signin();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Signup();
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ForgotPassword();
            }
        });
    }

    /**Method to cater for Forgotten Passwords**/
    private void ForgotPassword() {
        Intent forgot = new Intent(MainActivity.this, ForgotPasswordActivity.class);
        startActivity(forgot);
    }

    /**Method to Cater For Signups**/
    private void Signup() {
        Intent signup = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(signup);
    }

    /**Method to cater for Signing In**/
    private void Signin(){
        /**Reads In User Credentials**/
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(MainActivity.this, "Enter an e-mail!", Toast.LENGTH_LONG).show();
            return;
        }
        if(!email.contains("mail.dcu.ie")){
            Toast.makeText(MainActivity.this, "Enter a DCU e-mail!", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(MainActivity.this, "Enter a password!", Toast.LENGTH_LONG).show();
            return;
        }
        if(password.length() < 6){
            Toast.makeText(MainActivity.this, "Enter an e-mail!", Toast.LENGTH_LONG).show();
            return;
        }

        /**While Logging in Message**/
        Progress.setMessage("Fun Things are Happening");
        Progress.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Hurray Logged In!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, PassengerDecision.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, "Oops and Error Occurred", Toast.LENGTH_LONG).show();
                }
                Progress.dismiss();
            }
        });
    }
}
