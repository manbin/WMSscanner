package com.example.mantulis.barscanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
public class MainScreen extends AppCompatActivity {

    private Button scan_btn;
    private TextView scan_result;
    private Button new_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPrefLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        scan_result = (TextView)findViewById(R.id.Scanresult);
        new_user = (Button)findViewById(R.id.newuser);
        scan_btn = (Button)findViewById(R.id.scan1);
        String tempLoginLvl = pref.getString("LoginLevel", "0");
        scan_result.setText(tempLoginLvl);
        final Activity activity = this;
        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainScreen.this, RegisterUser.class));
            }
        });
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
    public void addProduct(View view){
        Intent i = new Intent(getApplicationContext(), AddProduct.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            }
            else{
                scan_result = (TextView)findViewById(R.id.Scanresult);
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
