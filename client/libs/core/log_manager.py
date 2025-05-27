import logging
from client.libs.texthandler import TextHandler

class LogManager:
    def __init__(self, log_widget) -> None:
        self.logger = logging.getLogger()
        handler = TextHandler(log_widget)
        handler.setFormatter(logging.Formatter('%(asctime)s - %(message)s'))
        self.logger.addHandler(handler)
        self.logger.setLevel(logging.INFO)

    def clean_logs(self, log_widget, max_lines=100) -> None:
        """Limpia los logs del widget de texto"""
        try:
            current_lines = int(log_widget.index('end-1c').split('.')[0])
            if current_lines > max_lines:
                log_widget.delete("1.0", f"{current_lines - max_lines}.0")
        except Exception as e:
            self.logger.error(f"Error al limpiar logs {e}")