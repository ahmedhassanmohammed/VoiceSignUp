package com.example.abanoub.voicebasedemailsystem;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

public class SignUp1Activity extends Activity implements View.OnClickListener {

    EditText fullName;
    EditText email;
    EditText password;
    EditText confirmPassword;
    Button signup_btn;
    TextView Gotologin;


    //Handler work every x time
    Handler h = new Handler();    // every 10 sec
    int delay = 10 * 1000; //1 second=1000 milisecond, 10*1000=15seconds
    Runnable runnable;


    // speech
    static String emailSpeech = "Please enter your email";
    static String nameSpeech = "Please enter your Full name";
    static String passSpeech = "Please enter your password";
    static String passError = "Password must be at least six character";
    static String SignUpFailedError = "somefield incorrect please enter again";
    static String RegisterationSuccess = "Registration successfully and you in home page";

    //Text to speech API
    TextToSpeech txtToSpeech;
    String nameString = null, emailString = null;
    String passwordString = null, confirmPasswordString = null;

    Boolean isEmail = false, isPassword = false;
    Boolean isName = false, isConfirmPassword = false;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        firebaseAuth = FirebaseAuth.getInstance();

        fullName = (EditText) findViewById(R.id.fullName);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        signup_btn = (Button) findViewById(R.id.signup_btn);
        Gotologin = (TextView) findViewById(R.id.login_link);

        txtToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    txtToSpeech.setLanguage(Locale.UK);
                }
            }
        });


        signup_btn.setOnClickListener(this);


        Gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp1Activity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible

        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                checkSignupFun();
                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    private void checkSignupFun() {
        if (emailString == null) {
            txtToSpeech.speak(emailSpeech, TextToSpeech.QUEUE_FLUSH, null);
            promptSpeechInput();
            isEmail = true;
            isPassword = false;
            isConfirmPassword=false;
            isName=false;
        }
        else if (passwordString == null) {
            txtToSpeech.speak(passSpeech, TextToSpeech.QUEUE_FLUSH, null);
            promptSpeechInput();
            isPassword = true;
            isEmail = false;
            isConfirmPassword =false;
            isName =false;
        }

        else if (passwordString.length() < 6) {
            passwordString = null;
            txtToSpeech.speak(passError, TextToSpeech.QUEUE_FLUSH, null);
            promptSpeechInput();
            isPassword = true;
            isEmail = false;
            isPassword = false;
            isConfirmPassword=false;


        }
        else if (nameString == null) {
            txtToSpeech.speak(nameSpeech, TextToSpeech.QUEUE_FLUSH, null);
            promptSpeechInput();
            isName = true;
            isPassword = false;
            isConfirmPassword=false;
            isEmail =false;
        }
        else {
            //SignUp
            SignupFunc();
        }


    }



    private void SignupFunc() {
        if (Utilities.isNetworkAvailable(SignUp1Activity.this)) {
            if (!emailString.endsWith("@vmail.com")) {
                emailString = emailString + "@vmail.com";
            }

            if (passwordString.startsWith("password")) {
                passwordString = passwordString.replace("password", "");
            }

            if (emailString.startsWith("email")) {
                emailString = emailString.replace("email", "");

            }
            if (confirmPasswordString.startsWith("Confirm Password")) {
                passwordString = passwordString.replace("Confirm Password", "");
            }

            if (nameString.startsWith("Full Name")) {
                emailString = emailString.replace("Full Name", "");

            }


            //remove all space between characters
            passwordString = passwordString.replaceAll("\\s+", "");
            emailString = emailString.replaceAll("\\s", "");
            confirmPasswordString = confirmPasswordString.replaceAll("\\s+", "");

        }

        if (Utilities.isNetworkAvailable(SignUp1Activity.this)) {
            if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(password.getText())
                    || TextUtils.isEmpty(fullName.getText()) || TextUtils.isEmpty(confirmPassword.getText()))
                Toast.makeText(SignUp1Activity.this, R.string.fields_cannot_be_empty, Toast.LENGTH_SHORT).show();
            else {
                if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(SignUp1Activity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        txtToSpeech.speak(RegisterationSuccess, TextToSpeech.QUEUE_FLUSH, null);

                                        Intent intent = new Intent(SignUp1Activity.this, SignUp2Activity.class);
                                        NewUser newUser = new NewUser(fullName.getText().toString(), email.getText().toString()
                                                , password.getText().toString());

                                        intent.putExtra("newUser", newUser);
                                        startActivity(intent);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        txtToSpeech.speak(SignUpFailedError, TextToSpeech.QUEUE_FLUSH, null);
                                        signupFailed();
                                        Toast.makeText(SignUp1Activity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else
                    Toast.makeText(SignUp1Activity.this, R.string.passwords_donot_match, Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(SignUp1Activity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
    }


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



    private void signupFailed(){
        fullName.setText("");
        email.setText("");
        password.setText("");
        confirmPassword.setText("");
        passwordString = null;
        emailString = null;
        isEmail = false;
        isPassword = false;
        isConfirmPassword=false;
        isName=false;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if (isEmail){
                        emailString = result.get(0);
                        email.setText(emailString);
                    }

                    if(isPassword){
                        Log.d("password is ",  result.get(0));
                        passwordString = result.get(0);
                        password.setText(passwordString);
                    }

                    if(isName){
                        Log.d("Fullname is ",  result.get(0));
                        nameString = result.get(0);
                        fullName.setText(nameString);
                    }

                    if(isConfirmPassword){
                        Log.d("confirm password is ",  result.get(0));
                        confirmPasswordString = result.get(0);
                        confirmPassword.setText(confirmPasswordString);
                    }

                    Log.d("response is ", result.get(0));
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup_btn:
                Log.d("response", "signup");
                txtToSpeech.speak(passSpeech, TextToSpeech.QUEUE_FLUSH, null);
                break;


        }
    }
}