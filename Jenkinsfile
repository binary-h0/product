pipeline {
    agent any

    environment {
        REGISTRY = 'binaryho.azurecr.io'
        IMAGE_NAME = 'product'
        AKS_CLUSTER = 'binaryho-aks'
        RESOURCE_GROUP = 'binaryho-rsrcgrp'
        AKS_NAMESPACE = 'default'
        AZURE_CREDENTIALS_ID = 'Azure-Cred'
        TENANT_ID = 'ecd8d459-73d3-48f6-bf87-629631dc2d71' // Service Principal 등록 후 생성된 ID
    }
 
    stages {
        stage('Clone Repository') {
            steps {
                checkout scm
            }
        }
        
        stage('Maven Build') {
            steps {
                withMaven(maven: 'Maven') {
                    sh 'mvn package -DskipTests'
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    image = docker.build("${REGISTRY}/${IMAGE_NAME}:v${env.BUILD_NUMBER}")
                }
            }
        }
        
        stage('Azure Login') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: env.AZURE_CREDENTIALS_ID, usernameVariable: 'AZURE_CLIENT_ID', passwordVariable: 'AZURE_CLIENT_SECRET')]) {
                        sh 'az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET --tenant ${TENANT_ID}'
                    }
                }
            }
        }
        
        stage('Push to ACR') {
            steps {
                script {
                    sh "az acr login --name ${REGISTRY.split('\\.')[0]}"
                    sh "docker push ${REGISTRY}/${IMAGE_NAME}:v${env.BUILD_NUMBER}"
                }
            }
        }
        
        stage('CleanUp Images') {
            steps {
                sh """
                docker rmi ${REGISTRY}/${IMAGE_NAME}:v$BUILD_NUMBER
                """
            }
        }
        
        stage('Update Helm Charts') {
            steps {
                script {
                    // Update values.yaml image tag
                    sh "sed -i 's/tag: \"latest\"/tag: \"v${env.BUILD_NUMBER}\"/g' helm/values.yaml"

                    // Commit and push changes to GitHub
                    withCredentials([usernamePassword(credentialsId: 'Github-Cred', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        sh """
                            git config --global user.email "jenkins@jenkins.com"
                            git config --global user.name "Jenkins"
                            git config --global credential.helper store
                            echo "https://$GIT_USERNAME:$GIT_PASSWORD@github.com" > ~/.git-credentials

                            git checkout main || git checkout -b main  # Ensure we're on the main branch
                            git pull origin main  # Update local repo
                            git add helm/values.yaml
                            git commit -m "Update image tag to v${env.BUILD_NUMBER}"
                            git push origin main
                        """
                        sh "rm -f ~/.git-credentials"
                    }
                }
            }
        }
    }
}