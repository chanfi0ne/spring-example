FROM maven:3.5-jdk-8-onbuild
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/usr/src/app/target/spring-sample-0.1.0.jar"]