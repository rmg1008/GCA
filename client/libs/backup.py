import subprocess
import os
import logging
from client.libs.config import Config

class Backup:
    def __init__(self):
        config = Config()
        self.backup_dir = config.get_backup_path()
        self.whitelist = config.get_whitelist()
        os.makedirs(self.backup_dir, exist_ok=True)

    def export_firewall(self) -> str | None:
        """Exporta la configuración del firewall a un archivo .wfw"""
        try:
            path = os.path.join(self.backup_dir, "firewall.wfw")
            if os.path.exists(path):
                os.remove(path)
            subprocess.check_output(f"netsh advfirewall export {path}", shell=True, stderr=subprocess.STDOUT)
            logging.info(f"Firewall exportado a: {path}")
            return path
        except subprocess.CalledProcessError as e:
            logging.debug(f"Error exportando firewall: {e.output.decode()}")
            return None

    def import_firewall_from_file(self) -> bool:
        """Restaura la configuración del firewall desde un archivo .wfw"""
        try:
            path = os.path.join(self.backup_dir, "firewall.wfw")
            if not os.path.isfile(path):
                logging.warning("No se encontró archivo firewall.wfw.")
                return False
            subprocess.check_output(f"netsh advfirewall import {path}", shell=True, stderr=subprocess.STDOUT)
            logging.info("Firewall restaurado desde archivo.")
            return True
        except subprocess.CalledProcessError as e:
            logging.debug(f"Error restaurando firewall: {e.output.decode()}")
            return False

    def export_network_dump(self) -> str | None:
        """Exporta la configuración de red a un archivo .txt"""
        try:
            path = os.path.join(self.backup_dir, "network_dump.txt")
            with open(path, "w", encoding="utf-8") as f:
                result = subprocess.run("netsh dump", shell=True, text=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                f.write(result.stdout)
            logging.info(f"Dump de red exportado a: {path}")
            return path
        except Exception as e:
            logging.debug(f"Error exportando configuración de red: {e}")
            return None

    def restore_network_dump(self) -> bool:
        """Restaura la configuración de red desde un archivo .txt"""
        try:
            path = os.path.join(self.backup_dir, "network_dump.txt")
            if not os.path.isfile(path):
                logging.warning("No se encontró el archivo de dump de red.")
                return False
            subprocess.check_output(f"netsh -f {path}", shell=True, stderr=subprocess.STDOUT)
            logging.info("Configuración de red restaurada desde dump.")
            return True
        except subprocess.CalledProcessError as e:
            logging.debug(f"Error restaurando configuración de red: {e.output.decode()}")
            return False

    def restore_network_interfaces(self) -> bool:
        """Restaura las interfaces de red que están desactivadas para permitir conexiones"""
        try:
            result = subprocess.run(
                'netsh interface show interface',
                shell=True, capture_output=True, text=True
            )
            if result.returncode != 0:
                logging.error("No se pudo obtener la lista de interfaces.")
                return False

            lines = result.stdout.splitlines()
            for line in lines:
                if "Disabled" in line:
                    parts = line.split()
                    if len(parts) >= 4:
                        interface_name = " ".join(parts[3:])
                        logging.info(f"Rehabilitando interfaz: {interface_name}")
                        subprocess.run(
                            f'netsh interface set interface name="{interface_name}" admin=enable',
                            shell=True
                        )
            logging.info("Revisión de interfaces de red completada.")
            return True
        except Exception as e:
            logging.debug(f"Error al restaurar interfaces de red: {e}")
            return False

    def export_all(self) -> bool:
        """ Maneja la exportación de todas las configuraciones"""
        logging.info("Exportando configuración de red y firewall...")
        ok_firewall = self.export_firewall() is not None
        ok_network = self.export_network_dump() is not None
        if ok_firewall and ok_network:
            return True
        else:
            return False

    def configurar_regla_firewall(self, nombre_regla: str, ip: str, port: int) -> bool:
        """Configura una regla de firewall"""
        try:

            if not nombre_regla or not ip:
                logging.error("No se especificó nombre o IP.")
                return False

            subprocess.run([
                "netsh", "advfirewall", "firewall", "delete", "rule",
                f"name={nombre_regla}"
            ], capture_output=True, text=True)

            comando = [
                "netsh", "advfirewall", "firewall", "add", "rule",
                f"name={nombre_regla}",
                "dir=out", "action=allow",
                "protocol=TCP",
                f"remoteip={ip}",
                "profile=any",
                "enable=yes"
            ]

            if port is not None:
                comando.append(f"remoteport={port}")

            subprocess.run(comando, check=True)

            logging.debug(f"Regla '{nombre_regla}' actualizada para {ip}")
            return True

        except subprocess.CalledProcessError as e:
            logging.error(f"Error al configurar la regla: {e}")
            return False

    def allow_withelist(self) -> bool:
        """ Se encarga de permitir conexiones desde las IPs permitidas"""
        valid = True
        logging.info("Permitir conexiones desde la lista de IPs permitidas...")
        for target in self.whitelist:
            logging.debug(f"Configurando regla para {target.get('name')}")
            result = self.configurar_regla_firewall(target.get('name'), target.get('host'),  target.get('port'))
            valid = valid and result
        return valid

    def restore_all(self) -> bool:
        """ Maneja la restauración de todas las configuraciones"""
        logging.info("Restaurando configuración de red y firewall...")
        ok_network = self.restore_network_dump()
        ok_firewall = self.import_firewall_from_file()
        ok_interfaces = self.restore_network_interfaces()
        ok_whitelist = self.allow_withelist()
        if ok_network and ok_firewall and ok_interfaces and ok_whitelist:
            return True
        else:
            return False