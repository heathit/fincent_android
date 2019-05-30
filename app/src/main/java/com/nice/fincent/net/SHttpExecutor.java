package com.nice.fincent.net;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.Callable;

/**
 * Created by stoas0605 on 2016-07-26.
 */
public class SHttpExecutor<T> extends AsyncTask<String, String, T> {

    private SHttpExecutorListener<T> sHttpResponseHandler;
    private Callable<T> callable;
    private Exception exception;

    public SHttpExecutor<T> setCallable(Callable<T> callable) {
        this.callable = callable;
        return this;
    }

    public SHttpExecutor<T> setHttpResponseHandler(SHttpExecutorListener<T> sHttpResponseHandler) {
        this.sHttpResponseHandler = sHttpResponseHandler;
        processSHttpExecutorS(sHttpResponseHandler);
        return this;
    }

    private void processSHttpExecutorS(SHttpExecutorListener<T> sHttpResponseHandler) {
        if(sHttpResponseHandler instanceof SHttpExecutorS) {
            ((SHttpExecutorS) sHttpResponseHandler).setHttpExecutor(this);
        }
    }

    @Override
    protected T doInBackground(String... urls) {
        try {
            return callable.call();
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
            this.exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(T reponse) {

        //super.onPostExecute(reponse);
        if(isCancelled()) onCancelled();

        if(hasException()) {
            setException();
            return;
        }

        setResponse(reponse);
    }

    @Override
    protected void onCancelled() {
        if(sHttpResponseHandler != null) sHttpResponseHandler.onException(exception);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    private boolean hasException() {
        return exception != null;
    }

    private void setException() {
        if(sHttpResponseHandler != null) sHttpResponseHandler.onException(exception);
    }

    private void setResponse(T reponse) {
        if(sHttpResponseHandler != null) sHttpResponseHandler.onResponse(reponse);
    }
}
