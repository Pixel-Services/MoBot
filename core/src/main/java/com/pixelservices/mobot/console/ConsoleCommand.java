package com.pixelservices.mobot.console;

import org.slf4j.Logger;

public interface ConsoleCommand {
    void execute(String[] args, Logger logger);
}
