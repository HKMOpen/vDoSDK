package com.hkm.vdlsdk;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zJJ on 1/17/2016.
 */
public class Util {

    private String[] filterout = new String[]{
            "com.antlib.thisavhelper",
            "com.beetalk"
    };


    public static void EasyVideoMessageShare(Context ctx, @Nullable String customheader, final String link) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        //  String nohtml = "I just got the video in full for " + content.replaceAll("\\<.*?>", "") + ", check it out @ " + link;
        String share = "I just got the video in full from facebook @ " + link;
        if (customheader != null) {
            share = customheader + " @ " + link;
        }
        sendIntent.putExtra(Intent.EXTRA_TEXT, share);
        sendIntent.setType("text/plain");
        ctx.startActivity(sendIntent);
    }

    public static void EasySoundCloudListShare(Context ctx, LinkedHashMap<String, String> result) {
        Iterator<Map.Entry<String, String>> iel = result.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (iel.hasNext()) {
            Map.Entry<String, String> el = iel.next();
            sb.append("==== Track ====\n");
            sb.append(el.getKey());
            sb.append("==== Link =====\n");
            sb.append(el.getValue());
            sb.append("==== ==== =====\n");
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        sendIntent.setType("text/plain");
        ctx.startActivity(sendIntent);
    }


    public static String LinkConfirmer(@Nullable String text) {
        if (text == null || text.isEmpty()) return "";
        final String get_link = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
        Pattern patternc = Pattern.compile(get_link);
        Matcher fm = patternc.matcher(text);
        if (fm.find()) {
            // Log.d("hackResult", fm.group(0));
            String k_start = fm.group(0);
            if (!k_start.isEmpty()) {
                return k_start;
            }
        }
        return "";
    }
}
