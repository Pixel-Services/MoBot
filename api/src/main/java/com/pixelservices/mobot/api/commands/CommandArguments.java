package com.pixelservices.mobot.api.commands;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;
import java.util.Map;

public class CommandArguments {
    private final Map<String, CommandArgument> args = new HashMap<>();

    public void put(String name, OptionMapping mapping) {
        args.put(name, new CommandArgument(mapping));
    }

    public CommandArgument get(String name) {
        return args.getOrDefault(name, new CommandArgument(null));
    }

    public String getAsString(String name) {
        return get(name).getAsString();
    }

    public Integer getAsInt(String name) {
        return get(name).getAsInt();
    }

    public Double getAsDouble(String name) {
        return get(name).getAsDouble();
    }

    public Boolean getAsBoolean(String name) {
        return get(name).getAsBoolean();
    }

    public net.dv8tion.jda.api.entities.Message.Attachment getAsAttachment(String name) {
        return get(name).getAsAttachment();
    }

    public boolean has(String name) {
        return args.containsKey(name) && args.get(name).isPresent();
    }

    public Map<String, CommandArgument> asMap() {
        return Map.copyOf(args);
    }
}

