package com.hkm.vdlsdk.client;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.util.Iterator;
import java.util.LinkedHashMap;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by zJJ on 1/17/2016.
 */
public class SoundCloud extends retrofitClientBasic {
    private Call<String> mCall;
    private String task_destination, csrftoken = "";
    private static final String FIELD_TOKEN = "csrfmiddlewaretoken";
    private boolean isSingleTrack = true;

    public interface Callback {
        void success(LinkedHashMap<String, String> result);

        void failture(String why);
    }

    @Override
    protected String getBaseEndpoint() {
        return "http://9soundclouddownloader.com/";
    }

    private String listEndPoint() {
        return "http://9soundclouddownloader.com/playlist-downloader";
    }

    private interface workerService {
        @FormUrlEncoded
        @POST("/download-playlist")
        Call<String> getMp3List(
                @Field(FIELD_TOKEN) final String token,
                @Field("playlist-url") final String h);

        @FormUrlEncoded
        @POST("/download-sound-track")
        Call<String> getMp3Track(
                @Field(FIELD_TOKEN) final String token,
                @Field("sound-url") final String h);

    }


    private static SoundCloud static_instance;

    public SoundCloud(Context c) {
        super(c);
    }


    public static SoundCloud newInstance(Context ctx) {
        return new SoundCloud(ctx);
    }

    public static SoundCloud getInstance(Context context) {
        if (static_instance == null) {
            static_instance = newInstance(context);
            return static_instance;
        } else {
            static_instance.setContext(context);
            return static_instance;
        }
    }

    private workerService createService() {
        return api.create(workerService.class);
    }


    public void pullFromUrl(final String urlFromSoundCloud, final Callback cb) {
        Uri sound_url_checker = Uri.parse(urlFromSoundCloud);
        if (sound_url_checker.getScheme() == null) {
            cb.failture("it is not a correct url");
            return;
        }
        if (!sound_url_checker.getAuthority().equalsIgnoreCase("soundcloud.com")) {
            cb.failture("Wrong starting domain");
            return;
        }
        isSingleTrack = sound_url_checker.getPathSegments().contains("sets") ? false : true;
        task_destination = urlFromSoundCloud;
        taskGetToken h = new taskGetToken(cb);
        h.execute();
    }


    private class taskGetToken extends AsyncTask<Void, Void, String> {
        final Callback cb;

        public taskGetToken(Callback callback) {
            cb = callback;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Request request = new Request.Builder()
                        .url(isSingleTrack ? getBaseEndpoint() : listEndPoint())
                        .build();
                okhttp3.Call call = client3.newCall(request);
                okhttp3.Response response = call.execute();
                if (!response.isSuccessful()) {
                    throw new IOException("server maybe down");
                }
                if (response.code() != 200) {
                    throw new IOException("server " + response.code());
                }
                ResponseBody body = response.body();
                final String unsolve = body.string();
                Element el = Jsoup.parse(unsolve).body();
                String token = el.select("input[name=" + FIELD_TOKEN).first().val();

                if (token.equalsIgnoreCase("")) {
                    throw new IOException("token did not get");
                }

                return token;
            } catch (IOException e) {
                cb.failture("Operational error:" + e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            csrftoken = s;
            rebuildRetrofit();
            workerService w = createService();
            mCall = isSingleTrack ? w.getMp3Track(s, task_destination) : w.getMp3List(s, task_destination);
            mCall.enqueue(new retrofit.Callback<String>() {
                @Override
                public void onResponse(Response<String> response, Retrofit retrofit) {
                    try {
                        if (!response.isSuccess()) {
                            cb.failture("response not success: " + response.message());
                            throw new Exception("response code:" + response.code());
                        }
                        LinkedHashMap<String, String> links = new LinkedHashMap<String, String>();
                        Document doc = Jsoup.parse(response.body());
                        // Elements els = doc.select("a:contains['download']");
                        Elements els = doc.select("a[download]");
                        Iterator<Element> iel = els.iterator();
                        while (iel.hasNext()) {
                            Element el = iel.next();
                            String link = el.attr("href");
                            String title = el.select("b").text();
                            links.put(title, link);
                        }

                        if (links.size() == 0) {
                            cb.failture("empty result");
                        } else {
                            cb.success(links);
                        }
                    } catch (Exception e) {
                        cb.failture(e.getLocalizedMessage());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    cb.failture(t.getLocalizedMessage());
                }
            });
        }
    }

    @Override
    protected com.squareup.okhttp.Request.Builder universal_header(com.squareup.okhttp.Request.Builder chain) {
        chain.addHeader("Cookie", "csrftoken=" + csrftoken);
        chain.addHeader("Accept", "charset=UTF-8");
        chain.addHeader("Content-Type", "text/html; charset=utf-8");
        return super.universal_header(chain);
    }

    @Override
    protected OkHttpClient createClient() {
        // enable cookies
        java.net.CookieManager cookieManager = new java.net.CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        return super.createClient();
    }
}
