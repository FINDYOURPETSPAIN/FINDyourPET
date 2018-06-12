/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

import findyourpet.Manage.Adaptador;
import findyourpet.R;
import findyourpet.sql.Dispositivos;

public class CallView extends AppCompatActivity {

    private ArrayList<Dispositivos> Items;
    private Adaptador adaptador;
    private ListView listaItems;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.activity_lista_localizacion);
        // Vinculamos el objeto ListView con el objeto del archivo XML
        listaItems = (ListView) findViewById(R.id.lista_dispositivos);
        // Llamamos al método loadItems()
        loadItems();
        añadirListenerLista();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent homeClass = new Intent(getApplicationContext(), Home.class);
            homeClass.putExtra("userId", getIntent().getIntExtra("userId", 0));
            startActivity(homeClass);
            CallView.this.finish();
            // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
            return true;
        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }


    // Método cargar Items
    private void loadItems() {

        ObjectMapper objectMapper = new ObjectMapper();

        String listItems =  getIntent().getStringExtra("devicesUser");

        try {
            Items = objectMapper.readValue(listItems, new TypeReference<ArrayList<Dispositivos>>(){});
        } catch (IOException e) {
            Items = new ArrayList<Dispositivos>();
        }

        // Creamos un nuevo Adaptador y le pasamos el ArrayList
        adaptador = new Adaptador(this, Items);

        // Desplegamos los elementos en el ListView
        listaItems.setAdapter(adaptador);
    }

    void añadirListenerLista() {

        listaItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                Dispositivos selectedItem = (Dispositivos) parent.getItemAtPosition(position);

                // Desglosamos el dispositivo seleccionado
                String nombreDispositivo = selectedItem.getNombre();
                String numeroDispositivo = selectedItem.getTelefono();

                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+numeroDispositivo));
                if (ActivityCompat.checkSelfPermission(CallView.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(), "Solicitando permiso para realizar llamadas, pulse de nuevo el dispositivo", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(CallView.this, new String[]{Manifest.permission.CALL_PHONE}, 225);

                }
                else {
                    Toast.makeText(getApplicationContext(), "Llamando a " + nombreDispositivo, Toast.LENGTH_LONG).show();
                    startActivity(i);
                }

            }
        });


    }



}
