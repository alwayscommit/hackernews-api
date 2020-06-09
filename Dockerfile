FROM maven:3.6.3-openjdk-8 AS build  
COPY src /hacker-news/src  
COPY pom.xml /hacker-news 
RUN mvn -f /hacker-news/pom.xml clean package

FROM openjdk:8
COPY --from=build /hacker-news/target/hackernews-api-service-0.0.1-SNAPSHOT.jar /hacker-news/hackernews-api-service-0.0.1-SNAPSHOT.jar  
EXPOSE 8080  
ENTRYPOINT ["java","-jar","/hacker-news/hackernews-api-service-0.0.1-SNAPSHOT.jar"]  