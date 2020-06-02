package com.rgs.myhome.Auth;

/*
  Developed by : R.Gnana Sreekar
  Github : https://github.com/gnanasreekar
  Linkdin : https://www.linkedin.com/in/gnana-sreekar/
  Instagram : https://www.instagram.com/gnana_sreekar/
  Website : https://gnanasreekar.com
*/


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.rgs.myhome.MainActivity;
import com.rgs.myhome.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    public EditText login_username, login_password;
    Button button_login;
    TextView signup, logintext;
    FirebaseAuth firebaseAuth;
    LinearLayout linearLayout,lyt_progress, no_net;
    String userEmail;
    Button signInButton;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean dataexist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();


        firebaseAuth = FirebaseAuth.getInstance();
        login_username = findViewById(R.id.username);
        login_password = findViewById(R.id.password);
        button_login = findViewById(R.id.button_login);
        signup = findViewById(R.id.signup);
        linearLayout = findViewById(R.id.login);
        lyt_progress = (LinearLayout) findViewById(R.id.login_loading);
        lyt_progress.setVisibility(View.GONE);
        logintext = findViewById(R.id.login_text);

        signInButton = findViewById(R.id.googlesignin);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataexist = true;
                Googlesignin();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(Login.this, SignUp.class);
                startActivity(I);
                finish();
            }
        });
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEmail = login_username.getText().toString();
                String userPaswd = login_password.getText().toString();

                if (userEmail.isEmpty()) {
                    login_username.setError("Provide your Email first!");
                    login_username.requestFocus();
                } else if (userPaswd.isEmpty()) {
                    login_password.setError("Enter Password!");
                    login_password.requestFocus();
                } else if (userEmail.isEmpty() && userPaswd.isEmpty()) {
                    Toast.makeText(Login.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(userEmail.isEmpty() && userPaswd.isEmpty())) {
                    firebaseAuth.signInWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                lyt_progress.setVisibility(View.VISIBLE);
                                lyt_progress.setAlpha(1.0f);
                                linearLayout.setVisibility(View.GONE);
                                updateUI(task.getResult().getUser());

                            }
                        }
                    });
                } else {
                    Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        login_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    button_login.performClick();
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        showexitDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void Googlesignin() {
        Intent signin = googleSignInClient.getSignInIntent();
        startActivityForResult(signin , RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
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

        firebaseAuth.fetchSignInMethodsForEmail("dgdfgdfs@gmail.com").addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethods = result.getSignInMethods();

                    Log.d("TAG212", "onComplete: " + signInMethods);

                    if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                        // User can sign in with email/password
                    } else if (signInMethods.contains(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD)) {
                        // User can sign in with email/link
                    }
                } else {
                    Log.e("TAG1312", "Error getting sign in methods for user", task.getException());
                }
            }
        });

        AuthCredential authCredential = GoogleAuthProvider.getCredential(o.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
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

            //To save data in Firebase Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + firebaseAuth.getUid());
            databaseReference.child("Name").setValue(user.getDisplayName());
            databaseReference.child("Email").setValue(user.getEmail());
            databaseReference.child("UID").setValue(firebaseAuth.getUid());
            editor.apply();

            startActivity(new Intent(Login.this, MainActivity.class));
            finish();


        } else {
            Toast.makeText(this, "Login to continue", Toast.LENGTH_SHORT).show();
        }
    }

    //Forgot password Dialog
    public void forgotpassword(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.forgotpass);
        dialog.setCancelable(true);

        final EditText email = dialog.findViewById(R.id.passemail);
        email.setText(userEmail);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Please Check your mail!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    //Exit Dialog
    private void showexitDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.exitdialog);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //  System.exit(0);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
