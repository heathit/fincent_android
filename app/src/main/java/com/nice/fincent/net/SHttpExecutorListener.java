package com.nice.fincent.net;

/**
 * Created by stoas0605 on 2016-07-26.
 */
public interface SHttpExecutorListener<T> {

    public void onResponse(T response);

    public void onCancelled();

    public void onException(Exception e);

    public static abstract class Base<T> implements SHttpExecutorListener<T> {

        @Override
        public void onCancelled() {

        }

        @Override
        public void onException(Exception exception) {

        }
    }
}
