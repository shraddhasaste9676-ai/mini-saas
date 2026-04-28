package com.example.issuetracker.controller;

import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.service.IssueService;
import com.example.issuetracker.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class IssueController {

    private final IssueService issueService;
    private final UserService userService;

    public IssueController(IssueService issueService, UserService userService) {
        this.issueService = issueService;
        this.userService = userService;
    }

    @GetMapping("/board")
    public String board(Model model) {
        List<Issue> issues = issueService.getAllIssues();
        model.addAttribute("todoIssues", issues.stream().filter(i -> i.getStatus() == IssueStatus.TO_DO).collect(Collectors.toList()));
        model.addAttribute("inProgressIssues", issues.stream().filter(i -> i.getStatus() == IssueStatus.IN_PROGRESS).collect(Collectors.toList()));
        model.addAttribute("inReviewIssues", issues.stream().filter(i -> i.getStatus() == IssueStatus.IN_REVIEW).collect(Collectors.toList()));
        model.addAttribute("doneIssues", issues.stream().filter(i -> i.getStatus() == IssueStatus.DONE).collect(Collectors.toList()));
        model.addAttribute("statuses", IssueStatus.values());
        return "board";
    }

    @GetMapping("/issue/new")
    public String showCreateForm(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "issue-form";
    }

    @PostMapping("/issue/new")
    public String createIssue(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam(required = false) String assignedToId,
                              @RequestParam(required = false) IssueStatus status,
                              Model model) {
        try {
            Long assigneeId = null;
            if (assignedToId != null && !assignedToId.trim().isEmpty()) {
                assigneeId = Long.valueOf(assignedToId);
            }
            issueService.createIssue(title, description, assigneeId, status);
            return "redirect:/board";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to create issue: " + e.getMessage());
            model.addAttribute("users", userService.getAllUsers());
            return "issue-form";
        }
    }

    @GetMapping("/issue/{id}")
    public String issueDetails(@PathVariable Long id, Model model) {
        model.addAttribute("issue", issueService.getIssueById(id));
        model.addAttribute("history", issueService.getIssueHistory(id));
        model.addAttribute("statuses", IssueStatus.values());
        return "issue-details";
    }

    @PostMapping("/issue/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam IssueStatus status,
                               @RequestParam(required = false, defaultValue = "/board") String redirectUrl) {
        issueService.updateStatus(id, status);
        return "redirect:" + redirectUrl;
    }
    
    @PostMapping("/issue/{id}/rollback")
    public String rollbackStatus(@PathVariable Long id) {
        issueService.rollbackStatus(id);
        return "redirect:/issue/" + id;
    }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/board";
    }
}