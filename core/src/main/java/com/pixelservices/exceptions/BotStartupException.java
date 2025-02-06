package com.pixelservices.exceptions;

/**
 * Exception thrown when the bot encounters a startup issue.
 * <p>
 * This exception is used to signal that there was a problem initializing the bot,
 * such as an invalid configuration or failure to create a shard manager.
 * </p>
 */
public class BotStartupException extends Exception {

    /**
     * Constructs a new BotStartupException with the specified detail message.
     *
     * @param message the detail message
     */
    public BotStartupException(String message) {
        super(message);
    }

    /**
     * Constructs a new BotStartupException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public BotStartupException(String message, Throwable cause) {
        super(message, cause);
    }
}