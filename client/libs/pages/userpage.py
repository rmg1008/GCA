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

        self.center_frame.grid_rowconfigure((0, 1, 2, 3), weight=1)
        self.center_frame.grid_columnconfigure((0, 1, 2, 3), weight=1)

        bold_font = font.Font(family="Helvetica", size=14, weight="bold")
        ttk.Label(self.center_frame, text="Panel de usuario", font=bold_font).grid(row=0, column=0, columnspan=4)

        self.subtitle_label = ttk.Label(self.center_frame, text="")
        self.subtitle_label.grid(row=1, column=0, columnspan=4)

        self.info_frame = tk.Frame(
            self.center_frame,
            bg="#f1f1f1",
            bd=1,
            relief="solid",
            padx=10,
            pady=8
        )

        self.device_info_text = tk.Text(
            self.info_frame,
            width=60,
            height=4,
            bg="#f1f1f1",
            relief="flat",
            font=("Helvetica", 11),
            wrap="word",
            borderwidth=0
        )
        self.device_info_text.grid(row=0, column=0, sticky="w")
        self.info_frame.grid_columnconfigure(0, weight=0)
        self.device_info_text.tag_configure("bold", font=("Helvetica", 11, "bold"))
        self.register_button = ttk.Button(self.center_frame, text="Registrar equipo", command=self.signup)
        self.delete_button = ttk.Button(self.center_frame, text="Eliminar equipo", command=self.delete)

        style = ttk.Style()
        style.configure(
            "Logout.TButton",
            foreground="red"
        )

        self.logout_button = ttk.Button(self.center_frame, text="Cerrar sesión", style="Logout.TButton", command=self.logout)
        self.logout_button.grid(row=3, column=2, padx=5)

    def tkraise(self, *args, **kwargs) -> None:
        """ Muestra registrar / eliminar equipo dependiendo del estado del registro """
        super().tkraise(*args, **kwargs)
        if self.controller.client.registry.get_registry_value("huellaDigital"):
            self.delete_button.grid(row=3, column=1, pady=5)
            self.register_button.grid_forget()
            self.subtitle_label.config(text="Cargando configuración...")
        else:
            self.register_button.grid(row=3, column=1)
            self.delete_button.grid_forget()
            self.subtitle_label.config(
                text="Este dispositivo no está operativo\n\nPulse en 'Registrar equipo' para activarlo",
                justify="center",
                anchor="center")

            self.info_frame.grid_forget()

        self.update_device_info(None)

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

    def update_device_info(self, config) -> None:
        """
        Actualiza con los datos del equipo cuando esté registrado.
        """
        registrado = self.controller.client.registry.get_registry_value("huellaDigital")
        if registrado and config:
            self.subtitle_label.config(text="Equipo registrado")
            self.device_info_text.config(state="normal")
            self.device_info_text.delete("1.0", tk.END)

            for clave, valor in config.items():
                self.device_info_text.insert(tk.END, f"{clave}: ", "bold")
                self.device_info_text.insert(tk.END, f"{valor}\n")

            self.device_info_text.config(state="disabled")
            self.info_frame.grid(row=2, column=0, columnspan=4, pady=5)
        else:
            self.info_frame.grid_forget()
            if not registrado:
                self.subtitle_label.config(text="Este dispositivo no está operativo\n\nPulse en 'Registrar equipo' para activarlo",justify="center",
                       anchor="center")

