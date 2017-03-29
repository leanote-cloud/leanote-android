package org.houxg.leamonax.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import org.houxg.leamonax.R;
import org.houxg.leamonax.model.Account;
import org.houxg.leamonax.network.ApiProvider;
import org.houxg.leamonax.service.AccountService;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);
        final Intent intent;
        if (AccountService.isSignedIn()) {
            Account account = Account.getCurrent();
            ApiProvider.getInstance().init(account.getHost());
            intent = MainActivity.getOpenIntent(this, false);
        } else {
            intent = new Intent(this, SignInActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
