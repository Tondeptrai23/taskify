package com.taskify.project.workitem.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.taskify.project.workitem.entity.WorkItemHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkItemHistoryRepository extends MongoRepository<WorkItemHistory, String> {
}
