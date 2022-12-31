package com.example.hablemos.actividadesEstudiante;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hablemos.R;
import com.example.hablemos.conexiones.ApiConexiones;
import com.example.hablemos.conexiones.ApiUtilidades;
import com.example.hablemos.databinding.ActivityCrearNuevoEstudianteBinding;
import com.example.hablemos.modelos.Estudiante;
import com.example.hablemos.modelosApoyo.ApiError;
import com.example.hablemos.modelosApoyo.DatePickerFragment;
import com.example.hablemos.modelosApoyo.Error;
import com.google.gson.Gson;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearNuevoEstudianteActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> launcher;
    private ActivityCrearNuevoEstudianteBinding binding;
    private String fechaEdad;
    private ApiConexiones apiConexiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCrearNuevoEstudianteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializarLauncher();
        //Conexion a la API
        apiConexiones = ApiUtilidades.getApiConexion();

        //GUARDAR UN NUEVO ESTUDIANTE
        binding.btnCrearEstActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(compruebaDatos())
                {
                    String nombre = binding.txtNombreNewEstActivity.getText().toString();
                    String apellidos = binding.txtApellidosNewEstActivity.getText().toString();
                    String email = binding.txtEmailNewEstActivity.getText().toString();
                    String password = binding.txtPasswordNewEstActivity.getText().toString();
                    String edad;

                    if(binding.txtDateNewEstActivity.getText().equals("")){
                        edad = null;
                    }
                    else{
                        edad = fechaEdad;
                    }

                    //Si es igual a la foto por defecto
                    ImageView v = binding.imgFotoNewEstActivity;

                    Drawable fotoActualPerfil = v.getDrawable().getCurrent();
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable fotoPorDefecto = getDrawable(R.drawable.plus_svgrepo_com);

                    Bitmap bitmap1 = drawableToBitmap(fotoActualPerfil);
                    Bitmap bitmap2 = drawableToBitmap(fotoPorDefecto);

                    boolean sonIguales = bitmap1.sameAs(bitmap2);
                    String fotoHex = null;

                    if(!sonIguales){
                        fotoHex = convertirImagenHex(bitmap1);
                    }

                    String info;
                    if(binding.txtInfoNewEstActivity.getText().toString().equals("")){
                        info = null;
                    }else{
                        info = binding.txtInfoNewEstActivity.getText().toString();
                    }

                    Estudiante estudiante = new Estudiante(nombre,apellidos,email,password,edad,info,fotoHex);

                    enviarEstudianteApi(estudiante);
                }
                else{
                    Toast.makeText(CrearNuevoEstudianteActivity.this, "Faltan datos por rellenar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //MOSTRAMOS EL CALENDAR PARA LA EDAD
        binding.txtDateNewEstActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });


        //PARA CAMBIAR LA FOTO DE PERFIL
        binding.imgFotoNewEstActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                intent.setType("image/*"); // a que tipo de datos queremos aplicar la acción
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // queremos un task nuevo

                launcher.launch(intent);
            }
        });
    }

    //LAUNCHER PARA RECOGER LA IMAGEN SELECCIONADA
    public void inicializarLauncher(){
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result)
                    {
                        if(result.getResultCode() == RESULT_OK && result.getData() != null){
                            Uri selectedImage = result.getData().getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),selectedImage);

                                binding.imgFotoNewEstActivity.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    //COMPROBAMOS QUE LOS DATOS OBLIGATORIOS ESTEN RELLENOS
    public boolean compruebaDatos()
    {
        boolean correcto = true;
        if(binding.txtNombreNewEstActivity.getText().toString().equals("")){
            correcto = false;

        }if(binding.txtApellidosNewEstActivity.getText().toString().equals(""))
        {
            correcto = false;
        }
        if(binding.txtEmailNewEstActivity.getText().toString().equals("")){
            correcto = false;
        }
        if(binding.txtPasswordNewEstActivity.getText().toString().equals("")){
            correcto = false;
        }

        return correcto;
    }

    //FUNCION PARA ENVIAR LOS DATOS A LA API
    public void enviarEstudianteApi(Estudiante estudiante){

        apiConexiones.crearEstudiante(estudiante).enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, @NonNull Response<Estudiante> response) {
                if(response.errorBody() != null){
                    Gson gson = new Gson();
                    try {
                        ApiError error = gson.fromJson(response.errorBody().string(), ApiError.class);

                        if(error.getApiError() != null){
                            AlertDialog.Builder builder = new AlertDialog.Builder(CrearNuevoEstudianteActivity.this);

                            builder.setTitle("Error al crear una cuenta");
                            //TODO HAY QUE CAMBIAR EL MENSAJE A ESPAÑOL
                            String mensaje = error.getApiError().getMessage();
                            builder.setMessage(mensaje);

                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    binding.txtEmailNewEstActivity.setText("");
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(response.isSuccessful()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(CrearNuevoEstudianteActivity.this);

                        builder.setTitle("¡Cuenta creada!");
                        builder.setMessage("Bienvendo a Hablemos");

                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //TODO NUEVA ACTIVIDAD A LA CUENTA DEL ESTUDIANTE
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                }

            }

            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {
                Toast.makeText(CrearNuevoEstudianteActivity.this,
                        "Error al guardar los datos", Toast.LENGTH_SHORT).show();
            }


        });
        //OBJETO EN JSON PARA VISUALIZARLO
        /*GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        String json = gson.toJson(estudiante);

        Log.i("ESTUDIANTE",json);*/
    }

    

    //MOSTRAMOS EL CALENDAR, MOSTRAMOS LA FECHA Y LA GUARDAMOS EN FORMATO STRING
    private void showDatePickerDialog()
    {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 porque enero cuenta como 0
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                binding.txtDateNewEstActivity.setText(selectedDate);

                final String fecha = year+"-"+(month+1)+"-"+day;

                fechaEdad = fecha;
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //CONVERTIR DE DRAWABLE A BITMAP
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    //CONVERTIR UN BITMAP A HEXADECIMAL
    public String convertirImagenHex(Bitmap bitmap){
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte [] imagenByte = array.toByteArray();
        String imagenString = display(imagenByte);

        return imagenString;
    }

    //CONVERTIR UN BYTE [] EN HEXADECIMAL
    public static String display(byte[] byteArray1) {
        StringBuilder stringBuilder = new StringBuilder();
        for(byte val : byteArray1)
        {
            stringBuilder.append(String.format("%02x", val&0xff));
        }
        return stringBuilder.toString();
    }

}