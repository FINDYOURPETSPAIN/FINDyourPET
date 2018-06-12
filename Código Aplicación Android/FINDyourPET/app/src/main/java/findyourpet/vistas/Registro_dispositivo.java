/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import findyourpet.R;
import findyourpet.sql.Dispositivos;

import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Registro_dispositivo extends AppCompatActivity implements View.OnClickListener{

    private EditText txtNombreDispositivo;
    private EditText txtNumeroTelefonoDispositivo;
    Button boton_registrar_dispositivo;

    private Dispositivos dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_dispositivo);

        iniciarVistas();
        iniciarListeners();
        iniciarObjetos();
        comprobarErrores();

    }

    private void  comprobarErrores(){
        View focusView = null;
        boolean cancel = false;
        txtNombreDispositivo.setError(null);

        if(getIntent().getStringExtra("deviceNameIntroducido") != null){
            txtNombreDispositivo.setText(getIntent().getStringExtra("deviceNameIntroducido"));
        }

        if(getIntent().getStringExtra("deviceNumberIntroducido") != null){
            txtNumeroTelefonoDispositivo.setText(getIntent().getStringExtra("deviceNumberIntroducido"));
        }

        if(getIntent().getBooleanExtra("tagNombre",false)) {
            txtNombreDispositivo.setError("Dispositivo ya registrado");
            focusView = txtNombreDispositivo;
        }

        if (cancel) {
            focusView.requestFocus();
        }

    }



    private void iniciarVistas() {

        txtNombreDispositivo = (EditText) findViewById(R.id.txtNombreDispositivo);
        txtNumeroTelefonoDispositivo = (EditText) findViewById(R.id.txtNumeroTelefonoDispositivo);
        boton_registrar_dispositivo = (Button) findViewById(R.id.btnRegistroDispositivo);

    }

    private void iniciarListeners() {

        boton_registrar_dispositivo.setOnClickListener(this);

    }


    /**
     * Este método inicializa los objetos necesarios para la clase
     */
    private void iniciarObjetos() {

        dis = new Dispositivos();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnRegistroDispositivo:
                postDataToSQLite();
            break;

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            final Intent listaDispositivos = new Intent (getApplicationContext(), List_Gestion.class);

            listaDispositivos.putExtra("userId", getIntent().getIntExtra("userId", 0));

            Thread inicio = new Thread(){

                public void run () {

                String stringUrl = "http://104.46.35.52:3000/users/" + getIntent().getIntExtra("userId",0) + "/devices";

                String op = getJSON(stringUrl, 1000000);

                listaDispositivos.putExtra("devicesUser", op);

                startActivity(listaDispositivos);
                Registro_dispositivo.this.finish();

                }
            };

            inicio.start();

            return true;
        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }


    private void postDataToSQLite() {
        // Aqui se deben realizar validaciones de inputs correctos
       if(txtNombreDispositivo.getText().toString().trim().isEmpty()) {

           txtNombreDispositivo.setError("Campo Obligatorio");
           View focusView = txtNombreDispositivo;
           focusView.requestFocus();
       }
       else if(txtNumeroTelefonoDispositivo.getText().toString().trim().isEmpty()) {

           txtNumeroTelefonoDispositivo.setError("Campo Obligatorio");
           View focusView = txtNumeroTelefonoDispositivo;
           focusView.requestFocus();
       }
       else {


           final String id_Dispositivo = txtNombreDispositivo.getText().toString().trim();
           final int id_usuario = getIntent().getIntExtra("userId", 0);
           final int imagen = this.getResources().getIdentifier("ic_connect2", "drawable", this.getPackageName());

           Thread inicio = new Thread() {
               public void run() {

                   String stringUrl = "http://104.46.35.52:3000/users/" + String.valueOf(id_usuario) +"/devices/"+ id_Dispositivo + "/exists";

                   String op = getJSON(stringUrl, 1000000);

                   ObjectMapper objectMapper = new ObjectMapper();

                   Boolean resultado = new Boolean(false);

                   try {
                       resultado = objectMapper.readValue(op, Boolean.class);
                   }
                   catch (JsonParseException e) {
                       e.printStackTrace();
                   } catch (JsonMappingException e) {
                       e.printStackTrace();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   if (!resultado) {

                       //COMO EL dispositivo NO EXISTE AHORA VAMOS INTRODUCIRLO EN LA BBDD

                       Thread inicio = new Thread() {
                           public void run() {

                               String postUrl = "http://104.46.35.52:3000/users/" + String.valueOf(id_usuario) +"/devices";
                               Gson gson = new Gson();
                               HttpURLConnection con = null;
                               URL u = null;
                               try {
                                   u = new URL(postUrl);
                                   con = (HttpURLConnection) u.openConnection();
                                   con.setDoOutput(true);
                                   con.setDoInput(true);
                                   con.setRequestProperty("Content-Type", "application/json");
                                   con.setRequestProperty("Accept", "application/json");
                                   con.setRequestMethod("POST");

                                   Dispositivos dis = new Dispositivos();

                                   dis.setId(0);
                                   dis.setNombre(txtNombreDispositivo.getText().toString().trim());
                                   dis.setTelefono(txtNumeroTelefonoDispositivo.getText().toString().trim());
                                   dis.setId_user(id_usuario);
                                   dis.setImg(imagen);

                                   String dispositivo = gson.toJson(dis);

                                   OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                                   wr.write(dispositivo);
                                   wr.flush();

                                   int status = con.getResponseCode();

                                   switch (status) {
                                       case 201:
                                           BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                           StringBuilder sb = new StringBuilder();
                                           String line;
                                           while ((line = br.readLine()) != null) {
                                               sb.append(line + "\n");
                                           }
                                           br.close();
                                           Snackbar popup = Snackbar.make(boton_registrar_dispositivo,  getString(R.string.registrado_dispositivo), Snackbar.LENGTH_LONG);
                                           View sbView = popup.getView();
                                           TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                           textView.setTextColor(Color.rgb(255, 255, 0));

                                           popup.show();

                                           final Intent listaDispositivos = new Intent (getApplicationContext(), List_Gestion.class);

                                           listaDispositivos.putExtra("userId", getIntent().getIntExtra("userId", 0));

                                           Thread inicio = new Thread(){

                                               public void run () {
                                                   try {

                                                       sleep(2000);

                                                   } catch (InterruptedException excepcion) {

                                                       excepcion.printStackTrace();

                                                   } finally {

                                                       String stringUrl = "http://104.46.35.52:3000/users/" + getIntent().getIntExtra("userId",0) + "/devices";

                                                       String op = getJSON(stringUrl, 1000000);

                                                       listaDispositivos.putExtra("devicesUser", op);

                                                       startActivity(listaDispositivos);
                                                       Registro_dispositivo.this.finish();
                                                   }
                                               }
                                           };

                                           inicio.start();

                                           break;
                                       case 400:
                                           Snackbar popup1 = Snackbar.make(boton_registrar_dispositivo, "Se ha producido un error en el registro", Snackbar.LENGTH_LONG);
                                           View sbView1 = popup1.getView();
                                           TextView textView1 = (TextView) sbView1.findViewById(android.support.design.R.id.snackbar_text);
                                           textView1.setTextColor(Color.rgb(255, 255, 0));

                                           popup1.show();
                                           break;
                                   }


                               } catch (MalformedURLException e) {
                                   e.printStackTrace();
                               } catch (ProtocolException e) {
                                   e.printStackTrace();
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }

                           }
                       };

                       inicio.start();

                   } else {

                       // Crear el Intent
                       final Intent regDis = new Intent(getApplicationContext(), Registro_dispositivo.class);

                       //esto es para pasar datos de una actividad a otra
                       regDis.putExtra("deviceNameIntroducido", txtNombreDispositivo.getText().toString().trim());
                       regDis.putExtra("deviceNumberIntroducido", txtNumeroTelefonoDispositivo.getText().toString().trim());
                       regDis.putExtra("tagNombre", true);
                       regDis.putExtra("userId", getIntent().getIntExtra("userId", 0));

                       startActivity(regDis);

                       Registro_dispositivo.this.finish();

                   }
               }
           };

           inicio.start();

       }
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
