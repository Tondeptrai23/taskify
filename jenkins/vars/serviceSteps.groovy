def buildService(String servicePath, String serviceName) {
    echo "Building ${serviceName}..."
    dir(servicePath) {
        sh 'mvn clean compile'
        echo "${serviceName} compiled"
    }
}

def testService(String servicePath, String serviceName) {
    echo "Testing ${serviceName}..."
    dir(servicePath) {
        sh 'mvn test'
        echo "${serviceName} tests completed"
    }
    
    junit allowEmptyResults: true, testResults: "${servicePath}/target/surefire-reports/TEST-*.xml"
}

def packageServicesInParallel(List services) {
    def stepsForParallel = [:]
    
    services.each { service ->
        if (service.enabled) {
            def servicePath = service.path
            def serviceName = service.name
            
            stepsForParallel[serviceName] = {
                dir(servicePath) {
                    sh 'mvn package -DskipTests'
                    echo "${serviceName} packaged"
                }
            }
        }
    }
    
    parallel stepsForParallel
}

return this