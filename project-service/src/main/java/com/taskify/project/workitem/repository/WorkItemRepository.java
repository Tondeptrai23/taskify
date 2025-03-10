package com.taskify.project.workitem.repository;

import com.taskify.project.workitem.entity.WorkItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkItemRepository extends MongoRepository<WorkItem, String> {
}
