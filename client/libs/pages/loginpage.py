import tkinter as tk
from tkinter import ttk, font


class LoginPage(tk.Frame):
    def __init__(self, parent, controller):
        super().__init__(parent)
        self.controller = controller

        self.grid_rowconfigure(0, weight=1)
        self.grid_columnconfigure(0, weight=1)

        self.center_frame = tk.Frame(self)
        self.center_frame.grid(row=0, column=0, sticky="nsew", padx=10, pady=10)

        self.center_frame.grid_rowconfigure((0, 1, 2, 3), weight=1)
        self.center_frame.grid_columnconfigure((0, 1), weight=1)

        bold_font = font.Font(family="Helvetica", size=14, weight="bold")

        ttk.Label(self.center_frame, text="Iniciar sesión", font=bold_font).grid(row=0, column=0, columnspan=2)

        ttk.Label(self.center_frame, text="Email *").grid(row=1, column=0, columnspan=2,sticky="n", pady=1)
        self.email_entry = tk.Entry(self.center_frame)
        self.email_entry.grid(row=1,  column=0, columnspan=2)

        ttk.Label(self.center_frame, text="Password *").grid(row=2, column=0, columnspan=2,sticky="n", pady=1)
        self.password_entry = tk.Entry(self.center_frame, show="*")
        self.password_entry.grid(row=2,  column=0, columnspan=2)

        ttk.Button(self.center_frame, text="Iniciar Sesión", command=self.login).grid(row=3, column=0, columnspan=2)

    def login(self):
        self.controller.client.set_email(self.email_entry.get())
        self.controller.client.set_password(self.password_entry.get())
        if self.controller.client.login():
            self.controller.show_frame("UserPage")
