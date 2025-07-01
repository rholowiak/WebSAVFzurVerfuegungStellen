package com.example.demo.controller;

import com.example.demo.service.RunCmdAndFTP;
import com.example.demo.model.Message;
import com.example.demo.util.RegexSampleCheck;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class SavfController {


    private final RunCmdAndFTP ftpService;
    private final RunCmdAndFTP runCmdAndFTP;

    public SavfController(RunCmdAndFTP ftpService, RunCmdAndFTP runCmdAndFTP) {
        this.ftpService = ftpService;
        this.runCmdAndFTP = runCmdAndFTP;
    }

    @GetMapping("/form")
    public String showForm() {
        return "form";
    }
    @GetMapping("/end")
    public String endSession() {
        return "end";
    }


    @PostMapping("/submit")
    public String submitForm(@RequestParam("sourcePath") String sourcePath,
                             @RequestParam("username") String username,
                             @RequestParam("password") String password,
                             Model model) {

        if (!RegexSampleCheck.useRegex(sourcePath)) {
            model.addAttribute("error1", "(1) Invalid input format in source path!");
            model.addAttribute("error2", "(2) Please check, reset and try again.");
            return "form";
        }

        try {
            List<Message> messages = ftpService.useCmdAndFTP(sourcePath, username, password);
            String sourcePathToFileName = sourcePath.substring(sourcePath.indexOf("/") + 1); //added to controller

            model.addAttribute("messages", messages);
            if (!FTPReply.isPositiveCompletion(runCmdAndFTP.getReplyCode())) {
                model.addAttribute("statusFailed", "Process failed! Check VPN connection");
                model.addAttribute("isSuccess", false); // ✅ Add this flag
            }
            if (FTPReply.isPositiveCompletion(runCmdAndFTP.getReplyCode())) {
                model.addAttribute("statusSuccessful", "Download completed");
                model.addAttribute("isSuccess", true); // ✅ Add this flag
                model.addAttribute("sourcePathToFileName", sourcePathToFileName); //added to controller
            }
            return "result";
        } catch (Exception e) {
            model.addAttribute("error1", "An unexpected error occurred.");
            model.addAttribute("error2", e.getMessage());
            model.addAttribute("statusFailed", "Process failed!");
            model.addAttribute("isSuccess", false); // ✅ Add this flag
            return "result";
        }

    }

}
