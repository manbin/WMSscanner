package com.example.mantulis.barscanner;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddProduct extends Activity{
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    private Button scan_btn;
    private TextView scan_result;
    String barcode;
    String pid;

    // url to create new product
    private static String url_add_product = "http://192.168.1.43/db_service/add_product.php";

    private static String url_product_detials = "http://192.168.1.43/db_service/get_product_details.php";

    private static String url_update_product = "http://192.168.1.43/db_service/update_product.php";

    // JSON Node names
    private static final String TAG_WAREHOUSE_AMOUNT = "warehouseAmount";
    private static final String TAG_WAREHOUSE_ID = "warehouseID";
    private static final String TAG_WAREHOUSE_NAME = "warehouseName";
    private static final String TAG_WAREHOUSES = "warehouses";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_QUANTITY = "quantity";
    private static final String TAG_SHORT = "shortdesc";
    private static JSONObject product = new JSONObject();
    private static JSONArray warehouses = null;
    private Spinner dropdownWrhs;
    private ArrayAdapter adapter;
    private WarehouseData warehouseData;
    WarehouseData[] warehouseList;
    private EditText quant;
    private String selectedWrhsID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);
        quant = (EditText)findViewById(R.id.amountText);
        scan_btn = (Button)findViewById(R.id.scan);
        dropdownWrhs = findViewById(R.id.spinnerWarehouse);
        dropdownWrhs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(!dropdownWrhs.getSelectedItem().toString().isEmpty()){
                    for(int g=0; g<warehouseList.length;g++){
                        if(warehouseList[g].getName().equals(dropdownWrhs.getSelectedItem().toString())){
                            int tempquantity = warehouseList[g].getAmount();
                            quant.setText(Integer.toString(tempquantity));
                            selectedWrhsID = warehouseList[g].getId();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);

                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            }
            else{
                //scan_result = (TextView)findViewById(R.id.Scanresult);
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                barcode = result.getContents().toString();
                pid = barcode;
                //pid = "666";
                new GetProductDetails().execute();
                //teting
                /*String type = "get details";
                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute(type, pid);*/
                //end testing
                //new CreateNewProduct().execute();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddProduct.this);
            pDialog.setMessage("Loading product details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            /*runOnUiThread(new Runnable() {
                public void run() {*/
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> paramss = new ArrayList<NameValuePair>();
                        paramss.add(new BasicNameValuePair("pid", pid));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json;
                        json = jsonParser.makeHttpRequest(
                                url_product_detials, "GET", paramss);

                        // check your log for json response
                        if(json != null)
                        Log.d("Single Product Details", json.toString());
                        else
                            Log.d("messsage maaan", "wtf nx");

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray(TAG_PRODUCT); // JSON Array

                            // get first product object from JSON Array
                            product = productObj.getJSONObject(0);
                            warehouses = json.getJSONArray(TAG_WAREHOUSES);
                            warehouseList = new WarehouseData[warehouses.length()];
                            for (int i = 0; i < warehouses.length(); i++) {
                                JSONObject c = warehouses.getJSONObject(i);

                                // Storing each json item in variable
                                String name = c.getString(TAG_WAREHOUSE_NAME);
                                String id = c.getString(TAG_WAREHOUSE_ID);
                                int wAmount = c.getInt(TAG_WAREHOUSE_AMOUNT);

                                WarehouseData tempW = new WarehouseData(name, id, wAmount);


                                // adding HashList to ArrayList
                                warehouseList[i] = tempW;
                            }


                        }else{
                            // product with pid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            /*    }
            });*/

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            //Spinner dropdownWrhs2 = findViewById(R.id.spinnerWarehouse);
                            // product with this pid found
                            // Edit Text
            EditText txtName = (EditText) findViewById(R.id.productName); //name
            //EditText txtPrice = (EditText) findViewById(R.id.productPrice);
            EditText txtShort = (EditText) findViewById(R.id.shortDescription); //price
            EditText txtDesc = (EditText) findViewById(R.id.attributesText); //description
            EditText txtQuantity = (EditText) findViewById(R.id.amountText); //quantity


                            // display product data in EditText
            try {
                populateSpinner();
                //dropdownWrhs.setAdapter(adapter);
                txtName.setText(product.getString(TAG_NAME));
                //txtPrice.setText(product.getString(TAG_PRICE));
                txtShort.setText(product.getString(TAG_SHORT));
                txtDesc.setText(product.getString(TAG_DESCRIPTION));
                //txtQuantity.setText(product.getString(TAG_QUANTITY));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }
    private void populateSpinner() {
        String[] items = new String[warehouseList.length];
        //String[] items = new String[]{"shit","nigger"};
        for(int m = 0; m < warehouseList.length; m++){
            Log.e("warehouse list id", m+": "+warehouseList[m].getId());
            items[m]= warehouseList[m].getName();
            Log.e("items", items[m]);
        }


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownWrhs.setAdapter(adapter);

    }

    /*class SaveProductDetails extends AsyncTask<String, String, String> {

        *//**
         * Before starting background thread Show Progress Dialog
         * *//*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddProduct.this);
            pDialog.setMessage("Saving product ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        *//**
         * Saving product
         * *//*
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String name = txtName.getText().toString();
            String price = txtPrice.getText().toString();
            String description = txtDesc.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_PRICE, price));
            params.add(new BasicNameValuePair(TAG_DESCRIPTION, description));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_product,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        *//**
         * After completing background task Dismiss the progress dialog
         * **//*
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product uupdated
            pDialog.dismiss();
        }
    }*/
    /*class CreateNewProduct extends AsyncTask<String, String, String> {

        *//**
         * Before starting background thread Show Progress Dialog
         * *//*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddProduct.this);
            pDialog.setMessage("Updating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        *//**
         * Creating product
         * *//*
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("barcode", barcode));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_add_product,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    //Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        *//**
         * After completing background task Dismiss the progress dialog
         * **//*
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }*/
}
