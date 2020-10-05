package com.example.dietguidenetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.dietguidenetwork.models.Nutrients;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Headers;

import static com.example.dietguidenetwork.MenuActivity.EXTRA_ID;
import static com.example.dietguidenetwork.MenuActivity.EXTRA_IMG;
import static com.example.dietguidenetwork.MenuActivity.EXTRA_TITLE;

public class DetailActivity extends AppCompatActivity {

   public static final String TAG = "DetailActivity";
   TextView textTitle,textCalories,textFat,textProtein,textCarbs;
   ImageView imageView;
   Button bAddFoodItem;
   List<Nutrients> nutrientList;
   int remainingCalories;
   double calories;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_detail);

       Intent intent = getIntent();
       final String URL_ID = "https://api.spoonacular.com/food/menuItems/";
       final String ID_API_KEY = "?apiKey=**************************";

       //Received from MenuActivity (menu items)
       //********************************************************
       String imageUrl = intent.getStringExtra(EXTRA_IMG);
       String title = intent.getStringExtra(EXTRA_TITLE);
       int id = intent.getIntExtra(EXTRA_ID,0);//need the id to pass into api call for details
       //********************************************************

       // getting the remaining calories from MenuActivity and casting to integer
       remainingCalories = getIntent().getIntExtra("remaining_calories",0);

       //Url to query the api for nutrition information
       String nutrientsURL = URL_ID + id +ID_API_KEY;

       imageView = findViewById(R.id.ivDetailPic);
       textTitle = findViewById(R.id.tvDetailTitle);
       textCalories = findViewById(R.id.tvDetailCalories);
       textFat = findViewById(R.id.tvDetailFat);
       textProtein = findViewById(R.id.tvDetailProtein);
       textCarbs = findViewById(R.id.tvDetailCarbs);
       bAddFoodItem = findViewById(R.id.button_add_item);

       Glide.with(this).load(imageUrl).into(imageView);
       textTitle.setText(title);

       //API call to get the nutrition info of a specific menu item
       AsyncHttpClient client = new AsyncHttpClient();
       client.get(nutrientsURL, new JsonHttpResponseHandler() {

           @Override
           public void onSuccess(int statusCode, Headers headers, JSON json) {
               Log.d(TAG, "onSuccess: ");
               JSONObject jsonObject = json.jsonObject;

               try {
                   JSONObject nutritionObject = jsonObject.getJSONObject("nutrition");
                   Nutrients nutrients = new Nutrients(nutritionObject);//creating a class object from a json object
                   calories = Double.valueOf(nutrients.getCalories().trim()).doubleValue();
                   double newRemCalories = (double)remainingCalories;


                   textCalories.setText("Calories:    " + calories);
                   textFat.setText("Fat:    " + nutrients.getFat());
                   textProtein.setText("Protein:    " +nutrients.getProtein());
                   textCarbs.setText("Carbs:    " +nutrients.getCarbs());

                   // painting in red the calorie number if there are more calories in
                   // this food item than in the remaining calories for the day
                   if(calories > newRemCalories){
                       textCalories.setTextColor(Color.parseColor("#ff0000"));
                   }
                   // and in green otherwise
                   else {
                       textCalories.setTextColor(Color.parseColor("#46ff00"));
                   }

               } catch (JSONException e) {
                   Log.e(TAG, "Hit json exception", e);
               }
           }

           @Override
           public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
               Log.d(TAG, "onFailure: ");
           }
       });


       bAddFoodItem.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent searchRestIntent = new Intent(DetailActivity.this,HomeActivity.class);
               searchRestIntent.putExtra("calories", calories);
               startActivity(searchRestIntent);
           }
       });
   }
}
