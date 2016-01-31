package com.hkm.vdlsdk.client;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by hesk on 21/1/16.
 */
public class YouTube extends retrofitClientBasic {
    private Call<String> mCall;
    private String task_destination, csrftoken = "";
    private boolean isSingleTrack = true;

    /* http://www.youtube.com/v/0zM3nApSvMg?fs=1&amp;hl=en_US&amp;rel=0
     http://www.youtube.com/embed/0zM3nApSvMg?rel=0
     http://www.youtube.com/watch?v=0zM3nApSvMg&feature=feedrec_grec_index
     http://www.youtube.com/watch?v=0zM3nApSvMg
     http://youtu.be/0zM3nApSvMg
     http://www.youtube.com/watch?v=0zM3nApSvMg#t=0m10s
     http://www.youtube.com/user/IngridMichaelsonVEVO#p/a/u/1/QdK8U-VIH_o*/


    public interface Callback {
        void success(String resource_link, String title);

        void failture(String why);
    }

    @Override
    protected String getBaseEndpoint() {
        return "http://mp4.ee";
    }

    private interface workerService {
        @GET("/watch")
        Call<String> getMp4DownloadList(@Query("v") final String youtube_id);
    }

    private static YouTube static_instance;

    public YouTube(Context c) {
        super(c);
    }


    public static YouTube newInstance(Context ctx) {
        return new YouTube(ctx);
    }

    public static YouTube getInstance(Context context) {
        if (static_instance == null) {
            static_instance = newInstance(context);
            return static_instance;
        } else {
            static_instance.setContext(context);
            return static_instance;
        }
    }

    private static final String PARSE_V1_FOUND_COMPLETE_LINK = "(?:https?:\\/\\/)?(?:www\\.)?youtu(?:.be\\/|be\\.com\\/watch\\?v=|be\\.com\\/v\\/)(.{8,})";
    private static final String PARSE_V3 = "(?:(?:https?:\\/\\/)?(?:youtu[.]be\\/)|(?:.*(?:embed|[/?&]v)i?(?:\\/|=)))([a-zA-z0-9-]*?)(?:$|[&?/].*$)";

    private workerService createService() {
        return api.create(workerService.class);
    }

    public static boolean tester(String content_url) {
        Pattern pattern1 = Pattern.compile(PARSE_V1_FOUND_COMPLETE_LINK);
        Matcher matcher1 = pattern1.matcher(content_url);
        if (matcher1.find()) {
            Log.d("hackResult", matcher1.group(1));
            String link_found = matcher1.group(1);
            return true;
        } else {
            return false;
        }
    }

    /**
     * parse it
     * @param content_url the context text
     * @param cb the callback
     */
    public void parseUrl(final String content_url, final Callback cb) {
        Pattern pattern2 = Pattern.compile(PARSE_V3);
        Matcher matcher2 = pattern2.matcher(content_url);
        // Log.i("hackResult", content_url);
        if (matcher2.find()) {
            Log.d("hackResult", matcher2.group(1));
            String clipId = matcher2.group(1);
            if (clipId.isEmpty()) {
                cb.failture("youtube Id is not found.");
                return;
            }
            fromMP4dee(clipId, cb);
           /* if (matcher.groupCount() > 0) {
                for (int i = 0; i < matcher.groupCount(); i++) {
                    String clipId = matcher.group(i);
                    Log.d("hackResult", clipId);
                }
            }*/
        } else {
            cb.failture("youtube Id is not found.");
        }


    }

    private void fromMP4dee(String cid, final Callback cb) {
        workerService work = createService();
        mCall = work.getMp4DownloadList(cid);
        mCall.enqueue(new retrofit.Callback<String>() {
            @Override
            public void onResponse(retrofit.Response<String> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if (response.code() == 200) {
                        try {
                            String xml = response.body();
                            //    Log.i("htmlokhttp", xml);
                            Document h = Jsoup.parse(xml);
                            String endpath = h.select("a#MP4").first().attr("href").toLowerCase();

                            StringBuilder sb = new StringBuilder();
                            sb.append(endpath);
                            sb.append("&asv=2");
                            Log.i("html_okhttp", sb.toString());

                            cb.success(sb.toString(), h.select("title").first().text());
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
                cb.failture("operational faliure:" + t.getCause().getMessage());
            }
        });
    }

    private String failure_message = "error";


    @Override
    protected com.squareup.okhttp.Request.Builder universal_header(com.squareup.okhttp.Request.Builder chain) {
        //    chain.addHeader("Cookie", "csrftoken=" + csrftoken);
        chain.addHeader("Accept", "*/*");
        chain.addHeader("Accept-Language", "zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4,zh-CN;q=0.2");
        chain.addHeader("Content-Type", "text/html; charset=utf-8");
        chain.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");
        return super.universal_header(chain);
    }

    @Override
    protected OkHttpClient createClient() {
        // enable cookies
        java.net.CookieManager cookieManager = new java.net.CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        OkHttpClient client = super.createClient();
        client.setConnectTimeout(7, TimeUnit.MINUTES); // connect timeout
        client.setReadTimeout(300, TimeUnit.SECONDS);    // socket timeout
        return client;
    }
}
