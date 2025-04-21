package com.pixelservices.mobot.api.commands;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method as a slash command.
 * <p>
 * This annotation is used to define a slash command for a Discord bot using JDA (Java Discord API).
 * It provides metadata such as the command name, permission requirements, aliases, description, and options.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SlashCommand {
    @NotNull String name();
    @NotNull Permission permission() default Permission.UNKNOWN;
    @NotNull String[] aliases() default {};
    @NotNull String description() default "";

}