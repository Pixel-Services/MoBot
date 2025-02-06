package com.pixelservices.console;

import com.pixelservices.logger.Logger;

public interface ConsoleCommand {
    void execute(String[] args, Logger logger);
}
