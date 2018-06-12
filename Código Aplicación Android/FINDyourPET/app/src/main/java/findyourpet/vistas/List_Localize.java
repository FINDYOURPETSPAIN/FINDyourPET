/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
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


public class List_Localize extends AppCompatActivity {

    private ArrayList<Dispositivos> Items;
    private Adaptador adaptador;
    private ListView listaItems;
    private final AppCompatActivity actividad = List_Localize.this;

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
            homeClass.putExtra("userId", getIntent().getIntExtra("userId",0));
            startActivity(homeClass);
            List_Localize.this.finish();
            // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
            return true;
        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }


    // Método cargar Items
    private void loadItems(){

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
               //cogemos los datos del dispositivo seleccionado
                Dispositivos selectedItem = (Dispositivos) parent.getItemAtPosition(position);

                // Desglosamos los datos que necsitamos del dispositivo seleccionado
                String nombreDispositivo = selectedItem.getNombre();
                String numeroDispositivo = selectedItem.getTelefono();

                try{

                    int chequear_permisos1 = ContextCompat.checkSelfPermission(List_Localize.this, Manifest.permission.SEND_SMS);
                    int chequear_permisos2 = ContextCompat.checkSelfPermission(List_Localize.this, Manifest.permission.READ_PHONE_STATE);
                    int chequear_permisos3 = ContextCompat.checkSelfPermission(List_Localize.this, Manifest.permission.READ_SMS);

                    if (chequear_permisos1 != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(getApplicationContext(), "No se tiene permiso para enviar sms, pulse de nuevo", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(List_Localize.this, new String[]{Manifest.permission.SEND_SMS}, 225);

                    }
                    if (chequear_permisos2 != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(getApplicationContext(), "No se tiene permiso para el estado del telefono, pulse de nuevo", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(List_Localize.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);

                    }
                    if (chequear_permisos3 != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(getApplicationContext(), "No se tiene permiso para lectura de Sms, pulse de nuevo", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(List_Localize.this, new String[]{Manifest.permission.READ_SMS}, 1);

                    }
                    else {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(numeroDispositivo, null, "DW", null, null);
                        Toast.makeText(getApplicationContext(), "Mensaje de localización enviado a " + nombreDispositivo, Toast.LENGTH_LONG).show();


                        //////////////---------------AQUI COMIENZA LA LECTURA TRAS EL ENVIO----------------////////////////

                        final Intent smsV = new Intent(getApplicationContext(), SmsIncoming.class);

                        smsV.putExtra("userId", getIntent().getIntExtra("userId", 0));
                        smsV.putExtra("numberPhone", numeroDispositivo);
                        smsV.putExtra("nombreMascota", nombreDispositivo);

                        Thread inicio = new Thread() {

                            public void run() {
                                try {

                                    sleep(1000);

                                } catch (InterruptedException excepcion) {

                                    excepcion.printStackTrace();

                                } finally {

                                    startActivity(smsV);
                                    List_Localize.this.finish();
                                }
                            }
                        };

                        inicio.start();

                        //Final del else///
                    }


                }

                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos." + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });


    }








}
