package com.taskify.project.common;

import com.taskify.project.project.entity.Project;
import com.taskify.project.project.entity.ProjectStatus;
import com.taskify.project.project.repository.ProjectRepository;
import com.taskify.project.workitem.entity.WorkItem;
import com.taskify.project.workitem.entity.WorkItemStatus;
import com.taskify.project.workitem.entity.WorkItemType;
import com.taskify.project.workitem.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final ProjectRepository projectRepository;
    private final WorkItemRepository workItemRepository;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTestData() {
        // Create a test project
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setOrganizationId(UUID.randomUUID()); // Mock org ID
        project.setStatus(ProjectStatus.ACTIVE);

        Project savedProject = projectRepository.save(project);

        // Create a test work item
        WorkItem workItem = new WorkItem();
        workItem.setType(WorkItemType.TASK);
        workItem.setTitle("Test Task");
        workItem.setDescription("Test Task Description");
        workItem.setStatus(WorkItemStatus.TODO);
        workItem.setProjectId(savedProject.getId());
        workItem.setReporterId(UUID.randomUUID()); // Mock reporter ID

        WorkItem savedWorkItem = workItemRepository.save(workItem);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("project", savedProject);
        response.put("workItem", savedWorkItem);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<Project> getProject(@PathVariable UUID id) {
        return projectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/workitem/{id}")
    public ResponseEntity<WorkItem> getWorkItem(@PathVariable String id) {
        return workItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}