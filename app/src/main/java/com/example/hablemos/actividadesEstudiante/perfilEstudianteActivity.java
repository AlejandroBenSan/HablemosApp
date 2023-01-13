package com.example.hablemos.actividadesEstudiante;

import androidx.activity.OnBackPressedDispatcher;
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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.example.hablemos.R;
import com.example.hablemos.conexiones.ApiConexiones;
import com.example.hablemos.conexiones.ApiUtilidades;
import com.example.hablemos.databinding.ActivityMenuEstudianteBinding;
import com.example.hablemos.databinding.ActivityPerfilEstudianteBinding;
import com.example.hablemos.modelos.Estudiante;
import com.example.hablemos.modelosApoyo.Constantes;
import com.example.hablemos.modelosApoyo.DatePickerFragment;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.DecoderException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class perfilEstudianteActivity extends AppCompatActivity {
    private ActivityPerfilEstudianteBinding binding;
    private Estudiante estudiante;
    private String fechaEdad;
    private ApiConexiones apiConexiones;
    private ActivityResultLauncher<Intent> launcher;
    private int idEstudiante;

    //CONTROLAMOS EL EVENTO AL PULSA EL BOTÓN ATRÁS
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){
            Intent intent = new Intent(perfilEstudianteActivity.this,menuEstudianteActivity.class);
            Bundle bundle = new Bundle();

            bundle.putSerializable("EMAIL",binding.txtEmailPerfilEstAct.getText().toString());
            intent.putExtras(bundle);

            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilEstudianteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiConexiones = ApiUtilidades.getApiConexion();
        obtenerEstudiante();
        completarCampos();
        inicializarLauncher();

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
                            ImageView v = binding.imgPerfilEstActi;

                            Drawable fotoActualPerfil = v.getDrawable().getCurrent();
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable fotoPorDefecto = getDrawable(R.drawable.person_male_svgrepo_com);
                            Bitmap b1 = drawableToBitmap(fotoActualPerfil);
                            Bitmap b2 = drawableToBitmap(fotoPorDefecto);

                            boolean iguales = b1.sameAs(b2);
                            String fotoHex = null;

                            if(!iguales){
                                fotoHex = convertirImagenHex(b1);
                            }

                            Estudiante estudianteEditado = new Estudiante();
                            estudianteEditado.setNombre(binding.txtNombrePerfilEstActi.getText().toString());
                            estudianteEditado.setApellidos(binding.txtApellidoPerfilEstActi.getText().toString());
                            estudianteEditado.setEmail(binding.txtEmailPerfilEstAct.getText().toString());
                            estudianteEditado.setContrasenya(binding.txtPasswordPerfilEstActi.getText().toString());
                            if(binding.txtEdadPerfilEstActi.getText().equals("")){
                                fechaEdad = null;
                            }

                            estudianteEditado.setEdad(fechaEdad);
                            estudianteEditado.setFoto(fotoHex);
                            if(binding.txtInfoPerfilEstActi.getText().equals("")){
                                estudianteEditado.setInfo(null);
                            }else{
                                estudianteEditado.setInfo(binding.txtInfoPerfilEstActi.getText().toString());
                            }

                            enviarEstudianteActualizadoAPI(estudianteEditado,idEstudiante);
                        }

                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        binding.btnCerSesionPerfilEstActi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(perfilEstudianteActivity.this);
                builder.setTitle("Confirmar");
                builder.setMessage("¿Seguro que desea cerrar la sesión?");

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(perfilEstudianteActivity.this,EstudianteLoginActivity.class);

                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create();
                builder.show();
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
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                intent.setType("image/*"); // a que tipo de datos queremos aplicar la acción
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // queremos un task nuevo

                launcher.launch(intent);
            }
        });
    }

    public void enviarEstudianteActualizadoAPI(Estudiante estudiante,int id){
        apiConexiones.actualizarEstudiante(estudiante, id).enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
                if(response.code() == Constantes.RESPUESTA_OK_API){
                    AlertDialog.Builder builder = new AlertDialog.Builder(perfilEstudianteActivity.this);
                    builder.setTitle("Confirmado");
                    builder.setMessage("Se han guardado los cambios con éxito");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(perfilEstudianteActivity.this,menuEstudianteActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("EMAIL",binding.txtEmailPerfilEstAct.getText().toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.create();
                    builder.show();
                }

                if(response.code() == Constantes.RESPUESTA_NOT_FOUND_API){
                    AlertDialog.Builder builder = new AlertDialog.Builder(perfilEstudianteActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Error");
                    builder.setMessage("Error al guardar los datos");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    builder.create();
                    builder.show();
                }
            }

            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(perfilEstudianteActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Error");
                builder.setMessage("Fallo al conectar con la API. Comuníquese con el desarrollador");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(perfilEstudianteActivity.this,menuEstudianteActivity.class);

                        startActivity(intent);
                        finish();
                    }
                });
                builder.create();
                builder.show();
            }
        });


    }

    public void obtenerEstudiante(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        estudiante = (Estudiante) bundle.getSerializable("ESTUDIANTE");

        idEstudiante = estudiante.getId();
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

    //CONVERTIMOS LA IMAGEN OBTENIDA DE LA API PARA PODER VISUALIZARLA
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

                                binding.imgPerfilEstActi.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    //CONVERTIR LA IMAGEN
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