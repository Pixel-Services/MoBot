package com.pixelservices.exceptions;

/**
 * Exception thrown when a command fails to execute.
 * This is a runtime exception, so it does not need to be declared in a method's throws clause.
 */
public class CommandExecuteException extends CommandException {
    /**
     * Constructs a new CommandExecuteException with the specified detail message.
     *
     * @param message the detail message
     */
    public CommandExecuteException(String message) {
        super(message);
    }

    /**
     * Constructs a new CommandExecuteException with the specified cause.
     *
     * @param e the cause of the exception
     */
    public CommandExecuteException(Exception e) {
        super(e);
    }
}
