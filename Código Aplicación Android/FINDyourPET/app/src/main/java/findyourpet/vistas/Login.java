/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
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


public class Login extends AppCompatActivity implements OnClickListener {


    // UI references.
    private EditText EmailText;
    private EditText PasswordText;
    private View mProgressView;
    private View ScrollView_Login;
    AppCompatButton Boton_Entrar;
    AppCompatButton Boton_Registrate;
    AppCompatButton Boton_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        iniciarVistas();
        iniciarListeners();
        comprobarErrores();

    }

    private void  comprobarErrores(){
        View focusView = null;
        boolean cancel = false;
        EmailText.setError(null);
        PasswordText.setError(null);

        if(getIntent().getStringExtra("userEmailIntroducido") != null){
            EmailText.setText(getIntent().getStringExtra("userEmailIntroducido"));
        }

        if(getIntent().getBooleanExtra("tagEmail",false)) {
            EmailText.setError("Usuario no Registrado");
            focusView = EmailText;
        }



        if(getIntent().getBooleanExtra("tagPass",false)) {
                PasswordText.setError("Contraseña Incorrecta");
                focusView = PasswordText;
        }


        if (cancel) {
            focusView.requestFocus();
        }

    }


    @SuppressLint("WrongViewCast")
    private void iniciarVistas() {

        EmailText = (EditText) findViewById(R.id.email);
        PasswordText = (EditText) findViewById(R.id.password);
        Boton_Entrar = (AppCompatButton) findViewById(R.id.boton_entrar);
        Boton_Registrate = (AppCompatButton) findViewById(R.id.boton_crearcuenta);
        Boton_Password = (AppCompatButton) findViewById(R.id.boton_password);
        ScrollView_Login = findViewById(R.id.scrollview_login);

    }

    private void iniciarListeners() {

        Boton_Entrar.setOnClickListener(this);
        Boton_Registrate.setOnClickListener(this);
        Boton_Password.setOnClickListener(this);

    }


    private boolean attemptLogin() {

        EmailText.setError(null);
        PasswordText.setError(null);

        String email = EmailText.getText().toString();
        String password = PasswordText.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(password)) {
            PasswordText.setError(getString(R.string.error_campo_obligatorio));
            focusView = PasswordText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            EmailText.setError(getString(R.string.error_campo_obligatorio));
            focusView = EmailText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            EmailText.setError(getString(R.string.error_email_invalido));
            focusView = EmailText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }

        return cancel;
    }


    // Comprobación de email.
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


    private boolean isPasswordValid(String password) {

        return password.length() > 4;

    }



    // Switch con todos los casos de los listeners

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.boton_entrar:

                if(!attemptLogin())
                    postDataToSQLite();
                break;

            case R.id.boton_crearcuenta:

                Intent registerView = new Intent (getApplicationContext(), PreRegistro.class);
                startActivity(registerView);
                Login.this.finish();
                break;

            case R.id.boton_password:

                //Versión buena
                Intent passwordView = new Intent (getApplicationContext(), Password.class);
                startActivity(passwordView);
                Login.this.finish();
                break;

        }
    }


    private void postDataToSQLite() {
        // Aqui se deben realizar validaciones de inputs correctos

        Thread inicio = new Thread() {
            public void run() {
                String stringUrl = "http://104.46.35.52:3000/users/" + EmailText.getText().toString().trim() + "/exists";

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

                            String stringUrl = "http://104.46.35.52:3000/users/" + EmailText.getText().toString().trim();

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

                            if(PasswordText.getText().toString().trim().equals(usuario.getPassword())){

                                // Crear el Intent
                                final Intent homeClass = new Intent(getApplicationContext(), Home.class);

                                //esto es para pasar datos de una actividad a otra
                                homeClass.putExtra("userId", usuario.getId());

                                startActivity(homeClass);

                                Login.this.finish();

                            }
                            else {

                                // Crear el Intent
                                final Intent login = new Intent(getApplicationContext(), Login.class);

                                //esto es para pasar datos de una actividad a otra
                                login.putExtra("userEmailIntroducido", usuario.getEmail());
                                login.putExtra("tagEmail", false);
                                login.putExtra("tagPass", true);

                                startActivity(login);

                                Login.this.finish();
                            }

                        }
                    };

                    inicio.start();

                } else {

                    // Crear el Intent
                    final Intent login = new Intent(getApplicationContext(), Login.class);

                    //esto es para pasar datos de una actividad a otra
                    login.putExtra("userEmailIntroducido", EmailText.getText().toString().trim());
                    login.putExtra("tagEmail", true);
                    login.putExtra("tagPass", false);

                    startActivity(login);

                    Login.this.finish();

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


}

