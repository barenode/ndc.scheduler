def version = ''
def app

pipeline {
    
    agent any    

    stages {
        stage('Maven Build') {
            steps {
                bat 'mvn -B -DskipTests clean package' 
                script {
                    version = readMavenPom().getVersion()
                }
                echo("VERSION=${version}")
            }
        }
        stage('Docker Image') {
            steps {
                bat 'mvn jib:dockerBuild'                    
            }
        }
        stage('Helm Chart') {
            steps {
                echo 'Helm Chart....'
                bat 'helm lint target/classes/helm/ndc-scheduler'
                bat 'helm package -d target/helm target/classes/helm/ndc-scheduler'
                bat "curl -u helm:helm http://localhost:8081/repository/helm-internal/ --upload-file target/helm/ndc-scheduler-${version}.tgz -v"
            }
        }
        stage('OKD Deployment') {
            steps {
                echo 'Deploy to OKD....'          
                sleep(5)                
                bat 'helm repo update' 
                // sleep(5)         
                // bat 'helm show chart helm-internal/ndc-testcontainer  --devel --version ${version}'
                bat 'helm search repo --devel'     
                catchError {         
                    bat 'helm delete ndc-scheduler'                
                }
                bat "helm upgrade --install --devel ndc-scheduler helm-internal/ndc-scheduler --version ${version}"
            }
        }
    }
}
