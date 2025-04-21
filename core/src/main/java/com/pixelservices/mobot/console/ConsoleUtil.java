package com.pixelservices.mobot.console;

import com.pixelservices.mobot.utils.AnsiColorUtil;
import com.pixelservices.mobot.utils.UpdateChecker;

public class ConsoleUtil {
    private final UpdateChecker updateChecker = new UpdateChecker();

    public void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                // uses 'cmd.exe' to execute the 'cls' command
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // for unix-like systems
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            // manual goofy clear
            for(int i=0; i<300; i++){
                System.out.println();
            }
        }
        generateTitleAscii();
    }

    private void generateTitleAscii(){
        print("#77DD77  __  __       ____        _   ");
        print("#77DD77 |  \\/  | ___ | __ )  ___ | |_    #FFFFFF-  Pixel Services");
        print("#77DD77 | |\\/| |/ _ \\|  _ \\ / _ \\| __|   #FFFFFF-  Version: " + updateChecker.getCurrentVersion());
        print("#77DD77 | |  | | (_) | |_) | (_) | |_    #FFFFFF-  Host: " + System.getProperty("os.name"));
        print("#77DD77 |_|  |_|\\___/|____/ \\___/ \\__|   #FFFFFF-  Memory: " + Runtime.getRuntime().maxMemory() / (1024 * 1024));
        print("  ");
        print("Welcome to the #77DD77MoBot CLI#FFFFFF. Type 'help' to see available commands.");
        print("  ");
    }

    private void print(String message) {
        String coloredMessage = AnsiColorUtil.applyColors(message);
        System.out.println(coloredMessage);
    }
}