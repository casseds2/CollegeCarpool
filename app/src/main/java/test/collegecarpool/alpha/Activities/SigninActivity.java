package test.collegecarpool.alpha.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import test.collegecarpool.alpha.R;

public class SigninActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btn_login, btn_forgot_password, btn_signup;

    private ProgressBar progressBar;

    private FirebaseAuth auth;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        auth = FirebaseAuth.getInstance();

        settings = this.getSharedPreferences("Login", 0);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_forgot_password = (Button) findViewById(R.id.btn_forgotPassword);
        btn_signup = (Button) findViewById(R.id.btn_Signup);

        if(returnSaved("Email") != null && returnSaved("Password") != null){
            String savedEmail = returnSaved("Email");
            String savedPassword = returnSaved("Password");
            inputEmail.setText(savedEmail, TextView.BufferType.EDITABLE);
            inputPassword.setText(savedPassword, TextView.BufferType.EDITABLE);
        }
        Toast.makeText(getApplicationContext(), "Email/Password:" + String.valueOf(returnSaved("Email")) + String.valueOf(returnSaved("Password")), Toast.LENGTH_LONG).show();

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

                /**Authenticate User With Credentials**/
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
        btn_forgot_password.setOnClickListener(null);
        btn_login.setOnClickListener(null);
    }
}
