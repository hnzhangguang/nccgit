package com.yonyou.audio.plugin.model;

public enum PersonalAudioFormat {
    AAC,
    MP3,
    M4A,
    WMA,
    WAV,
    FLAC;

    public String getFormat() {
        return name().toLowerCase();
    }
}