package com.hkm.vdlsdk.client;

import android.os.AsyncTask;

import com.hkm.vdlsdk.Util;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by hesk on 21/1/16.
 */
public class ValidationChecker extends AsyncTask<Void, Void, Void> {
    private OkHttpClient client3;
    protected HttpLoggingInterceptor loglevel;
    protected okhttp3.Interceptor interceptor;
    protected String success;
    protected check_cb cb;
    protected boolean get_redirection_link;
    private final okhttp3.Call call;

    private okhttp3.Request.Builder universal_header(okhttp3.Request.Builder chain) {
        // chain.addHeader("Accept", "application/json");
        return chain;
    }

    private void createLogLevel() {
        loglevel = new HttpLoggingInterceptor();
        loglevel.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private void createIntercept() {
        interceptor = new okhttp3.Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request newRequest = universal_header(chain.request().newBuilder()).build();
                return chain.proceed(newRequest);
            }
        };
    }

    private OkHttpClient createClient() {
        OkHttpClient.Builder b = new OkHttpClient().newBuilder();
        b.connectTimeout(10, TimeUnit.MINUTES);
        b.followRedirects(true);
        client3 = b.build();
        //  client3.interceptors().add(interceptor);
        //    client3.interceptors().add(loglevel);
        return client3;
    }

    private ValidationChecker(String url, check_cb checker) {
        createLogLevel();
        createIntercept();
        createClient();
        get_redirection_link = false;
        cb = checker;
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Util.LinkConfirmer(url)).build();
        call = client3.newCall(request);
    }

    public static ValidationChecker general_validation_check(String url, check_cb checkcb) {
        ValidationChecker t = new ValidationChecker(url, checkcb);
        t.execute();
        return t;
    }


    public static ValidationChecker redirection_link_finder(String url, check_cb checkcb) {
        ValidationChecker t = new ValidationChecker(url, checkcb);
        t.setFollowingGetter();
        t.execute();
        return t;
    }

    private void setFollowingGetter() {
        get_redirection_link = true;
    }

    @Override
    protected Void doInBackground(Void[] params) {
        try {
            okhttp3.Response response = call.execute();
            if (response.isSuccessful() && response.code() == 200) {
                success = "valid link";
                if (get_redirection_link) {
                    success = response.headers().get("location");
                }
                return null;
            } else {
                throw new Exception("invalidate link");
            }
        } catch (UnknownHostException e) {
            success = e.getCause().getMessage();
            cancel(true);
        } catch (IOException e) {
            success = e.getCause().getMessage();
            cancel(true);
        } catch (Exception e) {
            success = e.getMessage();
            cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        cb.success(success);
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        cb.failture(success);
    }

    public interface check_cb {
        void success(String delivered_product);

        void failture(String failure);
    }

    public void cancel() {
        if (!isCancelled()) {
            call.cancel();
            cancel(true);
        }
    }

}
