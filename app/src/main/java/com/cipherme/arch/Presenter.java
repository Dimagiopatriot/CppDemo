package com.cipherme.arch;

public interface Presenter<T> {

    void attachView(T mvpView);

    void detachView();
}
