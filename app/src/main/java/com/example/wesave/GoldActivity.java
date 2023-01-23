package com.example.wesave;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;

public class GoldActivity extends AppCompatActivity {

    private TextView gold_tv;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gold);

        gold_tv = findViewById(R.id.gold_tv);
        Button btn_gold = findViewById(R.id.btn_gold);


        requestQueue = Volley.newRequestQueue(this);
        btn_gold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse();
            }
        });
    }

    private void jsonParse() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.metals-api.com/api/latest?access_key=3f7vb5xjlonwe5763j79na6izovoz6qfz7rtqwydj7nrdlwfljw7co57fq5k&base=USD&symbols=XAU%2CXAG%2CXPD%2CXPT%2CXRH";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //gold_tv.setText(response.toString());

                        try {
                            JSONObject myResponse = new JSONObject(response.toString());
                            String rates = myResponse.getString("rates");
                            JSONObject Newrates = new JSONObject(rates);
                            double USD = Newrates.getDouble("USD");
                            double XAG = Newrates.getDouble("XAG");
                            double XAU = Newrates.getDouble("XAU");
                            double XPD = Newrates.getDouble("XPD");
                            double XPT = Newrates.getDouble("XPT");
                            double XRH = Newrates.getDouble("XRH");
                            gold_tv.setText("USD : US$ " + USD + "\n" +
                                    "Silver : US$ " + XAG + "\n" +
                                    "Gold : US$" + XAU + "\n" +
                                    "Palladium : US$ " + XPD + "\n" +
                                    "Platinum : US$ " + XPT + "\n"  +
                                    "Rhenium : US$ " + XRH);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                gold_tv.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}