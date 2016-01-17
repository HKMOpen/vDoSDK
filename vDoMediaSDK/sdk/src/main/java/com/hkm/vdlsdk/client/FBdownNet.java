package com.hkm.vdlsdk.client;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by zJJ on 1/16/2016.
 */
public class FBdownNet extends retrofitClientBasic {
    public interface fbdownCB {
        void success(String answer);

        void loginfirst(String here);

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

        @FormUrlEncoded
        @POST("/{redirect}")
        Call<String> login2Page(
                @Path("redirect") final String redirect,
                @Field("lsd") final String lsd,
                @Field("charset_test") final String charset_test,
                @Field("version") final String version,
                @Field("ajax") final String ajax,
                @Field("pxr") final String pxr,
                @Field("gps") final String gps,
                @Field("dimension") final String dimension,
                @Field("m_ts") final String m_ts,
                @Field("li") final String li,
                @Field("pass") final String pass,
                @Field("email") final String email
        );

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
        pipline_g gt = new pipline_g(cb);
        gt.execute(new String[]{url.toString()});
    }

    private class pipline_g extends AsyncTask<String, Void, Void> {
        final fbdownCB cb;
        boolean need_to_login_first = false;
        String success = "";

        public pipline_g(fbdownCB callback) {
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

                if (!response.isSuccessful()) {
                    throw new IOException("server maybe down");
                }

                if (response.code() != 200) {
                    throw new IOException("server " + response.code());
                }
/*
                final String solve = body.string();
                final String getTagVideoPartial = "\\/video_redirect(.*?)(.*)(=F)";

                Pattern pattern = Pattern.compile(getTagVideoPartial);
                Matcher matcher = pattern.matcher(solve);
                Log.i("hackResult", solve);
                if (matcher.find()) {
                    Log.d("hackResult", matcher.group(0));
                    success = matcher.group(0);
                }*/

                success = get_page_video_real_url(body);


                final String solve = body.string();
                if (success.isEmpty()) {
                    //find the login pattern
                    final String find_login = "\\/login.php\\?next=(.*?)(=\\d{2})";
                    Pattern patternc = Pattern.compile(find_login);
                    Matcher fm = patternc.matcher(solve);
                    if (fm.find()) {
                        Log.d("hackResult", fm.group(0));
                        success = fm.group(0);
                        if (success.isEmpty()) {
                            throw new IOException("cannot found the video");
                        } else {
                            need_to_login_first = true;
                            String login = "https://m.facebook.com" + success;


                            //As we need to wait for the Okhttp3 to be implemented on retrofit on the stable level we dont make a login use now.
                            // fb_login(login);
                            cancel(true);
                        }
                    } else
                        throw new IOException("cannot found the video");
                }
                success = "https://m.facebook.com" + success;
            } catch (IOException e) {
                success = e.getMessage();
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
            if (need_to_login_first)
                cb.loginfirst(success);
            else
                cb.failture(success);
        }
    }

    private String get_page_video_real_url(ResponseBody body) throws Exception {
        final String solve = body.string();
        final String getTagVideoPartial = "\\/video_redirect(.*?)(.*)(=F)";

        Pattern pattern = Pattern.compile(getTagVideoPartial);
        Matcher matcher = pattern.matcher(solve);
        Log.i("hackResult", solve);
        if (matcher.find()) {
            Log.d("hackResult", matcher.group(0));
            return matcher.group(0);
        }
        return "";
    }

    private void fb_login(final String login) throws IOException, Exception {
        okhttp3.Call callLogin = client3.newCall(new Request.Builder().url(login).build());
        okhttp3.Response responseLogin = callLogin.execute();
        if (!responseLogin.isSuccessful()) {
            throw new IOException("server maybe down");
        }
        if (responseLogin.code() != 200) {
            throw new IOException("server " + responseLogin.code());
        }
        ResponseBody bodyLogin = responseLogin.body();
        final String solveLogin = bodyLogin.string();

        Element el = Jsoup.parse(solveLogin).select("form.mobile-login-form").first();
        String action = el.attr("action");
        String lsd = el.select("input[type=hidden][name=lsd]").first().val();
        String charset_test = el.select("input[type=hidden][name=charset_test]").first().val();
        String version = el.select("input[type=hidden][name=version]").first().val();
        String ajax = el.select("input[type=hidden][name=ajax]").first().val();
        String width = el.select("input[type=hidden][name=width]").first().val();
        String pxr = el.select("input[type=hidden][name=pxr]").first().val();
        String gps = el.select("input[type=hidden][name=gps]").first().val();
        String m_ts = el.select("input[type=hidden][name=m_ts]").first().val();
        String li = el.select("input[type=hidden][name=li]").first().val();
        String login_email = "ooxhesk@yahoo.com.hk";
        String pass = "kam123";
        workerService work = createService();

        mCall = work.login2Page(action, lsd, charset_test, version, ajax, width, pxr, gps, m_ts, li, pass, login_email);
        retrofit.Response<String> response = mCall.execute();

        if (response.isSuccess()) {
            if (response.code() == 200) {
                try {

                    //  String r = get_page_video_real_url(response.raw().body());


                    // cb.success(sb.toString());
                } catch (Exception e) {
                    // cb.failture(e.getLocalizedMessage());
                }
            } else {
                // cb.failture("server maybe down");
            }
        } else {
            //cb.failture("server maybe down");
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
