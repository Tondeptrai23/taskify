pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9.6'
        jdk 'JDK 17'
    }
    
    environment {
        // Define service paths for easier reference
        COMMON_CORE_PATH = "libraries/common-lib-core"
        COMMON_WEB_PATH = "libraries/common-lib-web"
        DISCOVERY_PATH = "server-discovery"
        CONFIG_PATH = "config-server"
        AUTH_PATH = "microservices/auth-service"
        IAM_PATH = "microservices/iam-service"
        ORG_PATH = "microservices/organization-service" 
        PROJECT_PATH = "microservices/project-service"
        GATEWAY_PATH = "api-gateway"

        // Define coverage threshold
        COVERAGE_THRESHOLD = 70
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh "git fetch --all"
            }
        }
        
        stage('Detect Changes') {
            steps {
                script {
                    // Initialize change flags - default to true for pipeline testing/debugging
                    env.COMMON_CORE_CHANGED = "true"
                    env.COMMON_WEB_CHANGED = "true"
                    env.DISCOVERY_CHANGED = "true"
                    env.CONFIG_CHANGED = "true"
                    env.AUTH_CHANGED = "true"
                    env.IAM_CHANGED = "true"
                    env.ORG_CHANGED = "true"
                    env.PROJECT_CHANGED = "true"
                    env.GATEWAY_CHANGED = "true"
                    
                    try {
                        // Get the current commit hash
                        def currentCommit = sh(script: "git rev-parse HEAD", returnStdout: true).trim()
                        echo "Current commit: ${currentCommit}"
                        
                        // Try to get the changes
                        def changeSet = []
                        
                        // For first-time builds, there won't be a previous build
                        if (currentBuild.previousBuild) {
                            echo "This is not the first build - comparing with previous build"
                            
                            def previousCommit = sh(script: "git rev-parse HEAD~1", returnStdout: true).trim()
                            echo "Previous commit: ${previousCommit}"

                            // Get the changed files between the two commits
                            changeSet = sh(script: "git diff --name-only ${previousCommit} ${currentCommit}", returnStdout: true).trim()
                            
                            if (changeSet) {
                                changeSet = changeSet.split('\n')
                                echo "Changed files: ${changeSet.join(', ')}"
                                
                                // Reset all flags to false as we'll set them based on actual changes
                                env.COMMON_CORE_CHANGED = "false"
                                env.COMMON_WEB_CHANGED = "false"
                                env.DISCOVERY_CHANGED = "false"
                                env.CONFIG_CHANGED = "false"
                                env.AUTH_CHANGED = "false"
                                env.IAM_CHANGED = "false"
                                env.ORG_CHANGED = "false"
                                env.PROJECT_CHANGED = "false"
                                env.GATEWAY_CHANGED = "false"
                                
                                // Check each path in the change set
                                for (change in changeSet) {
                                    if (change.startsWith(COMMON_CORE_PATH)) {
                                        env.COMMON_CORE_CHANGED = "true"
                                        echo "Common Core Library changes detected"
                                    }
                                    else if (change.startsWith(COMMON_WEB_PATH)) {
                                        env.COMMON_WEB_CHANGED = "true"
                                        echo "Common Web Library changes detected"
                                    }
                                    else if (change.startsWith(DISCOVERY_PATH)) {
                                        env.DISCOVERY_CHANGED = "true"
                                        echo "Discovery Service changes detected"
                                    }
                                    else if (change.startsWith(CONFIG_PATH)) {
                                        env.CONFIG_CHANGED = "true"
                                        echo "Config Server changes detected"
                                    }
                                    else if (change.startsWith(AUTH_PATH)) {
                                        env.AUTH_CHANGED = "true"
                                        echo "Auth Service changes detected"
                                    }
                                    else if (change.startsWith(IAM_PATH)) {
                                        env.IAM_CHANGED = "true"
                                        echo "IAM Service changes detected"
                                    }
                                    else if (change.startsWith(ORG_PATH)) {
                                        env.ORG_CHANGED = "true"
                                        echo "Organization Service changes detected"
                                    }
                                    else if (change.startsWith(PROJECT_PATH)) {
                                        env.PROJECT_CHANGED = "true"
                                        echo "Project Service changes detected"
                                    }
                                    else if (change.startsWith(GATEWAY_PATH)) {
                                        env.GATEWAY_CHANGED = "true"
                                        echo "API Gateway changes detected"
                                    }
                                }
                                
                                // Propagate common library changes to dependent services
                                if (env.COMMON_CORE_CHANGED == "true") {
                                    env.COMMON_WEB_CHANGED = "true"
                                    env.AUTH_CHANGED = "true"
                                    env.IAM_CHANGED = "true" 
                                    env.ORG_CHANGED = "true"
                                    env.PROJECT_CHANGED = "true"
                                    env.GATEWAY_CHANGED = "true" 
                                    echo "Common Core Library change affects all services"
                                }
                                
                                if (env.COMMON_WEB_CHANGED == "true") {
                                    env.AUTH_CHANGED = "true"
                                    env.IAM_CHANGED = "true"
                                    env.ORG_CHANGED = "true"
                                    env.PROJECT_CHANGED = "true"
                                    echo "Common Web Library change affects all microservices"
                                }
                            } else {
                                echo "No changes detected or git diff returned empty result"
                            }
                        } else {
                            echo "This is the first build - building everything"
                            // First build, so all components need to be built
                        }
                    } catch (Exception e) {
                        echo "Error detecting changes: ${e.message}"
                        echo "Building all components as fallback"
                        // On error, build everything to be safe
                    }
                    
                    // Output summary of what will be built
                    echo "Services to build:"
                    echo "Common Core Library: ${env.COMMON_CORE_CHANGED}"
                    echo "Common Web Library: ${env.COMMON_WEB_CHANGED}"
                    echo "Discovery Service: ${env.DISCOVERY_CHANGED}"
                    echo "Config Server: ${env.CONFIG_CHANGED}" 
                    echo "Auth Service: ${env.AUTH_CHANGED}"
                    echo "IAM Service: ${env.IAM_CHANGED}"
                    echo "Organization Service: ${env.ORG_CHANGED}"
                    echo "Project Service: ${env.PROJECT_CHANGED}"
                    echo "API Gateway: ${env.GATEWAY_CHANGED}"
                }
            }
        }
        
        // BUILD PHASE
        stage('Build Phase') {
            stages {
                stage('Build Common Libraries') {
                    when {
                        expression { return env.COMMON_CORE_CHANGED == "true" || env.COMMON_WEB_CHANGED == "true" }
                    }
                    steps {
                        script {
                            if (env.COMMON_CORE_CHANGED == "true") {
                                dir(COMMON_CORE_PATH) {
                                    sh 'mvn clean install -DskipTests'
                                    echo "Common Core Library built and installed to local maven repository"
                                }
                            }
                            
                            if (env.COMMON_WEB_CHANGED == "true") {
                                dir(COMMON_WEB_PATH) {
                                    sh 'mvn clean install -DskipTests'
                                    echo "Common Web Library built and installed to local maven repository"
                                }
                            }
                        }
                    }
                }
                
                stage('Build Infrastructure Services') {
                    parallel {
                        stage('Discovery Service') {
                            when {
                                expression { return env.DISCOVERY_CHANGED == "true" }
                            }
                            steps {
                                dir(DISCOVERY_PATH) {
                                    sh 'mvn clean compile'
                                    echo "Discovery Service compiled"
                                }
                            }
                        }
                        
                        stage('Config Server') {
                            when {
                                expression { return env.CONFIG_CHANGED == "true" }
                            }
                            steps {
                                dir(CONFIG_PATH) {
                                    sh 'mvn clean compile'
                                    echo "Config Server compiled"
                                }
                            }
                        }
                    }
                }
                
                stage('Build Microservices') {
                    parallel {
                        stage('Auth Service') {
                            when {
                                expression { return env.AUTH_CHANGED == "true" }
                            }
                            steps {
                                dir(AUTH_PATH) {
                                    sh 'mvn clean compile'
                                    echo "Auth Service compiled"
                                }
                            }
                        }
                        
                        stage('IAM Service') {
                            when {
                                expression { return env.IAM_CHANGED == "true" }
                            }
                            steps {
                                dir(IAM_PATH) {
                                    sh 'mvn clean compile'
                                    echo "IAM Service compiled"
                                }
                            }
                        }
                        
                        stage('Organization Service') {
                            when {
                                expression { return env.ORG_CHANGED == "true" }
                            }
                            steps {
                                dir(ORG_PATH) {
                                    sh 'mvn clean compile'
                                    echo "Organization Service compiled"
                                }
                            }
                        }
                        
                        stage('Project Service') {
                            when {
                                expression { return env.PROJECT_CHANGED == "true" }
                            }
                            steps {
                                dir(PROJECT_PATH) {
                                    sh 'mvn clean compile'
                                    echo "Project Service compiled"
                                }
                            }
                        }
                        
                        stage('API Gateway') {
                            when {
                                expression { return env.GATEWAY_CHANGED == "true" }
                            }
                            steps {
                                dir(GATEWAY_PATH) {
                                    sh 'mvn clean compile'
                                    echo "API Gateway compiled"
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // TEST PHASE
        stage('Test Phase') {
            stages {
                stage('Test Infrastructure Services') {
                    parallel {
                        stage('Discovery Service') {
                            when {
                                expression { return env.DISCOVERY_CHANGED == "true" }
                            }
                            steps {
                                dir(DISCOVERY_PATH) {
                                    sh 'mvn test'
                                    echo "Discovery Service tests completed"
                                }
                            }
                            post {
                                always {
                                    junit allowEmptyResults: true, testResults: "${DISCOVERY_PATH}/target/surefire-reports/TEST-*.xml"
                                }
                            }
                        }
                        
                        stage('Config Server') {
                            when {
                                expression { return env.CONFIG_CHANGED == "true" }
                            }
                            steps {
                                dir(CONFIG_PATH) {
                                    sh 'mvn test'
                                    echo "Config Server tests completed"
                                }
                            }
                            post {
                                always {
                                    junit allowEmptyResults: true, testResults: "${CONFIG_PATH}/target/surefire-reports/TEST-*.xml"
                                }
                            }
                        }
                    }
                }
                
                stage('Test Microservices') {
                    parallel {
                        stage('Auth Service') {
                            when {
                                expression { return env.AUTH_CHANGED == "true" }
                            }
                            steps {
                                dir(AUTH_PATH) {
                                    sh 'mvn test'
                                    echo "Auth Service tests completed"
                                }
                            }
                            post {
                                always {
                                    junit allowEmptyResults: true, testResults: "${AUTH_PATH}/target/surefire-reports/TEST-*.xml"
                                }
                            }
                        }
                        
                        stage('IAM Service') {
                            when {
                                expression { return env.IAM_CHANGED == "true" }
                            }
                            steps {
                                dir(IAM_PATH) {
                                    sh 'mvn test jacoco:report jacoco:check'
                                    publishHTML(target: [
                                        allowMissing: false,
                                        alwaysLinkToLastBuild: true,
                                        keepAll: true,
                                        reportDir: "${IAM_PATH}/target/site/jacoco",
                                        reportFiles: 'index.html',
                                        reportName: 'IAM Service JaCoCo Report'
                                    ])
                                }
                            }
                            post {
                                always {
                                    junit allowEmptyResults: true, testResults: "${IAM_PATH}/target/surefire-reports/TEST-*.xml"
                                }
                            }
                        }
                        
                        stage('Organization Service') {
                            when {
                                expression { return env.ORG_CHANGED == "true" }
                            }
                            steps {
                                dir(ORG_PATH) {
                                    sh 'mvn test jacoco:report jacoco:check'

                                    publishHTML(target: [
                                        allowMissing: false,
                                        alwaysLinkToLastBuild: true,
                                        keepAll: true,
                                        reportDir: "${ORG_PATH}/target/site/jacoco",
                                        reportFiles: 'index.html',
                                        reportName: 'Organization Service JaCoCo Report'
                                    ])
                                }
                            }
                            post {
                                always {
                                    junit allowEmptyResults: true, testResults: "${ORG_PATH}/target/surefire-reports/TEST-*.xml"
                                }
                            }
                        }
                        
                        stage('Project Service') {
                            when {
                                expression { return env.PROJECT_CHANGED == "true" }
                            }
                            steps {
                                dir(PROJECT_PATH) {
                                    sh 'mvn test'
                                    echo "Project Service tests completed"
                                }
                            }
                            post {
                                always {
                                    junit allowEmptyResults: true, testResults: "${PROJECT_PATH}/target/surefire-reports/TEST-*.xml"
                                }
                            }
                        }
                        
                        stage('API Gateway') {
                            when {
                                expression { return env.GATEWAY_CHANGED == "true" }
                            }
                            steps {
                                dir(GATEWAY_PATH) {
                                    sh 'mvn test'
                                    echo "API Gateway tests completed"
                                }
                            }
                            post {
                                always {
                                    junit allowEmptyResults: true, testResults: "${GATEWAY_PATH}/target/surefire-reports/TEST-*.xml"
                                }
                            }
                        }
                    }
                }
                
                stage('Package Applications') {
                    parallel {
                        stage('Discovery Service') {
                            when {
                                expression { return env.DISCOVERY_CHANGED == "true" }
                            }
                            steps {
                                dir(DISCOVERY_PATH) {
                                    sh 'mvn package -DskipTests'
                                    echo "Discovery Service packaged"
                                }
                            }
                        }
                        
                        stage('Config Server') {
                            when {
                                expression { return env.CONFIG_CHANGED == "true" }
                            }
                            steps {
                                dir(CONFIG_PATH) {
                                    sh 'mvn package -DskipTests'
                                    echo "Config Server packaged"
                                }
                            }
                        }
                        
                        stage('Auth Service') {
                            when {
                                expression { return env.AUTH_CHANGED == "true" }
                            }
                            steps {
                                dir(AUTH_PATH) {
                                    sh 'mvn package -DskipTests'
                                    echo "Auth Service packaged"
                                }
                            }
                        }
                        
                        stage('IAM Service') {
                            when {
                                expression { return env.IAM_CHANGED == "true" }
                            }
                            steps {
                                dir(IAM_PATH) {
                                    sh 'mvn package -DskipTests'
                                    echo "IAM Service packaged"
                                }
                            }
                        }
                        
                        stage('Organization Service') {
                            when {
                                expression { return env.ORG_CHANGED == "true" }
                            }
                            steps {
                                dir(ORG_PATH) {
                                    sh 'mvn package -DskipTests'
                                    echo "Organization Service packaged"
                                }
                            }
                        }
                        
                        stage('Project Service') {
                            when {
                                expression { return env.PROJECT_CHANGED == "true" }
                            }
                            steps {
                                dir(PROJECT_PATH) {
                                    sh 'mvn package -DskipTests'
                                    echo "Project Service packaged"
                                }
                            }
                        }
                        
                        stage('API Gateway') {
                            when {
                                expression { return env.GATEWAY_CHANGED == "true" }
                            }
                            steps {
                                dir(GATEWAY_PATH) {
                                    sh 'mvn package -DskipTests'
                                    echo "API Gateway packaged"
                                }
                            }
                        }
                    }
                }
                
                stage('Archive Artifacts') {
                    steps {
                        script {
                            def artifactPaths = []
                            
                            if (env.DISCOVERY_CHANGED == "true") artifactPaths.add("${DISCOVERY_PATH}/target/*.jar")
                            if (env.CONFIG_CHANGED == "true") artifactPaths.add("${CONFIG_PATH}/target/*.jar")
                            if (env.AUTH_CHANGED == "true") artifactPaths.add("${AUTH_PATH}/target/*.jar")
                            if (env.IAM_CHANGED == "true") artifactPaths.add("${IAM_PATH}/target/*.jar")
                            if (env.ORG_CHANGED == "true") artifactPaths.add("${ORG_PATH}/target/*.jar")
                            if (env.PROJECT_CHANGED == "true") artifactPaths.add("${PROJECT_PATH}/target/*.jar")
                            if (env.GATEWAY_CHANGED == "true") artifactPaths.add("${GATEWAY_PATH}/target/*.jar")
                            
                            if (!artifactPaths.isEmpty()) {
                                archiveArtifacts artifacts: artifactPaths.join(','), fingerprint: true
                                echo "Artifacts archived"
                            } else {
                                echo "No artifacts to archive"
                            }
                        }
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Build, Test, and Code Coverage checks successful!'
        }
        failure {
            echo 'Build, Test, or Code Coverage check failed!'
        }
        always {
            echo 'Cleaning workspace...'
            cleanWs()
        }
    }
}