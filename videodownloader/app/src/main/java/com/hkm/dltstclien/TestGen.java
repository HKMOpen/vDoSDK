package com.hkm.dltstclien;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hkm.dltstclien.monkeyTest.testBasic;
import com.hkm.vdlsdk.client.FBdownNet;
import com.hkm.vdlsdk.client.SoundCloud;
import com.hkm.vdlsdk.model.urban.Term;

import java.util.Iterator;
import java.util.LinkedHashMap;

import retrofit.Call;

/**
 * Created by zJJ on 1/16/2016.
 */
public class TestGen extends testBasic {
    EditText field1, field2;
    Button b1, b2;
    ImageButton copy_current;
    FBdownNet client;
    private Call<Term> recheck;
    ClipboardManager clipboard;

    @Override
    public void onDestroy() {
        if (recheck != null) {
            recheck.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected int LayoutID() {
        return R.layout.test_general_main;
    }

    @Override
    protected void initBinding(View v) {
        super.initBinding(v);
        b1 = (Button) v.findViewById(R.id.getv);
        b2 = (Button) v.findViewById(R.id.paste);
        field1 = (EditText) v.findViewById(R.id.console_field_1);
        //  field2 = (EditText) v.findViewById(R.id.console_field_2);
        copy_current = (ImageButton) v.findViewById(R.id.copy_current);
    }

    @Override
    protected void run_bind_program_start() {
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//https://soundcloud.com/heskemo/sets/onepiecemusic
        //"https://www.facebook.com/shanghaiist/videos/10153940669221030/"
        final String t1 = "https://soundcloud.com/adealin/one-piece-epic-battle-theme";
        final String t2 = "https://soundcloud.com/heskemo/sets/songngn";
        field1.setText(t2);
        client = FBdownNet.getInstance(getActivity());

        final SoundCloud sndClient = SoundCloud.newInstance(getActivity());
        copy_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (console.getText().toString().isEmpty()) return;
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", console.getText().toString());
                clipboard.setPrimaryClip(clip);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                field1.setText(item.getText());
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (field1.getText().toString().isEmpty())
                    return;

                progress();
                /*
                client.getVideoUrl(
                        field1.getText().toString(),
                        new FBdownNet.fbdownCB() {
                            @Override
                            public void success(String answer) {
                                addMessage("====success====");
                                addMessage(answer);
                            }

                            @Override
                            public void failture(String why) {
                                addMessage("========error=========");
                                addMessage(why);
                            }
                        }
                );*/

                sndClient.pullFromUrl(field1.getText().toString(), new SoundCloud.Callback() {
                    @Override
                    public void success(LinkedHashMap<String, String> result) {
                        addMessage("====success====");
                        addMessage("resquest has result of " + result.size());
                        Iterator<String> iel = result.values().iterator();
                        while (iel.hasNext()) {
                            String el = iel.next();
                            addMessage("track =========================");
                            addMessage(el);

                        }
                        enableall();
                    }

                    @Override
                    public void failture(String why) {
                        addMessage("========error=========");
                        addMessage(why);
                        enableall();
                    }
                });

            }
        });
    }

    private void enableall() {
        betterCircleBar.setVisibility(View.GONE);
        b1.setEnabled(true);
        b2.setEnabled(true);
    }

    private void progress() {
        betterCircleBar.setVisibility(View.VISIBLE);
        b1.setEnabled(false);
        b2.setEnabled(false);
    }

}
