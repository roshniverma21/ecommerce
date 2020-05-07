package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;
import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity {
private Button LoginButton;
private EditText InputPhoneNumber,InputPassword;
private ProgressDialog loadingbar;
private String parentDbName="Users";//created so that this data doesnot get stored in users (login data)
public CheckBox chkBoxRememberMe;
private TextView AdminLink, NotAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        LoginButton = findViewById(R.id.login_btn);
        InputPhoneNumber = findViewById(R.id.login_phn_num_input);
        InputPassword = findViewById(R.id.login_password_input);
        AdminLink = findViewById(R.id.admin_panel_link);
        NotAdminLink = findViewById(R.id.not_admin_panel_link);

        loadingbar = new ProgressDialog(this);

       chkBoxRememberMe = findViewById(R.id.remember_me);
        Paper.init(this);//to initialise the paper dependendies


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }

        });

    // for admin clickability and its visibility and invisibility
        AdminLink.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            LoginButton.setText("Login Admin");//when you enters admin page
            AdminLink.setVisibility(View.INVISIBLE);//for invisible
            NotAdminLink.setVisibility(View.VISIBLE);//for visible
            parentDbName = "Admins";//the value will be then for admins
        }
    });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            LoginButton.setText("Login");
            AdminLink.setVisibility(View.VISIBLE);
            NotAdminLink.setVisibility(View.INVISIBLE);
            parentDbName = "Users";//if doesnot go to admin page then save in users page only
        }
    });
}
    private void LoginUser()
    {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        if (TextUtils.isEmpty(phone))
        {
         Toast.makeText(this, "Please write your Phone Number", Toast.LENGTH_SHORT).show();
        }
    else if (TextUtils.isEmpty(password))
         {
        Toast.makeText(this, "Please write your Password", Toast.LENGTH_SHORT).show();
         }
    else {
            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("Please wait, while we are checking the credentials");
            loadingbar.setCanceledOnTouchOutside(false);// if by mistake user touch anywhere on screen it will not be cancelled
            loadingbar.show();
            AllowAccessToAccount(phone,password);
        }
    }
    private void AllowAccessToAccount(final String phone, final String password) {


        if(chkBoxRememberMe.isChecked())//to check whether checkbox will store value or not is checked returns T/F so if it is ticked it will store values
        {
           Paper.book().write(Prevalent.UserPhoneKey,phone);//store phn number in class forusers and pass to phn memory
            Paper.book().write(Prevalent.UserPasswordKey,password);//store passwrd in class forusers
        }




        final DatabaseReference Rootref;// create database
        Rootref = FirebaseDatabase.getInstance().getReference();  //to take data to check in firebase database


        // for showing data in database in treee form
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists())// here we will be saving data of user so we will be making a new package name new
                {
                    Users usersdata = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);//to pass the data by foruses class
                    //ater passing data is retrived by getter and setter
                    //now we want only phn and passwrd so to confirm it we do it
                    if (usersdata.getPhone().equals(phone))//matching of phone at register and login activity
                    {
                        if (usersdata.getPassword().equals(password)) //if phone num matches then check for password
                        { //if paswrd matches with the data in database then allow user to enter into accnt
                            if (parentDbName.equals("Admins"))//if admin logged in
                            //keep value same as on firebase otherwise app crashes
                            {
                                Toast.makeText(loginActivity.this, "welcome!!Successfully Logged in", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();

                                Intent intent = new Intent(loginActivity.this, AdminCategoryActivity.class);//to send admin from login activity to adminaddproducts
                                startActivity(intent);
                            } else if (parentDbName.equals("Users"))//if user logged in
                            {
                                Toast.makeText(loginActivity.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();

                                Intent intent = new Intent(loginActivity.this, HomePageActivity.class);//to send user from login activity to homepage
                                Prevalent.CurrentOnlineUser = usersdata;//access data
                                startActivity(intent);
                            }
                        } else {
                            loadingbar.dismiss();
                            Toast.makeText(loginActivity.this, "Passsword Incorrect", Toast.LENGTH_SHORT).show();

                        }
                    }
                } else {
                    Toast.makeText(loginActivity.this, "Account with this" + phone + "number do not exist.", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

