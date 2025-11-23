package com.pixelservices.mobot.api.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class CommandArgument {
    private final OptionMapping option;

    public CommandArgument(OptionMapping option) {
        this.option = option;
    }

    public boolean isPresent() {
        return option != null;
    }

    public String getAsString() {
        return option != null ? option.getAsString() : null;
    }

    public Integer getAsInt() {
        return option != null ? option.getAsInt() : null;
    }

    public Boolean getAsBoolean() {
        return option != null ? option.getAsBoolean() : null;
    }

    public Double getAsDouble() {
        return option != null ? option.getAsDouble() : null;
    }

    public Message.Attachment getAsAttachment() {
        return option != null ? option.getAsAttachment() : null;
    }

    public OptionMapping getRaw() {
        return option;
    }
}

