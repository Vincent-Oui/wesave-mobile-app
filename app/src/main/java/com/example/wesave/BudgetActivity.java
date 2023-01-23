package com.example.wesave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.local.QueryResult;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.zip.Inflater;

public class BudgetActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView totalBudgetAmountTextView;
    private RecyclerView budgetRecyclerView;

    private FloatingActionButton fab;

    private DatabaseReference databaseReference, personalRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;

    private String post_key = "";
    private String item = "";
    private int amount = 0;

    Button btn_budget_delete, btn_budget_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget2);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Budget").child(mAuth.getCurrentUser().getUid());
        personalRef = FirebaseDatabase.getInstance().getReference().child("Personal").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        budgetRecyclerView = findViewById(R.id.budgetRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        budgetRecyclerView.setHasFixedSize(true);
        budgetRecyclerView.setLayoutManager(linearLayoutManager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Budget");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;


                for (DataSnapshot snap: snapshot.getChildren()){
                    BudgetData budgetData = snap.getValue(BudgetData.class);
                    totalAmount += budgetData.getBudgetAmount();
                    String sTotal = String.valueOf("Month Budget: RM" + totalAmount);
                    totalBudgetAmountTextView.setText(sTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                additem();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    int totalamount = 0;

                    for (DataSnapshot snap : snapshot.getChildren()){

                        BudgetData budgetData = snap.getValue(BudgetData.class);

                        totalamount += budgetData.getBudgetAmount();

                        String sttotal = String.valueOf("My Budget: RM" + totalamount);

                        totalBudgetAmountTextView.setText(sttotal);
                    }
                    int weeklyBudget = totalamount/4;
                    int dailyBudget = totalamount/30;
                    personalRef.child("budgetPersonal").setValue(totalamount);
                    personalRef.child("weeklyBudget").setValue(weeklyBudget);
                    personalRef.child("dailyBudget").setValue(dailyBudget);


                }else {
                    personalRef.child("budgetPersonal").setValue(0);
                    personalRef.child("weeklyBudget").setValue(0);
                    personalRef.child("dailyBudget").setValue(0);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void additem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        Spinner itemSpinner = myView.findViewById(R.id.budgetSpinner);
        EditText budgetAmount = myView.findViewById(R.id.budgetAmount);
        Button budgetCancel = myView.findViewById(R.id.budgetCancel);
        Button budgetSave = myView.findViewById(R.id.budgetSave);

        budgetSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newBudgetAmount = budgetAmount.getText().toString();
                String newBudgetItem = itemSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(newBudgetAmount)) {

                    budgetAmount.setError("Amount is required");
                    return;
                }

                if (newBudgetItem.equals("Select Item")){

                    Toast.makeText(BudgetActivity.this, "Select a valid item",Toast.LENGTH_SHORT).show();
                }

                else {

                    loader.setMessage("Adding a budget  item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String budgetId = databaseReference.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar calendar = Calendar.getInstance();
                    String budgetDate = dateFormat.format(calendar.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch, now);
                    Months months = Months.monthsBetween(epoch,now);

                    String budgetItemNdays = newBudgetItem+budgetDate;
                    String budgetItemNweeks = newBudgetItem+weeks.getWeeks();
                    String budgetItemNmonths = newBudgetItem+months.getMonths();


                    BudgetData budgetData = new BudgetData(newBudgetItem, budgetDate, budgetId,null, budgetItemNdays, budgetItemNweeks, budgetItemNmonths,
                            Integer.parseInt(newBudgetAmount), months.getMonths(), weeks.getWeeks());

                    databaseReference.child(budgetId).setValue(budgetData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(BudgetActivity.this,"Added successfully",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BudgetActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }

                            loader.dismiss();

                        }
                    });
                }

                dialog.dismiss();
            }
        });

        budgetCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<BudgetData> options = new FirebaseRecyclerOptions.Builder<BudgetData>()
                .setQuery(databaseReference, BudgetData.class).build();

        FirebaseRecyclerAdapter<BudgetData, MyViewHolder> badapter = new FirebaseRecyclerAdapter<BudgetData, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull BudgetData model) {

                holder.setItemAmount("Allocated Amount: RM" +model.getBudgetAmount());
                holder.setItemDate("Date: " + model.getBudgetDate());
                holder.setItemName("Budget Item: " + model.getBudgetItem());

                holder.note.setVisibility(View.GONE);

                switch (model.getBudgetItem()){
                    case "Transport":
                        holder.imageView.setImageResource(R.drawable.ic_baseline_transport);
                        break;

                    case "Food":
                        holder.imageView.setImageResource(R.drawable.ic_baseline_food);
                        break;

                    case "House":
                        holder.imageView.setImageResource(R.drawable.ic_baseline_house_24);
                        break;

                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.ic_baseline_entertainment);
                        break;

                    case "Education":
                        holder.imageView.setImageResource(R.drawable.ic_baseline_education);
                        break;

                    case "Charity":
                        holder.imageView.setImageResource(R.drawable.ic_baseline_charity);
                        break;

                    case "Apparel":
                        holder.imageView.setImageResource(R.drawable.ic_baseline_apparel);
                        break;

                    case "Health":
                        holder.imageView.setImageResource(R.drawable.ic_baseline_local_health);
                        break;

                    case "Personal":
                        holder.imageView.setImageResource(R.drawable.ic_baseline_personal);
                        break;

                    case "Other":
                        holder.imageView.setImageResource(R.drawable.ic_baseline_other);
                        break;

                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        item = model.getBudgetItem();
                        amount = model.getBudgetAmount();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new MyViewHolder(view);
            }
        };

        budgetRecyclerView.setAdapter(badapter);
        badapter.startListening();
        badapter.notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mView, aView;
        public ImageView imageView;
        public TextView note, date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            note = itemView.findViewById(R.id.note);
            date = itemView.findViewById(R.id.date);
        }

        public void setItemName (String itemName){
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount (String itemAmount){
            TextView amount = mView.findViewById(R.id.amount);
            amount.setText(itemAmount);
        }

        public void setItemDate (String itemDate){
            TextView date = mView.findViewById(R.id.date);
            date.setText(itemDate);
        }


    }

    private void updateData(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.budget_update_layout, null);

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();

        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNote = mView.findViewById(R.id.note);

        mNote.setVisibility(View.GONE);

        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        btn_budget_delete = mView.findViewById(R.id.btn_budgetDelete);
        btn_budget_update = mView.findViewById(R.id.btn_budgetUpdate);

        btn_budget_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                amount = Integer.parseInt(mAmount.getText().toString());

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calendar = Calendar.getInstance();
                String budgetDate = dateFormat.format(calendar.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks = Weeks.weeksBetween(epoch, now);
                Months months = Months.monthsBetween(epoch,now);

                String budgetItemNdays = item+budgetDate;
                String budgetItemNweeks = item+weeks.getWeeks();
                String budgetItemNmonths = item+months.getMonths();

                BudgetData budgetData = new BudgetData(item, budgetDate, post_key,null, budgetItemNdays, budgetItemNweeks, budgetItemNmonths, amount,
                        months.getMonths(), weeks.getWeeks());

                databaseReference.child(post_key).setValue(budgetData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(BudgetActivity.this,"Updated successfully",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();
            }
        });

        btn_budget_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(BudgetActivity.this,"Delete successfully",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();

            }
        });

        dialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.account){
            Intent intent = new Intent(BudgetActivity.this, AccountActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == android.R.id.home){

            finish();
            Intent intent = new Intent(BudgetActivity.this, Budget.class);
            startActivity(intent);

        }



        return super.onOptionsItemSelected(item);
    }



}