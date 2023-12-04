# Spring Boot Web Application


## Description

This Spring Boot web application provides functionality for managing assignments. It allows authenticated users to perform operations such as creating, retrieving, updating, and deleting assignments. Additionally, it offers a public health check API endpoint.

## Table of Contents

- [Spring Boot Web Application](#spring-boot-web-application)
  - [Description](#description)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)

## Features

**Authenticated Operations:**

- `GET /v1/assignments`: Get a list of all assignments.
- `POST /v1/assignments`: Create a new assignment.
- `GET /v1/assignments/{id}`: Get details of a specific assignment by its ID.
- `DELETE /v1/assignments/{id}`: Delete a specific assignment by its ID.
- `PUT /v1/assignments/{id}`: Update a specific assignment by its ID.

**Public Operation:**

- `GET /healthz`: Health Check API (Available to all users without authentication).

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) 8 or higher
- Maven build tool
- MySQL or PostgreSQL database (for storing assignments)
- IDE (Eclipse, IntelliJ IDEA, or your preferred choice)
- Git (for cloning the repository)

## Installation

To set up the Spring Boot web application, follow these steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/your-spring-boot-app.git

1. Navigate to the project directory:

    ```bash
   cd your-spring-boot-app

3. Configure your database connection in application.properties or application.yml.
    ```bash
   export MYSQL_USERNAME=your_username
   export MYSQL_PASSWORD=your_password


4. Build the application:
    ```bash
    mvn clean install
   
5. Run the application:
    ```bash
   mvn spring-boot:run


# Usage

To use this web application, follow these steps:

1. Access the web application by visiting [http://localhost:8080](http://localhost:8080) in your web browser or your preferred API client (e.g., Postman).

2. Authenticate using your credentials to access authenticated endpoints.

3. Use the provided API endpoints to manage assignments and check the health of the application.

## Authentication

To access authenticated endpoints, you must authenticate with valid credentials. This typically involves sending an Authorization header with an authentication token or username/password combination.

## API Endpoints

### Authenticated Endpoints

#### GET /v1/assignments

- **Description**: Get a list of all assignments.
- **Authentication**: Required.
- **Parameters**: None.
- **Response**: JSON array of assignment objects.

#### POST /v1/assignments

- **Description**: Create a new assignment.
- **Authentication**: Required.
- **Parameters**: Assignment data in the request body.
- **Response**: JSON object representing the created assignment.

#### GET /v1/assignments/{id}

- **Description**: Get details of a specific assignment by its ID.
- **Authentication**: Required.
- **Parameters**: id (assignment ID) in the URL path.
- **Response**: JSON object representing the assignment details.

#### DELETE /v1/assignments/{id}

- **Description**: Delete a specific assignment by its ID.
- **Authentication**: Required.
- **Parameters**: id (assignment ID) in the URL path.
- **Response**: Status code indicating success or failure.

#### PUT /v1/assignments/{id}

- **Description**: Update a specific assignment by its ID.
- **Authentication**: Required.
- **Parameters**: id (assignment ID) in the URL path and assignment data in the request body.
- **Response**: JSON object representing the updated assignment.

### Public Endpoint

#### GET /healthz

- **Description**: Health Check API.
- **Authentication**: Not required.
- **Parameters**: None.
- **Response**: Status message indicating the health of the application.



# Build custom machines images that can be to create virtual machines in cloud using Pulumi for Infrastructure as Code.

Install packer in terminal using the below command.
brew tap hashicorp/tap
brew install hashicorp/tap/packer

Create a package and add .hcl file, configure the AMI requirements in the file and execute the below commands
 
Set AWS profile befire running packer
export AWS_PROFILE=dev

check profile
aws configure list

Initialize packer
packer init .

Aligh the format of the packer file
packer fmt cloud.pkr.hcl

Validate packer
packer validate cloud.pkr.hcl

Build packer
packer build cloud.pkr.hcl

When you run Packer with this configuration file, the following steps will occur:

- Packer will start the build process and launch a new temporary EC2 instance in your AWS account using the specified source AMI (var.source_ami) as the base image.

- Packer will connect to the EC2 instance using SSH (Secure Shell).

- The shell provisioner will execute a series of commands on the EC2 instance. In this case, it will:

- Update the package list on the instance with sudo apt-get update.
Install MySQL Server on the instance with sudo apt-get install -y mysql-server.
Set the MySQL root user's password using sudo mysql -e "..."
Install OpenJDK 11 with sudo apt-get install -y openjdk-11-jdk.
Install Maven with sudo apt-get install -y maven.
Once all the provisioner commands have been executed successfully, Packer will stop the EC2 instance.

- Packer will create a new Amazon Machine Image (AMI) based on the state of the EC2 instance. This new AMI will include all the changes and software installations made during the provisioning process.

- The AMI will be registered in your AWS account with the specified name, description, and in the specified region.

- Packer will clean up by terminating the temporary EC2 instance.

- After running this Packer configuration, you will have a custom AMI that includes MySQL, Java (OpenJDK 11), and Maven installed, as well as any other software and configurations you specified. This AMI can be used to launch EC2 instances with these software packages pre-installed.



Set your sql password in command line

export DB_HOST=localhost:3306
export DB_NAME=csye6225 
export DB_USERNAME=root
export DB_PASSWORD=12345678
export SNS_ARN=arn:aws:sns:us-east-1:226534876078:mySNSTopic-6c2cec1
export AWS_REGION=us-east-1
packer validate cloud.pkr.hcl



# Steps to install MySQL in Debian

Create a Swap File: If you haven't already, create a swap file. This will act as "virtual" memory, and while it's slower than actual RAM, it can prevent the OOM killer from terminating processes.

Here's how you can create a 2GB swap file:
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab


# Import SSL/Tls certificate:

The AWS CLI command you provided is used to import an SSL/TLS certificate into AWS Certificate Manager (ACM). ACM is a service provided by AWS for managing SSL/TLS certificates that are used to secure web applications and services.

```
aws acm import-certificate --certificate fileb://Certificate.pem \
      --certificate-chain fileb://CertificateChain.pem \
      --private-key fileb://PrivateKey.pem
