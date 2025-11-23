package com.pixelservices.mobot.console.impl;

import com.pixelservices.mobot.console.ConsoleCommand;
import com.pixelservices.mobot.utils.UpdateChecker;
import org.slf4j.Logger;

public class VersionCommand implements ConsoleCommand {
    private final UpdateChecker updateChecker;

    public VersionCommand() {
        this.updateChecker = new UpdateChecker();
    }

    @Override
    public void execute(String[] args, Logger logger) {
        if (updateChecker.isLatest()) {
            logger.info("You are using the latest version of MoBot: {}.", updateChecker.getCurrentVersion());
        } else {
            logger.info("A new version of MoBot is available: {}. You are currently on version {}.", updateChecker.getLatestVersion(), updateChecker.getCurrentVersion());
        }
    }
}
