CREATE TABLE IF NOT EXISTS role (
                       id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS app_user (
                          id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
                                  user_id INT NOT NULL,
                                  role_id INT NOT NULL,
                                  PRIMARY KEY (user_id, role_id),
                                  FOREIGN KEY (user_id) REFERENCES app_user(id),
                                  FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE IF NOT EXISTS operating_system (
                                   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                   name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS template (
                          id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE,
                          description VARCHAR(255),
                          os_id INT NOT NULL,
                          updated_at TIMESTAMP,
                          FOREIGN KEY (os_id) REFERENCES operating_system(id)
);

CREATE TABLE IF NOT EXISTS command (
                         id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL UNIQUE,
                         description VARCHAR(255),
                         command_value TEXT
);

CREATE TABLE IF NOT EXISTS template_command (
                                  template_id INT NOT NULL,
                                  command_id INT NOT NULL,
                                  execution_order INT NOT NULL,
                                  parameter_values TEXT NULL,
                                  PRIMARY KEY (template_id, command_id),
                                  FOREIGN KEY (template_id) REFERENCES template(id),
                                  FOREIGN KEY (command_id) REFERENCES command(id)
);

CREATE TABLE IF NOT EXISTS device_group (
                              id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              parent INT,
                              template INT,
                              FOREIGN KEY (parent) REFERENCES device_group(id),
                              FOREIGN KEY (template) REFERENCES template(id)
);

CREATE TABLE IF NOT EXISTS device (
                        id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        finger_print VARCHAR(255) NOT NULL UNIQUE,
                        finger_print_hash VARCHAR(255) NOT NULL UNIQUE,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        created_at TIMESTAMP,
                        group_id INT NOT NULL,
                        os_id INT NOT NULL,
                        template INT,
                        FOREIGN KEY (group_id) REFERENCES device_group(id),
                        FOREIGN KEY (os_id) REFERENCES operating_system(id),
                        FOREIGN KEY (template) REFERENCES template(id)
);

INSERT IGNORE INTO app_user VALUES (1, "admin", "admin@testubu.es", SHA2("1234", 512));
INSERT IGNORE INTO operating_system VALUES (1, "Windows");
INSERT IGNORE INTO role VALUES (1, "Admin");
INSERT IGNORE INTO role VALUES (2, "Gestor");
INSERT IGNORE INTO user_roles VALUES (1, 1);
INSERT IGNORE INTO device_group (ID, name) VALUES (1, "UBU");
INSERT IGNORE INTO device_group (ID, name, parent) VALUES (2, "Biblioteca Central", 1);
INSERT IGNORE INTO device_group (ID, name, parent) VALUES (3, "Centro de I+D+I", 1);
INSERT IGNORE INTO device_group (ID, name, parent) VALUES (4, "Escuela Politécnica Superior - A", 1);
INSERT IGNORE INTO device_group (ID, name, parent) VALUES (5, "Escuela Politécnica Superior - C", 1);
INSERT IGNORE INTO device_group (ID, name, parent) VALUES (6, "Escuela Politécnica Superior - D", 1);
INSERT IGNORE INTO device_group (ID, name, parent) VALUES (7, "Facultad de Ciencias", 1);
INSERT IGNORE INTO device_group (ID, name, parent) VALUES (8, "Facultad de Educación", 1);
INSERT IGNORE INTO device_group (ID, name, parent) VALUES (9, "Sala 1", 2);
INSERT IGNORE INTO device_group (ID, name, parent) VALUES (10, "Sala 2", 2);

INSERT IGNORE INTO template(name, description, os_id, updated_at) VALUES
('Acceso completo', 'Sin restricciones de red o permisos', 1, CURRENT_TIMESTAMP),
('Acceso restringido', 'Acceso limitado a la red', 1, CURRENT_TIMESTAMP),
('Sin red', 'Red deshabilitada por completo ', 1, CURRENT_TIMESTAMP),
('Modo examen', 'Acceso a recursos definidos', 1, CURRENT_TIMESTAMP),
('Restaurar configuración', 'Accesos y permisos restablecidos', 1, CURRENT_TIMESTAMP);

INSERT IGNORE INTO command (name, description, command_value) VALUES
('Bloquear todo el tráfico', 'Bloquea todas las conexiones entrantes y salientes sin excepción',
 'netsh advfirewall firewall add rule name="Bloqueo Total Entrada" dir=in action=block remoteip=any && netsh advfirewall firewall add rule name="Bloqueo Total Salida" dir=out action=block remoteip=any'),

('Permitir todo el tráfico', 'Elimina el bloqueo total de conexiones',
 'netsh advfirewall firewall delete rule name="Bloqueo Total Entrada" && netsh advfirewall firewall delete rule name="Bloqueo Total Salida"'),

('Bloquear acceso a IP', 'Bloquea todo el tráfico hacia y desde una IP específica',
 'netsh advfirewall firewall add rule name="Bloquear IP Entrada" dir=in action=block remoteip={{direccionIP}} && netsh advfirewall firewall add rule name="Bloquear IP Salida" dir=out action=block remoteip={{direccionIP}}'),

('Permitir acceso a IP', 'Permite el tráfico hacia y desde una IP específica',
 'netsh advfirewall firewall add rule name="Permitir IP Entrada" dir=in action=allow remoteip={{direccionIP}} && netsh advfirewall firewall add rule name="Permitir IP Salida" dir=out action=allow remoteip={{direccionIP}}'),

('Bloquear puerto', 'Bloquea conexiones en un puerto TCP específico',
 'netsh advfirewall firewall add rule name="Bloquear Puerto Entrada" dir=in action=block protocol=TCP localport={{puerto}} && netsh advfirewall firewall add rule name="Bloquear Puerto Salida" dir=out action=block protocol=TCP remoteport={{puerto}}'),

('Permitir puerto', 'Permite conexiones en un puerto TCP específico',
 'netsh advfirewall firewall add rule name="Permitir Puerto Entrada" dir=in action=allow protocol=TCP localport={{puerto}} && netsh advfirewall firewall add rule name="Permitir Puerto Salida" dir=out action=allow protocol=TCP remoteport={{puerto}}'),

('Permitir acceso a IP y puerto', 'Permite tráfico hacia y desde una IP y puerto específicos',
'netsh advfirewall firewall add rule name="Permitir Entrada IP y Puerto" dir=in action=allow remoteip={{direccionIP}} protocol=TCP localport={{puerto}} && netsh advfirewall firewall add rule name="Permitir Salida IP y Puerto" dir=out action=allow remoteip={{direccionIP}} protocol=TCP remoteport={{puerto}}'),

('Bloquear acceso a IP y puerto', 'Bloquea tráfico hacia y desde una IP y puerto específicos',
 'netsh advfirewall firewall add rule name="Bloquear Entrada IP y Puerto" dir=in action=block remoteip={{direccionIP}} protocol=TCP localport={{puerto}} && netsh advfirewall firewall add rule name="Bloquear Salida IP y Puerto" dir=out action=block remoteip={{direccionIP}} protocol=TCP remoteport={{puerto}}'),

('Activar cortafuegos', 'Activa el cortafuegos de Windows en todos los perfiles (dominio, privado, público)',
 'netsh advfirewall set allprofiles state on'),

('Desactivar cortafuegos', 'Desactiva el cortafuegos de Windows en todos los perfiles (dominio, privado, público)',
 'netsh advfirewall set allprofiles state off'),

('Bloqueo de tráfico a todos los perfiles', 'Bloquea todo el tráfico de red en todos los perfiles, permitiendo añadir excepciones',
 'netsh advfirewall set allprofiles firewallpolicy blockinbound,blockoutbound'),

('Permitir acceso a un dominio determinado', 'Utiliza powershell para encontrar la ip del dominio',
 'powershell -Command "$ip = (Resolve-DnsName ''{{dominio}}'' -Type A | Where-Object { $_.IPAddress } | Select-Object -First 1).IPAddress; netsh advfirewall firewall add rule name=''Permitir IP Entrada'' dir=in action=allow remoteip=$ip | Out-Null; netsh advfirewall firewall add rule name=''Permitir IP Salida'' dir=out action=allow remoteip=$ip | Out-Null"'),

('Bloquear red local', 'Bloquea todo el tráfico hacia una determinada red local',
 'netsh advfirewall firewall add rule name="Bloquear LAN" dir=out action=block remoteip={{remoteIP}}'),

('Desactivar descubrimiento red', 'Deshabilita el descubrimiento de red en todos los perfiles',
 'netsh advfirewall firewall set rule group="Network Discovery" new enable=No'),

('Desactivar descubrimiento red', 'Deshabilita el acceso a la red a un programa determinado',
 'powershell -Command "$paths = @(''C:\Program Files'',''C:\Program Files (x86)'',''C:\Users\*\AppData\Local''); $exe = foreach ($p in $paths) { Get-ChildItem -Path $p -Recurse -Include *.exe -ErrorAction SilentlyContinue | Where-Object { $_.Name -like ''*{{name}}*'' } | Select-Object -ExpandProperty FullName -First 1; if ($_) { break } }; if ($exe) { netsh advfirewall firewall add rule name=''Bloqueo {{name}}'' dir=out action=block program=\"$exe\" enable=yes >$null 2>&1 }"'),

('Mostrar aviso', 'Mensaje de aviso a los usuarios',
 'echo MsgBox "{{aviso}}", 64, "Aviso" > %TEMP%\popup.vbs && start "" wscript.exe %TEMP%\popup.vbs'),

('Deshabilitar Administrador de tareas', 'Deshabilita la posibilidad de iniciar el administrador',
 'reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Policies\System" /v DisableTaskMgr /t REG_DWORD /d 1 /f'),

('Deshabilitar USB', 'Deshabilita el uso de unidades USB',
 'reg add "HKLM\SYSTEM\CurrentControlSet\Services\USBSTOR" /v Start /t REG_DWORD /d 4 /f'),

('Habilitar USB', 'Habilita el uso de unidades USB',
 'reg add "HKLM\SYSTEM\CurrentControlSet\Services\USBSTOR" /v Start /t REG_DWORD /d 3 /f'),

('Habilitar Administrador de tareas', 'Habilita el uso del administrador de tareas',
 'reg delete "HKCU\Software\Microsoft\Windows\CurrentVersion\Policies\System" /v DisableTaskMgr /f'),

('Forzar cierre app', 'Fuerza el cierre de una aplicación',
 'taskkill /f /im {{app}} 2>nul || echo {{app}} no se estaba ejecutando.');


-- Asignación a "Sin red"
INSERT IGNORE INTO template_command (template_id, command_id, execution_order, parameter_values) VALUES
(
    (SELECT id FROM template WHERE name = 'Sin red'),
    (SELECT id FROM command WHERE name = 'Bloquear todo el tráfico'),
    1,
    '{}'
);

-- Asignaciones a "Modo examen"
INSERT IGNORE INTO template_command (template_id, command_id, execution_order, parameter_values) VALUES
(
  (SELECT id FROM template WHERE name = 'Modo examen'),
  (SELECT id FROM command WHERE name = 'Bloqueo de tráfico a todos los perfiles'),
  1,
  '{}'
),
(
  (SELECT id FROM template WHERE name = 'Modo examen'),
  (SELECT id FROM command WHERE name = 'Permitir acceso a un dominio determinado'),
  2,
  '{"dominio":"ubuvirtual.ubu.es"}'
),
(
  (SELECT id FROM template WHERE name = 'Modo examen'),
  (SELECT id FROM command WHERE name = 'Deshabilitar red a software'),
  3,
  '{"name":"chrome.exe"}'
),
(
  (SELECT id FROM template WHERE name = 'Modo examen'),
  (SELECT id FROM command WHERE name = 'Mostrar aviso'),
  4,
  '{"aviso":"El examen va a comenzar"}'
),
(
  (SELECT id FROM template WHERE name = 'Modo examen'),
  (SELECT id FROM command WHERE name = 'Forzar cierre app'),
  5,
  '{"app":"chrome.exe"}'
),
(
  (SELECT id FROM template WHERE name = 'Modo examen'),
  (SELECT id FROM command WHERE name = 'Deshabilitar Administrador de tareas'),
  6,
  '{}'
),
(
  (SELECT id FROM template WHERE name = 'Modo examen'),
  (SELECT id FROM command WHERE name = 'Deshabilitar USB'),
  7,
  '{}'
);

-- Asignaciones a "Restaurar configuración"
INSERT IGNORE INTO template_command (template_id, command_id, execution_order, parameter_values) VALUES
(
  (SELECT id FROM template WHERE name = 'Restaurar configuración'),
  (SELECT id FROM command WHERE name = 'Habilitar USB'),
  1,
  '{}'
),
(
  (SELECT id FROM template WHERE name = 'Restaurar configuración'),
  (SELECT id FROM command WHERE name = 'Habilitar Administrador de tareas'),
  2,
  '{}'
);