CREATE TABLE "Role" (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE "app_user" (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL
);


CREATE TABLE "User_Roles" (
                              user_id BIGINT NOT NULL,
                              role_id BIGINT NOT NULL,
                              PRIMARY KEY (user_id, role_id),
                              FOREIGN KEY (user_id) REFERENCES "app_user"(id),
                              FOREIGN KEY (role_id) REFERENCES "Role"(id)
);

CREATE TABLE "Operating_System" (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE "Device_Group" (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(255) NOT NULL,
                                parent BIGINT,
                                FOREIGN KEY (parent) REFERENCES "Device_Group"(id)
);

CREATE TABLE "Device" (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          finger_print VARCHAR(255) NOT NULL UNIQUE,
                          name VARCHAR(255) NOT NULL UNIQUE,
                          created_at TIMESTAMP,
                          group_id BIGINT NOT NULL,
                          os_id BIGINT NOT NULL,
                          FOREIGN KEY (group_id) REFERENCES "Device_Group"(id),
                          FOREIGN KEY (os_id) REFERENCES "Operating_System"(id)
);

CREATE TABLE "Backup" (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          device_id BIGINT,
                          date TIMESTAMP,
                          config BLOB,
                          FOREIGN KEY (device_id) REFERENCES "Device"(id)
);

CREATE TABLE "Record" (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          device_id BIGINT,
                          date TIMESTAMP,
                          result BLOB,
                          FOREIGN KEY (device_id) REFERENCES "Device"(id)
);

CREATE TABLE "Templates" (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(255),
                             description VARCHAR(255),
                             os_id BIGINT NOT NULL,
                             FOREIGN KEY (os_id) REFERENCES "Operating_System"(id)
);

CREATE TABLE "Command" (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(255),
                           description VARCHAR(255),
                           "value" VARCHAR(255)
);

CREATE TABLE "Template_Command" (
                                    template_id BIGINT NOT NULL,
                                    command_id BIGINT NOT NULL,
                                    execution_order INT NOT NULL,
                                    PRIMARY KEY (template_id, command_id),
                                    FOREIGN KEY (template_id) REFERENCES "Templates"(id),
                                    FOREIGN KEY (command_id) REFERENCES "Command"(id)
);
