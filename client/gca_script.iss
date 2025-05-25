[Setup]
AppName=GCA
AppVersion=2.0
DefaultDirName={pf}\GCA
DefaultGroupName=GCA
UninstallDisplayIcon={app}\main.exe
OutputDir=output
OutputBaseFilename=GCA_Installer
Compression=lzma
SolidCompression=yes

[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"

[Files]
Source: "dist\main.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "resources\icono.ico"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\GCA"; Filename: "{app}\main.exe"; IconFilename: "{app}\icono.ico"
Name: "{commondesktop}\GCA"; Filename: "{app}\main.exe"; IconFilename: "{app}\icono.ico"; Tasks: desktopicon

[Tasks]
Name: "desktopicon"; Description: "Crear acceso directo en el escritorio"; GroupDescription: "Opciones adicionales:"

[Run]
Filename: "{app}\main.exe"; Description: "Lanzar GCA"; Flags: nowait postinstall skipifsilent
