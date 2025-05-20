SET FOREIGN_KEY_CHECKS = 0;
drop table if exists template_command;
drop table if exists command;
drop table if exists template;
drop table if exists record;
drop table if exists backup;
drop table if exists device;
drop table if exists device_group;
drop table if exists operating_system;
drop table if exists user_roles;
drop table if exists app_user;
drop table if exists role;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE Role (
                       id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE App_user (
                          id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL
);

CREATE TABLE User_Roles (
                                  user_id INT NOT NULL,
                                  role_id INT NOT NULL,
                                  PRIMARY KEY (user_id, role_id),
                                  FOREIGN KEY (user_id) REFERENCES App_user(id),
                                  FOREIGN KEY (role_id) REFERENCES Role(id)
);

CREATE TABLE Operating_System (
                                   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                   name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Template (
                          id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE,
                          description VARCHAR(255),
                          os_id INT NOT NULL,
                          updated_at TIMESTAMP,
                          FOREIGN KEY (os_id) REFERENCES Operating_System(id)
);

CREATE TABLE Command (
                         id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL UNIQUE,
                         description VARCHAR(255),
                         command_value VARCHAR(255)
);

CREATE TABLE Template_Command (
                                  template_id INT NOT NULL,
                                  command_id INT NOT NULL,
                                  execution_order INT NOT NULL,
                                  parameter_values TEXT NULL,
                                  PRIMARY KEY (template_id, command_id),
                                  FOREIGN KEY (template_id) REFERENCES Template(id),
                                  FOREIGN KEY (command_id) REFERENCES Command(id)
);

CREATE TABLE Device_Group (
                              id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              parent INT,
                              template INT,
                              FOREIGN KEY (parent) REFERENCES Device_Group(id),
                              FOREIGN KEY (template) REFERENCES Template(id)
);

CREATE TABLE Device (
                        id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        finger_print VARCHAR(255) NOT NULL UNIQUE,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        created_at TIMESTAMP,
                        group_id INT NOT NULL,
                        os_id INT NOT NULL,
                        template INT,
                        FOREIGN KEY (group_id) REFERENCES Device_Group(id),
                        FOREIGN KEY (os_id) REFERENCES Operating_System(id),
                        FOREIGN KEY (template) REFERENCES Template(id)
);

CREATE TABLE Record (
                           id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           device_id INT,
                           date TIMESTAMP,
                           result BLOB,
                           FOREIGN KEY (device_id) REFERENCES Device(id)
);

INSERT INTO App_user VALUES (1, "admin", "admin@testubu.es", SHA2( "1234", 512));
INSERT INTO Operating_System VALUES (1, "Windows");
INSERT INTO Role VALUES (1, "Admin");
INSERT INTO Role VALUES (2, "Gestor");
INSERT INTO User_Roles VALUES (1, 1);
INSERT INTO Device_Group (ID, name) VALUES (1, "UBU");
INSERT INTO Device_Group (ID, name, parent) VALUES (2, "Biblioteca Central", 1);
INSERT INTO Device_Group (ID, name, parent) VALUES (3, "Centro de I+D+I", 1);
INSERT INTO Device_Group (ID, name, parent) VALUES (4, "Escuela Politécnica Superior - A", 1);
INSERT INTO Device_Group (ID, name, parent) VALUES (5, "Escuela Politécnica Superior - C", 1);
INSERT INTO Device_Group (ID, name, parent) VALUES (6, "Escuela Politécnica Superior - D", 1);
INSERT INTO Device_Group (ID, name, parent) VALUES (7, "Facultad de Ciencias", 1);
INSERT INTO Device_Group (ID, name, parent) VALUES (8, "Facultad de Educación", 1);
INSERT INTO Device_Group (ID, name, parent) VALUES (9, "Biblioteca Central", 2);

INSERT INTO Device(finger_print, name, created_at, group_id, os_id) VALUES ("test", "test1", '2025-04-23 01:02:03', 9, 1);
INSERT INTO Device(finger_print, name, created_at, group_id, os_id) VALUES ("test2", "test2", '2025-04-23 01:02:03', 9, 1);

INSERT INTO Template(name, description, os_id) VALUES
('Windows 10', 'Template for Windows 10', 1),
('Windows 10 - Restrict accesses', 'Template for Windows 10 with limited network access', 1),
('Windows 11', 'Template for Windows 11', 1),
('Windows 11 - Without network', 'Template for Windows 11 that disables network access', 1);

INSERT INTO Command (name, description, command_value) VALUES
                                                       ('Enable Interface', 'Enable a network interface', 'netsh interface set interface name="{{interfaceName}}" admin=enable'),
                                                       ('Disable Interface', 'Disable a network interface', 'netsh interface set interface name="{{interfaceName}}" admin=disable'),
                                                       ('Block traffic', 'Block incoming traffic', 'netsh advfirewall firewall add rule name="Block All Other IPs" dir=in action=block remoteip=any'),
                                                       ('Allow traffic', 'Allow incoming traffic', 'netsh advfirewall firewall delete rule name="Block All Other IPs" dir=in'),
                                                       ('Allow certain ip', 'Allow specific ip', 'netsh advfirewall firewall add rule name="Allow Specific IP" dir=in action=allow remoteip={{ipAddress}}'),
                                                       ('Allow certain port', 'Allow specific port', 'netsh advfirewall firewall add rule name="Allow Specific Port" dir=in action=allow protocol=TCP localport={{port}}'),
                                                       ('Block certain ip', 'Block specific ip', 'netsh advfirewall firewall add rule name="Block Specific IP" dir=in action=block remoteip={{ipAddress}}'),
                                                       ('Block certain port', 'Block specific port', 'netsh advfirewall firewall add rule name="Block Specific Port" dir=in action=block protocol=TCP localport={{port}}'),
                                                       ('Enable Firewall', 'Enable the Windows Firewall for all profiles', 'netsh advfirewall set allprofiles state on'),
                                                       ('Disable Firewall', 'Disable the Windows Firewall for all profiles', 'netsh advfirewall set allprofiles state off');