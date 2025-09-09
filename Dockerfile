# ---------- Build ----------
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /src

# 1) Installer le module Domain dans le .m2 du conteneur
COPY StudyBuddyDomain/pom.xml StudyBuddyDomain/pom.xml
RUN mvn -q -DskipTests -f StudyBuddyDomain/pom.xml dependency:go-offline
COPY StudyBuddyDomain/src/ StudyBuddyDomain/src/
RUN mvn -q -DskipTests -f StudyBuddyDomain/pom.xml install

# 2) Builder le Service (qui dépend de Domain)
COPY StudyBuddyService/pom.xml StudyBuddyService/pom.xml
RUN mvn -q -DskipTests -f StudyBuddyService/pom.xml dependency:go-offline
COPY StudyBuddyService/src/ StudyBuddyService/src/
RUN mvn -q -DskipTests -U -f StudyBuddyService/pom.xml package

# ---------- Runtime ----------
FROM payara/server-full:6.2025.8-jdk17

# Déployer le WAR
COPY --from=build /src/StudyBuddyService/target/*.war /opt/payara/deployments/service.war

# Driver MySQL dans le domaine Payara 6 (chemin correct)
COPY --from=build /root/.m2/repository/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar \
     /opt/payara/appserver/glassfish/domains/domain1/lib/

# Pre-boot
COPY StudyBuddyService/docker/pre-boot-commands.asadmin /opt/payara/config/pre-boot-commands.asadmin

# Post-boot commands
COPY StudyBuddyService/docker/post-boot-commands.asadmin /opt/payara/config/post-boot-commands.asadmin
