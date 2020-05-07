package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText nameEdtText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;

    private String totalamount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalamount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "total Price= $ " + totalamount, Toast.LENGTH_SHORT).show();

        confirmOrderBtn = findViewById(R.id.confirm_final_order_btn);
        nameEdtText = findViewById(R.id.shipment_name);
        phoneEditText = findViewById(R.id.shipment_phone);
        addressEditText = findViewById(R.id.shipment_address);
        cityEditText = findViewById(R.id.shipment_city);

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });


    }

    private void Check() {
        if (TextUtils.isEmpty(nameEdtText.getText().toString())) {
            Toast.makeText(this, "Please Provide your full Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(nameEdtText.getText().toString())) {
            Toast.makeText(this, "Please Provide your full Phone Number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(nameEdtText.getText().toString())) {
            Toast.makeText(this, "Please Provide your full Address.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(nameEdtText.getText().toString())) {
            Toast.makeText(this, "Please Provide your full City Name.", Toast.LENGTH_SHORT).show();
        }
        else {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder()
    {
        String saveCurrentTime,saveCurrentDate;
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime =new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentDate.format(callForDate.getTime());
        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.CurrentOnlineUser.getPhone());

        HashMap<String,Object> ordersMap=new HashMap<>() ;
        ordersMap.put("Total Amount",totalamount);
        ordersMap.put("name",nameEdtText.getText().toString());
        ordersMap.put("phone",phoneEditText.getText().toString());
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("address",addressEditText.getText().toString());
        ordersMap.put("city",cityEditText.getText().toString());
        ordersMap.put("state","not shipped");

        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.CurrentOnlineUser.getPhone()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful())
                   {
                       Toast.makeText(ConfirmFinalOrderActivity.this,"your final order has been placed successfully",Toast.LENGTH_SHORT).show();
                       Intent intent=new Intent(ConfirmFinalOrderActivity.this,HomePageActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//to clear the orders placed
                       startActivity(intent);
                       finish();
                   }
                    }
                });

                }
            }
        });
        }
}

