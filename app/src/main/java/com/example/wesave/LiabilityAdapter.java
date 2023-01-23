package com.example.wesave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;
import org.joda.time.Years;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class LiabilityAdapter extends RecyclerView.Adapter<LiabilityAdapter.ViewHolder>{

    private Context lContext;
    private List<LiabilityData> myLiabilityList;

    private String post_key = "";
    private String item = "";
    private String note = "";
    private int amount = 0;
    //private int years_own =0;

    public LiabilityAdapter(Context lContext, List<LiabilityData> myLiabilityList) {
        this.lContext = lContext;
        this.myLiabilityList = myLiabilityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(lContext).inflate(R.layout.liability_retrieve_layout, parent, false);
        return new LiabilityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final LiabilityData liabilityData = myLiabilityList.get(position);

        holder.item.setText("Liability Type: " + liabilityData.getLiabilityItem());
        holder.amount.setText("Amount: RM" + liabilityData.getLiabilityAmount());
        holder.date.setText("Date: " + liabilityData.getLiabilityDate());
        holder.note.setText("Note: " + liabilityData.getLiabilityNotes());


        switch (liabilityData.getLiabilityItem()){
            case "Accounts Payable":
                holder.imageView.setImageResource(R.drawable.account_payable);
                break;

            case "Credit Card Bills":
                holder.imageView.setImageResource(R.drawable.credit_card);
                break;

            case "Loans":
                holder.imageView.setImageResource(R.drawable.loans);
                break;

            case "Mortgages":
                holder.imageView.setImageResource(R.drawable.mortgage);
                break;

            case "Student Loans":
                holder.imageView.setImageResource(R.drawable.student_loans);
                break;

            case "Taxes":
                holder.imageView.setImageResource(R.drawable.tax);
                break;

            case "Other":
                holder.imageView.setImageResource(R.drawable.ic_baseline_other);
                break;

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key = liabilityData.getLiabilityID();
                item = liabilityData.getLiabilityItem();
                amount = liabilityData.getLiabilityAmount();
                note = liabilityData.getLiabilityNotes();
                updateData();
            }
        });

    }

    private void updateData() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(lContext);
        LayoutInflater inflater = LayoutInflater.from(lContext);
        View aView = inflater.inflate(R.layout.liability_update_layout, null);

        myDialog.setView(aView);
        final AlertDialog dialog = myDialog.create();

        final TextView lItem = aView.findViewById(R.id.liabilityName);
        final EditText lAmount = aView.findViewById(R.id.lia_amount);
        final EditText lNote = aView.findViewById(R.id.lia_note);

        lNote.setText(note);
        lNote.setSelection(note.length());
        lNote.setText(note);

        lAmount.setText(String.valueOf(amount));
        lAmount.setSelection(String.valueOf(amount).length());

        Button btn_liability_delete = aView.findViewById(R.id.btn_liabilityDelete);
        Button btn_liability_update = aView.findViewById(R.id.btn_liabilityUpdate);

        btn_liability_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                amount = Integer.parseInt(lAmount.getText().toString());
                note = lNote.getText().toString();

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calendar = Calendar.getInstance();
                String liabilityDate = dateFormat.format(calendar.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Years years = Years.yearsBetween(epoch, now);

                String liabilityItemNdays = item+liabilityDate;
                String liabilityItemNyears = item+years.getYears();

                LiabilityData liabilityData = new LiabilityData(item, liabilityDate, post_key, liabilityItemNdays, liabilityItemNyears, amount,
                        years.getYears(),note);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Liability").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                reference.child(post_key).setValue(liabilityData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(lContext,"Updated successfully",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(lContext,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();
            }
        });

        btn_liability_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Liability").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(lContext,"Delete successfully",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(lContext,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();

            }
        });

        dialog.show();


    }

    @Override
    public int getItemCount() {
        return myLiabilityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView item, amount, date, note;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            note = itemView.findViewById(R.id.note);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}
