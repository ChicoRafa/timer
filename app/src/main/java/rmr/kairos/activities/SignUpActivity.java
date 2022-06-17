package rmr.kairos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import rmr.kairos.R;
import rmr.kairos.database.KairosDB;

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
    private KairosDB db;

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
                    db = new KairosDB(SignUpActivity.this);
                    long id = db.insertUser(username, password, mail);
                    if (id > 0) {
                        Toast.makeText(SignUpActivity.this, R.string.strSuccesfulRegister, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else Toast.makeText(getApplicationContext(), R.string.strAllFields, Toast.LENGTH_SHORT).show();
            }
        });


    }
}