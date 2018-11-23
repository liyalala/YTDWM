package com.ist_systems.ytdwm;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jmcaceres on 04/11/2017.
 */

public class Util {
    public static SoundPool sp;
    public static Map<Integer, Integer> suondMap;
    public static Context context;

    public static void initSoundPool(Context context) {
        Util.context = context;
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        suondMap = new HashMap<Integer, Integer>();
        suondMap.put(1, sp.load(context, R.raw.msg, 1));
    }

    public static void play(int sound, int number) {
        AudioManager am = (AudioManager) Util.context.getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = audioCurrentVolume / audioMaxVolume;
        sp.play(
                suondMap.get(sound),
                audioCurrentVolume,
                audioCurrentVolume,
                1,
                number,
                1);
    }
}
