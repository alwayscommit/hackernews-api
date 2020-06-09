FROM maven:3.6.3-openjdk-8 
EXPOSE 8080  
ENTRYPOINT ["java","-Dspring.data.mongodb.uri=mongodb://db:27017/","-jar","/hacker-news/hackernews-api-service-0.0.1-SNAPSHOT.jar"]  