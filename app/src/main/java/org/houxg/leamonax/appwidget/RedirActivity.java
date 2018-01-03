package org.houxg.leamonax.appwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.houxg.leamonax.ui.NotePreviewActivity;
import org.houxg.leamonax.utils.ToastUtils;

public class RedirActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Long word = getIntent().getLongExtra(NotePreviewActivity.EXT_NOTE_LOCAL_ID, -1);
        Intent intent = new Intent(RedirActivity.this, NotePreviewActivity.class);
        Bundle bundle = new Bundle();

        if (word == -1) {
            ToastUtils.show(this, "笔记编号错误");
        } else {
            bundle.putLong(NotePreviewActivity.EXT_NOTE_LOCAL_ID, word);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        finish();

    }
}
