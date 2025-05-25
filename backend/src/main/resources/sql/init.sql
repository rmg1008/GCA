CREATE TABLE IF NOT EXISTS Role (
                       id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS App_user (
                          id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS User_Roles (
                                  user_id INT NOT NULL,
                                  role_id INT NOT NULL,
                                  PRIMARY KEY (user_id, role_id),
                                  FOREIGN KEY (user_id) REFERENCES App_user(id),
                                  FOREIGN KEY (role_id) REFERENCES Role(id)
);

CREATE TABLE IF NOT EXISTS Operating_System (
                                   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                   name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Template (
                          id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE,
                          description VARCHAR(255),
                          os_id INT NOT NULL,
                          updated_at TIMESTAMP,
                          FOREIGN KEY (os_id) REFERENCES Operating_System(id)
);

CREATE TABLE IF NOT EXISTS Command (
                         id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL UNIQUE,
                         description VARCHAR(255),
                         command_value TEXT
);

CREATE TABLE IF NOT EXISTS Template_Command (
                                  template_id INT NOT NULL,
                                  command_id INT NOT NULL,
                                  execution_order INT NOT NULL,
                                  parameter_values TEXT NULL,
                                  PRIMARY KEY (template_id, command_id),
                                  FOREIGN KEY (template_id) REFERENCES Template(id),
                                  FOREIGN KEY (command_id) REFERENCES Command(id)
);

CREATE TABLE IF NOT EXISTS Device_Group (
                              id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              parent INT,
                              template INT,
                              FOREIGN KEY (parent) REFERENCES Device_Group(id),
                              FOREIGN KEY (template) REFERENCES Template(id)
);

CREATE TABLE IF NOT EXISTS Device (
                        id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        finger_print VARCHAR(255) NOT NULL UNIQUE,
                        finger_print_hash VARCHAR(255) NOT NULL UNIQUE,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        created_at TIMESTAMP,
                        group_id INT NOT NULL,
                        os_id INT NOT NULL,
                        template INT,
                        FOREIGN KEY (group_id) REFERENCES Device_Group(id),
                        FOREIGN KEY (os_id) REFERENCES Operating_System(id),
                        FOREIGN KEY (template) REFERENCES Template(id)
);

INSERT IGNORE INTO App_user VALUES (1, "admin", "admin@testubu.es", SHA2("1234", 512));
INSERT IGNORE INTO Operating_System VALUES (1, "Windows");
INSERT IGNORE INTO Role VALUES (1, "Admin");
INSERT IGNORE INTO Role VALUES (2, "Gestor");
INSERT IGNORE INTO User_Roles VALUES (1, 1);
INSERT IGNORE INTO Device_Group (ID, name) VALUES (1, "UBU");
INSERT IGNORE INTO Device_Group (ID, name, parent) VALUES (2, "Biblioteca Central", 1);
INSERT IGNORE INTO Device_Group (ID, name, parent) VALUES (3, "Centro de I+D+I", 1);
INSERT IGNORE INTO Device_Group (ID, name, parent) VALUES (4, "Escuela Politécnica Superior - A", 1);
INSERT IGNORE INTO Device_Group (ID, name, parent) VALUES (5, "Escuela Politécnica Superior - C", 1);
INSERT IGNORE INTO Device_Group (ID, name, parent) VALUES (6, "Escuela Politécnica Superior - D", 1);
INSERT IGNORE INTO Device_Group (ID, name, parent) VALUES (7, "Facultad de Ciencias", 1);
INSERT IGNORE INTO Device_Group (ID, name, parent) VALUES (8, "Facultad de Educación", 1);
INSERT IGNORE INTO Device_Group (ID, name, parent) VALUES (9, "Sala 1", 2);
INSERT IGNORE INTO Device_Group (ID, name, parent) VALUES (10, "Sala 2", 2);

INSERT IGNORE INTO Template(name, description, os_id, updated_at) VALUES
('Windows 10', 'Template for Windows 10', 1, CURRENT_TIMESTAMP),
('Windows 10 - Restrict accesses', 'Template for Windows 10 with limited network access', 1, CURRENT_TIMESTAMP),
('Windows 11', 'Template for Windows 11', 1, CURRENT_TIMESTAMP),
('Windows 11 - Without network', 'Template for Windows 11 that disables network access', 1, CURRENT_TIMESTAMP);

INSERT IGNORE INTO Command (name, description, command_value) VALUES
('Bloquear todo el tráfico', 'Bloquea todas las conexiones entrantes y salientes',
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
 'netsh advfirewall set allprofiles state off');