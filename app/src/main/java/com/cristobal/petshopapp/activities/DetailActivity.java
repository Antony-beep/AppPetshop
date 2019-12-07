package com.cristobal.petshopapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cristobal.petshopapp.R;
import com.cristobal.petshopapp.models.Mascota;
import com.cristobal.petshopapp.services.ApiService;
import com.cristobal.petshopapp.services.ApiServiceGenerator;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG=DetailActivity.class.getSimpleName();

    private Long id;

    private ImageView fotoImage;
    private TextView nombreText;
    private TextView razaText;
    private TextView edadText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        fotoImage = findViewById(R.id.foto_image);
        nombreText = findViewById(R.id.nombre_text);
        razaText = findViewById(R.id.raza_text);
        edadText = findViewById(R.id.edad_text);

        id=getIntent().getExtras().getLong("ID");
        Log.e(TAG,"id:"+id);

        iniatilize();

    }

    private void iniatilize() {
        ApiService service= ApiServiceGenerator.createService(this,ApiService.class);

        Call<Mascota> call=service.showMascota(id);

        call.enqueue(new Callback<Mascota>() {
            @Override
            public void onResponse(Call<Mascota> call, Response<Mascota> response) {
                    try{
                        if(response.isSuccessful()){
                            Mascota mascota=response.body();
                            Log.d(TAG,"mascota:"+mascota);

                            nombreText.setText(mascota.getNombre());
                            razaText.setText(mascota.getRaza());
                            edadText.setText(mascota.getEdad());

                            String url=ApiService.API_BASE_URL+"/api/mascotas/images/"+mascota.getImagen();
                            ApiServiceGenerator.createPicasso(DetailActivity.this).load(url).into(fotoImage);//CAMBIAR LA RECONCHA DE ....
                        }else{
                            throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                        }
                    }catch (Throwable t) {
                        Log.e(TAG, "onThrowable: " + t.getMessage(), t);
                        Toast.makeText(DetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }

            }

            @Override
            public void onFailure(Call<Mascota> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toast.makeText(DetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}
