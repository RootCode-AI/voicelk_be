package com.voicelk.voicelk_be.tts;

/**
 * Raised when the TTS bridge cannot be reached or refuses a synthesis request.
 *
 * <p>The bridge is a separate process, so its failures are operational rather than
 * programming errors: it may be down, still loading a checkpoint, or rejecting the
 * text. The {@code retryable} flag separates "the service is not there right now"
 * from "this request will never work".
 */
public class TtsBridgeException extends RuntimeException {

    private final boolean retryable;

    public TtsBridgeException(String message, boolean retryable) {
        super(message);
        this.retryable = retryable;
    }

    public TtsBridgeException(String message, boolean retryable, Throwable cause) {
        super(message, cause);
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
