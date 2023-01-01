package com.example.hablemos.actividadesEstudiante;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.hablemos.conexiones.ApiConexiones;
import com.example.hablemos.conexiones.ApiUtilidades;
import com.example.hablemos.databinding.ActivityMenuEstudianteBinding;
import com.example.hablemos.modelos.Estudiante;
import com.example.hablemos.modelosApoyo.Constantes;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class menuEstudianteActivity extends AppCompatActivity {

    private ActivityMenuEstudianteBinding  binding;
    private ApiConexiones apiConexiones;
    private Estudiante estudiante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuEstudianteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String email = recibirEmail();
        apiConexiones = ApiUtilidades.getApiConexion();

        obtenerEstudianteApi(email);
        Log.i("EMAIL",email);

        binding.imgPerfilMenuEstActi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.imgClasesMenuEstActi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.imgPagoMenuEstActi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.imgAjustesMenuEstActi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public String recibirEmail(){
        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        String email = (String) bundle.getSerializable("EMAIL");

        return email;
    }

    public void obtenerEstudianteApi(String email)
    {
        apiConexiones.obtenerEstudiante(email).enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
                Gson gson = new Gson();
                Log.i("CUERPO",response.body().toString());
                if(response.code() == 200)
                {
                    estudiante = response.body();
                    binding.lblNomApeMenuEstActiv.setText(estudiante.getNombre() + " "+estudiante.getApellidos());
                }
            }

            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {
                Log.i("ERROR CALL",call.toString());
                Log.i("ERROR T",t.toString());
            }
        });
    }
}