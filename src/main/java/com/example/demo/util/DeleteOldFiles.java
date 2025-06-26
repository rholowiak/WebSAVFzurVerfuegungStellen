package com.example.demo.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeleteOldFiles {
    public static List<String> delete(File directory, int daysOld) {
        List<String> messages = new ArrayList<>();

        if (!directory.exists() || !directory.isDirectory()) {
            messages.add("Directory does not exist: " + directory.getAbsolutePath());
            return messages;
        }

        long cutoff = System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000L);

        File[] oldFiles = directory.listFiles(file -> file.isFile() && file.lastModified() < cutoff);

        if (oldFiles != null) {
            for (File file : oldFiles) {
                if (file.delete()) {
                    messages.add("Deleted old file: " + file.getName());
                } else {
                    messages.add("Failed to delete: " + file.getName());
                }
            }
        }

        return messages;
    }
}