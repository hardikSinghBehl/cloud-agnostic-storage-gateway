FROM openjdk:17
EXPOSE 8080
COPY target/cloud-agnostic-storage-gateway-1.0.0.RELEASE.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
LABEL maintainer="Hardik Singh Behl" email="hardik.behl7444@gmail.com"
