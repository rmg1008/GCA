import logging
import os
import winreg
from typing import Any

GCA_SOFTWARE_KEY = r"SOFTWARE\GCA"

class Registry:
    def __init__(self):
        self.logger = logging.getLogger()

    def get_machine_guid(self) -> Any | None:
        """Obtiene el GUID de la máquina"""
        try:
            clave = winreg.OpenKey(winreg.HKEY_LOCAL_MACHINE,
                                   r"SOFTWARE\Microsoft\Cryptography")
            guid, _ = winreg.QueryValueEx(clave, "MachineGuid")
            winreg.CloseKey(clave)
            return guid
        except Exception as e:
            self.logger.error(f"Error al obtener guid: {e}")
            return None

    def get_registry_value(self, nombre) -> Any:
        """Obtiene el valor de un registro"""
        try:
            clave = winreg.OpenKey(winreg.HKEY_LOCAL_MACHINE, r"%s" % GCA_SOFTWARE_KEY, 0, winreg.KEY_READ)
            value, _ = winreg.QueryValueEx(clave, nombre)
            winreg.CloseKey(clave)
            self.logger.debug(f"Se ha obtenido el registro con nombre: {nombre} y valor: {value}")
            return value
        except FileNotFoundError:
            self.logger.debug(f"No se encontró el registro con el nombre: {nombre}")
            return False
        except Exception as e:
            self.logger.error(f"Error al obtener el registro: {e}")
            return False

    def save_registry(self, nombre, valor) -> bool:
        """Guarda una clave / valor en el registro local"""
        try:
            clave = winreg.CreateKey(winreg.HKEY_LOCAL_MACHINE, r"%s" % GCA_SOFTWARE_KEY)
            if isinstance(valor, int):
                tipo = winreg.REG_DWORD
            else:
                tipo = winreg.REG_SZ
                valor = str(valor)

            winreg.SetValueEx(clave, nombre, 0, tipo, valor)
            winreg.CloseKey(clave)
            return True
        except Exception as e:
            self.logger.error(f"Error al guardar el registro {nombre} con clave {valor}: {e}")
            return False

    def delete_registry_key(self, nombre) -> bool:
        """Elimina una clave del registro local"""
        try:
            if self.get_registry_value(nombre) is False:
                return False
            clave = winreg.OpenKey(winreg.HKEY_LOCAL_MACHINE, r"%s" % GCA_SOFTWARE_KEY, 0, winreg.KEY_SET_VALUE)
            winreg.DeleteValue(clave, nombre)
            winreg.CloseKey(clave)
            self.logger.info(f"Clave de registro eliminada: {nombre}")
            return True
        except FileNotFoundError:
            self.logger.warning(f"La clave '{nombre}' no existe en el registro.")
            return False
        except Exception as e:
            self.logger.error(f"Error al eliminar clave del registro: {e}")
            return False

    def get_machine_name(self) -> str:
        """Obtiene el nombre de la máquina"""
        return os.environ.get("COMPUTERNAME", "UNKNOWN")