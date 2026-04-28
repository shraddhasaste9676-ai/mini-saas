package com.example.issuetracker.repository;

import com.example.issuetracker.entity.IssueStatus;
import com.example.issuetracker.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByStatus(IssueStatus status);
}