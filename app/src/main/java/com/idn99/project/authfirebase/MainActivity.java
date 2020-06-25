package com.idn99.project.authfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    EditText edtEmail, edtPass;
    Button btnLoginEmail;
    FirebaseAuth auth;

    SignInButton signInButton;
    GoogleSignInClient googleSignInClient;
    private String TAG = "MainActivity";
    private int CODE_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = findViewById(R.id.edt_email);
        edtPass = findViewById(R.id.edt_password);
        btnLoginEmail = findViewById(R.id.btn_login_email);
        signInButton = findViewById(R.id.btn_login_google);
        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("488101877408-jupvh6kvdmn4emo2cotqj1n18te2lt61.apps.googleusercontent.com")
                .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, CODE_SIGN_IN);
            }
        });

        btnLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmail.getText().toString().isEmpty()){
                    edtEmail.setError("Email tidak boleh kosong");
                }else if (edtPass.getText().toString().isEmpty()){
                    edtPass.setError("Password tidak boleh kosong");
                }else{
                    loginCek();
                }
            }
        });

    }

    private void loginCek(){
        auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Berhasil Login", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Beranda.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(MainActivity.this, "Gagal Login, Cek Email dan Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleLoginGoogle(task);
        }
    }

    private void handleLoginGoogle(Task<GoogleSignInAccount> completedTask){

        try {
            GoogleSignInAccount gsi = completedTask.getResult(ApiException.class);
//            Toast.makeText(this, "Berhasil Login", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(gsi);
        }catch (ApiException e){
            Toast.makeText(this, "Gagal Login", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount gsi){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(gsi.getIdToken(), null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Berhasil Login", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(user);
                }else{
                    Toast.makeText(MainActivity.this, "Gagal Login", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user){

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String email = account.getEmail();
            String idUser = account.getId();
            Uri photoUser = account.getPhotoUrl();
//            Toast.makeText(this, personName, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), Beranda.class);
//            intent.putExtra("name", personName);
            startActivity(intent);
        }
    }
}
