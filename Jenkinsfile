pipeline {
    agent any
    tools {
        gradle "Gradle8"
        jdk "OpenJDK17"
    }
    environment {
        GRADLE_USER_HOME = "${WORKSPACE}/.gradle"
        }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh 'gradle clean build'
            }
        }
    }
}