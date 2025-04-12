import tkinter as tk
import threading
import sys
import winreg

from client.libs.client import Client
from client.libs.system import System

APP_NAME = "GCA"
ICON_PATH = "resources/icono.ico"

class GCA:
    def __init__(self):
        # Contenedor
        self.root = tk.Tk()
        self.root.title(APP_NAME)
        self.root.geometry("500x250")
        self.root.protocol("WM_DELETE_WINDOW", self.hide_window)

        tk.Label(self.root, text="Aplicación corriendo en segundo plano.").pack(pady=20)
        tk.Button(self.root, text="Salir completamente", command=self.stop).pack()

        label = tk.Label(self.root, text="Introduzca sus credenciales y pulse realizar registro")
        label.pack(pady=10)

        email_label = tk.Label(self.root, text="Email *")
        email_label.pack()
        email_entry = tk.Entry(self.root)
        email_entry.pack()

        password_label = tk.Label(self.root, text="Password *")
        password_label.pack()
        password_entry = tk.Entry(self.root, show="*")
        password_entry.pack()

        self.client = Client(email_entry, password_entry)

        boton = tk.Button(self.root, text="Registrar equipo", command=self.client.generate_fingerprint)
        boton.pack(pady=10)

        self.bandeja = System(self)
        threading.Thread(target=self.bandeja.init, daemon=True).start()
        threading.Thread(target=self.client.test_periodically, daemon=True).start()

        self.add_to_startup()

    def hide_window(self):
        self.root.withdraw()  # Oculta sin cerrar

    def stop(self):
        self.bandeja.exit()

    def add_to_startup(self):
        try:
            exe_path = sys.executable
            clave = winreg.OpenKey(winreg.HKEY_CURRENT_USER,
                                   r"Software\Microsoft\Windows\CurrentVersion\Run",
                                   0, winreg.KEY_SET_VALUE)
            winreg.SetValueEx(clave, APP_NAME, 0, winreg.REG_SZ, exe_path)
            winreg.CloseKey(clave)
        except Exception as e:
            print("Error al agregar al inicio:", e)

    def exec(self):
        self.root.mainloop()

if __name__ == "__main__":
    app = GCA()
    app.exec()
