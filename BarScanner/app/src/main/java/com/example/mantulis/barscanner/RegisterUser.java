package com.example.mantulis.barscanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterUser extends AppCompatActivity {

    private EditText CUsername;
    private EditText CPassword;
    private EditText CPassword2;
    private Button Register;
    private Spinner dropdown;

    private String salt = "hgHHrJMZmSuhV2H9";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        dropdown = findViewById(R.id.spinner);
        CUsername = (EditText)findViewById(R.id.regUsername);
        CPassword = (EditText)findViewById(R.id.regPassword);
        CPassword2 = (EditText)findViewById(R.id.regConfPassword);
        Register = (Button)findViewById(R.id.register);
        String[] items = new String[]{"admin", "user1", "user2", "user3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }
    public void Register(View view){
        if(CUsername.getText().toString().isEmpty() || CPassword.getText().toString().isEmpty() || CPassword2.getText().toString().isEmpty()){
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        }else if(CPassword.getText().toString().equals(CPassword2.getText().toString())){
            String check = "failure";
            String username = CUsername.getText().toString();
            String password = CPassword.getText().toString();
            String password2 = CPassword2.getText().toString();
            String type = "register";
            String level;
            switch(dropdown.getSelectedItem().toString()){
                case "admin": level="1";
                break;
                case "user1": level="2";
                break;
                case "user2": level="3";
                break;
                default:
                    level="4";
                break;
            }
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(type, username, md5(password+salt), check, level);
        }
        else{
            Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show();
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
}
