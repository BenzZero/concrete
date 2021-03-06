package com.example.concrete_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ItemsActivity extends AppCompatActivity {

    String items;
    ListView listView;
    ArrayList<Items> itemAdapter = new  ArrayList<Items>();
    JSONObject objDataResult, itemsObject;
    JSONArray jsonArray;
    Button confrimSend;
    SharedData sharedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        sharedData = SharedData.getInstance();

        confrimSend = findViewById(R.id.confrimSend);
        try {
            items = new ItemsActivity.RequestAsync().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            objDataResult = new JSONObject(items);
            jsonArray = objDataResult.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                itemsObject = jsonArray.getJSONObject(i);
                Items item = new Items( i+1, itemsObject.getString("cube"), Float.parseFloat(itemsObject.getString("price")), Float.parseFloat(itemsObject.getString("installment")) );
                itemAdapter.add(item);
            }

            if (jsonArray.length() > 0) {
                listView = (ListView) findViewById(R.id.listViewOrders);
                ItemsAdapter adapter = new ItemsAdapter(this, itemAdapter);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BottomDialog bottomSheet = new BottomDialog();

                        try {
                            itemsObject = jsonArray.getJSONObject(position);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", itemsObject.getString("id"));
                            bundle.putString("cube", itemsObject.getString("cube"));
                            bundle.putString("price", String.valueOf(Float.parseFloat(itemsObject.getString("price"))));

                            bottomSheet.setArguments(bundle);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        confrimSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(sharedData.sizeBaskets());
                if(sharedData.sizeBaskets() > 0) {
                    Intent confirmSend = new Intent(ItemsActivity.this, ConfirmSendActivity.class);
                    startActivityForResult(confirmSend, 1);
                } else {
                    Toast.makeText(getApplicationContext(), "กรุณาเลือกรายการก่อน", Toast.LENGTH_LONG).show();
                }
            }
        });

        if(savedInstanceState != null) {
            savedInstanceState.getInt("string_result_from_second_activity");
            Toast.makeText(getApplicationContext(), "toast" + savedInstanceState.getInt("string_result_from_second_activity"), Toast.LENGTH_LONG).show();
        }

    }

    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                sharedData.setBasketEmpty();
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    public void setResultFromFragment() {
        SharedData sharedData = SharedData.getInstance();
        confrimSend.setText("ยืนยันการสั่ง " + sharedData.sizeBaskets());
    }

    public class RequestAsync extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                return RequestHandler.sendGet(BuildConfig.SERVER_URL + "/items");
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }
    }
}

