FROM openjdk:8-alpine
RUN apk update && apk add bash

RUN mkdir -p /hacker-news

COPY target/hackernews-api-service-0.0.1-SNAPSHOT.jar /hacker-news/hackernews-api-service-0.0.1-SNAPSHOT.jar  

WORKDIR /hacker-news

ENTRYPOINT ["java","-Dspring.data.mongodb.uri=mongodb://hacker_news_db:27017/hacker_news_db","-jar","/hacker-news/hackernews-api-service-0.0.1-SNAPSHOT.jar"]  