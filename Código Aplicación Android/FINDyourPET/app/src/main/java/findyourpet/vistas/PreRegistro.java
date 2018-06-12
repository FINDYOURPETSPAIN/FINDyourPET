/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.vistas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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


public class PreRegistro extends AppCompatActivity implements View.OnClickListener {


    private LinearLayout registro;

    private ScrollView scrollview_registro;

    private LinearLayout layout_registro;

    private EditText textNombre;
    private EditText textEmail;
    private EditText textPassword;
    private EditText textPasswordRepeat;

    private Button boton_continuar;

    private Usuario usuario;
    View focusView = null;
    boolean cancel = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_doblepass);

        iniciarVistas();
        iniciarListeners();
        iniciarObjetos();
        comprobarErrores();
    }

    private void comprobarErrores(){
        View focusView = null;
        boolean cancel = false;
        textEmail.setError(null);

        if(getIntent().getStringExtra("userEmailIntroducido") != null){
            textEmail.setText(getIntent().getStringExtra("userEmailIntroducido"));
        }

        if(getIntent().getStringExtra("userName") != null){
            textNombre.setText(getIntent().getStringExtra("userName"));
        }

        if(getIntent().getBooleanExtra("tagEmail",false)) {
            textEmail.setError("Usuario ya Registrado!");
            focusView = textEmail;
        }

        if (cancel) {
            focusView.requestFocus();
        }

    }

    /**
     * Este metodo inicializa las vistas
     */


    private void iniciarVistas() {

        registro = (LinearLayout) findViewById(R.id.preregistro);

        scrollview_registro = (ScrollView) findViewById(R.id.scrollview_registro);

        layout_registro = (LinearLayout) findViewById(R.id.layout_preregistro);

        textNombre = (EditText) findViewById(R.id.nombre) ;
        textEmail = (EditText) findViewById(R.id.email);
        textPassword = (EditText) findViewById(R.id.password);
        textPasswordRepeat = (EditText) findViewById(R.id.password_repeat);

        boton_continuar = (Button) findViewById(R.id.boton_continuar);

    }

    /**
     * Este metodo inicializa los listeners
     */

    private void iniciarListeners() {

        boton_continuar.setOnClickListener(this);

    }


    /**
     * Este método inicializa los objetos necesarios para la clase
     */
    private void iniciarObjetos() {

        usuario = new Usuario();

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

    private boolean isPasswordValid(String password) {

        return password.length() > 4;

    }

    private boolean attemptLogin() {

        cancel = false;

        // Reset errors.
        textEmail.setError(null);
        textNombre.setError(null);
        textPassword.setError(null);
        textPasswordRepeat.setError(null);


        if (TextUtils.isEmpty(textNombre.getText().toString().trim())) {
            textNombre.setError(getString(R.string.error_campo_obligatorio));
            focusView = textNombre;
            cancel = true;
        }
        else {

            usuario.setNombre(textNombre.getText().toString().trim());

            if (TextUtils.isEmpty(textEmail.getText().toString().trim())) {
                textEmail.setError(getString(R.string.error_campo_obligatorio));
                focusView = textEmail;
                cancel = true;
            } else if (!isEmailValid(textEmail.getText().toString().trim())) {
                textEmail.setError(getString(R.string.error_email_invalido));
                focusView = textEmail;
                cancel = true;
            }
            else {
                usuario.setEmail(textEmail.getText().toString().trim());

                if (TextUtils.isEmpty(textPassword.getText().toString().trim())) {
                    textPassword.setError(getString(R.string.error_campo_obligatorio));
                    focusView = textPassword;
                    cancel = true;
                }else if (!isPasswordValid(textPassword.getText().toString().trim())) {
                    textPassword.setError(getString(R.string.Error_password_invalida));
                    focusView = textPassword;
                    cancel = true;
                }else if (TextUtils.isEmpty(textPasswordRepeat.getText().toString().trim())) {
                    textPasswordRepeat.setError(getString(R.string.Error_password_diferente));
                    focusView = textPasswordRepeat;
                    cancel = true;
                }
                else {

                    String pass1 = textPassword.getText().toString().trim();
                    String pass2 = textPasswordRepeat.getText().toString().trim();

                    if (!pass1.equals(pass2)) { 
                        textPasswordRepeat.setError(getString(R.string.Error_password_diferente));
                        focusView = textPasswordRepeat;
                        cancel = true;
                    }
                    else{

                        usuario.setPassword(textPassword.getText().toString().trim());

                    }
                }

            }

        }

        if (cancel) {
            focusView.requestFocus();
        }

        return cancel;
    }



    @SuppressLint("ResourceAsColor")
    private void postDataToSQLite() {


        Thread inicio = new Thread() {
            public void run() {
                String stringUrl = "http://104.46.35.52:3000/users/" + textEmail.getText().toString().trim() + "/exists";

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

                    // Crear el Intent
                    final Intent PreReg = new Intent(getApplicationContext(), PreRegistro.class);

                    //esto es para pasar datos de una actividad a otra
                    PreReg.putExtra("userEmailIntroducido", usuario.getEmail());
                    PreReg.putExtra("userName", usuario.getNombre());
                    PreReg.putExtra("tagEmail", true);

                    startActivity(PreReg);

                    PreRegistro.this.finish();

                } else {

                    final Intent registroclass = new Intent (getApplicationContext(), Registro.class);

                    registroclass.putExtra("userEmail", usuario.getEmail());
                    registroclass.putExtra("userPass", usuario.getPassword());
                    registroclass.putExtra("userName", usuario.getNombre());

                    startActivity(registroclass);
                    PreRegistro.this.finish();

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
            PreRegistro.this.finish();
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
