#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        gradle "Gradle7"
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

        stage('SonarQube') {
            steps {
                withCredentials([
                        usernamePassword(credentialsId: 'ci-user', usernameVariable: 'repository_username', passwordVariable: 'repository_password'),
                        string(variable: 'sonar_token', credentialsId: 'ci-user-sonarqube-token')]) {
                    configFileProvider([configFile(fileId: 'gradle-settings', targetLocation: '.gradle/init.d/settings.gradle')]) {
                        script {
                            sh "gradle sonar"
                        }
                    }
                }
            }
        }
    }
}
