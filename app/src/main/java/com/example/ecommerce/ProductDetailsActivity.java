package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecommerce.Model.Products;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
    private Button addToCartButton;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView ProductPrice,ProductDescription,ProductName;
    private String productID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        productID= getIntent().getStringExtra("pid");// will receive id from the homepage

        ProductPrice=findViewById(R.id.product_price_details);
        ProductDescription=findViewById(R.id.product_description_details);
        ProductName=findViewById(R.id.product_name_details);
        numberButton=findViewById(R.id.number_btn);
        productImage=findViewById(R.id.product_image_details);
        addToCartButton=findViewById(R.id.pd_add_to_cart_button);

        getProductDetails(productID);

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToCartList();

            }
        });
    }
    private void addingToCartList()
    {
        String saveCurrentTime,saveCurrentDate;
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime =new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentDate.format(callForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object> cartMap =new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",ProductName.getText().toString());
        cartMap.put("price",ProductPrice.getText().toString());
        cartMap.put("time",saveCurrentTime);
        cartMap.put("date",saveCurrentDate);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");//null

        //store it
        cartListRef.child("User View").child(Prevalent.CurrentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    cartListRef.child("Admin View").child(Prevalent.CurrentOnlineUser.getPhone())
                            .child("Products").child(productID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ProductDetailsActivity.this,"Added to Cart List",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(ProductDetailsActivity.this,HomePageActivity.class);
                            startActivity(intent);
                    }
                    });
                }
            }
        });

    }
    private void getProductDetails(String productID)
    {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Products products=dataSnapshot.getValue(Products.class);
                    ProductName.setText(products.getPname());
                    ProductDescription.setText(products.getDescription());
                    ProductPrice.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(productImage);










                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
