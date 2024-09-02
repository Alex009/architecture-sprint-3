FROM maven:3.8.6-eclipse-temurin-17-alpine@sha256:e88c1a981319789d0c00cd508af67a9c46524f177ecc66ca37c107d4c371d23b AS builder
WORKDIR /build

# dependencies cache
COPY pom.xml .
RUN mvn dependency:go-offline

# app build
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17.0.5_8-jre-focal@sha256:e0835d0b51dfc2bd8f3b97ff7d819d1e7d09ab906309ae8d2b3e9395a6c60a2f
WORKDIR /opt/app
COPY --from=builder /build/target/smart-home-device-heating-rest-1.0-SNAPSHOT.jar app.jar
RUN groupadd --system javauser && \
    useradd --system --no-create-home --shell /usr/sbin/nologin --gid javauser javauser && \
    chown -R javauser:javauser .
USER javauser
ENTRYPOINT ["java", "-jar", "app.jar"]
