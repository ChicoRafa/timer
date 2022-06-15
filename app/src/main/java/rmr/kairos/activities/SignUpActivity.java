package rmr.kairos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import rmr.kairos.R;

/**
 *
 */
public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPass, etMail;
    private Button registerButton;
    private TextView tvToLogin;
    private ImageView imBack;
    private final int RQ_LOGIN = 14;
    private final int RQ_REGISTER = 15;
    private final String IK_LOGIN = "login_key";
    private Intent intentFromLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etUsername = findViewById(R.id.etUsernameR);
        etPass = findViewById(R.id.etPassR);
        etMail = findViewById(R.id.etMailR);
        registerButton = findViewById(R.id.registerButton);
        tvToLogin = findViewById(R.id.tvToLogin);
        imBack = findViewById(R.id.imBack);
        this.intentFromLogin = getIntent();

        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToLogin = new Intent(SignUpActivity.this, LoginActivity.class);
                intentToLogin.putExtra(IK_LOGIN,RQ_REGISTER);
                setResult(RQ_LOGIN,intentToLogin);
                finish();
            }
        });

        tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToLogin = new Intent(SignUpActivity.this, LoginActivity.class);
                intentToLogin.putExtra(IK_LOGIN,RQ_REGISTER);
                setResult(RQ_LOGIN,intentToLogin);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password, mail;
                username = String.valueOf(etUsername.getText().toString());
                password = String.valueOf(etPass.getText().toString());
                mail = String.valueOf(etMail.getText().toString());

                if (!username.equals("") && !password.equals("") && !mail.equals("")) {
                    //Start ProgressBar first (Set visibility VISIBLE)
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[3];
                            field[0] = "username";
                            field[1] = "password";
                            field[2] = "email";
                            //Creating array for data
                            String[] data = new String[3];
                            data[0] = username;
                            data[1] = password;
                            data[2] = mail;
                            PutData putData = new PutData("http://192.168.0.15/login/LogIn-SignUp-master/LogIn-SignUp-master/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if (result.equals("Sign Up Success")) {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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