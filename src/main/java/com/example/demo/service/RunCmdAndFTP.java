package com.example.demo.service;

import com.example.demo.model.Message;
import com.example.demo.util.DeleteOldFiles;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RunCmdAndFTP {

    private int replyCode; // <-- Add this field
    public int getReplyCode() { // <-- Add this getter
        return replyCode;
    }

    public List<Message> useCmdAndFTP(String sourcePath, String username, String password) {
        List<Message> messages = new ArrayList<>();
        FTPClient ftpClient = new FTPClient();

        try {
            messages.add(new Message("Cleaning up old files...", "black", false, LocalDateTime.now()));

            File targetDir = new File("c:/infor/");
            List<String> cleanupMessages = DeleteOldFiles.delete(targetDir, 2);
            cleanupMessages.forEach(msg -> messages.add(new Message(msg, "black", false, LocalDateTime.now())));

            if (!targetDir.exists() && !targetDir.mkdirs()) {
                messages.add(new Message("Error: Failed to create target directory.", "red", true, LocalDateTime.now()));
                return messages;
            }

            messages.add(new Message("Connecting to FTP server...", "black", false, LocalDateTime.now()));

// connect to server nlbaid07
            ftpClient.connect("nlbaid07");
            replyCode = ftpClient.getReplyCode();// <-- Save to field

            if (!FTPReply.isPositiveCompletion(replyCode)) {
                messages.add(new Message("FTP server refused connection. Reply code: " + replyCode, "red", true, LocalDateTime.now()));
                ftpClient.disconnect();
                return messages;
            }

            boolean login = ftpClient.login(username, password);
            if (!login) {
                messages.add(new Message("Login failed. Please check your username/password or VPN connection.", "red", true, LocalDateTime.now()));
                ftpClient.disconnect();
                return messages;
            }

            messages.add(new Message("Connection to FTP server successful.", "green", false, LocalDateTime.now()));

// Set binary mode
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            messages.add(new Message("Set transfer mode to binary (bin).", "black", false, LocalDateTime.now()));

// Build the local file path
            String sourcePathToFileName = sourcePath.substring(sourcePath.indexOf("/") + 1);
            File localFile = new File("c:/infor/" + sourcePathToFileName + ".SAVF");

// Retrieve the file
            try (FileOutputStream fos = new FileOutputStream(localFile)) {
                boolean success = ftpClient.retrieveFile(sourcePath, fos);
                if (success) {
                    messages.add(new Message("File downloaded successfully to: " + localFile.getAbsolutePath(), "black", false, LocalDateTime.now()));
                } else {
                    messages.add(new Message("Failed to download file: " + sourcePath, "red", true, LocalDateTime.now()));
                }
            }

// server nlbaid07 logout
            ftpClient.logout();
            messages.add(new Message("Download completed.", "green", false, LocalDateTime.now()));

        } catch (IOException e) {
            replyCode = ftpClient.getReplyCode();// <-- Save to field in exception
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                messages.add(new Message("FTP server refused connection. Reply code: " + replyCode, "red", false, LocalDateTime.now()));
            }
            messages.add(new Message("Error: " + e.getMessage(), "red", false, LocalDateTime.now()));
            messages.add(new Message("Login failed. Please check your username/password or VPN connection.", "red", true, LocalDateTime.now()));

        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                messages.add(new Message("Error disconnecting: " + ex.getMessage(), "red", false, LocalDateTime.now()));
            }
        }
        return messages;
    }
}
