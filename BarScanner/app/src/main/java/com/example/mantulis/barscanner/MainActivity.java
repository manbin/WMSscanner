package com.example.mantulis.barscanner;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_SUCCESS = "success";
/*    private Button scan_btn;
    private TextView scan_result;*/
    private EditText Username;
    private EditText Password;
    private Button Login;
    private String salt = "hgHHrJMZmSuhV2H9";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Username = (EditText) findViewById(R.id.textUsername);
        Password = (EditText) findViewById(R.id.textPassword);
        Login = (Button)findViewById(R.id.login);

        Login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(validate(Username.getText().toString(), Password.getText().toString())){
                }
            }
        });


        /*scan_btn = (Button)findViewById(R.id.scan1);
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
        });*/
    }

    public boolean validate(String username1, String password1){
        String check = "failure";
        String username = username1;
        String password = password1;
        String type = "login";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, username, md5(password+salt), check);
        if(check == "success"){
            return true;
        }
        else {
            return false;
        }
    }
    public static String md5(String input) {

        String md5 = null;

        if(null == input) return null;

        try {

            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Update input string in message digest
            digest.update(input.getBytes(), 0, input.length());

            //Converts message digest value in base 16 (hex)
            md5 = new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
        return md5;
    }
   /* public void addProduct(View view){
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
    }*/
}
