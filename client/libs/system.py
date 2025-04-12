import sys

from PIL import Image
from pystray import Menu, MenuItem, Icon


class System:
    def __init__(self, app):
        self.app = app
        self.icon = None

    def show_window(self):
        self.app.root.after(0, self.app.root.deiconify)

    def exit(self):
        if self.icon:
            self.icon.stop()
        self.app.root.after(0, self.app.root.destroy)
        sys.exit()

    def init(self):
        from client.main import ICON_PATH, APP_NAME
        image = Image.open(ICON_PATH)
        menu = Menu(
            MenuItem("Mostrar", self.show_window),
            MenuItem("Salir", self.exit)
        )
        self.icon = Icon(APP_NAME, image, APP_NAME, menu)
        self.icon.run()
