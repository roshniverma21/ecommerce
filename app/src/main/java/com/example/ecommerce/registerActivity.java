package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.GenericArrayType;
import java.util.HashMap;

public class registerActivity extends AppCompatActivity {
     private Button CreateAccbtn;
     private EditText InputName, InputPhoneNumber, Inputpassword;
     private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccbtn = findViewById(R.id.register_create_acc_btn);
        InputName = findViewById(R.id.register_name_input);
        InputPhoneNumber = findViewById(R.id.register_phn_num_input);
        Inputpassword = findViewById(R.id.register_password_input);
        loadingbar=new ProgressDialog(this);

        CreateAccbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();   //method created
            }
        });
    }

    private void CreateAccount()    //method defined
    {
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = Inputpassword.getText().toString();

        //to check whether anything from them is empty or not
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please write your Name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please write your Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your Password", Toast.LENGTH_SHORT).show();
        }
        else
            {
                loadingbar.setTitle("Create Account");
                loadingbar.setMessage("Please wait, while we are checking the credentials");
                loadingbar.setCanceledOnTouchOutside(false);// if by mistake user touch anywhere on screen it will not be cancelled
                loadingbar.show();
                //to check whether provided number is already in database or not if it is not then only accnt will be created
                ValidatephoneNumber(name,phone,password);
            }
    }
    // create method to validate phone number in database so add dependency also before making method
    private void ValidatephoneNumber(final String name, final String phone, final String password)
    {
        final DatabaseReference Rootref;// create database
        Rootref= FirebaseDatabase.getInstance().getReference();  //to take data to check in firebase database
        Rootref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //to check no. to be unique if it already exist then pop up comes that you cant create accnt by this num

                //if you cant create
                if (!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password",password);
                    userdataMap.put("name", name);
                    Rootref.child("Users").child(phone).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(registerActivity.this, "Congratulations,your account has been created", Toast.LENGTH_SHORT).show();//when accnt created
                                loadingbar.dismiss();
                                Intent intent =new Intent(registerActivity.this,loginActivity.class);      //to send user to login activity
                                startActivity(intent);
                            }
                            else
                            {
                                loadingbar.dismiss();
                                Toast.makeText(registerActivity.this,"Network error:Please try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                    {
                    Toast.makeText(registerActivity.this, "This" + phone + "already exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(registerActivity.this, "Please try again using another phone number", Toast.LENGTH_SHORT).show();//for suggestion
                    Intent intent = new Intent(registerActivity.this, MainActivity.class);      //to send user to login activity
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}














