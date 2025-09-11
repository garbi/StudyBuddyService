FROM payara/server-full:6.2025.8-jdk17

# (optionnel) bake tes scripts asadmin dans l'image au lieu de les monter
COPY docker/pre-boot-commands.asadmin /opt/payara/config/pre-boot-commands.asadmin
COPY docker/post-boot-commands.asadmin /opt/payara/config/post-boot-commands.asadmin

# Wrapper d'entrée : exporte le SDK puis lance Payara
COPY --chmod=0755 docker/entrypoint.sh /entrypoint.sh
# Debug JDWP propre (la JVM le lit elle-même)
#ENV JAVA_TOOL_OPTIONS=

ENTRYPOINT ["/entrypoint.sh"]
