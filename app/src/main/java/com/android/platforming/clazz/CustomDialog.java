package com.android.platforming.clazz;

import static com.android.platforming.clazz.FirestoreManager.getFirebaseAuth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.android.platforming.activity.MainActivity;
import com.android.platforming.activity.SignInActivity;
import com.android.platforming.interfaze.ListenerInterface;
import com.example.platforming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CustomDialog {
    AlertDialog dialog;

    public void messageDialog(Activity activity, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_message, null);

        TextView message = view.findViewById(R.id.tv_message_message);
        message.setText(msg);

        Button confirm = view.findViewById(R.id.btn_message_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        builder.setView(view);

        dialog = builder.create();
        dialog.show();
    }

    public void passwordResetDialog(Context context, ListenerInterface listenerInterface){
        final EditText editText = new EditText(context);

        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle("비밀번호 찾기");
        ad.setMessage("이메일을 입력해 주세요.");
        ad.setView(editText);
        ad.setPositiveButton("입력", (dialog, which) -> {
            String email= editText.getText().toString();
            listenerInterface.onSuccess(email);
        });
        ad.setNegativeButton("최소", (dialog, which) -> {
            listenerInterface.onFail();
        });
        ad.show();
    }

    public void selectDialog(Activity activity, String title, ListenerInterface listenerInterface){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_select, null);

        TextView message = view.findViewById(R.id.tv_select_message);
        message.setText("계속하시겠습니까?");

        Button cancel = view.findViewById(R.id.btn_select_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button confirm = view.findViewById(R.id.btn_select_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerInterface.onSuccess();
                dialog.dismiss();
            }
        });

        builder.setView(view);

        dialog = builder.create();
        dialog.show();
    }

    public void verificationDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("본인인증");

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_verification, null);

        Button cancel = view.findViewById(R.id.btn_verification_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        EditText et_password = view.findViewById(R.id.et_verification_password);

        Button zontinue = view.findViewById(R.id.btn_verification_continue);
        zontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = et_password.getText().toString();
                FirebaseUser user = getFirebaseAuth().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(User.getUser().getEmail(), password);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        dialog.dismiss();
                        newPasswordDialog(activity, user);
                    }
                    else{
                        dialog.dismiss();
                        messageDialog(activity, "인증에 실패했습니다.");
                    }
                });
            }
        });

        builder.setView(view);

        dialog = builder.create();
        dialog.show();
    }

    private void newPasswordDialog(Activity activity, FirebaseUser user){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("비밀번호 변경");

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_setting_newpassword, null);

        Button cancel = view.findViewById(R.id.btn_newpassword_cancel);
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        EditText newpassword = view.findViewById(R.id.et_newpassword_newpassword);
        EditText passwordcheck = view.findViewById(R.id.et_newpassword_passwordcheck);

        Button change = view.findViewById(R.id.btn_newpassword_change);
        change.setOnClickListener(v -> {
            String newPassword = newpassword.getText().toString();
            if(newPassword.equals(passwordcheck.getText().toString())){
                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            messageDialog(activity, "비밀번호를 성공적으로 변경했습니다.");
                        } else {
                            messageDialog(activity, "비밀번호 변경을 실패했습니다.");
                        }
                    }
                });
            }
        });

        builder.setView(view);

        dialog = builder.create();
        dialog.show();
    }

    public void attendanceCheckDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("출석체크");

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_attendancecheck, null);

        TextView message = view.findViewById(R.id.tv_select_message);
        message.setText("계속하시겠습니까?");

        Button cancel = view.findViewById(R.id.btn_select_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button confirm = view.findViewById(R.id.btn_select_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerInterface.onSuccess();
                dialog.dismiss();
            }
        });

        builder.setView(view);

        dialog = builder.create();
        dialog.show();
    }

    public void themeDialog(Activity activity, List themes){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_design_theme, null);
        builder.setTitle("테마");

        RadioGroup radioGroup = view.findViewById(R.id.rg_dialog_theme);
        RadioButton[] radioThemeList = new RadioButton[themes.size()];

        for(int i = 0; i< themes.size();i++){
            RadioButton radioButton = new RadioButton(activity);
            radioButton.setTextColor(Color.BLACK);
            if (themes.get(i).equals((long)0)){
                radioButton.setId(500+0);
                radioButton.setText("라이트 테마(기본)");
            }
            else if(themes.get(i).equals((long)1)){
                radioButton.setId(500+1);
                radioButton.setText("핑크 테마");
            }
            else if(themes.get(i).equals((long)2)){
                radioButton.setId(500+2);
                radioButton.setText("블루 테마");
            }
            else if(themes.get(i).equals((long)3)){
                radioButton.setId(500+3);
                radioButton.setText("그린 테마");
            }
            else if(themes.get(i).equals((long)4)){
                radioButton.setId(500+4);
                radioButton.setText("블랙 테마");
            }
            radioThemeList[i] = radioButton;
            radioGroup.addView(radioThemeList[i]);
        }

        Button apply = view.findViewById(R.id.btn_dialog_theme_apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = radioGroup.getCheckedRadioButtonId() - (int)500;
                Log.d("check_index_", String.valueOf(index));
                PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("theme", index).apply();
                MainActivity.reCreate();
                activity.recreate();

                dialog.dismiss();
            }
        });

        Button getout = view.findViewById(R.id.btn_dialog_theme_getout);
        getout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        builder.setView(view);

        dialog = builder.create();
        dialog.show();
    }

    public void fontDialog(Activity activity, List fonts){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_design_theme, null);
        builder.setTitle("폰트");

        RadioGroup radioGroup = view.findViewById(R.id.rg_dialog_theme);
        RadioButton[] radioThemeList = new RadioButton[fonts.size()];

        for(int i = 0; i< fonts.size();i++){
            RadioButton radioButton = new RadioButton(activity);
            radioButton.setTextColor(Color.BLACK);
            if (fonts.get(i).equals((long)0)){
                radioButton.setId((int)600);
                radioButton.setText("레페리 포인트(기본)");
            }
            else if(fonts.get(i).equals((long)1)){
                radioButton.setId(600+1);
                radioButton.setText("나눔손글씨 느릿느릿체");
            }
            else if(fonts.get(i).equals((long)2)){
                radioButton.setId(600+2);
                radioButton.setText("나눔손글씨 다시시작해");
            }
            else if(fonts.get(i).equals((long)3)){
                radioButton.setId(600+3);
                radioButton.setText("갈무리9");
            }
            else if(fonts.get(i).equals((long)4)){
                radioButton.setId(600+4);
                radioButton.setText("무궁화체");
            }
            radioThemeList[i] = radioButton;
            radioGroup.addView(radioThemeList[i]);
        }

        Button apply = view.findViewById(R.id.btn_dialog_theme_apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = radioGroup.getCheckedRadioButtonId() - (int)600;
                Log.d("check_index_", String.valueOf(index));
                PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("font", index).apply();
                MainActivity.reCreate();
                activity.recreate();

                dialog.dismiss();
            }
        });

        Button getout = view.findViewById(R.id.btn_dialog_theme_getout);
        getout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        builder.setView(view);

        dialog = builder.create();
        dialog.show();
    }
}