package com.cristobal.petshopapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cristobal.petshopapp.R;
import com.cristobal.petshopapp.models.Mascota;
import com.cristobal.petshopapp.services.ApiService;
import com.cristobal.petshopapp.services.ApiServiceGenerator;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG=RegisterActivity.class.getSimpleName();

    private ImageView imagenPreview;

    private EditText nombreInput;
    private EditText razaInput;
    private EditText edadInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imagenPreview=findViewById(R.id.imagen_preview);
        nombreInput=findViewById(R.id.nombre_input);
        razaInput=findViewById(R.id.raza_input);
        edadInput=findViewById(R.id.edad_input);

    }

    private static final int REQUEST_CAMERA=100;

    public void takePicture(View view){
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_CAMERA);
    }
    private Bitmap bitmap;

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==REQUEST_CAMERA){
            if(resultCode==RESULT_OK){
                bitmap=(Bitmap) data.getExtras().get("data");
                bitmap=scaleBitmapDown(bitmap,800);
                imagenPreview.setImageBitmap(bitmap);
            }
        }
    }
    public void callRegister(View view){
        String nombre= nombreInput.getText().toString();
        String raza=razaInput.getText().toString();
        String edad=edadInput.getText().toString();

        if(nombre.isEmpty()||raza.isEmpty()){
            Toast.makeText(this,"Nombre y raza son campos requeridos",Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService service= ApiServiceGenerator.createService(this,ApiService.class);
        Call<Mascota> call;

        if(bitmap==null){
            call=service.createMascota(nombre,raza,edad);

        }else{
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            byte[] byteArray = stream.toByteArray();

            RequestBody requestFile=RequestBody.create(MediaType.parse("image/jpeg"),byteArray);
            MultipartBody.Part imagenPart=MultipartBody.Part.createFormData("imagen","photo.jpg",requestFile);

            RequestBody nombrePart=RequestBody.create(MultipartBody.FORM,nombre);
            RequestBody razaPart=RequestBody.create(MultipartBody.FORM,raza);
            RequestBody edadPart=RequestBody.create(MultipartBody.FORM,edad);

            call=service.createMascota(nombrePart,razaPart,edadPart,imagenPart);

        }
        call.enqueue(new Callback<Mascota>() {
            @Override
            public void onResponse(@NonNull Call<Mascota> call,@NonNull Response<Mascota> response) {
                try{
                    if(response.isSuccessful()){
                        Mascota mascota=response.body();
                        Log.d(TAG,"mascota:"+mascota);
                        Toast.makeText(RegisterActivity.this,"Registro satisfactorio",Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);

                        finish();
                    }else{
                        throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                    }
                }catch (Throwable t){
                    Log.e(TAG,"onThrowable:"+t.getMessage(),t);
                    Toast.makeText(RegisterActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Mascota> call,@NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
    private Bitmap scaleBitmapDown(Bitmap bitmap,int maxDimension){

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;


        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

}

