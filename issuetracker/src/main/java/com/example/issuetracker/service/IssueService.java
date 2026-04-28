package com.example.issuetracker.service;

import com.example.issuetracker.entity.Issue;
import com.example.issuetracker.entity.IssueHistory;
import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.repository.IssueHistoryRepository;
import com.example.issuetracker.repository.IssueRepository;
import com.example.issuetracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import java.util.List;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final IssueHistoryRepository issueHistoryRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public IssueService(IssueRepository issueRepository, UserRepository userRepository, IssueHistoryRepository issueHistoryRepository) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.issueHistoryRepository = issueHistoryRepository;
    }

    @Transactional
    public Issue createIssue(String title, String description, Long assigneeId, IssueStatus initialStatus) {
        Issue issue = new Issue();
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setStatus(initialStatus != null ? initialStatus : IssueStatus.TO_DO);
        
        if (assigneeId != null) {
            issue.setAssignee(userRepository.findById(assigneeId).orElse(null));
        }

        return issueRepository.save(issue);
    }

    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    public Issue getIssueById(Long id) {
        return issueRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void updateStatus(Long id, IssueStatus status) {
        Issue issue = issueRepository.findById(id).orElseThrow();
        issue.setStatus(status);
        issueRepository.save(issue);
    }
    
    public List<IssueHistory> getIssueHistory(Long issueId) {
        return issueHistoryRepository.findByIssueIdOrderByChangedAtDesc(issueId);
    }

    @Transactional
    public void rollbackStatus(Long issueId) {
        // Calling the MySQL Stored Procedure
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("RollbackIssueStatus");
        query.registerStoredProcedureParameter("p_issue_id", Long.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter("p_issue_id", issueId);
        query.execute();
        
        // Refresh the JPA context for this entity if needed or just let the caller fetch it again
    }
}