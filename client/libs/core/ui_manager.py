import tkinter as tk
from tkinter.scrolledtext import ScrolledText


class UIManager:
    def __init__(self, root: tk.Tk) -> None:
        self.root = root
        self.root.geometry("600x400")
        self.root.protocol("WM_DELETE_WINDOW", self.hide_window)
        self.page_container = tk.Frame(self.root)
        self.page_container.pack(fill="both", expand=True)
        self.page_container.grid_rowconfigure(0, weight=1)
        self.page_container.grid_columnconfigure(0, weight=1)

        self.log_text = ScrolledText(self.root, height=6, state='normal')
        self.log_text.pack(padx=10, pady=5, fill='x')

    def get_log_widget(self) -> ScrolledText:
        return self.log_text

    def hide_window(self) -> None:
        self.root.withdraw()

    def get_container(self) -> tk.Frame:
        return self.page_container