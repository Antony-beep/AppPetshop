package com.cristobal.petshopapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cristobal.petshopapp.activities.LoginActivity;
import com.cristobal.petshopapp.activities.RegisterActivity;
import com.cristobal.petshopapp.adapters.MascotasAdapter;
import com.cristobal.petshopapp.models.Mascota;
import com.cristobal.petshopapp.services.ApiService;
import com.cristobal.petshopapp.services.ApiServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG= MainActivity.class.getSimpleName();
    private RecyclerView mascotaList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mascotaList=findViewById(R.id.recyclerview);
        mascotaList.setLayoutManager(new LinearLayoutManager(this));

        mascotaList.setAdapter(new MascotasAdapter());

        initialize();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initialize() {
        ApiService service= ApiServiceGenerator.createService(this,ApiService.class);

        service.getMascotas().enqueue(new Callback<List<Mascota>>() {
            @Override
            public void onResponse(Call<List<Mascota>> call,@NonNull Response<List<Mascota>> response) {
                try{
                    if(response.isSuccessful()){
                        List<Mascota> mascotas=response.body();
                        Log.d(TAG,"mascotas:"+mascotas);

                        MascotasAdapter adapter=(MascotasAdapter) mascotaList.getAdapter();
                        adapter.setMascotas(mascotas);
                        adapter.notifyDataSetChanged();
                    }else{
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }

                }catch (Throwable t){
                    Log.e(TAG,"onThrowable:"+t.getMessage(),t);
                    Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Mascota>> call,@NonNull Throwable t) {
                Log.e(TAG,"onFailure:"+t.getMessage(),t);
                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private static final int REQUEST_REGISTER_FORM=100;

    public void showRegister(View view){
        startActivityForResult(new Intent(this,RegisterActivity.class),REQUEST_REGISTER_FORM);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==REQUEST_REGISTER_FORM){
            initialize();
        }
    }
    private void logout(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().remove("islogged").remove("token").commit();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
