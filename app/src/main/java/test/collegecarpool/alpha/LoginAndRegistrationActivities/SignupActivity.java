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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputFirstName, inputSecondName;
    private Button btn_Signup, btn_back;
    private SharedPreferences settings;

    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private String email;
    private String password ;
    private String firstName;
    private String secondName;

    private final static String TAG = "SignUpActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /*Get Firebase Instances*/
        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        settings = this.getSharedPreferences("Login", 0);

        /*Define XML Elements*/
        btn_Signup = (Button) findViewById(R.id.btn_Signup);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btn_back = (Button) findViewById(R.id.btn_back);
        inputFirstName = (EditText) findViewById(R.id.first_name);
        inputSecondName = (EditText) findViewById(R.id.second_name);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        /*Set Listener for Signup Button*/
        btn_Signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                /*Retrieve Text from Fields*/
                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();
                firstName = inputFirstName.getText().toString().trim();
                secondName = inputSecondName.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter a Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), "Enter a Stronger Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "Email: " + email);
                if(!email.substring(email.indexOf("@"), email.length()).equals("@mail.dcu.ie")){
                    Toast.makeText(getApplicationContext(), "Enter a DCU Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                /*Create a User*/
                createUser(email, password);
                saveLogin(email, password);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void createUser(String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "Successfully Created: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            try {
                                Toast.makeText(SignupActivity.this, "Error Signing Up", Toast.LENGTH_SHORT).show();
                            }
                            catch(Exception e){
                                Log.e("SignupActivity", "Failed to Create User");
                            }
                        }
                        else {
                            saveUserProfile();
                            startActivity(new Intent(SignupActivity.this, SigninActivity.class));
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private void saveLogin(String email, String password){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.apply();
    }

    /*Add the User to a Database*/
    public void saveUserProfile(){
        UserProfile userProfile = new UserProfile(firstName, secondName, email, 0, 0, 10);
        HashMap<String, Object> children = new HashMap<>();
        if(auth.getCurrentUser() != null) {
            children.put(auth.getCurrentUser().getUid(), userProfile.toMap());
            userRef.updateChildren(children);
            Log.d(TAG, "USER PROFILE SAVED TO FIREBASE");
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        btn_Signup.setOnClickListener(null);
        btn_back.setOnClickListener(null);
    }
}
