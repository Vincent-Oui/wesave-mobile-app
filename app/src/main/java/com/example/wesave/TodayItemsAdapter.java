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
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodayItemsAdapter extends RecyclerView.Adapter<TodayItemsAdapter.ViewHolder>{

    private Context mContext;
    private List<BudgetData> myDataList;

    private String post_key = "";
    private String item = "";
    private String note = "";
    private int amount = 0;

    public TodayItemsAdapter(Context mContext, List<BudgetData> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout, parent, false);
        return new TodayItemsAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final BudgetData budgetData = myDataList.get(position);

        holder.item.setText("Item: " + budgetData.getBudgetItem());
        holder.amount.setText("Amount: RM" + budgetData.getBudgetAmount());
        holder.date.setText("Date: " + budgetData.getBudgetDate());
        holder.note.setText("Notes: " + budgetData.getBudgetNotes());

        switch (budgetData.getBudgetItem()){
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key = budgetData.getBudgetID();
                item = budgetData.getBudgetItem();
                amount = budgetData.getBudgetAmount();
                note = budgetData.getBudgetNotes();
                updateData();
            }
        });

    }

    private void updateData() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mView = inflater.inflate(R.layout.budget_update_layout, null);

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();

        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNote = mView.findViewById(R.id.note);
        mNote.setText(note);
        mNote.setSelection(note.length());

        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        Button btn_budget_delete = mView.findViewById(R.id.btn_budgetDelete);
        Button btn_budget_update = mView.findViewById(R.id.btn_budgetUpdate);

        btn_budget_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                amount = Integer.parseInt(mAmount.getText().toString());
                note = mNote.getText().toString();

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

                BudgetData budgetData = new BudgetData(item, budgetDate, post_key,note, budgetItemNdays, budgetItemNweeks, budgetItemNmonths, amount,
                        months.getMonths(), weeks.getWeeks());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                reference.child(post_key).setValue(budgetData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(mContext,"Updated successfully",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();
            }
        });

        btn_budget_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(mContext,"Delete successfully",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext,task.getException().toString(),Toast.LENGTH_SHORT).show();
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
        return myDataList.size();
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
