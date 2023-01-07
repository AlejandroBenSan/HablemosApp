package com.example.hablemos.actividadesEstudiante;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.example.hablemos.R;
import com.example.hablemos.conexiones.ApiConexiones;
import com.example.hablemos.conexiones.ApiUtilidades;
import com.example.hablemos.databinding.ActivityMenuEstudianteBinding;
import com.example.hablemos.databinding.ActivityPerfilEstudianteBinding;
import com.example.hablemos.modelos.Estudiante;
import com.example.hablemos.modelosApoyo.DatePickerFragment;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.DecoderException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class perfilEstudianteActivity extends AppCompatActivity {
    private ActivityPerfilEstudianteBinding binding;
    private Estudiante estudiante;
    private String fechaEdad;
    private ApiConexiones apiConexiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilEstudianteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiConexiones = ApiUtilidades.getApiConexion();
        obtenerEstudiante();
        completarCampos();


        binding.btnGuardaPerfilEstActi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comprobarCampos()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(perfilEstudianteActivity.this);

                    builder.setTitle("Confirmar");
                    builder.setMessage("¿Desea guardar los cambios?");

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                }
            }
        });

        binding.txtEdadPerfilEstActi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        binding.imgPerfilEstActi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO poder cambiar la imagen pinchando en ella
            }
        });
    }

    public void enviarEstudianteActualizadoAPI(Estudiante estudiante,int id){
        apiConexiones.actualizarEstudiante(estudiante, id).enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {

            }

            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {

            }
        });
    }

    public void obtenerEstudiante(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        estudiante = (Estudiante) bundle.getSerializable("ESTUDIANTE");
    }

    public void completarCampos() {
        binding.txtNombrePerfilEstActi.setText(estudiante.getNombre());
        binding.txtApellidoPerfilEstActi.setText(estudiante.getApellidos());
        binding.txtEmailPerfilEstAct.setText(estudiante.getEmail());
        binding.txtPasswordPerfilEstActi.setText(estudiante.getContrasenya());
        binding.txtEdadPerfilEstActi.setText(estudiante.getEdad());

        if(estudiante.getInfo() != null){
            binding.txtInfoPerfilEstActi.setText(estudiante.getInfo());
        }

        if(estudiante.getFoto() != null){
            Bitmap fotoBitmap = convertirHexBitmap(estudiante.getFoto());
            binding.imgPerfilEstActi.setImageBitmap(fotoBitmap);
        }else{
            binding.imgPerfilEstActi.setImageResource(R.drawable.person_male_svgrepo_com);
        }
    }

    public boolean comprobarCampos(){
        boolean respuesta = true;

        if (binding.txtNombrePerfilEstActi.getText().toString().equals("")) {
            respuesta = false;
        }

        if(binding.txtApellidoPerfilEstActi.getText().toString().equals("")){
            respuesta = false;
        }

        if(binding.txtEmailPerfilEstAct.getText().toString().equals("")){
            respuesta = false;
        }

        if(binding.txtPasswordPerfilEstActi.getText().toString().equals("")){
            respuesta = false;
        }

        return respuesta;
    }

    private void showDatePickerDialog()
    {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 porque enero cuenta como 0
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                binding.txtEdadPerfilEstActi.setText(selectedDate);

                final String fecha = year+"-"+(month+1)+"-"+day;

                fechaEdad = fecha;
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public Bitmap convertirHexBitmap(String hexa){
        int cantidad = hexa.length();

        byte[] datos = new byte[cantidad / 2];
        for (int i = 0; i< cantidad; i+=2){
            datos[i / 2] = (byte) (byte) ((Character.digit(hexa.charAt(i), 16) << 4)
                    + Character.digit(hexa.charAt(i+1), 16));
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(datos,0,datos.length);

        return bitmap;
    }


}