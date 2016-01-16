package com.hkm.vdlsdk.client;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by zJJ on 1/16/2016.
 */
public class FBdownNet extends retrofitClientBasic {
    public interface fbdownCB {
        void success(String answer);

        void failture(String why);
    }

    @Override
    protected String getBaseEndpoint() {
        return "http://www.fbdown.net";
    }

    private interface workerService {
        @Headers({
                "Accept: charset=UTF-8"
        })
        @FormUrlEncoded
        @POST("/down.php")
        Call<String> getvideo(@Field("URL") final String fburl);

        @GET("/story.php")
        Call<String> getvideo(@Query("story_fbid") final String fburl, @Query("id") final String id);
    }


    private static FBdownNet static_instance;

    public FBdownNet(Context c) {
        super(c);
    }


    public static FBdownNet newInstance(Context ctx) {
        return new FBdownNet(ctx);
    }

    public static FBdownNet getInstance(Context context) {
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

    private Call<String> mCall;

    private void mFacebookNetwork(Uri url, final fbdownCB cb) {
/*
        Retrofit newfacebookmnetwork = new Retrofit.Builder()
                .baseUrl(url.toString())
                .addConverterFactory(new ToStringConverter())
                .client(createClient())
                .build();

        workerService work = newfacebookmnetwork.create(workerService.class);
        mCall = work.getvideo(url.getQueryParameter("story_fbid"), url.getQueryParameter("id"));
        mCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if (response.code() == 200) {
                        try {
                            String xml = response.body();
                            Document h = Jsoup.parse(xml);
                            String f = h.select("#mInlineVideoPlayer").attr("src").toLowerCase();
                            cb.success(f);
                        } catch (Exception e) {
                            cb.failture(e.getLocalizedMessage());
                        }
                    } else {
                        cb.failture("server maybe down");
                    }
                } else {
                    cb.failture("server maybe down");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                cb.failture("operational faliure:" + t.getLocalizedMessage());
            }
        });
*/

        gethml gt = new gethml(cb);
        gt.execute(new String[]{url.toString()});
    }

    private class gethml extends AsyncTask<String, Void, Void> {
        final fbdownCB cb;
        String success = "";

        public gethml(fbdownCB callback) {
            cb = callback;
        }



        @Override
        protected Void doInBackground(String[] params) {
            try {
                final String connection_url = params[0];

                Request request = new Request.Builder()
                        .url(connection_url)
                        .build();
                okhttp3.Call call = client3.newCall(request);
                okhttp3.Response response = call.execute();
                ResponseBody body = response.body();

                final String solve = body.string();
                final String getTagVideoPartial = "\\/video_redirect(.*?)(.*)(=F)";

                Pattern pattern = Pattern.compile(getTagVideoPartial);
                Matcher matcher = pattern.matcher(solve);
                if (matcher.find()) {

                    Log.d("hackResult", matcher.group(0));
                    success = matcher.group(0);
                }

                success = "https://m.facebook.com" + success;

            } catch (IOException e) {
                cb.failture(e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cb.success(success);
        }
    }

    private void fbdown_main_site(String n, final fbdownCB cb) {
        workerService work = createService();
        mCall = work.getvideo(n);
        mCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(retrofit.Response<String> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if (response.code() == 200) {
                        try {
                            String xml = response.body();
                            //    String safe = Jsoup.clean(xml, Whitelist.basic());
                            Document h = Jsoup.parse(xml);
                            String endpath = h.select("a[href^=\"v.php\"]").attr("href").toLowerCase();

                            StringBuilder sb = new StringBuilder();
                            sb.append(getBaseEndpoint())
                                    .append("/")
                                    .append(endpath);

                            cb.success(sb.toString());
                        } catch (Exception e) {
                            cb.failture(e.getLocalizedMessage());
                        }
                    } else {
                        cb.failture("server maybe down");
                    }
                } else {
                    cb.failture("server maybe down");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                cb.failture("operational faliure:" + t.getLocalizedMessage());
            }
        });
    }

    public void getVideoUrl(String n, final fbdownCB cb) {
        if (Uri.parse(n).getQueryParameter("story_fbid") != null) {
            String storyid = Uri.parse(n).getQueryParameter("story_fbid");
            if (!storyid.equalsIgnoreCase("")) {
                mFacebookNetwork(Uri.parse(n), cb);
            } else {
                cb.failture("wrong url");
            }
            return;
        } else if (Uri.parse(n).getScheme() == null) {
            cb.failture("it is not a correct url");
            return;
        }

        fbdown_main_site(n, cb);
    }

    public void stopRequest() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

}
