package com.humbertobioca.whr.Navegacao;

import android.app.Activity;
import android.content.Intent;

import com.humbertobioca.whr.Activity.LoginActivity;
import com.humbertobioca.whr.Activity.MainActivity;

public class Navegacao {
    private void abrirMainActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        //startActivity(intent);
       // finish();
    }
}
