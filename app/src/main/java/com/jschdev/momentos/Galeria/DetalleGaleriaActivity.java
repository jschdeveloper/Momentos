package com.jschdev.momentos.Galeria;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.jschdev.momentos.utils.UtilsFotos;
import com.jschdev.momentos.R;

public class DetalleGaleriaActivity extends Activity {

    private UtilsFotos utilsFotos;
    private DetalleGaleriaAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_galeria);

        viewPager = (ViewPager) findViewById(R.id.pager);
        utilsFotos = new UtilsFotos(getApplicationContext(), this);

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);

        adapter = new DetalleGaleriaAdapter(DetalleGaleriaActivity.this, utilsFotos.misFotos());

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);
    }
}
