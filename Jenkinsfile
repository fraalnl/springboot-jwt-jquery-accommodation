pipeline {
    agent any

    tools {
        maven "Maven-3.9.5"
    }

    environment {
        SONAR_HOST_URL = 'http://localhost:9000'  
        SONARQUBE_TOKEN = credentials('accommodation-sonar-token') //configured in Jenkins
        SONAR_PROJECT_KEY = 'accommodation-app' //configured in sonarqube
    }

    stages {       
        stage('Build') {
            steps {
                checkout([$class: 'GitSCM',
                          userRemoteConfigs: [[
                            url: 'https://github.com/fraalnl/springboot-jwt-jquery-accommodation.git',
                          ]],                        
                          branches: [[name: 'main']]])
                bat "mvn clean package -DskipTests"
            }
        }

        stage('Unit Tests') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    echo 'Unit tests completed'
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Integration Tests & Publish Coverage') {
            steps {
                bat 'mvn verify -Dsurefire.useFile=false -Dfailsafe.useFile=false'
            }
            post {
                always {
                    echo 'Integration tests completed'
                    junit '**/target/failsafe-reports/*.xml'
                    recordCoverage(
					    tools: [[parser: 'JACOCO']],
					    id: 'jacoco',
					    name: 'JaCoCo Coverage',
					    sourceCodeRetention: 'EVERY_BUILD'
					) // Publishes JaCoCo merged report using Coverage Plugin, replacing deprecated Jacoco plugin
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                bat """
                mvn sonar:sonar ^
                  -Dsonar.token=%SONARQUBE_TOKEN% ^
                  -Dsonar.host.url=%SONAR_HOST_URL% ^
                  -Dsonar.projectKey=%SONAR_PROJECT_KEY% ^
                  -Dsonar.coverage.jacoco.xmlReportPaths=target/jacoco-report-merged/jacoco.xml ^
                  -Dsonar.java.binaries=target/classes
                """
            }
            post {
                always {
                    echo 'SonarQube analysis completed'
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/target/surefire-reports/**, **/target/failsafe-reports/**, **/target/site/jacoco/**, **/target/*.jar', fingerprint: true
                echo 'Artifacts archived successfully'
            }
        }
    }

    post {
        always {
            echo "Build completed with status: ${currentBuild.result}"
        }
    }
}