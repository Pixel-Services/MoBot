package com.pixelservices.mobot.exceptions;

/**
 * Exception thrown when a command related issue occurs.
 * This is a runtime exception, so it does not need to be declared in a method's throws clause.
 */
public class CommandException extends RuntimeException {
    /**
     * Constructs a new CommandException with the specified detail message.
     *
     * @param message the detail message
     */
    public CommandException(String message) {
        super(message);
    }

    /**
     * Constructs a new CommandException with the specified cause.
     *
     * @param e the cause of the exception
     */
    public CommandException(Exception e) {
        super(e);
    }

    /**
     * Constructs a new CommandException with the specified detail message.
     *
     * @param message the detail message
     */
    public CommandException(String message, Exception e) {
        super(message, e);
    }
}
