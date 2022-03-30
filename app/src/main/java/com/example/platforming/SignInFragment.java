package com.example.platforming;

import static com.example.platforming.Variable.firebaseAuth;
import static com.example.platforming.Variable.mGoogleApiClient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        AutoSignIn();

        setGoogle(view.findViewById(R.id.google_signIn), "Google로 계속하기");
        setFacebook(view);
        SetListener(view);

        return view;
    }

    //리스너 설정
    private void SetListener(View view){
        Button confirm = (Button)view.findViewById(R.id.confirm_signIn);
        Button signOut = (Button)view.findViewById(R.id.signUp_signIn);
        Button findPassword = (Button)view.findViewById(R.id.findPassword_singIn);
        TextView email = (TextView)view.findViewById(R.id.email_signIn);
        TextView password = (TextView)view.findViewById(R.id.password_signIn);
        Switch autoSignIn = (Switch)view.findViewById(R.id.autoSignIn_signIn);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn(email.getText().toString(), password.getText().toString(), autoSignIn.isChecked());
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout_signIn, new SignUpFragment()).addToBackStack(null).commit();
            }
        });
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail(email.getText().toString());

                Bundle bundle = new Bundle();
                bundle.putString("Type", "findPassword");
                EmailAlarmFragment emailAlarmFragment = new EmailAlarmFragment();
                emailAlarmFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout_signIn, emailAlarmFragment).addToBackStack(null).commit();
            }
        });
    }

    //자동 로그인
    private void AutoSignIn(){
        if(firebaseAuth.getCurrentUser() != null){
            toggleActivity();
        }
    }

    //로그인(Email)
    private void SignIn(String email, String password, boolean autoSignIn){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //로그 출력
                    Log.w("SignInFragment", "signInWithEmailAndPassword Success");

                    //Activity 변경
                    toggleActivity();
                }else{
                    //로그 출력
                    Log.w("SignInFragment", "signInWithEmailAndPassword Error");
                    CustomDialog.ErrorDialog(getContext(), "아이디가 없거나 비밀번호가 맞지 않습니다.");
                }
            }
        });
    }

    //로그인(Google)
    private int RC_SIGN_IN = 9001;

    protected void setGoogle(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                break;
            }
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.w("SignInActivity", "Google SignIn success");
                            toggleActivity();
                        }else{
                            Log.w("SignInActivity", "Google SignIn fail");
                        }
                    }
                });
    }


    //로그인(Facebook)
    private CallbackManager callbackManager;

    void setFacebook(View view){

        callbackManager = CallbackManager.Factory.create();

        //callbackManager
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.facebook_signIn);
        loginButton.setFragment(this);// If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Log.w("SignInActivity", "Facebook SignIn onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.w("SignInActivity", "Facebook SignIn onError");
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Log.w("SignInActivity", "Facebook SignIn success");
                            toggleActivity();
                        } else {
                            // 로그인 실패
                            Log.w("SignInActivity", "Facebook SignIn fail");
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w("SignInActivity", "onActivityReuslt");
        //Google
        if (requestCode == RC_SIGN_IN) {
            Log.w("SignInActivity", "Google");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.w("Result", result.getStatus().toString());
            if (result.isSuccess()) {
                Log.w("Google", "signIn success");
                //구글 로그인 성공해서 파베에 인증
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                //구글 로그인 실패
                Log.w("Google", "signIn fail");
            }
        }

        //Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void toggleActivity(){

        Intent mainIntent = new Intent(getContext(), MainActivity.class);
        getActivity().startActivity(mainIntent);
        getActivity().finish();
    }
}