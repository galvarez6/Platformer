package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.media.SoundPool;

public class SoundManager {

    ///////////////enum for choosing what sound is played////////////
    /////////this method give us words with value meaning we know what
    /////////words are asocated to what sound instead of using
    /////////an if or switch case
    public enum Sound {
        COIN_PICKUP(R.raw.coin_pickup),
        EXPLODE(R.raw.explode),
        EXTRA_LIFE(R.raw.extra_life),
        GUN_UPGRADE(R.raw.gun_upgrade),
        HIT_GUARD(R.raw.hit_guard),
        JUMP(R.raw.jump),
        RICOCHET(R.raw.ricochet),
        SHOOT(R.raw.shoot),
        TELEPORT(R.raw.teleport);

        public final int resourceId;
        private int soundId;

        Sound(int resourceId) {
            this.resourceId = resourceId;
        }
    }

    //single instance of the sound class so no other are created
    //this is used becuase the application uses sound and
    ///we want to control access to the sounds
    private static SoundManager theInstance;
    ////////////////////////////////////////////////////////////

    private final SoundPool soundPool;


    ///////constructor for sound manager
        private SoundManager(Context context) {
        soundPool = new SoundPool.Builder().setMaxStreams(Sound.values().length).build();
        for (Sound sound: Sound.values()) {
            sound.soundId = soundPool.load(context, sound.resourceId, 1);
        }
    }

    //constructor for singleton
    public static SoundManager instance(Context context) {
        if (theInstance == null) {
            theInstance = new SoundManager(context);
        }
        return theInstance;
    }

    public void play(Sound sound) {
        soundPool.play(sound.soundId, 1, 1, 0, 0, 1);
    }
}