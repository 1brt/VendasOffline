package br.com.vendasoffline.vendasoffline.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.classes.GetJson;
import br.com.vendasoffline.vendasoffline.helpers.InputValidation;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

/**
 * Created by home on 26/06/17.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity = LoginActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutUsuario;
    private TextInputLayout textInputLayoutSenha;

    private TextInputEditText textInputEditTextUsuario;
    private TextInputEditText textInputEditTextSenha;

    private AppCompatButton appCompatButtonLogin;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;

    private AppCompatTextView textViewLinkRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        // Sincroniza os dados, pegando pelo webservice.
        new GetJson(getBaseContext()).execute();

        initViews();
        initListeners();
        initObjects();
    }

    private void initViews() {

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        textInputLayoutUsuario = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutSenha = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputEditTextUsuario = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextSenha = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);
        textViewLinkRegister = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);

    }

    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        inputValidation = new InputValidation(activity);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                verifyFromSQLite();
                break;
            case R.id.textViewLinkRegister:
                // Navigate to RegisterActivity
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
                emptyInputEditText();
                break;
        }
    }

    private void verifyFromSQLite() {
        String usuario = textInputEditTextUsuario.getText().toString().trim();

        if (usuario.contains("@")){
            if (!inputValidation.isInputEditTextFilled(textInputEditTextUsuario, textInputLayoutUsuario, getString(R.string.error_message_email))) {
                return;
            }
            if (!inputValidation.isInputEditTextEmail(textInputEditTextUsuario, textInputLayoutUsuario, getString(R.string.error_message_email))) {
                return;
            }
            if (!inputValidation.isInputEditTextFilled(textInputEditTextSenha, textInputLayoutSenha, getString(R.string.error_message_email))) {
                return;
            }
        }

        if (databaseHelper.checkUser(usuario, textInputEditTextSenha.getText().toString().trim())) {
            Intent menuPrincipal = new Intent(this, MainActivity.class);
            menuPrincipal.putExtra("Usuario",usuario);
            startActivity(menuPrincipal);
            //emptyInputEditText();
            finish();

        } else {
            // Snack Bar to show success message that record is wrong
            Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
        }
    }

    private void emptyInputEditText() {
        textInputEditTextUsuario.setText(null);
        textInputEditTextSenha.setText(null);
    }
}