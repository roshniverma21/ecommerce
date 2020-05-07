package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextBtn;
    private TextView txtTotalAmount,txtmsg1;
    private int overalltotalprice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        NextBtn = findViewById(R.id.next_btn);
        txtTotalAmount = findViewById(R.id.total_price);
        txtmsg1=findViewById(R.id.msg1);

        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {   txtTotalAmount.setText("Total price=$" + String.valueOf(overalltotalprice));
                Intent intent=new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overalltotalprice));//for conversion of int value to string
                startActivity(intent);
                finish();
            }
        });

    }

    protected void onStart() {
        super.onStart();
        txtTotalAmount.setText("Total price=$" + String.valueOf(overalltotalprice));

        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options=new FirebaseRecyclerOptions.Builder<Cart>().
                setQuery(cartListRef.child("User View").child(Prevalent.CurrentOnlineUser.getPhone()//cartlist ref node   userview node then product node and inside all products added by user to cart list
                ).child("Products"),Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull Cart cart)
            {
              cartViewHolder.txtProductQuantity.setText("Quantity= " + cart.getQuantity());//to show the items purchased for checkout
              cartViewHolder.txtProductPrice.setText("Price" + cart.getPrice() + "$");
              cartViewHolder.txtProductName.setText(cart.getPname());
              int onetypeproductprice =((Integer.valueOf(cart.getPrice())))* Integer.valueOf((cart.getQuantity()));//to calculate total payment
              overalltotalprice=overalltotalprice+onetypeproductprice;


              cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view)
                  {
                      CharSequence options[]= new CharSequence[]
                              {
                                      "Edit ",//index 0
                                      "Remove"//index 1
                              };
                      AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                      builder.setTitle("Cart Options:");//dialog box tittle

                      builder.setItems(options, new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i)
                          {
                              if (i==0)//for edit
                              {
                                  Intent intent=new Intent(CartActivity.this,ProductDetailsActivity.class);
                                  intent.putExtra("pid",cart.getPid());
                                  startActivity(intent);

                              }
                              if(i==1)//for remove
                              {
                                  cartListRef.child("User View").child(Prevalent.CurrentOnlineUser.getPhone()).child("Products").
                                          child(cart.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful())
                                     {
                                         Toast.makeText(CartActivity.this,"Item removed",Toast.LENGTH_SHORT).show();
                                         Intent intent=new Intent(CartActivity.this,HomePageActivity.class);
                                         startActivity(intent);
                                     }
                                      }
                                  });
                              }
                          }
                      });
                      builder.show();
                  }
              });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.carts_items_layout,parent,false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private void CheckOrderState()
    {
        DatabaseReference ordersRef;
                ordersRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.CurrentOnlineUser.getPhone());
                ordersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String shippingstate = dataSnapshot.child("state").getValue().toString();
                            String username = dataSnapshot.child("name").getValue().toString();

                            if (shippingstate.equals("shipped"))
                            {
                                txtTotalAmount.setText("Dear" + username + "\n order is shipped successfully.");
                                recyclerView.setVisibility(View.GONE);
                                txtmsg1.setVisibility(View.VISIBLE);
                                NextBtn.setVisibility(View.GONE);
                                Toast.makeText(CartActivity.this,"You can purchase more orders once you received your first final order",Toast.LENGTH_SHORT).show();
                            }
                            else if (shippingstate.equals("not shipped"))
                            {
                                txtTotalAmount.setText("Shippind state = not Shipped" );
                                recyclerView.setVisibility(View.GONE);
                                txtmsg1.setVisibility(View.VISIBLE);
                                NextBtn.setVisibility(View.GONE);
                                Toast.makeText(CartActivity.this,"You can purchase more orders once you received your first final order",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}