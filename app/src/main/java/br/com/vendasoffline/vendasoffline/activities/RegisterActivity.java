package br.com.vendasoffline.vendasoffline.activities;

import android.support.annotation.Nullable;
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
import br.com.vendasoffline.vendasoffline.helpers.InputValidation;
import br.com.vendasoffline.vendasoffline.model.User;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = RegisterActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutNome;
    private TextInputLayout textInputLayoutUsuario;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutSenha;
    private TextInputLayout textInputLayoutConfirmaSenha;

    private TextInputEditText textInputEditTextNome;
    private TextInputEditText textInputEditTextUsuario;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextSenha;
    private TextInputEditText textInputEditTextConfirmaSenha;

    private AppCompatButton appCompatButtonRegister;
    private AppCompatTextView appCompatTextViewLoginLink;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }

    private void initViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutNome = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutUsuario = (TextInputLayout) findViewById(R.id.textInputLayoutUsuario);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutSenha = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmaSenha = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);

        textInputEditTextNome = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        textInputEditTextUsuario = (TextInputEditText) findViewById(R.id.textInputEditTextUsuario);
        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextSenha = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmaSenha = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);

        appCompatButtonRegister = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewLoginLink = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);

    }

    private void initListeners() {
        appCompatButtonRegister.setOnClickListener(this);
        appCompatTextViewLoginLink.setOnClickListener(this);
    }

    private void initObjects() {
        inputValidation = new InputValidation(activity);
        databaseHelper = new DatabaseHelper(activity);
        user = new User();

    }


    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.appCompatButtonRegister:
                postDataToSQLite();
                break;

            case R.id.appCompatTextViewLoginLink:
                finish();
                break;
        }
    }

    private void postDataToSQLite() {
        boolean hasUser,hasEmail;


        if (!inputValidation.isInputEditTextFilled(textInputEditTextNome, textInputLayoutNome, getString(R.string.error_message_name))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextUsuario, textInputLayoutUsuario, getString(R.string.error_message_user))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextSenha, textInputLayoutSenha, getString(R.string.error_message_password))) {
            return;
        }
        if (!inputValidation.isInputEditTextMatches(textInputEditTextSenha, textInputEditTextConfirmaSenha,
                textInputLayoutConfirmaSenha, getString(R.string.error_password_match))) {
            return;
        }

        hasUser  = databaseHelper.checkUser(textInputEditTextUsuario.getText().toString().trim());
        hasEmail = databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim());

        if (!hasUser && !hasEmail) {

            user.setNome(textInputEditTextNome.getText().toString().trim());
            user.setUsuario(textInputEditTextUsuario.getText().toString().trim());
            user.setEmail(textInputEditTextEmail.getText().toString().trim());
            user.setSenha(textInputEditTextSenha.getText().toString().trim());

            databaseHelper.addUser(user);

            // Snack Bar to show success message that record saved successfully
            Snackbar.make(nestedScrollView, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
            emptyInputEditText();


        } else if(hasUser){
            // Snack Bar to show error message that record already exists
            Snackbar.make(nestedScrollView, getString(R.string.error_user_exists), Snackbar.LENGTH_LONG).show();
        } else if(hasEmail) {
            // Snack Bar to show error message that record already exists
            Snackbar.make(nestedScrollView, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();
        }


    }

    private void emptyInputEditText() {
        textInputEditTextNome.setText(null);
        textInputEditTextUsuario.setText(null);
        textInputEditTextEmail.setText(null);
        textInputEditTextSenha.setText(null);
        textInputEditTextConfirmaSenha.setText(null);
        textInputEditTextNome.requestFocus();
    }
}