import logging
import tkinter as tk

class TextHandler(logging.Handler):
    def __init__(self, text_widget):
        super().__init__()
        self.text_widget = text_widget

        self.text_widget.tag_config("INFO", foreground="blue")
        self.text_widget.tag_config("WARNING", foreground="orange")
        self.text_widget.tag_config("ERROR", foreground="red")

    def emit(self, record) -> None:
        """Escribe el mensaje en el widget de texto"""
        msg = self.format(record)
        level = record.levelname
        try:
            self.text_widget.after(0, self._append_text, msg, level)
        except RuntimeError as e:
            print("No se pudo escribir el log en la GUI:", e)

    def _append_text(self, msg, level) -> None:
        """Agrega el mensaje al widget de texto"""
        self.text_widget.insert(tk.END, msg + '\n', level)
        self.text_widget.see(tk.END)
