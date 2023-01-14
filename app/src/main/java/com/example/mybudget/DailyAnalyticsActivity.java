package com.example.mybudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DailyAnalyticsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;

    private Toolbar toolbar;

    private TextView totalAmountSpentOnTxt, analyticsTransportAmountTxt, analyticsFoodAmountTxt, analyticsHouseAmountTxt, analyticsEntertainmentAmountTxt, analyticsEducationAmountTxt,
            analyticsCharityAmountTxt, analyticsApparelAmountTxt, analyticsHealthAmountTxt, analyticsPersonalAmountTxt, analyticsOtherAmountTxt, monthSpentAmountTxt,
            monthRatioSpendingTxt;

    private RelativeLayout relativeLayoutTransport, relativeLayoutFood, relativeLayoutHouse, relativeLayoutEntertainment, relativeLayoutEducation,
            relativeLayoutCharity, relativeLayoutApparel, relativeLayoutHealth, relativeLayoutPersonal, relativeLayoutOther, linearLayoutAnalysis;

    private AnyChartView anyChartView;

    private ImageView transport_statusImg, Food_statusImg, House_statusImg, Entertainment_statusImg, Education_statusImg, Charity_statusImg, Apparel_statusImg,
            Health_statusImg, Personal_statusImg, Other_statusImg, monthRatioSpendingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_analytics);

        //ToolBar
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Daily Analysis");

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);

        //TextViews
        totalAmountSpentOnTxt = findViewById(R.id.totalAmountSpentOn);

        analyticsTransportAmountTxt = findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmountTxt = findViewById(R.id.analyticsFoodAmount);
        analyticsHouseAmountTxt = findViewById(R.id.analyticsHouseAmount);
        analyticsEntertainmentAmountTxt = findViewById(R.id.analyticsEntertainmentAmount);
        analyticsEducationAmountTxt = findViewById(R.id.analyticsEducationAmount);
        analyticsCharityAmountTxt = findViewById(R.id.analyticsCharityAmount);
        analyticsApparelAmountTxt = findViewById(R.id.analyticsApparelAmount);
        analyticsHealthAmountTxt = findViewById(R.id.analyticsHealthAmount);
        analyticsPersonalAmountTxt = findViewById(R.id.analyticsPersonalAmount);
        analyticsOtherAmountTxt = findViewById(R.id.analyticsOtherAmount);

        monthSpentAmountTxt = findViewById(R.id.monthSpentAmount);
        monthRatioSpendingTxt = findViewById(R.id.monthRatioSpending);

        //ImageViews
        transport_statusImg = findViewById(R.id.transport_status);
        Food_statusImg = findViewById(R.id.Food_status);
        House_statusImg = findViewById(R.id.House_status);
        Entertainment_statusImg = findViewById(R.id.Entertainment_status);
        Education_statusImg = findViewById(R.id.Education_status);
        Charity_statusImg = findViewById(R.id.Charity_status);
        Apparel_statusImg = findViewById(R.id.Apparel_status);
        Health_statusImg = findViewById(R.id.Health_status);
        Personal_statusImg = findViewById(R.id.Personal_status);
        Other_statusImg = findViewById(R.id.Other_status);

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
        getTotalDayExpending();

        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                loadGraph();
                setStatusAndImageResource();
            }
            },2000
        );
    }

    private void getTotalWeekOtherExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNDay = "Other" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
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
                        analyticsOtherAmountTxt.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayOther").setValue(totalAmount);
                }
                else{
                    relativeLayoutOther.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekPersonalExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNDay = "Personal" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
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
                        analyticsPersonalAmountTxt.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayPersonal").setValue(totalAmount);
                }
                else{
                    relativeLayoutPersonal.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekHealthExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNDay = "Health" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
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
                        analyticsHealthAmountTxt.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayHealth").setValue(totalAmount);
                }
                else{
                    relativeLayoutHealth.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekApparelExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNDay = "Apparel" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
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
                        analyticsApparelAmountTxt.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayApparel").setValue(totalAmount);
                }
                else{
                    relativeLayoutApparel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekCharityExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNDay = "Charity" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
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
                        analyticsCharityAmountTxt.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayCharity").setValue(totalAmount);
                }
                else{
                    relativeLayoutCharity.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekEducationExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNDay = "Education" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
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
                        analyticsEducationAmountTxt.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayEducation").setValue(totalAmount);
                }
                else{
                    relativeLayoutEducation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekEntertainmentExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNDay = "Entertainment" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
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
                        analyticsEntertainmentAmountTxt.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayEntertainment").setValue(totalAmount);
                }
                else{
                    relativeLayoutEntertainment.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekHouseExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNDay = "House" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
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
                        analyticsHouseAmountTxt.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayHouse").setValue(totalAmount);
                }
                else{
                    relativeLayoutHouse.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekFoodExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNDay = "Food" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
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
                        analyticsFoodAmountTxt.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayFood").setValue(totalAmount);
                }
                else{
                    relativeLayoutFood.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalWeekTransportExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNDay = "Transport" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNDay").equalTo(itemNDay);
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
                        analyticsTransportAmountTxt.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayTrans").setValue(totalAmount);
                }
                else{
                    relativeLayoutTransport.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalDayExpending(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    int totalAmount = 0;
                    for(DataSnapshot ds : snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    totalAmountSpentOnTxt.setText("Total Day's Expending is: $" + totalAmount);
                    monthSpentAmountTxt.setText("Total Spent: $" + totalAmount);
                }
                else{
                    totalAmountSpentOnTxt.setText("You have not spent today");
                    anyChartView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGraph(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    //transport
                    int transportTotal;
                    if(snapshot.hasChild("dayTrans")){
                        transportTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    }else{
                        transportTotal = 0;
                    }

                    //food
                    int foodTotal;
                    if(snapshot.hasChild("dayFood")){
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    }else{
                        foodTotal = 0;
                    }

                    //house
                    int houseTotal;
                    if(snapshot.hasChild("dayHouse")){
                        houseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    }else{
                        houseTotal = 0;
                    }

                    //entertainment
                    int entertainmentTotal;
                    if(snapshot.hasChild("dayEntertainment")){
                        entertainmentTotal = Integer.parseInt(snapshot.child("dayEntertainment").getValue().toString());
                    }else{
                        entertainmentTotal = 0;
                    }

                    //education
                    int educationTotal;
                    if(snapshot.hasChild("dayEducation")){
                        educationTotal = Integer.parseInt(snapshot.child("dayEducation").getValue().toString());
                    }else{
                        educationTotal = 0;
                    }

                    //charity
                    int charityTotal;
                    if(snapshot.hasChild("dayCharity")){
                        charityTotal = Integer.parseInt(snapshot.child("dayCharity").getValue().toString());
                    }else{
                        charityTotal = 0;
                    }

                    //apparel
                    int apparelTotal;
                    if(snapshot.hasChild("dayApparel")){
                        apparelTotal = Integer.parseInt(snapshot.child("dayApparel").getValue().toString());
                    }else{
                        apparelTotal = 0;
                    }

                    //health
                    int healthTotal;
                    if(snapshot.hasChild("dayHealth")){
                        healthTotal = Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                    }else{
                        healthTotal = 0;
                    }

                    //personal
                    int personalTotal;
                    if(snapshot.hasChild("dayPersonal")){
                        personalTotal = Integer.parseInt(snapshot.child("dayPersonal").getValue().toString());
                    }else{
                        personalTotal = 0;
                    }

                    //other
                    int otherTotal;
                    if(snapshot.hasChild("dayOther")){
                        otherTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    }else{
                        otherTotal = 0;
                    }


                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport", transportTotal));
                    data.add(new ValueDataEntry("Food", foodTotal));
                    data.add(new ValueDataEntry("House", houseTotal));
                    data.add(new ValueDataEntry("Entertainment", entertainmentTotal));
                    data.add(new ValueDataEntry("Education", educationTotal));
                    data.add(new ValueDataEntry("Charity", charityTotal));
                    data.add(new ValueDataEntry("Apparel", apparelTotal));
                    data.add(new ValueDataEntry("Health", healthTotal));
                    data.add(new ValueDataEntry("Personal", personalTotal));
                    data.add(new ValueDataEntry("Other", otherTotal));

                    pie.data(data);
                    pie.title("Daily Analysis");
                    pie.labels().position("outside");
                    pie.legend().title().enabled(true);
                    pie.legend().title().text("Items Spent On").padding(0d, 0d, 10d, 0d);
                    pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER);
                    anyChartView.setChart(pie);
                    }
                else{
                    Toast.makeText(DailyAnalyticsActivity.this, "Child does not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setStatusAndImageResource(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //transport
                    float TransportTotal;
                    if(snapshot.hasChild("dayTrans")){
                        TransportTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    }else{
                        TransportTotal = 0;
                    }

                    //Food
                    float FoodTotal;
                    if(snapshot.hasChild("dayFood")){
                        FoodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    }else{
                        FoodTotal = 0;
                    }

                    //House
                    float HouseTotal;
                    if(snapshot.hasChild("dayHouse")){
                        HouseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    }else{
                        HouseTotal = 0;
                    }

                    //Entertainment
                    float EntertainmentTotal;
                    if(snapshot.hasChild("dayEntertainment")){
                        EntertainmentTotal = Integer.parseInt(snapshot.child("dayEntertainment").getValue().toString());
                    }else{
                        EntertainmentTotal = 0;
                    }

                    //Education
                    float EducationTotal;
                    if(snapshot.hasChild("dayEducation")){
                        EducationTotal = Integer.parseInt(snapshot.child("dayEducation").getValue().toString());
                    }else{
                        EducationTotal = 0;
                    }

                    //Charity
                    float CharityTotal;
                    if(snapshot.hasChild("dayCharity")){
                        CharityTotal = Integer.parseInt(snapshot.child("dayCharity").getValue().toString());
                    }else{
                        CharityTotal = 0;
                    }

                    //Apparel
                    float ApparelTotal;
                    if(snapshot.hasChild("dayApparel")){
                        ApparelTotal = Integer.parseInt(snapshot.child("dayApparel").getValue().toString());
                    }else{
                        ApparelTotal = 0;
                    }

                    //Health
                    float HealthTotal;
                    if(snapshot.hasChild("dayHealth")){
                        HealthTotal = Integer.parseInt(snapshot.child("dayHealth").getValue().toString());
                    }else{
                        HealthTotal = 0;
                    }

                    //Personal
                    float PersonalTotal;
                    if(snapshot.hasChild("dayPersonal")){
                        PersonalTotal = Integer.parseInt(snapshot.child("dayPersonal").getValue().toString());
                    }else{
                        PersonalTotal = 0;
                    }

                    //Other
                    float OtherTotal;
                    if(snapshot.hasChild("dayOther")){
                        OtherTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    }else{
                        OtherTotal = 0;
                    }

                    float monthTotalSpentAmount;
                    if(snapshot.hasChild("today")){
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("today").getValue().toString());
                    }else{
                        monthTotalSpentAmount = 0;
                    }

                    //..........Getting Ratios..........
                    //transport
                    float TransportRatio;
                    if(snapshot.hasChild("dayTransRatio")){
                        TransportRatio = Integer.parseInt(snapshot.child("dayTransRatio").getValue().toString());
                    }else{
                        TransportRatio = 0;
                    }

                    //Food
                    float FoodRatio;
                    if(snapshot.hasChild("dayFoodRatio")){
                        FoodRatio = Integer.parseInt(snapshot.child("dayFoodRatio").getValue().toString());
                    }else{
                        FoodRatio = 0;
                    }

                    //House
                    float HouseRatio;
                    if(snapshot.hasChild("dayHouseRatio")){
                        HouseRatio = Integer.parseInt(snapshot.child("dayHouseRatio").getValue().toString());
                    }else{
                        HouseRatio = 0;
                    }

                    //Entertainment
                    float EntertainmentRatio;
                    if(snapshot.hasChild("dayEntertainmentRatio")){
                        EntertainmentRatio = Integer.parseInt(snapshot.child("dayEntertainmentRatio").getValue().toString());
                    }else{
                        EntertainmentRatio = 0;
                    }

                    //Education
                    float EducationRatio;
                    if(snapshot.hasChild("dayEducationRatio")){
                        EducationRatio = Integer.parseInt(snapshot.child("dayEducationRatio").getValue().toString());
                    }else{
                        EducationRatio = 0;
                    }

                    //Charity
                    float CharityRatio;
                    if(snapshot.hasChild("dayCharityRatio")){
                        CharityRatio = Integer.parseInt(snapshot.child("dayCharityRatio").getValue().toString());
                    }else{
                        CharityRatio = 0;
                    }

                    //Apparel
                    float ApparelRatio;
                    if(snapshot.hasChild("dayApparelRatio")){
                        ApparelRatio = Integer.parseInt(snapshot.child("dayApparelRatio").getValue().toString());
                    }else{
                        ApparelRatio = 0;
                    }

                    //Health
                    float HealthRatio;
                    if(snapshot.hasChild("dayHealthRatio")){
                        HealthRatio = Integer.parseInt(snapshot.child("dayHealthRatio").getValue().toString());
                    }else{
                        HealthRatio = 0;
                    }

                    //Personal
                    float PersonalRatio;
                    if(snapshot.hasChild("dayPersonalRatio")){
                        PersonalRatio = Integer.parseInt(snapshot.child("dayPersonalRatio").getValue().toString());
                    }else{
                        PersonalRatio = 0;
                    }

                    //Other
                    float OtherRatio;
                    if(snapshot.hasChild("dayOtherRatio")){
                        OtherRatio = Integer.parseInt(snapshot.child("dayOtherRatio").getValue().toString());
                    }else{
                        OtherRatio = 0;
                    }

                    float monthTotalSpentAmountRatio;
                    if(snapshot.hasChild("dailyBudget")){
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("dailyBudget").getValue().toString());
                    }else{
                        monthTotalSpentAmountRatio = 0;
                    }

                    //...........Image Resources............
                    float monthPercent = (monthTotalSpentAmount/monthTotalSpentAmountRatio)*100;
                    if(monthPercent<50){
                        monthRatioSpendingTxt.setText(monthPercent+" %" + " used of" + monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpendingImage.setImageResource(R.drawable.green);
                    }else if(monthPercent >= 50 && monthPercent< 100){
                        monthRatioSpendingTxt.setText(monthPercent+" %" + " used of" + monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpendingImage.setImageResource(R.drawable.brown);
                    }else{
                        monthRatioSpendingTxt.setText(monthPercent+" %" + " used of" + monthTotalSpentAmountRatio + ". Status:");
                        monthRatioSpendingImage.setImageResource(R.drawable.red);
                    }

                    //Transport
                    float TransportPercent = (TransportTotal/TransportRatio)*100;
                    if(TransportPercent<50){
                        analyticsTransportAmountTxt.setText(TransportPercent+" %" + " used of" + TransportRatio + ". Status:");
                        transport_statusImg.setImageResource(R.drawable.green);
                    }else if(TransportPercent >= 50 && TransportPercent< 100){
                        analyticsTransportAmountTxt.setText(TransportPercent+" %" + " used of" + TransportRatio + ". Status:");
                        transport_statusImg.setImageResource(R.drawable.brown);
                    }else{
                        analyticsTransportAmountTxt.setText(TransportPercent+" %" + " used of" + TransportRatio + ". Status:");
                        transport_statusImg.setImageResource(R.drawable.red);
                    }

                    //Food
                    float FoodPercent = (FoodTotal/FoodRatio)*100;
                    if(FoodPercent<50){
                        analyticsFoodAmountTxt.setText(FoodPercent+" %" + " used of" + FoodRatio + ". Status:");
                        Food_statusImg.setImageResource(R.drawable.green);
                    }else if(FoodPercent >= 50 && FoodPercent< 100){
                        analyticsFoodAmountTxt.setText(FoodPercent+" %" + " used of" + FoodRatio + ". Status:");
                        Food_statusImg.setImageResource(R.drawable.brown);
                    }else{
                        analyticsFoodAmountTxt.setText(FoodPercent+" %" + " used of" + FoodRatio + ". Status:");
                        Food_statusImg.setImageResource(R.drawable.red);
                    }

                    //House
                    float HousePercent = (HouseTotal/HouseRatio)*100;
                    if(HousePercent<50){
                        analyticsHouseAmountTxt.setText(HousePercent+" %" + " used of" + HouseRatio + ". Status:");
                        House_statusImg.setImageResource(R.drawable.green);
                    }else if(HousePercent >= 50 && HousePercent< 100){
                        analyticsHouseAmountTxt.setText(HousePercent+" %" + " used of" + HouseRatio + ". Status:");
                        House_statusImg.setImageResource(R.drawable.brown);
                    }else{
                        analyticsHouseAmountTxt.setText(HousePercent+" %" + " used of" + HouseRatio + ". Status:");
                        House_statusImg.setImageResource(R.drawable.red);
                    }

                    //Entertainment
                    float EntertainmentPercent = (EntertainmentTotal/EntertainmentRatio)*100;
                    if(EntertainmentPercent<50){
                        analyticsEntertainmentAmountTxt.setText(EntertainmentPercent+" %" + " used of" + EntertainmentRatio + ". Status:");
                        Entertainment_statusImg.setImageResource(R.drawable.green);
                    }else if(EntertainmentPercent >= 50 && EntertainmentPercent< 100){
                        analyticsEntertainmentAmountTxt.setText(EntertainmentPercent+" %" + " used of" + EntertainmentRatio + ". Status:");
                        Entertainment_statusImg.setImageResource(R.drawable.brown);
                    }else{
                        analyticsEntertainmentAmountTxt.setText(EntertainmentPercent+" %" + " used of" + EntertainmentRatio + ". Status:");
                        Entertainment_statusImg.setImageResource(R.drawable.red);
                    }

                    //Education
                    float EducationPercent = (EducationTotal/EducationRatio)*100;
                    if(EducationPercent<50){
                        analyticsEducationAmountTxt.setText(EducationPercent+" %" + " used of" + EducationRatio + ". Status:");
                        Education_statusImg.setImageResource(R.drawable.green);
                    }else if(EducationPercent >= 50 && EducationPercent< 100){
                        analyticsEducationAmountTxt.setText(EducationPercent+" %" + " used of" + EducationRatio + ". Status:");
                        Education_statusImg.setImageResource(R.drawable.brown);
                    }else{
                        analyticsEducationAmountTxt.setText(EducationPercent+" %" + " used of" + EducationRatio + ". Status:");
                        Education_statusImg.setImageResource(R.drawable.red);
                    }

                    //Charity
                    float CharityPercent = (CharityTotal/CharityRatio)*100;
                    if(CharityPercent<50){
                        analyticsCharityAmountTxt.setText(CharityPercent+" %" + " used of" + CharityRatio + ". Status:");
                        Charity_statusImg.setImageResource(R.drawable.green);
                    }else if(CharityPercent >= 50 && CharityPercent< 100){
                        analyticsCharityAmountTxt.setText(CharityPercent+" %" + " used of" + CharityRatio + ". Status:");
                        Charity_statusImg.setImageResource(R.drawable.brown);
                    }else{
                        analyticsCharityAmountTxt.setText(CharityPercent+" %" + " used of" + CharityRatio + ". Status:");
                        Charity_statusImg.setImageResource(R.drawable.red);
                    }

                    //Apparel
                    float ApparelPercent = (ApparelTotal/ApparelRatio)*100;
                    if(ApparelPercent<50){
                        analyticsApparelAmountTxt.setText(ApparelPercent+" %" + " used of" + ApparelRatio + ". Status:");
                        Apparel_statusImg.setImageResource(R.drawable.green);
                    }else if(ApparelPercent >= 50 && ApparelPercent< 100){
                        analyticsApparelAmountTxt.setText(ApparelPercent+" %" + " used of" + ApparelRatio + ". Status:");
                        Apparel_statusImg.setImageResource(R.drawable.brown);
                    }else{
                        analyticsApparelAmountTxt.setText(ApparelPercent+" %" + " used of" + ApparelRatio + ". Status:");
                        Apparel_statusImg.setImageResource(R.drawable.red);
                    }

                    //Health
                    float HealthPercent = (HealthTotal/HealthRatio)*100;
                    if(HealthPercent<50){
                        analyticsHealthAmountTxt.setText(HealthPercent+" %" + " used of" + HealthRatio + ". Status:");
                        Health_statusImg.setImageResource(R.drawable.green);
                    }else if(HealthPercent >= 50 && HealthPercent< 100){
                        analyticsHealthAmountTxt.setText(HealthPercent+" %" + " used of" + HealthRatio + ". Status:");
                        Health_statusImg.setImageResource(R.drawable.brown);
                    }else{
                        analyticsHealthAmountTxt.setText(HealthPercent+" %" + " used of" + HealthRatio + ". Status:");
                        Health_statusImg.setImageResource(R.drawable.red);
                    }

                    //Personal
                    float PersonalPercent = (PersonalTotal/PersonalRatio)*100;
                    if(PersonalPercent<50){
                        analyticsPersonalAmountTxt.setText(PersonalPercent+" %" + " used of" + PersonalRatio + ". Status:");
                        Personal_statusImg.setImageResource(R.drawable.green);
                    }else if(PersonalPercent >= 50 && PersonalPercent< 100){
                        analyticsPersonalAmountTxt.setText(PersonalPercent+" %" + " used of" + PersonalRatio + ". Status:");
                        Personal_statusImg.setImageResource(R.drawable.brown);
                    }else{
                        analyticsPersonalAmountTxt.setText(PersonalPercent+" %" + " used of" + PersonalRatio + ". Status:");
                        Personal_statusImg.setImageResource(R.drawable.red);
                    }

                    //Other
                    float OtherPercent = (OtherTotal/OtherRatio)*100;
                    if(OtherPercent<50){
                        analyticsOtherAmountTxt.setText(OtherPercent+" %" + " used of" + OtherRatio + ". Status:");
                        Other_statusImg.setImageResource(R.drawable.green);
                    }else if(OtherPercent >= 50 && OtherPercent< 100){
                        analyticsOtherAmountTxt.setText(OtherPercent+" %" + " used of" + OtherRatio + ". Status:");
                        Other_statusImg.setImageResource(R.drawable.brown);
                    }else{
                        analyticsOtherAmountTxt.setText(OtherPercent+" %" + " used of" + OtherRatio + ". Status:");
                        Other_statusImg.setImageResource(R.drawable.red);
                    }
                }
                else{
                    Toast.makeText(DailyAnalyticsActivity.this, "Set Status and Image Resources Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}