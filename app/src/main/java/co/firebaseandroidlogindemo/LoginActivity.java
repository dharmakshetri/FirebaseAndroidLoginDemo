package co.firebaseandroidlogindemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static  final  String TAG="FIREBASE AUTH";
   // private Button btnLogin;
    private EditText etEmail, etPassword;
    private TextView tvStatus,tvDetails;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Views
        tvStatus=(TextView) findViewById(R.id.status);
        tvDetails=(TextView) findViewById(R.id.detail);
        etEmail=(EditText) findViewById(R.id.et_email);
        etPassword=(EditText) findViewById(R.id.et_password);


        //Buttons
        findViewById(R.id.btnSignIn).setOnClickListener(this);
        findViewById(R.id.btnCreateAccount).setOnClickListener(this);
        findViewById(R.id.btnSignOut).setOnClickListener(this);

        mAuth=FirebaseAuth.getInstance();

        firebaseAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                if(firebaseUser!=null){
                    Log.e(TAG," onAuthStateChange: singed_in"+firebaseUser.getUid());
                }else{
                    Log.e(TAG," onAuthStateChange: singed_out");
                }
                updateUI(firebaseUser);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth!=null){
            mAuth.addAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
    if(v.getId()==R.id.btnSignIn){
        singIn(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
    }else if(v.getId()==R.id.btnCreateAccount){
        createNewAccount(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
    }else  if(v.getId()==R.id.btnSignOut){
        signOut();
    }
    }


    //update UIs
    public void updateUI(FirebaseUser user){

        hideProgressDialog();

        if(user!=null){

            tvStatus.setText(getString(R.string.emailpassword_status_fmt, user.getEmail()));
            tvDetails.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.btnSignOut).setVisibility(View.VISIBLE);
        } else {
            tvStatus.setText(R.string.signed_out);
            tvDetails.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.btnSignOut).setVisibility(View.GONE);
        }
    }


    // for sign out

    public  void signOut(){
        mAuth.signOut();
        updateUI(null);
    }
    // for sigin

    public  void singIn(String email, String password){
        if(!validateForm()){
            return;
        }

        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"User Login Failed", Toast.LENGTH_SHORT).show();
                            tvStatus.setText("Authentication Failed, Create New Account or Enter correct Credentials");
                        }

                        hideProgressDialog();
                    }
                });

    }

    // create new account
    public void createNewAccount(String email, String password){
        if(!validateForm()){
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                         if(!task.isSuccessful()){
                             Toast.makeText(LoginActivity.this,"User Create Sucessuflly", Toast.LENGTH_SHORT).show();
                         }
                        hideProgressDialog();
                    }
                });


    }

    public boolean validateForm(){
        boolean valid=true;

        String email=etEmail.getText().toString().trim();
        String password=etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            etEmail.setError("Required, Email field");
            valid=false;
        }else{
            etEmail.setError(null);
        }

        if(TextUtils.isEmpty(password)){
            etPassword.setError("Requried, Password Field");
            valid=false;
        }else{
            etPassword.setError(null);
        }

        return valid;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
