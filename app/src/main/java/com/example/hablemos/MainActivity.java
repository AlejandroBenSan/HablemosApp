package com.example.hablemos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.hablemos.actividadesEstudiante.EstudianteLoginActivity;
import com.example.hablemos.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        redondearImagenes();

        //Al pulsar en la imagen de estudiante
        binding.imgEstudiante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EstudianteLoginActivity.class);

                startActivity(intent);
            }
        });

        //Al pulsar en la imagen de profesor
        binding.imgProfesor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void redondearImagenes(){
        Drawable alumnoDrawable = getResources().getDrawable(R.drawable.alumno);
        Drawable profesorDrawable = getResources().getDrawable(R.drawable.profesor);

        //extraemos el drawable en un bitmap
        Bitmap alumnoBitmap = ((BitmapDrawable) alumnoDrawable).getBitmap();
        Bitmap profesorBitmap = ((BitmapDrawable) profesorDrawable).getBitmap();

        //creamos el drawable redondeado
        RoundedBitmapDrawable alumnoRounded = RoundedBitmapDrawableFactory.create(getResources(), alumnoBitmap);
        RoundedBitmapDrawable profesorRounded = RoundedBitmapDrawableFactory.create(getResources(), profesorBitmap);

        //asignamos el CornerRadius
        alumnoRounded.setCornerRadius(alumnoBitmap.getHeight());
        profesorRounded.setCornerRadius(profesorBitmap.getHeight());

        ImageView imageViewAlumno = (ImageView) findViewById(R.id.imgEstudiante);
        ImageView imageViewProfesor = (ImageView) findViewById(R.id.imgProfesor);

        imageViewAlumno.setImageDrawable(alumnoRounded);
        imageViewProfesor.setImageDrawable(profesorRounded);
    }
}