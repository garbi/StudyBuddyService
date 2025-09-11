FROM payara/server-full:6.2025.8-jdk17

# (optionnel) bake tes scripts asadmin dans l'image au lieu de les monter
#COPY docker/pre-boot-commands.asadmin /opt/payara/config/pre-boot-commands.asadmin
#COPY docker/post-boot-commands.asadmin /opt/payara/config/post-boot-commands.asadmin

# Driver MySQL dans le domaine Payara 6 (chemin correct)
#COPY /root/.m2/repository/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar /opt/payara/appserver/glassfish/domains/domain1/lib/mysql-connector-j.jar
# Wrapper d'entrée : exporte le SDK puis lance Payara
COPY --chmod=0755 docker/entrypoint.sh /entrypoint.sh

#ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar /opt/payara/appserver/glassfish/domains/domain1/lib/mysql-connector-j.jar

ENTRYPOINT ["/entrypoint.sh"]
