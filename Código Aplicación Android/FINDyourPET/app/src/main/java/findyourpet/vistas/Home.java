/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import findyourpet.R;
import findyourpet.sql.Usuario;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout home;

    private CardView aniadirDispositivo;
    private CardView navegacion;
    private CardView settingsPhone;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        iniciarObjetos();
        iniciarVistas();
        iniciarListeners();
    }

    /**
     * Este metodo inicializa las vistas
     */

    private void iniciarVistas() {

        home = (LinearLayout) findViewById(R.id.layout_home_front);


        aniadirDispositivo = (CardView) findViewById(R.id.añadir_dispositivo);
        navegacion = (CardView) findViewById(R.id.navegacion);
        settingsPhone = (CardView) findViewById(R.id.settings_phone);
    }

    /**
     * Este metodo inicializa los escuchadores
     */

    private void iniciarListeners() {

        aniadirDispositivo.setOnClickListener(this);
        navegacion.setOnClickListener(this);
        settingsPhone.setOnClickListener(this);

    }

    /**
     * Este metodo inicializa las opciones extras
     */

    private void iniciarObjetos() {

        this.usuario = new Usuario();
        // rellena el usuario en la vista viniendo de la vista anterior
        setUsuario();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navegacion:

                //Petición al servidor para traer los dispositivos de usuario
                Thread inicio = new Thread() {
                    public void run() {
                        String stringUrl = "http://104.46.35.52:3000/users/" + getIntent().getIntExtra("userId",0) + "/devices";

                        String op = getJSON(stringUrl, 1000000);

                        // Agrega los dispositivos
                        Intent listLocalize = new Intent (getApplicationContext(), List_Localize.class);

                        listLocalize.putExtra("userId", getIntent().getIntExtra("userId",0));
                        listLocalize.putExtra("devicesUser", op);

                        startActivity(listLocalize);

                        Home.this.finish();


                    }
                };

                inicio.start();
                break;
            case R.id.añadir_dispositivo:

                //Petición al servidor para traer los dispositivos de usuario
                Thread inicio1 = new Thread() {
                    public void run() {
                        String stringUrl = "http://104.46.35.52:3000/users/" + getIntent().getIntExtra("userId",0) + "/devices";

                        String op = getJSON(stringUrl, 1000000);

                        // Agrega los dispositivos
                        Intent gestionDispositivos = new Intent (getApplicationContext(), List_Gestion.class);

                        gestionDispositivos.putExtra("userId", getIntent().getIntExtra("userId",0));
                        gestionDispositivos.putExtra("devicesUser", op);

                        startActivity(gestionDispositivos);

                        Home.this.finish();


                    }
                };

                inicio1.start();

                break;
            case R.id.settings_phone:

                //Petición al servidor para traer los dispositivos de usuario
                Thread inicio2 = new Thread() {
                    public void run() {
                        String stringUrl = "http://104.46.35.52:3000/users/" + getIntent().getIntExtra("userId",0) + "/devices";

                        String op = getJSON(stringUrl, 1000000);

                        // Agrega los dispositivos
                        Intent call = new Intent (getApplicationContext(), CallView.class);

                        call.putExtra("userId", getIntent().getIntExtra("userId",0));
                        call.putExtra("devicesUser", op);

                        startActivity(call);

                        Home.this.finish();


                    }
                };

                inicio2.start();

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_exit_to_app)
                    .setTitle(R.string.cerrar_sesion)
                    .setMessage(R.string.salir)
                    .setNegativeButton(R.string.cancelar, null)//sin listener
                    .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            //Salir
                            Home.this.finish();
                        }
                    })
                    .show();

            // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
            return true;
        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }


    private void setUsuario (){

        //Carga el usuario con los valores pasados desde el login

        this.usuario.setEmail(getIntent().getStringExtra("userEmail"));
        this.usuario.setId(getIntent().getIntExtra("userId",0));
        this.usuario.setNombre(getIntent().getStringExtra("userName"));


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
