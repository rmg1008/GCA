import subprocess
import base64
import json
import time
import logging
import requests
from typing import Any
from tkinter import messagebox
from client.libs.backup import Backup
from client.libs.config import Config
from client.libs.registry import Registry

class Client:
    def __init__(self, email_entry='', password_entry='') -> None:
        self.token = None
        self.email = email_entry
        self.password = password_entry
        config = Config()
        self.base_url = config.get_base_url()
        self.is_rollback_enabled = config.get_rollback()
        self.retrieval_time = config.get_retrieval_time()
        self.registry = Registry()

    def set_email(self, email) -> None:
        self.email = email

    def set_password(self, password) -> None:
        self.password = password

    def get_token_expiration(self) -> Any | None:
        try:
            if not hasattr(self, 'token'):
                return None

            parts = self.token.split('.')
            if len(parts) != 3:
                return None

            payload = parts[1] + '=' * (-len(parts[1]) % 4)  # padding
            decoded_bytes = base64.urlsafe_b64decode(payload)
            payload_json = json.loads(decoded_bytes)

            return payload_json.get("exp", None)  # epoch timestamp
        except Exception as e:
            logging.error(f"No se pudo extraer la expiración del token: {e}")
            return None

    def is_token_expired(self) -> bool:
        exp = self.get_token_expiration()
        if exp is None:
            return True
        return time.time() > exp

    def is_form_valid(self) -> bool:
        if self.email == "" or self.password == "":
            messagebox.showerror("Introduzca credenciales", "Debe rellenar email y contraseña")
            return False
        else:
            return True

    def login(self) -> bool:
        """Inicia sesión en el servidor"""
        try:
            if self.is_form_valid():
                userid = self.email
                password = self.password
                response = requests.post(self.base_url + "login", json={'email': userid, 'password': password})
                if response and response.status_code == 200:
                    messagebox.showinfo("Se ha iniciado sesión", "Bienvenido!")
                    data = response.json()
                    self.token = data["token"]
                    return True
                else:
                    messagebox.showerror("Error al iniciar sesión", "Usuario o contraseña incorrectos")
                    logging.error("Usuario o contraseña incorrectos.")
                    return False
            else:
                logging.error("Debe rellenar email y contraseña.")
                return False
        except Exception as e:
            logging.error(f"Error al iniciar sesión: {e}")
            return False

    def generate_fingerprint(self) -> None:
        """Genera una huella digital almacenándola en el registro y en el servidor"""
        try:
            if self.token is None:
                messagebox.showerror("Error", "No se ha iniciado sesión")
                return
            guid = self.registry.get_machine_guid()
            pc = self.registry.get_machine_name()
            if guid:
                huella = f"{guid}/{pc}"
                if self.registry.save_registry("huellaDigital", huella):
                    endpoint = self.base_url + "registerDevice"
                    data = {
                        "fingerprint": huella,
                        "name": pc,
                        "group": 1,
                        "os": 1
                    }
                    headers = {"Authorization": "Bearer {}".format(self.token)}
                    response = requests.post(endpoint, json=data, headers=headers)
                    if response.status_code == 200:
                        messagebox.showinfo("Éxito", f"Huella guardada:\n{huella}")
                        Backup().export_all()
                    else:
                        logging.error(f"{response.text}")
                else:
                    messagebox.showerror("Error",
                                         "No se pudo guardar en el registro.\nEjecuta como administrador.")
            else:
                messagebox.showerror("Error", "No se pudo obtener el MachineGuid.")
        except Exception as e:
            messagebox.showerror("Error", repr(e))

    def delete_fingerprint(self) -> None:
        """Elimina la huella digital almacenada en el registro y en el servidor"""
        try:
            if self.token is None:
                messagebox.showerror("Error", "No se ha iniciado sesión")
                return
            if self.on_delete_confirm() is True:
                huella = self.registry.get_registry_value("huellaDigital")
                if self.registry.delete_registry_key("huellaDigital"):
                    self.delete_template_registries()
                    endpoint = self.base_url + "config/device"
                    data = {
                        "fingerprint": huella,
                    }
                    headers = {"Authorization": "Bearer {}".format(self.token)}
                    response = requests.delete(endpoint, json=data, headers=headers)
                    if response.status_code == 200:
                        messagebox.showinfo("Éxito", f"Huella eliminada:\n{huella}")
                        Backup().restore_all()
                    else:
                        logging.error(f"{response.text}")
                else:
                    messagebox.showerror("Error",
                                         "No se pudo eliminar el registro.")
        except Exception as e:
            messagebox.showerror("Error", repr(e))

    def on_delete_confirm(self) -> bool:
        confirmed = messagebox.askyesno("Eliminar registro", "¿Estás seguro de que deseas eliminar el registro?")
        if confirmed:
            return True
        else:
            return False

    def ejecutar_comandos(self, config_string) -> bool:
        """Ejecuta los comandos de configuración"""
        if not config_string.strip():
            logging.info("No hay comandos para ejecutar.")
            return False

        comandos = [cmd.strip() for cmd in config_string.split("&&") if cmd.strip()]

        for i, comando in enumerate(comandos, start=1):
            logging.info(f"Comando {i}: {comando}")
            try:
                resultado = subprocess.run(
                    comando,
                    shell=True,
                    text=True,
                    stdout=subprocess.PIPE,
                    stderr=subprocess.PIPE
                )
                if resultado.returncode == 0:
                    logging.info("El comando se ha ejecutado correctamente.")
                else:
                    logging.error(f"Error al ejecutar el comando: {resultado.stdout.strip()}")
                    return False
            except Exception as e:
                logging.error(f"Error al ejecutar el comando: {e}")
                break
        return True

    def search_for_config(self) -> None:
        """Busca la configuración en el servidor de manera periódica"""
        Backup().restore_all()
        self.delete_template_registries()
        while True:
            huella = self.registry.get_registry_value("huellaDigital")
            if huella:
                config_data = self.get_config(huella)
                if config_data:
                    config_string = config_data.get("config")
                    config_last_update = config_data.get("lastUpdate")
                    config_id = config_data.get("id")
                    if config_string:
                        last_update = self.registry.get_registry_value("lastUpdate")
                        last_template_id = self.registry.get_registry_value("templateID")
                        if ((not last_update or last_update < config_last_update)
                                or (not last_template_id or last_template_id != config_id)):
                                self.apply_commands(config_last_update, config_string, config_id)
                    else:
                        logging.debug("La configuración está vacía.")
                        self.check_if_template_was_unassigned_and_do_rollback()
                else:
                    logging.debug("No hay configuración.")
                    self.check_if_template_was_unassigned_and_do_rollback()

            time.sleep(self.retrieval_time)

    def delete_template_registries(self) -> None:
        """Elimina los registros de la plantilla en el dispositivo"""
        self.registry.delete_registry_key("lastUpdate")
        self.registry.delete_registry_key("templateID")

    def check_if_template_was_unassigned_and_do_rollback(self) -> None:
        """ Si no hay configuración pero hay una huella y lastUpdate, quiere decir que se ha desasignado la plantilla"""
        if self.registry.get_registry_value("lastUpdate"):
            self.delete_template_registries()
            Backup().restore_all()

    def get_config(self, fingerprint) -> Any | None:
        """Obtiene la configuración del servidor"""
        url = self.base_url + "config"
        try:
            response = requests.get(url, params={"huella": fingerprint})
            if response.status_code == 200:
                logging.info(f"Config: {response.json()}")
                return response.json()
            else:
                logging.error(f"{response.text}")
                return None
        except Exception as e:
            logging.error(f"Error al obtener la configuración: {e}")
            return None

    def apply_commands(self, config_last_update, config_string, config_id) -> None:
        """Aplica los comandos de configuración"""
        Backup().restore_all()
        if self.ejecutar_comandos(config_string):
            self.registry.save_registry("lastUpdate", config_last_update)
            self.registry.save_registry("templateID", config_id)
        elif self.is_rollback_enabled: # Si está activo, se hace un rollback de la configuración
            Backup().restore_all()