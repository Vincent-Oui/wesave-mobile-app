package com.example.wesave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.anychart.AnyChartView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class DailyAnalyticsActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;

    private FirebaseAuth mAuth;
    private String onlineUserID = "";
    private DatabaseReference expenseRef, personalRef;

    private TextView totalBudgetAmountTextView, analyticsTransportAmount, analyticsFoodAmount, analyticsHouseAmount, analyticsEntertainmentAmount;
    private TextView analyticsEducationAmount, analyticsCharityAmount, analyticsApparelAmount, analyticsHealthAmount, analyticsPersonalAmount, analyticsOtherAmount;

    private RelativeLayout relativeLayoutTransport, relativeLayoutFood, relativeLayoutHouse, relativeLayoutEntertainment, relativeLayoutEducation;
    private RelativeLayout relativeLayoutCharity, relativeLayoutApparel, relativeLayoutHealth, relativeLayoutPersonal, relativeLayoutOther, linearLayoutAnalysis;

    private AnyChartView anyChartView;

    private TextView progress_ratio_transport, progress_ratio_food, progress_ratio_house, progress_ratio_entertainment, progress_ratio_education, monthSpentAmount;
    private TextView progress_ratio_charity, progress_ratio_apparel, progress_ratio_health, progress_ratio_personal, progress_ratio_other, monthRatioSpending;

    private ImageView status_Image_transport, status_Image_food, status_Image_house, status_Image_entertainment, status_Image_education;
    private ImageView status_Image_charity, status_Image_apparel, status_Image_health, status_Image_personal, status_Image_other, monthRatioSpending_Image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_analytics);

        settingsToolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Today Analytics");

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();

        expenseRef = FirebaseDatabase.getInstance().getReference("Expenses").child(onlineUserID);
        personalRef = FirebaseDatabase.getInstance().getReference("Personal").child(onlineUserID);

        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);

        //General Analytic
        monthSpentAmount = findViewById(R.id.monthSpentAmount);
        linearLayoutAnalysis = findViewById(R.id.linearLayoutAnalysis);
        monthRatioSpending = findViewById(R.id.monthRatioSpending);
        monthRatioSpending_Image = findViewById(R.id.monthRatioSpending_Image);

        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount = findViewById(R.id.analyticsFoodAmount);
        analyticsHouseAmount = findViewById(R.id.analyticsHouseAmount);
        analyticsEntertainmentAmount = findViewById(R.id.analyticsEntertainmentAmount);
        analyticsEducationAmount = findViewById(R.id.analyticsEducationAmount);
        analyticsCharityAmount = findViewById(R.id.analyticsCharityAmount);
        analyticsApparelAmount = findViewById(R.id.analyticsApparelAmount);
        analyticsHealthAmount = findViewById(R.id.analyticsHealthAmount);
        analyticsPersonalAmount = findViewById(R.id.analyticsPersonalAmount);
        analyticsOtherAmount = findViewById(R.id.analyticsOtherAmount);

        //Relative Layout View
        relativeLayoutTransport = findViewById(R.id.relativeLayoutTransport);
        relativeLayoutFood = findViewById(R.id.relativeLayoutFood);
        relativeLayoutHouse = findViewById(R.id.relativeLayoutHouse);
        relativeLayoutEntertainment = findViewById(R.id.relativeLayoutEntertainment);
        relativeLayoutEducation = findViewById(R.id.relativeLayoutEducation);
        relativeLayoutCharity = findViewById(R.id.relativeLayoutCharity);
        relativeLayoutApparel = findViewById(R.id.relativeLayoutApparel);
        relativeLayoutHealth = findViewById(R.id.relativeLayoutHealth);
        relativeLayoutPersonal = findViewById(R.id.relativeLayoutPersonal);
        relativeLayoutOther = findViewById(R.id.relativeLayoutOther);

        //TextViews
        progress_ratio_transport = findViewById(R.id.progress_ratio_transport);
        progress_ratio_food = findViewById(R.id.progress_ratio_food);
        progress_ratio_house = findViewById(R.id.progress_ratio_house);
        progress_ratio_entertainment = findViewById(R.id.progress_ratio_entertainment);
        progress_ratio_education = findViewById(R.id.progress_ratio_education);
        progress_ratio_charity = findViewById(R.id.progress_ratio_charity);
        progress_ratio_apparel = findViewById(R.id.progress_ratio_apparel);
        progress_ratio_health = findViewById(R.id.progress_ratio_health);
        progress_ratio_personal = findViewById(R.id.progress_ratio_personal);
        progress_ratio_other = findViewById(R.id.progress_ratio_other);

        //ImageView
        status_Image_transport = findViewById(R.id.status_Image_transport);
        status_Image_food = findViewById(R.id.status_Image_food);
        status_Image_house = findViewById(R.id.status_Image_house);
        status_Image_entertainment = findViewById(R.id.status_Image_entertainment);
        status_Image_education = findViewById(R.id.status_Image_education);
        status_Image_charity = findViewById(R.id.status_Image_charity);
        status_Image_apparel = findViewById(R.id.status_Image_apparel);
        status_Image_health = findViewById(R.id.status_Image_health);
        status_Image_personal = findViewById(R.id.status_Image_personal);
        status_Image_other = findViewById(R.id.status_Image_other);

        //AnyChartView
        anyChartView = findViewById(R.id.anyChartView);



    }
}