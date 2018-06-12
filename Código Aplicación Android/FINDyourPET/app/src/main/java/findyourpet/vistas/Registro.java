/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import findyourpet.R;
import findyourpet.sql.Usuario;


public class Registro extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private LinearLayout registro;

    private ScrollView scrollview_registro;

    private LinearLayout layout_registro;

    private Spinner spinnerPreguntas;
    private EditText textRespuesta;
    private String[] preguntas;
    private List<String> lista;
    private ArrayAdapter<String> comboAdapter;
    private String nombre;

    private Button boton_registrate;

    private Usuario usuario;
    View focusView = null;
    boolean cancel = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_pregunta);

        iniciarVistas();
        iniciarListeners();
        iniciarObjetos();
    }

    /**
     * Este metodo inicializa las vistas
     */


    private void iniciarVistas() {

        registro = (LinearLayout) findViewById(R.id.registro);

        scrollview_registro = (ScrollView) findViewById(R.id.scrollview_login);

        layout_registro = (LinearLayout) findViewById(R.id.layout_registro);

        textRespuesta = (EditText) findViewById(R.id.respuesta) ;

        spinnerPreguntas = (Spinner) findViewById(R.id.spinner);

        boton_registrate = (Button) findViewById(R.id.boton_continuar);

    }

    /**
     * Este metodo inicializa los listeners
     */

    private void iniciarListeners() {

        boton_registrate.setOnClickListener(this);

    }


    /**
     * Este método inicializa los objetos necesarios para la clase
     */
    private void iniciarObjetos() {

        usuario = new Usuario();

        usuario.setEmail(getIntent().getStringExtra("userEmail"));
        usuario.setPassword(getIntent().getStringExtra("userPass"));
        usuario.setNombre(getIntent().getStringExtra("userName"));

        spinnerPreguntas.setOnItemSelectedListener(this);
        lista = new ArrayList<>();
        preguntas = new String[] {"¿Cuál es tu libro favorito?", "¿Quién fue tu mejor amigo de la infancia?", "¿Quién es tu profesor/a favorito de la infancia?",
                "Nombre de tu primera mascota", "Película favorita", "Lugar favorito en el mundo" };
        Collections.addAll(lista, preguntas);
        comboAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, lista);
        spinnerPreguntas.setAdapter(comboAdapter);

    }

    /**
     * Listener para los botones
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.boton_continuar:
                if(!attemptLogin())
                    postDataToSQLite();
            break;

        }
    }

    /**
     * Este metodo inicializa las vistas
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner:
                //Almaceno el nombre de la pregunta seleccionada
                nombre = preguntas[position];

                Toast.makeText(this, "Pregunta: " + nombre, Toast.LENGTH_SHORT).show();

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch (parent.getId()){
            case R.id.spinner:
                Toast.makeText(this, "Seleccione una pregunta", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 4;

    }

    private boolean attemptLogin() {

        cancel = false;

        // Reset errors.
        textRespuesta.setError(null);

        if (TextUtils.isEmpty(textRespuesta.getText().toString().trim())) {
            textRespuesta.setError(getString(R.string.error_campo_obligatorio));
            focusView = textRespuesta;
            cancel = true;
        }else {

            usuario.setpregunta(spinnerPreguntas.getSelectedItem().toString());
            usuario.setRespuesta(textRespuesta.getText().toString().trim());

        }

        if (cancel) {
            focusView.requestFocus();
        }

        return cancel;
    }



    /**
     * acciones de la BBDD para añadir usuarios, cuando clicamos en el boton register pasará por aqui
     *
     *
     */

    @SuppressLint("ResourceAsColor")
    private void postDataToSQLite() {


        Thread inicio = new Thread() {
            public void run() {
                String postUrl = "http://104.46.35.52:3000/users";
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

                    String user = gson.toJson(usuario);

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(user);
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
                            Snackbar popup = Snackbar.make(boton_registrate, getString(R.string.registrado_email_password), Snackbar.LENGTH_LONG);
                            View sbView = popup.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.rgb(255, 255, 0));

                            popup.show();

                            final Intent loginClass = new Intent (getApplicationContext(), Login.class);

                            Thread inicio = new Thread(){

                                public void run () {
                                    try {

                                        sleep(3000);

                                    } catch (InterruptedException excepcion) {

                                        excepcion.printStackTrace();

                                    } finally {

                                        startActivity(loginClass);
                                        Registro.this.finish();
                                    }
                                }
                            };

                            inicio.start();

                            break;
                        case 400:
                            Snackbar popup1 = Snackbar.make(boton_registrate, "Se ha producido un error en el registro", Snackbar.LENGTH_LONG);
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

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent login = new Intent(getApplicationContext(), Login.class);
            startActivity(login);
            Registro.this.finish();
            // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
            return true;
        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }


    // Comprobación de email
    public static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );



    private boolean isEmailValid(String email) {

        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }




}
