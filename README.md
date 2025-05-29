# GCA

## Requirements

- [Docker](https://www.docker.com/)

## Installation

1. Clone the repository:
    ```bash
   git clone https://github.com/rmg1008/GCA.git
   cd GCA
    ```
2. Build and start all the services:
    ```bash
    docker-compose up --build
    ```

## Services

| Service    | Technology       | URL / Port               |
|------------|------------------|--------------------------|
| Frontend   | Angular + Nginx  | [http://localhost:4200]  |
| Backend    | Spring Boot      | [http://localhost:8080]  |
| Database   | MariaDB          | `localhost:3306`         |
