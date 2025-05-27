import tkinter as tk
import logging
from tkinter import ttk, font

class UserPage(tk.Frame):
    def __init__(self, parent, controller) -> None:
        super().__init__(parent)
        self.controller = controller

        self.grid_rowconfigure(0, weight=1)
        self.grid_columnconfigure(0, weight=1)

        self.center_frame = tk.Frame(self)
        self.center_frame.grid(row=0, column=0, sticky="nsew")  # Para expandir el frame

        self.center_frame.grid_rowconfigure((0, 1, 2), weight=1)
        self.center_frame.grid_columnconfigure(0, weight=1)

        bold_font = font.Font(family="Helvetica", size=14, weight="bold")
        ttk.Label(self.center_frame, text="Panel de usuario", font=bold_font).grid(row=0, column=0, columnspan=2)

        self.register_button = ttk.Button(self.center_frame, text="Registrar equipo", command=self.signup)
        self.delete_button = ttk.Button(self.center_frame, text="Eliminar equipo", command=self.delete)

        self.logout_button = ttk.Button(self.center_frame, text="Cerrar sesión", command=self.logout)
        self.logout_button.grid(row=2, column=0, pady=10)

    def tkraise(self, *args, **kwargs) -> None:
        """ Muestra registrar / eliminar equipo dependiendo del estado del registro """
        super().tkraise(*args, **kwargs)
        if self.controller.client.registry.get_registry_value("huellaDigital"):
            self.delete_button.grid(row=1, column=0, pady=5)
            self.register_button.grid_forget()
        else:
            self.register_button.grid(row=1, column=0, pady=5)
            self.delete_button.grid_forget()

    def signup(self) -> None:
        """ Registra el equipo """
        if self.controller.client.is_token_expired():
            logging.warning("Token expirado. Redirigiendo a login.")
            self.controller.show_frame("LoginPage")
        else:
            self.controller.client.generate_fingerprint()
            self.tkraise()

    def delete(self) -> None:
        """ Elimina el equipo """
        if self.controller.client.is_token_expired():
            logging.warning("Token expirado. Redirigiendo a login.")
            self.controller.show_frame("LoginPage")
        else:
            self.controller.client.delete_fingerprint()
            self.tkraise()

    def logout(self) -> None:
        """ Cierra sesión """
        self.controller.show_frame("LoginPage")

