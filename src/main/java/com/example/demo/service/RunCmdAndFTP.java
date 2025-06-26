package com.example.demo.service;
import org.springframework.stereotype.Service;

package com.example.demo;
import javax.swing.*;
import java.io.*;

public class RunCmdAndFTP {

    public void useCmdAndFTP(final String sourcePath, final String user, final String pass,
                             final JTextArea outputArea, final JProgressBar progressBar, final JLabel statusLabel)
    {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    outputArea.append(message + "\n");
                    statusLabel.setText(message); // Update the status label with the latest message
                }
            }

            @Override
            protected Void doInBackground() {
                try {
                    DeleteOldFiles.delete(new File("C:/infor/"), 2, outputArea);

                    publish("Starting FTP process...");

                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);

                    File targetDir = new File("c:/infor/");
                    if (!targetDir.exists() && !targetDir.mkdirs()) {
                        publish("Error: Failed to create target directory.");
                        return null;
                    }

                    publish("Creating FTP script...");

                    File scriptFile = FTPScriptFileCreator.createScriptFile(sourcePath, pass, user);

                    publish("Connecting to FTP server...");

                    ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "ftp -s:" + scriptFile.getAbsolutePath());
                    builder.redirectErrorStream(true);
                    Process process = builder.start();
                    Thread.sleep(1000); // 1-second delay


                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        publish("FTP process exited with code: " + exitCode + " -> you need to connect to VPN!");
                        Thread.sleep(1000); // 1-second delay
                    }

                    publish("Downloading file...");

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            publish(line);
                        }
                    }

                    scriptFile.deleteOnExit();

                    publish("Download complete.");

                } catch (IOException | InterruptedException e) {
                    publish("Error: " + e.getMessage());
                }

                return null;
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                publish("FTP process completed.");
            }
        };
        worker.execute();
    }
}
