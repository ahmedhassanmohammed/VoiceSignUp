package com.example.abanoub.voicebasedemailsystem;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abanoub.voicebasedemailsystem.Shaking.MyService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;

public class SignInActivity extends Activity implements View.OnClickListener{

    TextView GotoSignUp;
    FirebaseAuth firebaseAuth;

    public static boolean isServiceRunning = false;


    //Handler work every x time
    Handler h = new Handler();    // every 10 sec
    int delay = 10*1000; //1 second=1000 milisecond, 10*1000=15seconds
    Runnable runnable;

    //UI View
    EditText emailEdit, passwordEdit;
    Button loginBtn;
    String emailString = null, passwordString = null;

    Boolean isEmail = false, isPassword = false;

    static String emailSpeech = "Please enter your email";
    static String passSpeech = "Please enter your password";
    static String passError = "Password must be at least six character";
    static String loginFailedError = "User name or password incorrect please enter again";
    static String loginSuccess = "Login successfully and you in home home";

    //Text to speech API
    TextToSpeech txtToSpeech;

    //speech to text API
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        startService(new Intent(this, MyService.class));

        firebaseAuth = FirebaseAuth.getInstance();

        emailEdit= (EditText) findViewById(R.id.emailEdit);
        passwordEdit= (EditText) findViewById(R.id.passwordEdit);
        loginBtn= (Button) findViewById(R.id.loginBtn);
        GotoSignUp = (TextView) findViewById(R.id.signup_link);

        loginBtn.setOnClickListener(this);
        GotoSignUp.setOnClickListener(this);

        txtToSpeech =new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    txtToSpeech.setLanguage(Locale.UK);
                }
            }
        });

//        email.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(password.getText())==false) {
//                    signin_btn.setEnabled(true);
//                } else {
//                    signin_btn.setEnabled(false);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });
//
//        password.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(email.getText())==false) {
//                    signin_btn.setEnabled(true);
//
//                } else {
//                    signin_btn.setEnabled(false);
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent=new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onResume() {
        //start handler as activity become visible

        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                checkLoginFun();
                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    private void checkLoginFun(){
        if (emailString == null){
            txtToSpeech.speak(emailSpeech, TextToSpeech.QUEUE_FLUSH, null);
            promptSpeechInput();
            isEmail = true;
            isPassword = false;
        }else{
            if (passwordString == null){
                txtToSpeech.speak(passSpeech, TextToSpeech.QUEUE_FLUSH, null);
                promptSpeechInput();
                isPassword = true;
                isEmail = false;
            }else{
                Log.d("response length" , "  "+ passwordString.length());
                if(passwordString.length() < 6 ){
                    passwordString = null;
                    txtToSpeech.speak(passError, TextToSpeech.QUEUE_FLUSH, null);
                    promptSpeechInput();
                    isPassword = true;
                    isEmail = false;
                }else{
                    //login
                    loginFunc();
                }

            }
        }
    }

    private void loginFunc(){
        if (Utilities.isNetworkAvailable(SignInActivity.this)) {
            if(emailString.endsWith("@vmail.com")){
            }else{
                emailString = emailString + "@vmail.com";
            }

            if(passwordString.startsWith("password")){
                passwordString = passwordString.replace("password", "");
            }

            if (emailString.startsWith("email")) {
                emailString = emailString.replace("email", "");

            }


            //remove all space between characters
            passwordString = passwordString.replaceAll("\\s+","");
            emailString = emailString.replaceAll("\\s", "");

            firebaseAuth.signInWithEmailAndPassword(emailString, passwordString)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("response", "Succcccccccccccccccccccccessssssss");
                                txtToSpeech.speak(loginSuccess, TextToSpeech.QUEUE_FLUSH, null);
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                txtToSpeech.speak(loginFailedError, TextToSpeech.QUEUE_FLUSH, null);
                                loginFailed();
                                Log.d("response", "Faileeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeed");

                            }
                        }
                    });
        }else
            Toast.makeText(SignInActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
    }

    private void loginFailed(){
        emailEdit.setText("");
        passwordEdit.setText("");
        passwordString = null;
        emailString = null;
        isEmail = false;
        isPassword = false;
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true); //exit app
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //called to stop handler if user login successfully
        h.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //called to stop handler if user login successfully
        h.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginBtn:
                Log.d("response", "Login");
                txtToSpeech.speak(passSpeech, TextToSpeech.QUEUE_FLUSH, null);
                break;

            case R.id.signup_link:
                Intent intent = new Intent(SignInActivity.this, SignUp1Activity.class);
                startActivity(intent);
                break;
        }
    }

    //speech to text
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if (isEmail){
                        emailString = result.get(0);
                        emailEdit.setText(emailString);
                    }

                    if(isPassword){
                        Log.d("password is ",  result.get(0));
                        passwordString = result.get(0);
                        passwordEdit.setText(passwordString);
                    }

                    Log.d("response is ", result.get(0));
                }
                break;
            }
        }
    }
}