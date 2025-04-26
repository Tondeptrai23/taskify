def buildAndPushImages(List services, String namespace, String tag, String branchName) {
    def stepsForParallel = [:]
    
    // Determine if this is the main branch
    def isMainBranch = (branchName == 'main')
    
    services.each { service ->
        if (service.enabled) {
            def serviceName = service.name
            def dockerfile = service.dockerfile
            
            stepsForParallel["${serviceName}"] = {
                // Build the Docker image
                def imageName = "${namespace}/${serviceName}"
                
                // Create unique, descriptive tag
                def uniqueTag = "${tag}"
                def imageTagged = "${imageName}:${uniqueTag}"
                
                if (isMainBranch) {
                    // For main branch, also tag as latest
                    sh "docker build -f ${dockerfile} -t ${imageTagged} -t ${imageName}:latest ."
                    echo "${serviceName} Docker image built: ${imageTagged} (also tagged as latest)"
                    
                    sh "docker push ${imageTagged}"
                    sh "docker push ${imageName}:latest"
                    echo "${serviceName} Docker images pushed: ${imageTagged} and latest"
                } else {
                    // For feature branches, only use the specific tag
                    sh "docker build -f ${dockerfile} -t ${imageTagged} ."
                    echo "${serviceName} Docker image built: ${imageTagged}"
                    
                    sh "docker push ${imageTagged}"
                    echo "${serviceName} Docker image pushed: ${imageTagged}"
                }
            }
        }
    }
    
    parallel stepsForParallel
}

return this