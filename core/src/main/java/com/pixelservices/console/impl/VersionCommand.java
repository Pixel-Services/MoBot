package com.pixelservices.console.impl;

import com.pixelservices.console.ConsoleCommand;
import com.pixelservices.logger.Logger;
import com.pixelservices.utils.UpdateChecker;

public class VersionCommand implements ConsoleCommand {
    private final UpdateChecker updateChecker;

    public VersionCommand() {
        this.updateChecker = new UpdateChecker();
    }

    @Override
    public void execute(String[] args, Logger logger) {
        if (updateChecker.isLatest()) {
            logger.info("You are using the latest version of MoBot: " + updateChecker.getCurrentVersion() + ".");
        } else {
            logger.info("A new version of MoBot is available: " + updateChecker.getLatestVersion() + ". You are currently on version " + updateChecker.getCurrentVersion() + ".");
        }
    }
}
