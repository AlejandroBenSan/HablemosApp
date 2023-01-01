package com.example.hablemos.actividadesEstudiante;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hablemos.conexiones.ApiConexiones;
import com.example.hablemos.conexiones.ApiUtilidades;
import com.example.hablemos.databinding.ActivityEstudianteLoginBinding;
import com.example.hablemos.modelosApoyo.Constantes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstudianteLoginActivity extends AppCompatActivity
{
    private ActivityEstudianteLoginBinding binding;
    private ApiConexiones apiConexiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEstudianteLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiConexiones = ApiUtilidades.getApiConexion();
        //Acceder con email y contraseña
        binding.btnAccederLoginEstActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.txtEmailLoginEstActivity.getText().toString();
                String contrasenya = binding.txtLoginPasswordEstActivity.getText().toString();
                verificarEmailPass(email,contrasenya);
            }
        });

        //Crear una cuenta
        binding.txtCreaCuentaLoginEstActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EstudianteLoginActivity.this, CrearNuevoEstudianteActivity.class);

                startActivity(intent);
            }
        });
    }

    public void verificarEmailPass(String email, String contrasenya){

        apiConexiones.comprobarLogin(email,contrasenya).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                int codigo = response.code();

                if(codigo == Constantes.RESPUESTA_OK_API){
                    Intent intent = new Intent(EstudianteLoginActivity.this,menuEstudianteActivity.class);
                    Bundle bundle = new Bundle();

                    String email = binding.txtEmailLoginEstActivity.getText().toString();

                    bundle.putSerializable("EMAIL",email);
                    intent.putExtras(bundle);

                    startActivity(intent);

                }else if (codigo == Constantes.RESPUESTA_NOT_FOUND_API){

                    AlertDialog.Builder builder = new AlertDialog.Builder(EstudianteLoginActivity.this);

                    builder.setTitle("Acceso");
                    builder.setMessage("Email o contraseña erroneos");

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            binding.txtEmailLoginEstActivity.setText("");
                            binding.txtLoginPasswordEstActivity.setText("");
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if(codigo == Constantes.RESPUESTA_ERROR_TRY_API){
                    AlertDialog.Builder builder = new AlertDialog.Builder(EstudianteLoginActivity.this);

                    builder.setTitle("Error");
                    builder.setMessage("Problema internos con la API. Comuniquese con el desarrollador");

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(EstudianteLoginActivity.this);

                builder.setTitle("Error");
                builder.setMessage("Problema con la conexión. Comuniquese con el desarrollador");

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}