package org.houxg.leamonax.ui;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.houxg.leamonax.R;
import org.houxg.leamonax.utils.SkinCompatUtils;
import org.houxg.leamonax.utils.StatusBarUtils;

public class BaseActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    protected void initToolBar(Toolbar toolbar) {
        initToolBar(toolbar, false);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected void initToolBar(Toolbar toolbar, boolean hasBackArrow) {
        if (toolbar != null) {
            mToolbar = toolbar;
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(ContextCompat.getColor(this, SkinCompatUtils.isThemeNight() ? R.color.primary_text_light_night : R.color.black));
            toolbar.setTitle(getTitle());
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null && hasBackArrow) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(SkinCompatUtils.isThemeNight() ? R.drawable.ic_arrow_back_white : R.drawable.ic_arrow_back_black);
            }
        }
    }

    public void updateTitleTextColorByTheme() {
        if (mToolbar != null) {
            mToolbar.setTitleTextColor(ContextCompat.getColor(this, SkinCompatUtils.isThemeNight() ? R.color.primary_text_light_night : R.color.black));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void setStatusBarColor() {
        int id;
        if (SkinCompatUtils.isThemeNight()) {
            id = R.color.colorPrimary_night;
        } else {
            id = R.color.colorPrimary;
        }
        StatusBarUtils.setColor(this, getResources().getColor(id));
    }
}
