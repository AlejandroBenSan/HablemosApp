package com.example.hablemos.actividadesProfesor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.hablemos.databinding.ActivityMenuProfesorBinding;

public class MenuProfesorActivity extends AppCompatActivity {
    private ActivityMenuProfesorBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuProfesorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}