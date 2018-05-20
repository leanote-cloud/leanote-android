package org.houxg.leamonax.editor;

import android.webkit.JavascriptInterface;

import org.greenrobot.eventbus.EventBus;
import org.houxg.leamonax.model.CompleteEvent;

public class HostApp {

    @JavascriptInterface
    public void loadCompleted() {
        EventBus.getDefault().post(new CompleteEvent());
    }

}
