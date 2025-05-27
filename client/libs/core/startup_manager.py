import logging
import subprocess
import sys

class StartupManager:
    def __init__(self):
        self.exe_path = sys.executable
        self.logger = logging.getLogger()

    def register_task(self) -> None:
        """ Crea una tarea programada para ejecutar el programa al iniciar el equipo """
        exe_path = sys.executable
        try:
            subprocess.run([
                'schtasks', '/Create',
                '/TN', 'GCA',
                '/TR', f'"{exe_path}"',
                '/SC', 'ONLOGON',
                '/RL', 'HIGHEST',
                '/F'
            ], check=True)
        except subprocess.CalledProcessError as e:
           self.logger.error(f"Error al crear la tarea programada: {e}")