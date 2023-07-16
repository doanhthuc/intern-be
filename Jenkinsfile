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
        stage("Deploy") {
            when {
                anyOf {
                    branch "develop"
                }
            }
            steps {
                sshagent(['da-nang-intership-ci-ssh']) {
                    sh 'scp -o StrictHostKeyChecking=no server-app/build/libs/server-app-0.0.1-SNAPSHOT.jar easyquizy@easy-quizy.mgm-edv.de:/home/easyquizy/app/'
                    sh 'ssh -o StrictHostKeyChecking=no easyquizy@easy-quizy.mgm-edv.de ./app/run_server.sh'
                }
            }
        }
    }
}