pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9.6'
        jdk 'JDK 17'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Common Libraries') {
            steps {
                sh 'cd libraries/common-lib-core && mvn clean install -DskipTests'
                sh 'cd libraries/common-lib-web && mvn clean install -DskipTests'
            }
        }
        
        stage('Build Microservices') {
            parallel {
                stage('Build Config Server') {
                    steps {
                        sh 'cd config-server && mvn clean package -DskipTests'
                    }
                }
                stage('Build Discovery Server') {
                    steps {
                        sh 'cd server-discovery && mvn clean package -DskipTests'
                    }
                }
                stage('Build Auth Service') {
                    steps {
                        sh 'cd microservices/auth-service && mvn clean package -DskipTests'
                    }
                }
                stage('Build IAM Service') {
                    steps {
                        sh 'cd microservices/iam-service && mvn clean package -DskipTests'
                    }
                }
                stage('Build Organization Service') {
                    steps {
                        sh 'cd microservices/organization-service && mvn clean package -DskipTests'
                    }
                }
                stage('Build Project Service') {
                    steps {
                        sh 'cd microservices/project-service && mvn clean package -DskipTests'
                    }
                }
                stage('Build API Gateway') {
                    steps {
                        sh 'cd api-gateway && mvn clean package -DskipTests'
                    }
                }
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }
    }
}