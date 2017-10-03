FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/manager.jar /manager/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/manager/app.jar"]
