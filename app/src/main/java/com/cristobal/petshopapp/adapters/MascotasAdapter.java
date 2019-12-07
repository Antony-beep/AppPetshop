package com.cristobal.petshopapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.cristobal.petshopapp.R;
import com.cristobal.petshopapp.activities.DetailActivity;
import com.cristobal.petshopapp.models.Mascota;
import com.cristobal.petshopapp.services.ApiService;
import com.cristobal.petshopapp.services.ApiServiceGenerator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MascotasAdapter extends RecyclerView.Adapter<MascotasAdapter.ViewHolder> {
    private static final String TAG=MascotasAdapter.class.getSimpleName();
    private List<Mascota> mascotas;
    public MascotasAdapter(){
        this.mascotas=new ArrayList<>();
    }
    public void setMascotas(List<Mascota> mascotas){
        this.mascotas=mascotas;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView fotoImage;
        TextView nombreText;
        TextView edadText;
        ImageButton menuButton;

        ViewHolder(View itemView){
            super(itemView);
            fotoImage=itemView.findViewById(R.id.foto_image);
            nombreText=itemView.findViewById(R.id.nombre_text);
            edadText=itemView.findViewById(R.id.edad_text);
            menuButton=itemView.findViewById(R.id.menu_button);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mascota,parent,false);
        return new ViewHolder(itemView);
    }
    @Override
    public  void onBindViewHolder(@NonNull ViewHolder viewHolder,final int position){
        final Context context=viewHolder.itemView.getContext();
        final Mascota mascota=this.mascotas.get(position);

        viewHolder.nombreText.setText(mascota.getNombre());
        viewHolder.edadText.setText(mascota.getEdad());

        String url= ApiService.API_BASE_URL+"/api/mascotas/images/"+mascota.getImagen();
        ApiServiceGenerator.createPicasso(context).load(url).into(viewHolder.fotoImage);

        viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove_button:

                                ApiService service = ApiServiceGenerator.createService(this,ApiService.class);

                                service.destroyMascota(mascota.getId()).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                        try {

                                            if (response.isSuccessful()) {

                                                String message = response.body();
                                                Log.d(TAG, "message: " + message);

                                                // Eliminar item del recyclerView y notificar cambios
                                                mascotas.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, mascotas.size());

                                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                            } else {
                                                throw new Exception(ApiServiceGenerator.parseError(response).getMessage());
                                            }

                                        } catch (Throwable t) {
                                            Log.e(TAG, "onThrowable: " + t.getMessage(), t);
                                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                        Log.e(TAG, "onFailure: " + t.getMessage(), t);
                                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                });

                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("ID", mascota.getId());
                context.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount(){
        return this.mascotas.size();
    }
}
