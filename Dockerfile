FROM amazoncorretto:11
EXPOSE 8080
ADD target/spring-boot-coursework.jar spring-boot-coursework.jar
ENTRYPOINT ["java","-jar","/spring-boot-coursework.jar"]