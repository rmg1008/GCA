import base64
import os
import subprocess
import time
import winreg
from tkinter import messagebox
import requests


class Client:
    def __init__(self, email_entry, password_entry):
        self.email_entry = email_entry
        self.password_entry = password_entry

    def export_config(self):
        try:
            subprocess.check_output("netsh advfirewall export tempfirewall.wfw", shell=True, stderr=subprocess.STDOUT)
            with open("tempfirewall.wfw", "rb") as f:
                contenido = f.read()
            configuracion = base64.b64encode(contenido)
            os.remove("tempfirewall.wfw")
            return configuracion
        except subprocess.CalledProcessError as e:
            raise RuntimeError("command '{}' return with error (code {}): {}".format(e.cmd, e.returncode, e.output))

    def import_config(self, configuracion):
        try:
            with open('tempfirewall.wfw', 'ab') as f:
                f.seek(0)
                f.write(configuracion)
            subprocess.check_output("netsh advfirewall import tempfirewall.wfw", shell=True, stderr=subprocess.STDOUT)
            os.remove("tempfirewall.wfw")
        except subprocess.CalledProcessError as e:
            raise RuntimeError("command '{}' return with error (code {}): {}".format(e.cmd, e.returncode, e.output))

    def get_machine_guid(self):
        try:
            clave = winreg.OpenKey(winreg.HKEY_LOCAL_MACHINE,
                                   r"SOFTWARE\Microsoft\Cryptography")
            guid, _ = winreg.QueryValueEx(clave, "MachineGuid")
            winreg.CloseKey(clave)
            return guid
        except Exception as e:
            return None

    def save_registry(self, nombre, valor):
        try:
            clave = winreg.CreateKey(winreg.HKEY_LOCAL_MACHINE,
                                     r"SOFTWARE\GCA")
            winreg.SetValueEx(clave, nombre, 0, winreg.REG_SZ, valor)
            winreg.CloseKey(clave)
            return True
        except Exception as e:
            return False

    def get_machine_name(self):
        return os.environ.get("COMPUTERNAME", "UNKNOWN")

    def login(self):
        userid = self.email_entry.get()
        password = self.password_entry.get()
        response = requests.post("http://192.168.1.132:8080/login", json={'email': userid, 'password': password})
        if response and response.status_code == 200:
            messagebox.showinfo("Se ha iniciado sesión", "Bienvenido, Admin!")
            return response.text
        else:
            return False

    def is_form_valid(self):
        if self.email_entry.get() == "" or self.password_entry.get() == "":
            messagebox.showerror("Introduzca credenciales", "Debe rellenar email y contraseña")
            return False
        else:
            return True

    def generate_fingerprint(self):
        try:
            if self.is_form_valid():
                token = self.login()
                if token:
                    configuracion = self.export_config()
                    if configuracion:
                        print(configuracion)
                        messagebox.showinfo("Éxito", "Configuración obtenida")
                    else:
                        messagebox.showerror("Error", "No se pudo obtener la configuración")
                    guid = self.get_machine_guid()
                    pc = self.get_machine_name()
                    if guid:
                        huella = f"{guid}/{pc}"
                        if self.save_registry("huellaDigital", huella):
                            endpoint = "http://192.168.1.132:8080/registerDevice"
                            data = {
                                "fingerprint": huella,
                                "name": pc,
                                "group": 1,
                                "os": 1
                            }
                            headers = {"Authorization": "Bearer {}".format(token)}

                            requests.post(endpoint, json=data, headers=headers)
                            messagebox.showinfo("Éxito", f"Huella guardada:\n{huella}")
                        else:
                            messagebox.showerror("Error",
                                                 "No se pudo guardar en el registro.\nEjecuta como administrador.")
                    else:
                        messagebox.showerror("Error", "No se pudo obtener el MachineGuid.")

                    self.import_config(base64.b64decode(configuracion))
        except Exception as e:
            messagebox.showerror("Error", repr(e))

    def test_periodically(self):
        while True:
            messagebox.showinfo("Test", "This is just a test...")
            time.sleep(60)
