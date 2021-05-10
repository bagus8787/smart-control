package com.example.smart_control.ui.loginFirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smart_control.R;
import com.example.smart_control.ui.user.feeder.HomeFeederActivity;
import com.example.smart_control.ui.user.feeder.ScanDeviceActivity;
import com.example.smart_control.ui.user.feeder.ScanSecretKeyActivity;
import com.example.smart_control.utils.SharedPrefManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFirebaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //Deklarasi Variable
    ProgressDialog progressDialog; //Membuat kemajuan/progres saat login
    private SignInButton googleButton; //Sebuah Tombol khusus Untuk Login
    private GoogleSignInOptions GSO; //Untuk Konfigurasi Google SignIn
    private GoogleApiClient mGoogleApiClient; //Membuat API/Antarmuka Untuk Client
    private FirebaseAuth firebaseAuth; //Untuk Autentifikasi
    private FirebaseAuth.AuthStateListener authStateListener; //Untuk Menangani Kajadian Saat Autentifikasi
    private final int RC_SIGN_ID = 9001; //Kode Unik Untuk SignIn
    private final String TAG = "SignInAcivity";  //Untuk Log Debugging
    FirebaseAnalytics firebaseAnalytics;

    SharedPrefManager sharedPrefManager;

    Button btn_secret_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_firebase);

        sharedPrefManager = new SharedPrefManager(this);
        btn_secret_key = findViewById(R.id.btn_secret_key);
        btn_secret_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginFirebaseActivity.this, ScanSecretKeyActivity.class));
                finish();
            }
        });

        googleButton = findViewById(R.id.login_google);
        FirebaseConnect();
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });
    }


    //Kumpulan Konfigurasi Untuk Menghubungkan dengan Firebase
    private void FirebaseConnect(){
        //Statement
        firebaseAnalytics = FirebaseAnalytics.getInstance(this); //Menghubungkan dengan Firebase Analytics
        firebaseAuth = FirebaseAuth.getInstance(); //Menghubungkan dengan Firebase Authentifikasi
        progressDialog = new ProgressDialog(this); //Sebuah ProgressDialog untuk menandakan program sedang dieksekusi

        //Authentifikasi Listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                /*
                 * Jika User sebelumnya telah masuk/login dan belum keluar/logout, secara otomatis
                 * Saat aplikasi dibuka kembali, Activity ini akan langsung dialihkan pada Activiy MainMenu
                 */

                Log.d("email", "= " + firebaseAuth.getCurrentUser());


                if(firebaseAuth.getCurrentUser() != null){
                    if(sharedPrefManager.getSpSecretKey() == ""){
                        startActivity(new Intent(LoginFirebaseActivity.this, ScanDeviceActivity.class));
                        finish();
                    } else {
                        Log.d("emailsss", "= " + firebaseAuth.getCurrentUser().getDisplayName());

                        sharedPrefManager.saveSPString(SharedPrefManager.SP_EMAIL, firebaseAuth.getCurrentUser().getEmail());
                        sharedPrefManager.saveSPString(SharedPrefManager.SP_NAMA, firebaseAuth.getCurrentUser().getDisplayName());
                        sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN,true);
//                        firebaseAuth.getCurrentUser().getEmail();
                        startActivity(new Intent(LoginFirebaseActivity.this, HomeFeederActivity.class));
//                        finish();
                    }

                    finish();
                }
            }
        };

        // Konfigurasi GoogleSignIn, Dengan cara bagaimanakah User akan Masuk
        GSO = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, GSO)
                .build();
    }

    //Konfiurasi Untuk Masuk ke Akun Google
    private void SignIn(){
        Intent GSignIN = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(GSignIN, RC_SIGN_ID);
        progressDialog.setMessage("Mohon Tunggu...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Autentikasi Berhasil", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(),"Autentikasi Gagal", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    //Menagani Kejadian Saat Gagal Koneksi
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "OnConnectionFailed" + connectionResult);
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(),"Koneksi Tidak Terhubung", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Jika User Telah Masuk/Login, makan akan mengangani kajadian apakah user telah masuk
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    //Saat Aktifitas dihentikan, maka listener akan dihapus
    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Mendapatkan Akses untuk SignIn/Masuk Jika permintaan dari Firebase Autentifikasi Terpenuhi
        if (requestCode == RC_SIGN_ID) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else {
                progressDialog.dismiss();
            }
        }else{
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}