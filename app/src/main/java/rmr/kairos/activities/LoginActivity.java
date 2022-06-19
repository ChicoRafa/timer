package rmr.kairos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import rmr.kairos.R;
import rmr.kairos.database.KairosDB;
import rmr.kairos.model.Usuario;


/**
 * Clase que permite realizar el inicio de sesión en la aplicación
 * Conecta con la base de datos SQLite para cotejar los datos introducidos y da acceso
 * @author Rafa M.
 * @version 2.0
 * @since 1.0
 */
public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPass; // edit text con contraseña y usuario
    private Button loginButton; //botón de login
    private TextView tvToRegister;
    private ImageView imBack;
    private final int RQ_LOGIN = 14;
    private final String IK_LOGIN = "login_key";
    private ActivityResultLauncher<Intent> launcher;
    private final ActivityResultRegistry mRegistry;
    private KairosDB db;

    public LoginActivity(){
        this.mRegistry= new ActivityResultRegistry() {
            @Override
            public <I, O> void onLaunch(int requestCode, @NonNull ActivityResultContract<I, O> contract, I input, @Nullable ActivityOptionsCompat options) {
                ComponentActivity activity = LoginActivity.this;
                Intent intent = contract.createIntent(activity, input);
                Bundle optionsBundle = null;
                if (intent.getExtras() != null && intent.getExtras().getClassLoader() == null) {
                    intent.setExtrasClassLoader(activity.getClassLoader());
                }
                ActivityCompat.startActivityForResult(activity, intent, requestCode, optionsBundle);
            }
        };
    }

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
                Intent intentToMain = new Intent(LoginActivity.this, rmr.kairos.activities.MainActivity.class);
                startActivity(intentToMain);
                finish();
            }
        });

        tvToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToRegister = new Intent(LoginActivity.this, rmr.kairos.activities.SignUpActivity.class);
                intentToRegister.putExtra(IK_LOGIN, RQ_LOGIN);
                launcher.launch(intentToRegister);

            }
        });
        this.setUpLauncher();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password;
                username = String.valueOf(etUsername.getText().toString());
                password = String.valueOf(etPass.getText().toString());

                if (!username.equals("") && !password.equals("")) {
                    db = new KairosDB(LoginActivity.this);
                    ArrayList<Usuario> listaUsuarios = db.selectUsers();
                    for (int i = 0; i < listaUsuarios.size(); i++) {
                        if (listaUsuarios.get(i).getUsername().equals(username) && listaUsuarios.get(i).getPassword().equals(password)) {
                            Toast.makeText(LoginActivity.this, R.string.strSuccesfulLogin , Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("Logueado",true);
                            startActivity(intent);
                            finish();
                            Log.d("RMRTAG", "onClick: " + listaUsuarios.get(0).getUsername());
                        }
                    }
                }else Toast.makeText(LoginActivity.this, R.string.strLoginFail , Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void setUpLauncher(){
        this.launcher = this.mRegistry.register(IK_LOGIN,
                new ActivityResultContracts.StartActivityForResult(),
                null);
    }
}