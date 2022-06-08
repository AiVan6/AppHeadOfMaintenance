package romanow.abc.android.service;

import firefighter.core.UniException;

public interface NetBack {
    public void onError(int code, String mes);
    public void onError(UniException ee);
    public void onSuccess(Object val);
    }
