package com.example.hablemos.actividadesProfesor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.hablemos.actividadesEstudiante.EstudianteLoginActivity;
import com.example.hablemos.actividadesEstudiante.menuEstudianteActivity;
import com.example.hablemos.conexiones.ApiConexiones;
import com.example.hablemos.conexiones.ApiUtilidades;
import com.example.hablemos.databinding.ActivityProfesorLoginBinding;
import com.example.hablemos.modelosApoyo.Constantes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfesorLoginActivity extends AppCompatActivity {
    private ActivityProfesorLoginBinding binding;
    private ApiConexiones apiConexiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfesorLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiConexiones = ApiUtilidades.getApiConexion();

        binding.txtCreaCuentaLoginProfActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfesorLoginActivity.this, CrearNuevoProfesorActivity.class);

                startActivity(intent);
                finish();
            }
        });

        binding.btnAccederLoginProfActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.txtEmailLoginProfActivity.getText().toString();
                String contrasenya = binding.txtLoginPasswordProfActivity.getText().toString();
                verificarEmailPass(email,contrasenya);
            }
        });
    }

    public void verificarEmailPass(String email,String contrasenya){
        apiConexiones.comprobarLoginProfesor(email,contrasenya).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                int codigo = response.code();

                if(codigo == Constantes.RESPUESTA_OK_API){
                    Intent intent = new Intent(ProfesorLoginActivity.this, MenuProfesorActivity.class);
                    Bundle bundle = new Bundle();

                    String email = binding.txtEmailLoginProfActivity.getText().toString();

                    bundle.putSerializable("EMAIL",email);
                    intent.putExtras(bundle);

                    startActivity(intent);
                    finish();

                }else if (codigo == Constantes.RESPUESTA_NOT_FOUND_API){

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfesorLoginActivity.this);

                    builder.setTitle("Acceso");
                    builder.setMessage("Email o contraseña erroneos");

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            binding.txtEmailLoginProfActivity.setText("");
                            binding.txtLoginPasswordProfActivity.setText("");
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if(codigo == Constantes.RESPUESTA_ERROR_TRY_API){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfesorLoginActivity.this);

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
            public void onFailure(Call<String> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfesorLoginActivity.this);

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