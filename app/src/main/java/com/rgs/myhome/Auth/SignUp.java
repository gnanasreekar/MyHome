package com.rgs.myhome.Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rgs.myhome.MainActivity;
import com.rgs.myhome.R;

import java.util.Date;

public class SignUp extends AppCompatActivity {

    public EditText emailId;
    public EditText password;
    public EditText username;
    Button buttom_signup;
    TextView signIn;
    FirebaseAuth firebaseAuth;
    LinearLayout lyt_progress, signup;
    ProgressBar progressBar;
    String name;
    CharSequence s;
    Button signInButton;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        setTitle("SignUp");

        firebaseAuth = FirebaseAuth.getInstance();

        {
            emailId = findViewById(R.id.email_signup);
            username = findViewById(R.id.username_signup);
            password = findViewById(R.id.password_signup);
            buttom_signup = findViewById(R.id.button_signup);
            signIn = findViewById(R.id.signin_signup);
            signup = findViewById(R.id.signup_layout);
            signInButton = findViewById(R.id.googlesignin_signup);


        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Googlesignin();
            }
        });


        buttom_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lyt_progress.setVisibility(View.VISIBLE);
                lyt_progress.setAlpha(1.0f);
                signup.setVisibility(View.GONE);
                final String emailID = emailId.getText().toString();
                String paswd = password.getText().toString();
                name = username.getText().toString();

                if (emailID.isEmpty()) {
                    username.setError("Set your Username");
                    username.requestFocus();
                } else if (name.isEmpty()) {
                    emailId.setError("Provide your Email first!");
                    emailId.requestFocus();
                } else if (paswd.isEmpty()) {
                    password.setError("Set your password");
                    password.requestFocus();
                } else if (!(emailID.isEmpty() && paswd.isEmpty() && name.isEmpty())) {
                    firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUp.this.getApplicationContext(),
                                        "SignUp unsuccessful: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                task.getResult().getUser().updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("Hekkfdf", "User profile updated.");
                                                }
                                            }
                                        });


                                //Date
                                Date d = new Date();
                                s = DateFormat.format("MMMM d, yyyy HH:mm:ss", d.getTime());

                                //Storing data to display in the Nav bar and in the app
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sp", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("uid", firebaseAuth.getUid());
                                editor.putString("name", name);
                                editor.putString("email", emailID);
                                editor.apply();

                                //To save data in Firebase Database
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + firebaseAuth.getUid());
                                databaseReference.child("Name").setValue(name);
                                databaseReference.child("Email").setValue(emailID);
                                databaseReference.child("UID").setValue(firebaseAuth.getUid());
                                databaseReference.child("Date").setValue(s);
                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                intent.putExtra("uid", firebaseAuth.getUid());
                                startActivity(intent);

                            }
                        }
                    });
                } else {
                    Toast.makeText(SignUp.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, Login.class));
            }
        });


    }

    private void Googlesignin() {
        Intent signin = googleSignInClient.getSignInIntent();
        startActivityForResult(signin, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handelsignin(task);
        }
    }

    private void handelsignin(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            firebaseauth(acc);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void firebaseauth(GoogleSignInAccount o) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(o.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {

//            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (user != null) {

            sharedPreferences = getApplicationContext().getSharedPreferences("sp", 0);
            editor = sharedPreferences.edit();
            editor.putString("uid", user.getUid());
            editor.putString("name", user.getDisplayName());
            editor.putString("email", user.getEmail());
            editor.apply();

            //To save data in Firebase Database

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + firebaseAuth.getUid());
            databaseReference.child("Name").setValue(user.getDisplayName());
            databaseReference.child("Email").setValue(user.getEmail());
            databaseReference.child("UID").setValue(user.getUid());

            Intent intent = new Intent(SignUp.this, MainActivity.class);
            intent.putExtra("uid", user.getUid());
            startActivity(intent);
            finish();


        } else {
            Toast.makeText(this, "Login to continue", Toast.LENGTH_SHORT).show();
        }
    }
}
