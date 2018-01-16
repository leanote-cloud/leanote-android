package org.houxg.leamonax.model;


public class SyncEvent {
    private boolean isSucceed;
    private String msg;

    public SyncEvent(boolean isSucceed) {
        this.isSucceed = isSucceed;
    }

    public SyncEvent(boolean isSucceed, String msg) {
        this.isSucceed = isSucceed;
        this.msg = msg;
    }

    public boolean isSucceed() {
        return isSucceed;
    }

    public String getMsg() {
        return msg;
    }
}
