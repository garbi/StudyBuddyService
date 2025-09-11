#!/bin/sh
set -e

# Si on veut exposer le SDK Payara vers l’hôte (pour IntelliJ "Application server")
if [ -n "${EXPORT_SDK_DIR}" ]; then
  mkdir -p "${EXPORT_SDK_DIR}"
  if [ ! -d "${EXPORT_SDK_DIR}/appserver" ]; then
    echo "[payara] Exporting Payara appserver to ${EXPORT_SDK_DIR} ..."
    cp -a /opt/payara/appserver "${EXPORT_SDK_DIR}/"
    chmod -R a+rX "${EXPORT_SDK_DIR}/appserver" || true
  else
    echo "[payara] SDK already exported at ${EXPORT_SDK_DIR}/appserver"
  fi
fi

# Démarre Payara
exec /opt/payara/scripts/startInForeground.sh