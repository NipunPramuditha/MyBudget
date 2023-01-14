package com.example.mybudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class WeeklyAnalyticsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;

    private Toolbar toolbar;

    private TextView totalAmountSpentOn, analyticsTransportAmount, analyticsFoodAmount, analyticsHouseAmount, analyticsEntertainmentAmount, analyticsEducationAmount,
            analyticsCharityAmount, analyticsApparelAmount, analyticsHealthAmount, analyticsPersonalAmount, analyticsOtherAmount, monthSpentAmount,
            monthRatioSpending;

    private RelativeLayout relativeLayoutTransport, relativeLayoutFood, relativeLayoutHouse, relativeLayoutEntertainment, relativeLayoutEducation,
            relativeLayoutCharity, relativeLayoutApparel, relativeLayoutHealth, relativeLayoutPersonal, relativeLayoutOther, linearLayoutAnalysis;

    private PieChart anyChartView;

    private ImageView transport_status, Food_status, House_status, Entertainment_status, Education_status, Charity_status, Apparel_status,
            Health_status, Personal_status, Other_status, monthRatioSpendingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_analytics);

        //ToolBar
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Weekly Analysis");

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);

        //TextViews
        totalAmountSpentOn = findViewById(R.id.totalAmountSpentOn);

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

        monthSpentAmount = findViewById(R.id.monthSpentAmount);
        monthRatioSpending = findViewById(R.id.monthRatioSpending);

        //ImageViews
        transport_status = findViewById(R.id.transport_status);
        Food_status = findViewById(R.id.Food_status);
        House_status = findViewById(R.id.House_status);
        Entertainment_status = findViewById(R.id.Entertainment_status);
        Education_status = findViewById(R.id.Education_status);
        Charity_status = findViewById(R.id.Charity_status);
        Apparel_status = findViewById(R.id.Apparel_status);
        Health_status = findViewById(R.id.Health_status);
        Personal_status = findViewById(R.id.Personal_status);
        Other_status = findViewById(R.id.Other_status);

        monthRatioSpendingImage = findViewById(R.id.monthRatioSpendingImage);

        //RelativeLayouts
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

        linearLayoutAnalysis = findViewById(R.id.linearLayoutAnalysis);

        //PieChart
        anyChartView = findViewById(R.id.anyChartView);

        getTotalWeekTransportExpense();
        getTotalWeekFoodExpense();
        getTotalWeekHouseExpense();
        getTotalWeekEntertainmentExpense();
        getTotalWeekEducationExpense();
        getTotalWeekCharityExpense();
        getTotalWeekApparelExpense();
        getTotalWeekHealthExpense();
        getTotalWeekPersonalExpense();
        getTotalWeekOtherExpense();
        getTotalWeekSpending();
    }

    private void getTotalWeekSpending() {

    }

    private void getTotalWeekOtherExpense() {

    }

    private void getTotalWeekPersonalExpense() {

    }

    private void getTotalWeekHealthExpense() {

    }

    private void getTotalWeekApparelExpense() {

    }

    private void getTotalWeekCharityExpense() {

    }

    private void getTotalWeekEducationExpense() {

    }

    private void getTotalWeekEntertainmentExpense() {

    }

    private void getTotalWeekHouseExpense() {

    }

    private void getTotalWeekFoodExpense() {

    }

    private void getTotalWeekTransportExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        String itemNWeek = "Transport" + weeks.getWeeks();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNWeek").equalTo(itemNWeek);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for(DataSnapshot ds : snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsTransportAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("weekTrans").setValue(totalAmount);
                }
                else{
                    relativeLayoutTransport.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}