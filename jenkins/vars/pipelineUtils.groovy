def detectChanges(Map config) {
    def serviceFlags = [:]
    
    echo "Detecting changes in git repository"
    
    // Initialize change flags - default to true for pipeline testing/debugging
    serviceFlags.COMMON_CORE_CHANGED = true
    serviceFlags.COMMON_WEB_CHANGED = true
    serviceFlags.DISCOVERY_CHANGED = true
    serviceFlags.CONFIG_CHANGED = true
    serviceFlags.AUTH_CHANGED = true
    serviceFlags.IAM_CHANGED = true
    serviceFlags.ORG_CHANGED = true
    serviceFlags.PROJECT_CHANGED = true
    serviceFlags.GATEWAY_CHANGED = true
    
    try {
        // Get the current commit hash
        def currentCommit = sh(script: "git rev-parse HEAD", returnStdout: true).trim()
        echo "Current commit: ${currentCommit}"
        
        // Try to get the changes
        def changeSet = []
        
        def previousCommit = env.GIT_PREVIOUS_SUCCESSFUL_COMMIT ?: ""
        
        if (previousCommit) {
            echo "Previous successful commit: ${previousCommit}"

            // Reset all flags to false as we'll set them based on actual changes
            serviceFlags.COMMON_CORE_CHANGED = false
            serviceFlags.COMMON_WEB_CHANGED = false
            serviceFlags.DISCOVERY_CHANGED = false
            serviceFlags.CONFIG_CHANGED = false
            serviceFlags.AUTH_CHANGED = false
            serviceFlags.IAM_CHANGED = false
            serviceFlags.ORG_CHANGED = false
            serviceFlags.PROJECT_CHANGED = false
            serviceFlags.GATEWAY_CHANGED = false
            
            // Get the changed files between the two commits
            changeSet = sh(script: "git diff --name-only ${previousCommit} ${currentCommit}", returnStdout: true).trim()
            
            if (changeSet) {
                changeSet = changeSet.split('\n')
                echo "Changed files: ${changeSet.join(', ')}"
                
                // Check each path in the change set
                for (change in changeSet) {
                    if (change.startsWith("Jenkinsfile")) {
                        echo "Jenkinsfile changes detected - rebuilding everything"
                        serviceFlags.COMMON_CORE_CHANGED = true
                        serviceFlags.COMMON_WEB_CHANGED = true
                        serviceFlags.DISCOVERY_CHANGED = true
                        serviceFlags.CONFIG_CHANGED = true
                        serviceFlags.AUTH_CHANGED = true
                        serviceFlags.IAM_CHANGED = true
                        serviceFlags.ORG_CHANGED = true
                        serviceFlags.PROJECT_CHANGED = true
                        serviceFlags.GATEWAY_CHANGED = true
                    }
                    else if (change.startsWith(config.COMMON_CORE_PATH)) {
                        serviceFlags.COMMON_CORE_CHANGED = true
                        echo "Common Core Library changes detected"
                    }
                    else if (change.startsWith(config.COMMON_WEB_PATH)) {
                        serviceFlags.COMMON_WEB_CHANGED = true
                        echo "Common Web Library changes detected"
                    }
                    else if (change.startsWith(DISCOVERY_PATH)) {
                        serviceFlags.DISCOVERY_CHANGED = true
                        echo "Discovery Service changes detected"
                    }
                    else if (change.startsWith(CONFIG_PATH)) {
                        serviceFlags.CONFIG_CHANGED = true
                        echo "Config Server changes detected"
                    }
                    else if (change.startsWith(AUTH_PATH)) {
                        serviceFlags.AUTH_CHANGED = true
                        echo "Auth Service changes detected"
                    }
                    else if (change.startsWith(IAM_PATH)) {
                        serviceFlags.IAM_CHANGED = true
                        echo "IAM Service changes detected"
                    }
                    else if (change.startsWith(ORG_PATH)) {
                        serviceFlags.ORG_CHANGED = true
                        echo "Organization Service changes detected"
                    }
                    else if (change.startsWith(PROJECT_PATH)) {
                        serviceFlags.PROJECT_CHANGED = true
                        echo "Project Service changes detected"
                    }
                    else if (change.startsWith(GATEWAY_PATH)) {
                        serviceFlags.GATEWAY_CHANGED = true
                        echo "API Gateway changes detected"
                    }
                }
                
                // Propagate common library changes to dependent services
                if (serviceFlags.COMMON_CORE_CHANGED) {
                    serviceFlags.COMMON_WEB_CHANGED = true
                    serviceFlags.AUTH_CHANGED = true
                    serviceFlags.IAM_CHANGED = true 
                    serviceFlags.ORG_CHANGED = true
                    serviceFlags.PROJECT_CHANGED = true
                    serviceFlags.GATEWAY_CHANGED = true 
                    echo "Common Core Library change affects all services"
                }
                
                if (serviceFlags.COMMON_WEB_CHANGED) {
                    serviceFlags.AUTH_CHANGED = true
                    serviceFlags.IAM_CHANGED = true
                    serviceFlags.ORG_CHANGED = true
                    serviceFlags.PROJECT_CHANGED = true
                    echo "Common Web Library change affects all microservices"
                }
            }
        }
    } catch (Exception e) {
        echo "Error detecting changes: ${e.message}"
        echo "Building all components as fallback"
    }
    
    return serviceFlags
}

return this