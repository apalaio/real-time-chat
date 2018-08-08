package app.real_time_chat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.io.IOException;

import static app.real_time_chat.R.id.btn_register;
import static app.real_time_chat.R.id.btn_signin;


public class LoginActivity extends AppCompatActivity {


    // UI references.
    private TextView txtAbout;
    private TextView txtEmail;
    private TextView txtPwd;
    private FirebaseAuth firebaseAuth;
    private Button btnRegister;
    private Button btnSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.chaticon);

        txtEmail = (TextView) findViewById(R.id.email);
        txtPwd = (TextView) findViewById(R.id.password);
        btnRegister = (Button) findViewById(btn_register);
        btnSignin = (Button) findViewById(btn_signin);


        firebaseAuth = FirebaseAuth.getInstance();

        txtAbout = (TextView) findViewById(R.id.textViewAbout);


        // "about" textview
        txtAbout = (TextView) findViewById(R.id.textViewAbout);
        txtAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("About");
                alertDialog.setMessage(" Real time chat app! \n Author: Aristotelis Palaiologopoulos \n University of Piraeus ");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait...", "Proccessing...", true);

                try{
                    (firebaseAuth.signInWithEmailAndPassword(txtEmail.getText().toString(), txtPwd.getText().toString()))
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                                        startActivity(i);
                                    } else {
                                        Log.e("ERROR", task.getException().toString());
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }catch(Exception e) {
                    progressDialog.hide();
                    e.printStackTrace();
                   Toast.makeText(LoginActivity.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**  else
     Toast.makeText(LoginActivity.this, "Please enter a valid email address/ password", Toast.LENGTH_LONG).show();
     *
     * public void btnUserLogin_Click(View v) {
        if (txtEmail.getText() != null && txtPwd.getText() != null) {
            final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait...", "Proccessing...", true);

            (firebaseAuth.signInWithEmailAndPassword(txtEmail.getText().toString(), txtPwd.getText().toString()))
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();

                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                                startActivity(i);
                            } else {
                                Log.e("ERROR", task.getException().toString());
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        } else
            Toast.makeText(LoginActivity.this, "Please enter a valid email address/ password", Toast.LENGTH_LONG).show();
    }

    public void btnUserLoginRegister_Click(View v){

        Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intent);
    } **/
}

