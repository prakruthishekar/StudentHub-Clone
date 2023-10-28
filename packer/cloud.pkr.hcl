packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = ">= 1.0.0"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}


variable "ssh_username" {
  type    = string
  default = "admin"
}

variable "source_ami" {
  type    = string
  default = "ami-06db4d78cb1d3bbf9"
}

variable "AWS_ACCESS_KEY_ID" {
  type    = string
  default = ""
}
variable "AWS_SECRET_ACCESS_KEY" {
  type    = string
  default = ""
}

// variable "MYSQL_PASSWORD" {
//   type      = string
//   default   = ""
//   sensitive = true
// }

// variable "MYSQL_USERNAME" {
//   type    = string
//   default = "your_default_mysql_username" # You can replace this with your default MySQL username
// }

// variable "spring_boot_profile" {
//   description = "Spring Boot profile to use during the build"
//   default     = "default" # Default profile if not provided
// }

variable "DB_HOST" {
  type    = string
  default = "your_default_db_host"
}

variable "DB_PORT" {
  type    = string
  default = "your_default_db_port"
}


# https://www.packer.io/plugins/builders/amazon/ebs
source "amazon-ebs" "webapp-ami" {
    region  = var.aws_region
    ami_name        = "debian-12-ami_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
    ami_description = "AMI for Spring Boot Application with MySQL"
    ami_regions = [var.aws_region]
    ami_users = ["073745101099"]
    instance_type = "t2.micro"
    source_ami    = var.source_ami
    ssh_username = var.ssh_username
    access_key   = "${var.AWS_ACCESS_KEY_ID}"
    secret_key   = "${var.AWS_SECRET_ACCESS_KEY}"

}

build {
  name    = "debian-12-ami"
  sources = ["source.amazon-ebs.webapp-ami"]


  provisioner "file" {
  source      = "/home/runner/work/webapp/webapp/target/assignment1-0.0.1-SNAPSHOT.jar"
  destination = "~/"
}
  provisioner "file" {
    source      = "packer/webapp.service"
    destination = "/tmp/webapp.service"
  }


  provisioner "shell" {
    inline = [
      "sudo apt-get update",
      "sudo apt-get upgrade -y",
      "sudo apt-get install -y openjdk-17-jdk",  # Install Java
      "sudo apt-get install -y unzip",
    ]
  }

  provisioner "shell" {
  inline = [
      "sudo mv /tmp/webapp.service /etc/systemd/system/webapp.service",  # Rename the service file
      "sudo groupadd prodGroup",  # Create a group
      "sudo useradd -s /bin/bash -G prodGroup prod",  # Create a user and add to the group
      "sudo chown -R prod:prodGroup ~/",  # Change ownership of the application directory
      // "cd /opt/webapp",  # Change to the application directory
      "sudo chmod +x assignment1-0.0.1-SNAPSHOT.jar",  # Add execute permissions to the JAR file if needed
      # Create a systemd service unit for your Java application
      "sudo systemctl daemon-reload",  # Reload systemd to recognize the new service
      "sudo systemctl enable webapp",  # Enable the Java application as a systemd service
      "sudo systemctl start webapp",  # Start the Java application
    ]
  }
}

