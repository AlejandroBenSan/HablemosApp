package com.example.hablemos.actividadesProfesor;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.hablemos.actividadesEstudiante.CrearNuevoEstudianteActivity;
import com.example.hablemos.actividadesEstudiante.menuEstudianteActivity;
import com.example.hablemos.conexiones.ApiConexiones;
import com.example.hablemos.conexiones.ApiUtilidades;
import com.example.hablemos.databinding.ActivityCrearNuevoEstudianteBinding;
import com.example.hablemos.databinding.ActivityCrearNuevoProfesorBinding;
import com.example.hablemos.modelos.Profesor;
import com.example.hablemos.modelosApoyo.ApiError;
import com.example.hablemos.modelosApoyo.DatePickerFragment;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearNuevoProfesorActivity extends AppCompatActivity {

    private ActivityCrearNuevoProfesorBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private String fechaEdad;
    private ApiConexiones apiConexiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCrearNuevoProfesorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializarLauncher();

        apiConexiones = ApiUtilidades.getApiConexion();

        binding.imgFotoNewProfActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                intent.setType("image/*"); // a que tipo de datos queremos aplicar la acción
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // queremos un task nuevo

                launcher.launch(intent);
            }
        });

        binding.txtDateNewProfActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        binding.btnCrearProfActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(compruebaDatos()){
                    String nombre = binding.txtNombreNewProfActivity.getText().toString();
                    String apellidos = binding.txtApellidosNewProfActivity.getText().toString();
                    String email = binding.txtEmailNewProfActivity.getText().toString();
                    String password = binding.txtPasswordNewProfActivity.getText().toString();
                    String edad;

                    if(binding.txtDateNewProfActivity.getText().equals("")){
                        edad = null;
                    }
                    else{
                        edad = fechaEdad;
                    }

                    ImageView v = binding.imgFotoNewProfActivity;
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

                    if(binding.txtInfoNewProfActivity.getText().toString().equals("")){
                        info = null;
                    }else{
                        info = binding.txtInfoNewProfActivity.getText().toString();
                    }

                    Profesor profesor = new Profesor(nombre,apellidos,email,password,edad,info,fotoHex);
                    Log.i("PROFESOR",profesor.toString());
                    enviarProfesorApi(profesor);

                }else{
                    Toast.makeText(CrearNuevoProfesorActivity.this, "Faltan datos por rellenar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void enviarProfesorApi(Profesor profesor){
        apiConexiones.crearProfesor(profesor).enqueue(new Callback<Profesor>() {
            @Override
            public void onResponse(Call<Profesor> call, Response<Profesor> response) {
                if(response.errorBody() != null){
                    Gson gson = new Gson();

                    try{
                        ApiError error = gson.fromJson(response.errorBody().string(), ApiError.class);

                        if(error.getApiError() != null){
                            AlertDialog.Builder builder = new AlertDialog.Builder(CrearNuevoProfesorActivity.this);

                            builder.setTitle("Error al crear una cuenta");
                            //TODO HAY QUE CAMBIAR EL MENSAJE A ESPAÑOL
                            String mensaje = error.getApiError().getMessage();
                            builder.setMessage(mensaje);

                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    binding.txtEmailNewProfActivity.setText("");
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }else{
                    if(response.code() == 200){
                        AlertDialog.Builder builder = new AlertDialog.Builder(CrearNuevoProfesorActivity.this);

                        builder.setTitle("¡Cuenta creada!");
                        builder.setMessage("Bienvendo a Hablemos");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(CrearNuevoProfesorActivity.this, MenuProfesorActivity.class);
                                Bundle bundle = new Bundle();

                                String email = binding.txtEmailNewProfActivity.getText().toString();

                                bundle.putSerializable("EMAIL",email);
                                intent.putExtras(bundle);

                                startActivity(intent);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                }
            }

            @Override
            public void onFailure(Call<Profesor> call, Throwable t) {
                Toast.makeText(CrearNuevoProfesorActivity.this,
                        "Error al guardar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //COMPROBAMOS QUE LOS DATOS OBLIGATORIOS ESTEN RELLENOS
    public boolean compruebaDatos()
    {
        boolean correcto = true;
        if(binding.txtNombreNewProfActivity.getText().toString().equals("")){
            correcto = false;

        }if(binding.txtApellidosNewProfActivity.getText().toString().equals(""))
    {
        correcto = false;
    }
        if(binding.txtEmailNewProfActivity.getText().toString().equals("")){
            correcto = false;
        }
        if(binding.txtPasswordNewProfActivity.getText().toString().equals("")){
            correcto = false;
        }

        return correcto;
    }

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

                                binding.imgFotoNewProfActivity.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void showDatePickerDialog()
    {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 porque enero cuenta como 0
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                binding.txtDateNewProfActivity.setText(selectedDate);

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
    public String convertirImagenHex(Bitmap bitmap) {
        int anchoNuevo = bitmap.getWidth() / 8;
        int altoNuevo = bitmap.getHeight() / 8;

        Bitmap bitmapReducido = Bitmap.createScaledBitmap(bitmap,anchoNuevo,altoNuevo,false);
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        //SI DA PROBLEMAS AL LEER LA ACTIVIDAD DEBEREMOS BAJAR LA CALIDAD DE LA IMAGEN
        bitmapReducido.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte [] imagenByte = array.toByteArray();
        String imagenString = display(imagenByte);

        return imagenString;
    }

    //CONVERTIR UN BYTE [] EN HEXADECIMAL
    public static String display(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for(byte val : byteArray)
        {
            stringBuilder.append(String.format("%02x", val&0xff));
        }
        return stringBuilder.toString();
    }
}