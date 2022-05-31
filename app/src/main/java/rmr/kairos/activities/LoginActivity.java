package rmr.kairos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import rmr.kairos.R;


/**
 * Clase que permite realizar el inicio de sesión en la aplicación
 * Conecta con la base de datos MySQL para cotejar los datos introducidos y da acceso
 * @author Rafa M.
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPass; // edit text con contraseña y usuario
    private Button loginButton; //botón de login
    private TextView tvToRegister;
    private ImageView imBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsernameL);
        etPass = findViewById(R.id.etPassL);
        loginButton = findViewById(R.id.loginButton);
        tvToRegister = findViewById(R.id.tvToRegister);
        imBack = findViewById(R.id.imBack);

        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToMain = new Intent(getApplicationContext(), rmr.kairos.activities.MainActivity.class);
                startActivity(intentToMain);
                finish();
            }
        });

        tvToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToRegister = new Intent(getApplicationContext(), rmr.kairos.activities.SignUpActivity.class);
                startActivity(intentToRegister);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password;
                username = String.valueOf(etUsername.getText().toString());
                password = String.valueOf(etPass.getText().toString());

                if (!username.equals("") && !password.equals("")) {
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[2];
                            field[0] = "username";
                            field[1] = "password";
                            //Creating array for data
                            String[] data = new String[2];
                            data[0] = username;
                            data[1] = password;
                            PutData putData = new PutData("http://192.168.0.14/login/LogIn-SignUp-master/LogIn-SignUp-master/login.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if (result.equals("Login Success")) {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                    //End ProgressBar (Set visibility to GONE)
                                }
                            }
                            //End Write and Read data with URL
                        }
                    });
                } else
                    Toast.makeText(getApplicationContext(), R.string.strAllFields, Toast.LENGTH_SHORT).show();
            }
        });

    }
}