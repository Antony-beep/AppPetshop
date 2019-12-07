package com.cristobal.petshopapp.services;

import com.cristobal.petshopapp.models.Mascota;
import com.cristobal.petshopapp.models.Usuario;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    String API_BASE_URL="http://10.0.2.2:8089";

    @GET("/api/mascotas")
    Call<List<Mascota>> getMascotas();

    @FormUrlEncoded
    @POST("/api/mascotas")
    Call<Mascota> createMascota(@Field("nombre") String nombre,
                                  @Field("raza") String raza,
                                  @Field("edad") String edad);
    @Multipart
    @POST("/api/mascotas")
    Call<Mascota> createMascota(@Part("nombre") RequestBody nombre,
                                  @Part("raza") RequestBody raza,
                                  @Part("edad") RequestBody edad,
                                  @Part MultipartBody.Part imagen
    );
    @DELETE("/api/mascotas/{id}")
    Call<String> destroyMascota(@Path("id") Long id);

    @GET("/api/mascotas/{id}")
    Call<Mascota> showMascota(@Path("id") Long id);

    @FormUrlEncoded
    @POST("/auth/login")
    Call<Usuario> login(@Field("correo") String correo,
                        @Field("password") String password);

    @GET("/api/profile")
    Call<Usuario> getProfile();

}
