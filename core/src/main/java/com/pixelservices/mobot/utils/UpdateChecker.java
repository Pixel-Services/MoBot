package com.pixelservices.mobot.utils;

import com.pixelservices.config.ConfigFactory;
import com.pixelservices.config.YamlConfig;
import com.pixelservices.logger.Logger;
import com.pixelservices.logger.LoggerFactory;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private final Logger logger = LoggerFactory.getLogger("UpdateChecker");
    private final String currentVersion;

    public UpdateChecker() {
        YamlConfig config = ConfigFactory.getYamlConfig("meta.yml");
        currentVersion = config.getString("version");
    }

    public boolean isLatest() {
        return !isSuperiorVersion(getLatestVersion(), currentVersion);
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getLatestVersion() {
        try {
            URL url = new URL("https://api.github.com/repos/Pixel-Services/MoBot/releases/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            if (connection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                JSONObject json = new JSONObject(content.toString());

                return json.getString("tag_name").substring(1);
            } else {
                logger.error("Failed to check for updates. HTTP response code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            logger.error("An error occurred while checking for updates.", e);
        }

        return "unknown";
    }

    private boolean isSuperiorVersion(String latestVersion, String currentVersion) {
        String[] latestParts = latestVersion.split("[^0-9]+");
        String[] currentParts = currentVersion.split("[^0-9]+");

        int length = Math.max(latestParts.length, currentParts.length);
        for (int i = 0; i < length; i++) {
            int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
            int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            if (latestPart > currentPart) {
                return true;
            } else if (latestPart < currentPart) {
                return false;
            }
        }
        return false;
    }
}