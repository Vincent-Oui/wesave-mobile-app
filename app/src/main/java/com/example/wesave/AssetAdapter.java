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

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder>{

    private Context aContext;
    private List<AssetData> myAssetList;

    private String post_key = "";
    private String item = "";
    private String note = "";
    private int amount = 0;
    //private int years_own =0;

    public AssetAdapter(Context aContext, List<AssetData> myAssetList) {
        this.aContext = aContext;
        this.myAssetList = myAssetList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aContext).inflate(R.layout.asset_retrieve_layout, parent, false);
        return new AssetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final AssetData assetData = myAssetList.get(position);

        holder.item.setText("Asset Type: " + assetData.getAssetItem());
        holder.amount.setText("Amount: RM" + assetData.getAssetAmount());
        holder.date.setText("Date: " + assetData.getAssetDate());
        holder.note.setText("Note: " + assetData.getAssetNotes());


        switch (assetData.getAssetItem()){
            case "Cash and Cash Equivalents":
                holder.imageView.setImageResource(R.drawable.cash);
                break;

            case "Accounts Receivable":
                holder.imageView.setImageResource(R.drawable.account_receivable);
                break;

            case "Investments":
                holder.imageView.setImageResource(R.drawable.investment);
                break;

            case "House and Land":
                holder.imageView.setImageResource(R.drawable.house_land);
                break;

            case "Vehicles":
                holder.imageView.setImageResource(R.drawable.vehicle);
                break;

            case "Personal Properties":
                holder.imageView.setImageResource(R.drawable.personal_properties);
                break;

            case "Other":
                holder.imageView.setImageResource(R.drawable.ic_baseline_other);
                break;

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key = assetData.getAssetID();
                item = assetData.getAssetItem();
                amount = assetData.getAssetAmount();
                note = assetData.getAssetNotes();
                updateData();
            }
        });

    }

    private void updateData() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(aContext);
        LayoutInflater inflater = LayoutInflater.from(aContext);
        View aView = inflater.inflate(R.layout.asset_update_layout, null);

        myDialog.setView(aView);
        final AlertDialog dialog = myDialog.create();

        final TextView aItem = aView.findViewById(R.id.assetName);
        final EditText aAmount = aView.findViewById(R.id.ass_amount);
        final EditText aNote = aView.findViewById(R.id.ass_note);

        aNote.setText(note);
        aNote.setSelection(note.length());
        aNote.setText(note);

        aAmount.setText(String.valueOf(amount));
        aAmount.setSelection(String.valueOf(amount).length());

        Button btn_asset_delete = aView.findViewById(R.id.btn_assetDelete);
        Button btn_asset_update = aView.findViewById(R.id.btn_assetUpdate);

        btn_asset_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                amount = Integer.parseInt(aAmount.getText().toString());
                note = aNote.getText().toString();

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calendar = Calendar.getInstance();
                String assetDate = dateFormat.format(calendar.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Years years = Years.yearsBetween(epoch, now);

                String assetItemNdays = item+assetDate;
                String assetItemNyears = item+years.getYears();

                AssetData assetData = new AssetData(item, assetDate, post_key, assetItemNdays, assetItemNyears, amount,
                        years.getYears(),note);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Asset").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                reference.child(post_key).setValue(assetData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(aContext,"Updated successfully",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(aContext,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();
            }
        });

        btn_asset_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Asset").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(aContext,"Delete successfully",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(aContext,task.getException().toString(),Toast.LENGTH_SHORT).show();
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
        return myAssetList.size();
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
