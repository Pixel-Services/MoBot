package com.pixelservices.mobot.api.commands;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a choice for a SlashCommandArgument.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface SlashCommandChoice {
    @NotNull String name();
    @NotNull String value();
}
