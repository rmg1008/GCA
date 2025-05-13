drop table if exists backup;
drop table if exists record;
drop table if exists device;
drop table if exists device_group;
drop table if exists template_command;
drop table if exists command;
drop table if exists templates;
drop table if exists operating_system;
drop table if exists user_role;
drop table if exists role;
drop table if exists user;
drop table if exists App_user;


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

CREATE TABLE Device_Group (
                        id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        parent INT,
                        FOREIGN KEY (parent) REFERENCES Device_Group(id)
);

CREATE TABLE Device (
                         id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         finger_print VARCHAR(255) NOT NULL UNIQUE,
                         name VARCHAR(255) NOT NULL UNIQUE,
                         created_at TIMESTAMP,
                         group_id INT NOT NULL,
                         os_id INT NOT NULL,
                         FOREIGN KEY (group_id) REFERENCES Device_Group(id),
                         FOREIGN KEY (os_id) REFERENCES Operating_System(id)
);


CREATE TABLE Backup (
                        id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        device_id INT,
                        date TIMESTAMP,
                        config BLOB,
                        FOREIGN KEY (device_id) REFERENCES Device(id)
);

CREATE TABLE Record (
                           id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           device_id INT,
                           date TIMESTAMP,
                           result BLOB,
                           FOREIGN KEY (device_id) REFERENCES Device(id)
);

CREATE TABLE Templates (
                            id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255),
                            description VARCHAR(255),
                            os_id INT NOT NULL,
                            FOREIGN KEY (os_id) REFERENCES Operating_System(id)
);

CREATE TABLE Command (
                          id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255),
                          description VARCHAR(255),
                          value VARCHAR(255)
);

CREATE TABLE Template_Command (
                                   template_id INT NOT NULL,
                                   command_id INT NOT NULL,
                                   execution_order INT NOT NULL,
                                   PRIMARY KEY (template_id, command_id),
                                   FOREIGN KEY (template_id) REFERENCES Templates(id),
                                   FOREIGN KEY (command_id) REFERENCES Command(id)
);
INSERT INTO App_user VALUES (1, "admin", "admin@testubu.es", SHA2( "1234", 512));
INSERT INTO Operating_System VALUES (1, "Windows");
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
INSERT INTO Device(finger_print, name, created_at, group_id, os_id) VALUES ("test2", "test2", '2025-04-23 01:02:03', 9, 1)