package com.hkm.vdlsdk.model.dictapi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zJJ on 1/16/2016.
 */
public class Term {
    @SerializedName("Word")
    public String resultType;
    @SerializedName("PartOfSpeech")
    public String speech;
    @SerializedName("Forms")
    public List<String> relates;
    @SerializedName("Definitions")
    public List<String> definitions;
}
