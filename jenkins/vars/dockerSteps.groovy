def buildAndPushImages(List services, String namespace, String tag) {
    def stepsForParallel = [:]
    
    services.each { service ->
        if (service.enabled) {
            def serviceName = service.name
            def dockerfile = service.dockerfile
            
            stepsForParallel["${serviceName}"] = {
                // Build the Docker image
                def imageName = "${namespace}/${serviceName}"
                def imageTagged = "${imageName}:${tag}"
                def imageLatest = "${imageName}:latest"
                
                sh "docker build -f ${dockerfile} -t ${imageTagged} -t ${imageLatest} ."
                echo "${serviceName} Docker image built: ${imageTagged}"
                
                // Push the Docker image
                sh "docker push ${imageTagged}"
                sh "docker push ${imageLatest}"
                echo "${serviceName} Docker image pushed: ${imageTagged}"
            }
        }
    }
    
    parallel stepsForParallel
}

return this