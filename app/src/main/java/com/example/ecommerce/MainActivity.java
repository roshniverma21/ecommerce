package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinnowbutton, loginbutton;
    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinnowbutton= findViewById(R.id.main_join_now_btn);     //intialised
        loginbutton= findViewById(R.id.main_login_btn);
        loadingbar=new ProgressDialog(this);

        Paper.init(this);//initialised paper dependencies too store data

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent =new Intent(MainActivity.this,loginActivity.class);      //to send user to login activity
                startActivity(intent);
            }
        });
        joinnowbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,registerActivity.class);      //to send user to register activity
                startActivity(intent);

            }
        });
        //to retrieve the users data
        String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);

        if(UserPhoneKey!="" && UserPasswordKey!="")//we have used "" (null) to check if their is nothing empty
        {
            if(!TextUtils.isEmpty(UserPasswordKey)  && !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserPhoneKey,UserPasswordKey);//method created

                loadingbar.setTitle("Already Logged In");
                loadingbar.setMessage("Please wait.");
                loadingbar.setCanceledOnTouchOutside(false);// if by mistake user touch anywhere on screen it will not be cancelled
                loadingbar.show();
            }
        }
    }
    private void AllowAccess(final String phone, final String password)
    {


        final DatabaseReference Rootref;// create database
        Rootref = FirebaseDatabase.getInstance().getReference();  //to take data to check in firebase database


        // for showing data in database in treee form
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists())// here we will be saving data of user so we will be making a new package name new
                {
                    Users usersdata=dataSnapshot.child("Users").child(phone).getValue(Users.class);//to pass the data by users class
                    //after passing data is retrived by getter and setter
                    //now we want only phn and passwrd so to confirm it we do it
                    if(usersdata.getPhone().equals(phone))//matching of phone at register and login activity
                    {
                        if (usersdata.getPassword().equals(password)) //if phone num matches then check for password
                        { //if paswrd matches with the data in database then allow user to enter into accnt
                            Toast.makeText(MainActivity.this,"You are already logged in", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();

                            Intent intent =new Intent(MainActivity.this,HomePageActivity.class);      //to send user from login activity to homepage
                            Prevalent.CurrentOnlineUser = usersdata;
                            startActivity(intent);
                        }
                        else {
                            loadingbar.dismiss();
                            Toast.makeText(MainActivity.this, "Passsword Incorrect", Toast.LENGTH_SHORT).show();

                        }
                    }
                } else {

                    Toast.makeText(MainActivity.this, "Account with this" + phone + "number do not exist.", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(MainActivity.this, "You need to create a new account. ", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


