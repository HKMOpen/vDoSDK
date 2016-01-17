package com.hkm.dltstclien;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.hkm.vdlsdk.Util;
import com.hkm.vdlsdk.client.FBdownNet;
import com.hkm.vdlsdk.client.SoundCloud;
import com.hkm.vdlsdk.model.urban.Term;
import com.hkm.videosdkui.application.Dialog.ErrorMessage;
import com.hkm.videosdkui.monkeyTest.testBasic;

import java.util.Iterator;
import java.util.LinkedHashMap;

import retrofit.Call;

/**
 * Created by zJJ on 1/16/2016.
 */
public class TestGen extends testBasic {
    EditText field1, field2;
    Button b1, b2, b3;
    ImageButton copy_current;

    private LinkedHashMap<String, String> soundcloud_result;
    private String fb_video_result;

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
        b3 = (Button) v.findViewById(R.id.getv);
        b2 = (Button) v.findViewById(R.id.paste);
        b1 = (Button) v.findViewById(R.id.getsnd);
        field1 = (EditText) v.findViewById(R.id.console_field_1);
        //  field2 = (EditText) v.findViewById(R.id.console_field_2);
        copy_current = (ImageButton) v.findViewById(R.id.copy_current);

        final String t1 = "https://soundcloud.com/adealin/one-piece-epic-battle-theme";
        final String t2 = "https://soundcloud.com/heskemo/sets/songngn";
        field1.setText(t2);
    }

    private void setClip(String info) {
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", info);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    protected void run_bind_program_start() {
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        //https://soundcloud.com/heskemo/sets/onepiecemusic
        //https://www.facebook.com/shanghaiist/videos/10153940669221030/
        final FBdownNet fbclient = FBdownNet.getInstance(getActivity());
        final SoundCloud sndClient = SoundCloud.newInstance(getActivity());
       /* copy_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (console.getText().toString().isEmpty()) return;
                setClip(console.getText().toString());
            }
        });*/
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                field1.setText(item.getText());
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress();
                fbclient.getVideoUrl(
                        field1.getText().toString(),
                        new FBdownNet.fbdownCB() {
                            @Override
                            public void success(String answer) {
                                addMessage("====success====");
                                addMessage(answer);
                                setClip(answer);
                                enableall();
                                Util.EasyVideoMessageShare(getActivity(), null, answer);
                                fb_video_result = answer;
                                soundcloud_result = null;
                            }

                            @Override
                            public void failture(String why) {
                                addMessage("========error=========");
                                addMessage(why);
                                enableall();
                            }

                            @Override
                            public void loginfirst(String why) {
                                addMessage("========need to login first=========");
                                addMessage(why);
                                enableall();
                            }
                        }
                );
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (field1.getText().toString().isEmpty())
                    return;
                progress();
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
                        fb_video_result = null;
                        soundcloud_result = result;
                        Util.EasySoundCloudListShare(getActivity(), result);
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

        copy_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fb_video_result != null) {
                    Util.EasyVideoMessageShare(getActivity(), null, fb_video_result);
                }

                if (soundcloud_result != null) {
                    Util.EasySoundCloudListShare(getActivity(), soundcloud_result);
                }

                if (soundcloud_result == null && fb_video_result == null) {
                    ErrorMessage.alert("There is no item converted.", getChildFragmentManager());
                }
            }
        });
    }

    private void enableall() {
        completeloading();
        b1.setEnabled(true);
        b2.setEnabled(true);
        b3.setEnabled(true);
    }

    private void progress() {
        betterCircleBar.setVisibility(View.VISIBLE);
        b1.setEnabled(false);
        b2.setEnabled(false);
        b3.setEnabled(false);
    }

}
