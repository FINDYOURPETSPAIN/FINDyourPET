/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import findyourpet.Manage.Adaptador_gestion;
import findyourpet.R;
import findyourpet.sql.Dispositivos;


public class List_Gestion extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Dispositivos> Items;
    private Adaptador_gestion adaptador;
    private ListView listaItems;
    Button boton_aniadir_dispositivo;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.activity_lista_gestion);
        iniciarVistas();
        iniciarListeners();
        // Vinculamos el objeto ListView con el objeto del archivo XML
        listaItems = (ListView) findViewById(R.id.lista_dispositivos);
        // Llamamos al método loadItems()
        loadItems();

    }

    private void iniciarVistas() {

        boton_aniadir_dispositivo = (Button) findViewById(R.id.boton_aniadir_dispositivo);

    }

    private void iniciarListeners() {

        boton_aniadir_dispositivo.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.boton_aniadir_dispositivo:
                // Agrega los dispositivos
                Intent regDispositivo = new Intent (getApplicationContext(), Registro_dispositivo.class);

                regDispositivo.putExtra("userId", getIntent().getIntExtra("userId",0));

                startActivity(regDispositivo);

                List_Gestion.this.finish();
                break;


        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent homeClass = new Intent(getApplicationContext(), Home.class);
            homeClass.putExtra("userId", getIntent().getIntExtra("userId",0));
            startActivity(homeClass);
            List_Gestion.this.finish();
            // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
            return true;
        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }

    public void onUpdate(){

        final Intent listaDispositivos = new Intent (getApplicationContext(), List_Gestion.class);

        listaDispositivos.putExtra("userId", getIntent().getIntExtra("userId", 0));

        Thread inicio = new Thread(){

            public void run () {

                String stringUrl = "http://104.46.35.52:3000/users/" + getIntent().getIntExtra("userId",0) + "/devices";

                String op = getJSON(stringUrl, 1000000);

                listaDispositivos.putExtra("devicesUser", op);

                startActivity(listaDispositivos);
                List_Gestion.this.finish();

            }
        };

        inicio.start();

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
        adaptador = new Adaptador_gestion(this, Items, this);
    // Desplegamos los elementos en el ListView
        listaItems.setAdapter(adaptador);
    }

    public final String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setUseCaches(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

}
