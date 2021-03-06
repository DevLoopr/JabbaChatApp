package org.codekidd.jabbachatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterSignUpActivity extends AppCompatActivity {
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;
    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolbar;

    private ProgressDialog mRegProgress;
//    to store more fields at a time instead of just one add the below
    private DatabaseReference mDatabase;

    private DatabaseReference mUserDatabase;




//============================GITHUB @Dcodekidd========2018==========BINGHAM UNIVERSITY===========================

//written by Nyako Epahraim Alhamdu 12th March 2018
//    GITHUB @Dcodekidd
// START onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sign_up);

        mToolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mDisplayName = (TextInputLayout)findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout)findViewById(R.id.reg_email);
        mPassword = (TextInputLayout)findViewById(R.id.reg_password);
        mCreateBtn = (Button)findViewById(R.id.reg_create_btn);

//        setting listener for mCreateBtn
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                when users click the button I want to retrieve the data from the TextInputLayout fields and register to the firebase database
                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    register_user(display_name, email, password);

                }


            }
        });



    } //END onCreate

//              ================REGISTER USER METHOD===============================================
//Register new user method START register_user()
    private void register_user(final String display_name, final String email, String password) {

        //               TASKS TO CHECK IF EMAIL, PASSWORD FIELDS ARE EMPTY? ALSO IF PASSWORD LENGTH IS TOO SHORT ETC


//        check for empty User name Field
        if (display_name.isEmpty()){
            mDisplayName.setError("Username is required!");
            mDisplayName.requestFocus();
            return;
        }

//        check for empty Email Field!
        if (email.isEmpty()){
            mEmail.setError("Email is required!");
            mEmail.requestFocus();
            return;
        }

//        check for Valid Email Address
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmail.setError("Please enter a valid Email Address");
            mEmail.requestFocus();
            return;
        }

//        check for empty Password Field
        if (password.isEmpty()){
            mPassword.setError("Password is required!");
            mPassword.requestFocus();
            return;
        }
//        set notice for minimum password to be 6 as in firebase auth documentation
        if (password.length()< 6){
            mPassword.setError("Password too short! Minimum length of password is 6");
            mPassword.requestFocus();
            return;
        }



        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {



//               check if task is successful
               if (task.isSuccessful()){


//                   ================================UID=====================================
//                   create a firebase user the current user
                   FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
//                   getting the user ID UID
                   String uid = current_user.getUid();
//                   after getting the uid the data can now be stored accordingly in the firebase raltime database
//                   the below points to the root dir
                   mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
//                   now storing the values for the user To add complex data im creating a HashMap


                   String device_token = FirebaseInstanceId.getInstance().getToken();


//                   ==========================HASHMAP=======================================
                   HashMap<String,String> userMap = new HashMap<>();
                   userMap.put("name",display_name);
                   userMap.put("email",email);
                   userMap.put("status","Hey there! I'm using jabbaChat App.");
                   userMap.put("bio","my occupation");
                   userMap.put("hobbies","my hobbies");
                   userMap.put("image","default");
                   userMap.put("thumb_image","default");

                   userMap.put("device_token", device_token);

//                   now to set our values
                   mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
//                           check if it does the work
                           if (task.isSuccessful()) {

                               mRegProgress.dismiss();

                               Intent mainIntent = new Intent(RegisterSignUpActivity.this, MainActivity.class);
                               mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(mainIntent);
                               finish();




                           }
                       }
                   });




               } else {
                   mRegProgress.hide();
//                   Error user is not registered successfully
                   Toast.makeText(RegisterSignUpActivity.this,"Oops! something went wrong, Please check and try again", Toast.LENGTH_LONG).show();

               }
           }
       });

    } //END register_user()



}

