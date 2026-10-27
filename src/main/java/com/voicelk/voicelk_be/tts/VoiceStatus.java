package com.voicelk.voicelk_be.tts;

/**
 * Where a listed voice is in its life.
 *
 * <p>Only an available voice can be used for speech. The other two stay in the list so
 * the client can show users what is on the way and what has gone, instead of a voice
 * silently appearing or disappearing.
 */
public enum VoiceStatus {

    /** Served by the bridge and selectable. */
    AVAILABLE,

    /** Announced but not served yet. */
    COMING_SOON,

    /** No longer supported; kept in the list only to tell users it is gone. */
    RETIRED
}
