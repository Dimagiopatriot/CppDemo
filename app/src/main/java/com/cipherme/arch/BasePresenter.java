package com.cipherme.arch;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<T extends MVPView> implements Presenter<T> {

    private T mMvpView;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void attachView(T mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mMvpView = null;
    }

    protected boolean isViewAttached() {
        return mMvpView != null;
    }

    protected T getMvpView() {
        return mMvpView;
    }

    protected void checkIfViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    private static class MvpViewNotAttachedException extends RuntimeException {

        MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}
