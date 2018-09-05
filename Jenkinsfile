pipeline {
  agent none

  environment {
    //REGISTRY_URL      = '998750339583.dkr.ecr.us-east-1.amazonaws.com'
    REGISTRY_URL      = '998750339583.dkr.ecr.us-east-1.amazonaws.com'
    DOCKER_IMAGE_NAME = 'spring-example'
  }

  stages {
    stage('Pre-Build') {
      agent { label 'master' }
      steps {
        script {
            //env.WORKSPACE = pwd()
            env.VERSION = readFile("code/spring-example/version.txt").trim()
        }

        echo "${env.VERSION}"
      }
    }
    stage('Build') {
        agent {
          docker { 
            image 'maven:3.2-jdk-8'
            args '-v /root/.m2:/root/.m2'
          }
        }
        steps {
            sh 'mvn -f code/spring-example/pom.xml -B -DskipTests clean package'
        }
    }
    stage('Test'){
      agent {
        docker { 
          image 'maven:3.2-jdk-8'
          args '-v /root/.m2:/root/.m2'
        }
      }
      steps {
          sh 'mvn -f code/spring-example/pom.xml clean test'
      }
      post {
        always {
            junit '**/target/surefire-reports/*.xml' 
        }
      }
    }
    stage('SonarQube Analysis'){
      agent {
        docker { 
          image 'maven:3.2-jdk-8'
          args '-v /root/.m2:/root/.m2 --network=jenkinspipeline_sonar-network'
        }
      }
      steps {
          sh "mvn -f code/spring-example/pom.xml sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -Dsonar.login=40c0d8eb2bb86fee496ef6ec50e6378ffa126438"
      }
    }
    stage('Container Build'){
      agent {
        docker {
          image 'docker:stable-dind'
        }
      }
      steps{
        sh "echo ${env.BUILD_ID}"
        sh "docker build -t ${env.DOCKER_IMAGE_NAME}:${env.VERSION} --build-arg BUILD_NUMBER=${env.BUILD_ID} code/spring-example"
        
      }
    }
    stage('Container Tag + Push'){
      agent { label 'master' }
      steps {

        withCredentials([usernamePassword(credentialsId: 'amazon-ecr', usernameVariable: 'ID', passwordVariable: 'KEY')]) {

          sh "\$(docker run --env AWS_ACCESS_KEY_ID=$ID --env AWS_SECRET_ACCESS_KEY=$KEY --env AWS_DEFAULT_REGION=us-east-1 garland/aws-cli-docker aws ecr get-login --no-include-email)"

        }

        sh "docker tag ${env.DOCKER_IMAGE_NAME}:${env.VERSION} ${env.REGISTRY_URL}/${env.DOCKER_IMAGE_NAME}:${env.VERSION}"
        sh "docker push ${env.REGISTRY_URL}/${env.DOCKER_IMAGE_NAME}:${env.VERSION}"

      }
    }
    
    stage('Notifications'){
      agent {
        any {
        }
      }
      steps{
        sh "curl -X POST -H 'Content-type: application/json' --data '{\"text\":\"${env.JOB_NAME} Build ${env.BUILD_ID} Completed!\"}' https://hooks.slack.com/services/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"
      }
    }
    
    stage('Patch Build'){

      agent { label 'master' }
      steps {
        sh "kubectl set image deployment/spring-example-deployment java=${env.REGISTRY_URL}/${env.DOCKER_IMAGE_NAME}:${env.VERSION}"
      }

    }

  }
}
