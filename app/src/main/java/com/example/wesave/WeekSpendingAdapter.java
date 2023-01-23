package com.example.wesave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeekSpendingAdapter extends RecyclerView.Adapter<WeekSpendingAdapter.ViewHolder>{

    private Context mContext;
    private List<BudgetData> myDataList;

    public WeekSpendingAdapter(Context mContext, List<BudgetData> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout, parent, false);
        return new WeekSpendingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final BudgetData budgetData = myDataList.get(position);

        holder.item.setText("Item: " + budgetData.getBudgetItem());
        holder.amount.setText("Amount: RM" + budgetData.getBudgetAmount());
        holder.date.setText("Date: " + budgetData.getBudgetDate());
        holder.note.setText("Note: " + budgetData.getBudgetNotes());

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
