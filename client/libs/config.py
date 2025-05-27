import json
import os
import sys

class Config:
    _instance = None
    def __new__(cls, config_filename='config.json'):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._load_config(config_filename)
        return cls._instance

    def _load_config(self, config_filename) -> None:
        """Carga la configuración desde un archivo JSON"""
        if getattr(sys, 'frozen', False):
            root_path = sys._MEIPASS
        else:
            # En modo desarrollo (fuentes), asumimos ruta relativa a /client
            project_root = os.path.dirname(os.path.abspath(os.path.join(__file__, '..', '..')))
            root_path = os.path.join(project_root, 'client')

        config_path = os.path.join(root_path, 'resources',config_filename)

        if not os.path.exists(config_path):
            raise FileNotFoundError(f"No se encontró el archivo de configuración: {config_path}")

        with open(config_path, 'r', encoding='utf-8') as f:
            self.config = json.load(f)

        if 'base_url' not in self.config:
            raise ValueError("El archivo de configuración debe contener la clave 'base_url'.")

    def get_base_url(self) -> str:
        return self.config.get('base_url')

    def get_backup_path(self) -> str:
        return self.config.get('backup_path')

    def get_whitelist(self) -> list:
        return self.config.get('whitelist')

    def get_rollback(self) -> str:
        return self.config.get('rollback_when_command_error', True)

    def get_retrieval_time(self) -> int:
        return self.config.get('get_config_retrieval_time_in_seconds', 60)
