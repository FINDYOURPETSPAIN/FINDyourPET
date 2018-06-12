/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import findyourpet.R;
import findyourpet.sql.Usuario;

public class Password extends AppCompatActivity implements View.OnClickListener{

    private EditText passwordEmail;
    private Button resetearPassword;
    private boolean cancel;
    View focusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        passwordEmail = (EditText) findViewById(R.id.email_password);

        resetearPassword = (Button) findViewById(R.id.boton_password_reset);

        iniciarListeners();
        comprobarErrores();

    }

    private void iniciarListeners() {

        resetearPassword.setOnClickListener(this);

    }

    private void  comprobarErrores(){
        View focusView = null;
        boolean cancel = false;
        passwordEmail.setError(null);

        if(getIntent().getStringExtra("userEmailIntroducido") != null){
            passwordEmail.setText(getIntent().getStringExtra("userEmailIntroducido"));
        }

        if(getIntent().getBooleanExtra("tagEmail",false)) {
            passwordEmail.setError("Usuario no Registrado");
            focusView = passwordEmail;
        }

        if (cancel) {
            focusView.requestFocus();
        }

    }


    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.boton_password_reset:
                if(!attemptLogin())
                    postDataToSQLite();
                break;

        }
    }


    private boolean attemptLogin() {

        cancel = false;

        // Reset errors.
        passwordEmail.setError(null);


        if (TextUtils.isEmpty(passwordEmail.getText().toString().trim())) {
             passwordEmail.setError(getString(R.string.error_campo_obligatorio));
             focusView = passwordEmail;
             cancel = true;
        } else if (!isEmailValid(passwordEmail.getText().toString().trim())) {
             passwordEmail.setError(getString(R.string.error_email_invalido));
             focusView = passwordEmail;
             cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        }

        return cancel;
    }

    private boolean isEmailValid(String email) {

        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


    private void postDataToSQLite() {


        Thread inicio = new Thread() {
            public void run() {
                String stringUrl = "http://104.46.35.52:3000/users/" + passwordEmail.getText().toString().trim() + "/exists";

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
                if (resultado) {

                    //COMO EL USUARIO EXISTE AHORA VAMOS A COMPROBAR SUS DATOS SI SON CORRECTOS
                    //SI LO SON LE METEMOS A HOME, SINO RECARGAMOS EL LOGIN CON DATOS INCORRECTOS

                    Thread inicio = new Thread() {
                        public void run() {

                            String stringUrl = "http://104.46.35.52:3000/users/" + passwordEmail.getText().toString().trim();

                            String op = getJSON(stringUrl, 1000000);

                            ObjectMapper objectMapper = new ObjectMapper();

                            Usuario usuario = new Usuario();

                            try {
                                usuario = objectMapper.readValue(op, Usuario.class);
                            }
                            catch (JsonParseException e) {
                                e.printStackTrace();
                            } catch (JsonMappingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //Ya tenemos los datos del usuario, vamos a comprobar s su contraseña es correcta

                            final Intent Responder = new Intent (getApplicationContext(), Responder_preguntas.class);

                            Responder.putExtra("userEmail", passwordEmail.getText().toString().trim());
                            Responder.putExtra("userPregunta", usuario.getPregunta());
                            Responder.putExtra("userRespuesta", usuario.getRespuesta());

                            startActivity(Responder);
                            Password.this.finish();

                        }
                    };

                    inicio.start();

                } else {

                    // Crear el Intent
                    final Intent pass = new Intent(getApplicationContext(), Password.class);

                    //esto es para pasar datos de una actividad a otra
                    pass.putExtra("userEmailIntroducido", passwordEmail.getText().toString().trim());
                    pass.putExtra("tagEmail", true);

                    startActivity(pass);

                    Password.this.finish();

                }
            }
        };

        inicio.start();

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



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent login = new Intent(getApplicationContext(), Login.class);
            startActivity(login);
            Password.this.finish();
            // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
            return true;
        }
        //para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }

}
