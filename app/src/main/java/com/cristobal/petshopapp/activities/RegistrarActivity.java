package com.cristobal.petshopapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cristobal.petshopapp.R;

public class RegistrarActivity extends AppCompatActivity {
    Button btn_ir_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        btn_ir_login=findViewById(R.id.btn_ir_login);
        btn_ir_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrarActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
}
