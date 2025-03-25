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
        
        // BUILD PHASE
        stage('Build Phase') {
            stages {
                stage('Build Common Libraries') {
                    steps {
                        dir('libraries/common-lib-core') {
                            sh 'mvn clean install package -DskipTests'
                        }
                        dir('libraries/common-lib-web') {
                            sh 'mvn clean install package -DskipTests'
                        }
                    }
                }
                
                stage('Build Infrastructure Services') {
                    steps {
                        dir('server-discovery') {
                            sh 'mvn clean compile'
                        }
                        dir('config-server') {
                            sh 'mvn clean compile'
                        }
                    }
                }
                
                stage('Build Microservices') {
                    parallel {
                        stage('Auth Service') {
                            steps {
                                dir('microservices/auth-service') {
                                    sh 'mvn clean compile'
                                }
                            }
                        }
                        stage('IAM Service') {
                            steps {
                                dir('microservices/iam-service') {
                                    sh 'mvn clean compile'
                                }
                            }
                        }
                        stage('Organization Service') {
                            steps {
                                dir('microservices/organization-service') {
                                    sh 'mvn clean compile'
                                }
                            }
                        }
                        stage('Project Service') {
                            steps {
                                dir('microservices/project-service') {
                                    sh 'mvn clean compile'
                                }
                            }
                        }
                        stage('API Gateway') {
                            steps {
                                dir('api-gateway') {
                                    sh 'mvn clean compile'
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
                    steps {
                        dir('server-discovery') {
                            sh 'mvn test'
                        }
                        dir('config-server') {
                            sh 'mvn test'
                        }
                    }
                    post {
                        always {
                            junit '**/target/surefire-reports/TEST-*.xml'
                        }
                    }
                }
                
                stage('Test Microservices') {
                    parallel {
                        stage('Auth Service') {
                            steps {
                                dir('microservices/auth-service') {
                                    sh 'mvn test'
                                }
                            }
                            post {
                                always {
                                    junit 'microservices/auth-service/target/surefire-reports/TEST-*.xml'
                                }
                            }
                        }
                        stage('IAM Service') {
                            steps {
                                dir('microservices/iam-service') {
                                    sh 'mvn test'
                                }
                            }
                            post {
                                always {
                                    junit 'microservices/iam-service/target/surefire-reports/TEST-*.xml'
                                }
                            }
                        }
                        stage('Organization Service') {
                            steps {
                                dir('microservices/organization-service') {
                                    sh 'mvn test'
                                }
                            }
                            post {
                                always {
                                    junit 'microservices/organization-service/target/surefire-reports/TEST-*.xml'
                                }
                            }
                        }
                        stage('Project Service') {
                            steps {
                                dir('microservices/project-service') {
                                    sh 'mvn test'
                                }
                            }
                            post {
                                always {
                                    junit 'microservices/project-service/target/surefire-reports/TEST-*.xml'
                                }
                            }
                        }
                        stage('API Gateway') {
                            steps {
                                dir('api-gateway') {
                                    sh 'mvn test'
                                }
                            }
                            post {
                                always {
                                    junit 'api-gateway/target/surefire-reports/TEST-*.xml'
                                }
                            }
                        }
                    }
                }
                
                stage('Package Applications') {
                    steps {
                        dir('server-discovery') {
                            sh 'mvn package -DskipTests'
                        }
                        dir('config-server') {
                            sh 'mvn package -DskipTests'
                        }
                        dir('microservices/auth-service') {
                            sh 'mvn package -DskipTests'
                        }
                        dir('microservices/iam-service') {
                            sh 'mvn package -DskipTests'
                        }
                        dir('microservices/organization-service') {
                            sh 'mvn package -DskipTests'
                        }
                        dir('microservices/project-service') {
                            sh 'mvn package -DskipTests'
                        }
                        dir('api-gateway') {
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
                
                stage('Archive Artifacts') {
                    steps {
                        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Build and Test successful!'
        }
        failure {
            echo 'Build or Test failed!'
        }
    }
}