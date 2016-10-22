package com.example.collegecarpool;

import android.app.ProgressDialog;
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

public class SignupActivity extends AppCompatActivity {

    /**Define Firebase**/
    private FirebaseAuth firebaseAuth;

    /**Define UI Elements**/
    private EditText editTextEmail, editTextPassword;
    private Button buttonSignUp;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /**Associate the UI**/
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        buttonSignUp = (Button)findViewById(R.id.signup);
        progressDialog = new ProgressDialog(this);

        /**Initialize Firebase**/
        firebaseAuth = FirebaseAuth.getInstance();

        buttonSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                registerUser();
            }
        });
    }

    private void registerUser() {
        /**Reads In User Credentials**/
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter an e-mail!", Toast.LENGTH_LONG).show();
            return;
        }
        if(!email.contains("mail.dcu.ie")){
            Toast.makeText(this, "Enter a DCU e-mail!", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter a password!", Toast.LENGTH_LONG).show();
            return;
        }
        if(password.length() < 6){
            Toast.makeText(this, "Enter a stronger password", Toast.LENGTH_LONG).show();
            return;
        }

        /**While Logging in Message**/
        progressDialog.setMessage("Fun Things are Happening");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignupActivity.this, "Hurray Logged In!", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(SignupActivity.this, "Oops and Error Occurred", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}
