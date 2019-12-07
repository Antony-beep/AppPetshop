package com.cristobal.petshopapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cristobal.petshopapp.MainActivity;
import com.cristobal.petshopapp.R;
import com.cristobal.petshopapp.models.ApiError;
import com.cristobal.petshopapp.models.Usuario;
import com.cristobal.petshopapp.services.ApiService;
import com.cristobal.petshopapp.services.ApiServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG=LoginActivity.class.getSimpleName();
    private EditText et_correo;
    private EditText et_contra;
    private Button loginButton;
    private Button btn_ir_registro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_ir_registro=findViewById(R.id.btn_ir_registro);
        btn_ir_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrarActivity.class));
                finish();
            }
        });

        et_correo=findViewById(R.id.et_correo);
        et_contra=findViewById(R.id.et_contra);
        loginButton=findViewById(R.id.btn_loguear);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        loadLastCorreo();

        verifyLoginStatus();
    }
    private void login(){
        String correo=et_correo.getText().toString();
        String contraseña=et_contra.getText().toString();
        if(correo.isEmpty()){
            Toast.makeText(this, "Ingrese el correo", Toast.LENGTH_SHORT).show();
            return;

        }
        if(contraseña.isEmpty()){
            Toast.makeText(this, "Ingrese la contraseña", Toast.LENGTH_SHORT).show();
            return;

        }
        ApiService service= ApiServiceGenerator.createService(ApiService.class);

        Call<Usuario> call=service.login(correo,contraseña);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    Usuario usuario=response.body();
                    Log.d(TAG,"usuario:"+usuario);

                    SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    sp.edit().putString("correo",usuario.getCorreo()).putString("token",usuario.getToken())
                            .putBoolean("islogged",true).commit();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                    Toast.makeText(LoginActivity.this, "Bienvenido " + usuario.getNombre(), Toast.LENGTH_LONG).show();
                }else{
                    ApiError error=ApiServiceGenerator.parseError(response);
                    Toast.makeText(LoginActivity.this, "onError:" + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "onFailure: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void loadLastCorreo(){
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);

        String correo=sp.getString("correo",null);
        if(correo!=null){
            et_correo.setText(correo);
        }
    }

    private void verifyLoginStatus(){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean islogged = sp.getBoolean("islogged", false);

        if(islogged){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

}
