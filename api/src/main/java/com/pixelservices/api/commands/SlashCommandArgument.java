package com.pixelservices.api.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Annotation to define an argument for a SlashCommand.
 * <p>
 * This annotation is used to provide metadata for arguments of a method annotated with @SlashCommand.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(SlashCommandArguments.class)
public @interface SlashCommandArgument {
    @NotNull String name();
    @NotNull String description() default "";
    @NotNull OptionType type();
    @NotNull SlashCommandChoice[] choices() default {};
    boolean required() default true;
    boolean autoComplete() default false;
}

