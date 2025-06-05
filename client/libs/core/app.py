import logging
import sys
import tkinter as tk
import threading
from pathlib import Path
from tkinter import ttk

from client.libs.client import Client
from client.libs.core.log_manager import LogManager
from client.libs.core.startup_manager import StartupManager
from client.libs.core.ui_manager import UIManager
from client.libs.pages.loginpage import LoginPage
from client.libs.core.system import System
from client.libs.pages.userpage import UserPage

APP_NAME = "GCA"
# Ruta dinámica para soportar PyInstaller
if getattr(sys, 'frozen', False):
    BASE_DIR = Path(sys._MEIPASS)  # ruta temporal usada por PyInstaller
else:
    BASE_DIR = Path(__file__).resolve().parent.parent.parent

ICON_PATH = BASE_DIR / "resources" / "icono.ico"

class App:
    def __init__(self) -> None:
        self.root = tk.Tk()
        self.root.resizable(False, False)
        self.root.title(APP_NAME)

        ttk.Style().theme_use('xpnative')  # Tema de la aplicación

        self.set_app_icon()
        self.ui = UIManager(self.root)
        self.logger = LogManager(self.ui.get_log_widget())
        self.startup = StartupManager()

        ttk.Button(self.root, text="Cerrar APP", command=self.stop).pack()

        self.client = Client(self)

        self.frames = {}
        self.set_app_pages()

        self.show_frame("LoginPage")  # Carga esta página al iniciar la APP
        self.bandeja = System(self, icon_path=ICON_PATH, app_name=APP_NAME)
        self.init_threads()
        self.startup.register_task()
        self.clean_logs_periodically()

    def set_app_icon(self):
        """Carga el icono de la aplicación"""
        try:
            self.root.iconbitmap(ICON_PATH)
        except Exception as e:
            logging.warning(f"No se pudo cargar el icono: {e}")

    def set_app_pages(self) -> None:
        """Crea los frames de todas las páginas de la aplicación"""
        container = self.ui.get_container()
        for PageClass in (LoginPage, UserPage):
            frame = PageClass(parent=container, controller=self)
            self.frames[PageClass.__name__] = frame
            frame.grid(row=0, column=0, sticky="nsew")

        self.client.set_frames(self.frames)

    def show_frame(self, name) -> None:
        """Muestra un frame en la interfaz gráfica"""
        frame = self.frames[name]
        frame.tkraise()

    def init_threads(self) -> None:
        """ Inicia dos hilos, uno para la bandeja de mensajes y otro para buscar la configuración """
        threading.Thread(target=self.bandeja.init, daemon=True).start()
        threading.Thread(target=self.client.search_for_config, daemon=True).start()

    def clean_logs_periodically(self) -> None:
        self.logger.clean_logs(self.ui.get_log_widget())
        self.root.after(30000, self.clean_logs_periodically)

    def stop(self) -> None:
        """Cierra la aplicación"""
        if hasattr(self, 'bandeja'):
            self.bandeja.exit()

    def exec(self) -> None:
        """Inicia la aplicación"""
        self.root.mainloop()