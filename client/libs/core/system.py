import sys
from PIL import Image
from pystray import Menu, MenuItem, Icon

class System:
    def __init__(self, app, icon_path=None, app_name=None):
        self.icon = None
        self.app = app
        self.icon_path = icon_path
        self.app_name = app_name

    def show_window(self):
        self.app.root.after(0, self.app.root.deiconify)

    def exit(self):
        if self.icon:
            self.icon.stop()
        self.app.root.after(0, self.app.root.destroy)
        sys.exit()

    def init(self):
        image = Image.open(self.icon_path)
        menu = Menu(
            MenuItem("Mostrar", self.show_window),
            MenuItem("Salir", self.exit)
        )
        self.icon = Icon(self.app_name, image, self.app_name, menu)
        self.icon.run()
