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
  region          = var.aws_region
  ami_name        = "debian-12-ami_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for Spring Boot Application with MySQL"
  ami_regions     = [var.aws_region]
  ami_users       = ["073745101099"]
  instance_type   = "t2.micro"
  source_ami      = var.source_ami
  ssh_username    = var.ssh_username
  access_key      = "${var.AWS_ACCESS_KEY_ID}"
  secret_key      = "${var.AWS_SECRET_ACCESS_KEY}"

}

build {
  name    = "debian-12-ami"
  sources = ["source.amazon-ebs.webapp-ami"]

  post-processor "manifest" {
    output     = "manifest.json"
    strip_path = true
  }

  provisioner "file" {
    source      = "/home/runner/work/webapp/webapp/target/assignment1-0.0.1-SNAPSHOT.jar"
    destination = "~/"
  }

  provisioner "file" {
    source      = "/home/runner/work/webapp/webapp/systemd/webapp.service"
    destination = "~/"
  }

  provisioner "file" {
    source      = "/home/runner/work/webapp/webapp/systemd/CloudWatchAgent.json"
    destination = "~/"
  }

  // provisioner "file" {
  //     source      = "target/assignment1-0.0.1-SNAPSHOT.jar"
  //     destination = "~/"
  //   }

  // provisioner "file" {
  //   source      = "systemd/webapp.service"
  //   destination = "~/"
  // }

  // provisioner "file" {
  //   source      = "systemd/CloudWatchAgent.json"
  //   destination = "~/"
  // }

  provisioner "shell" {
    inline = [
      "sudo mkdir /opt/webapp",
      "sudo mv ~/assignment1-0.0.1-SNAPSHOT.jar /opt/webapp",
    ]
  }

  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1",
    ]
    inline = [
      "sudo apt-get update",
      "sudo apt-get upgrade -y",
      "sudo apt-get install unzip",
      "sudo apt-get install -y expect",
      "sudo apt-get install -y openjdk-17-jdk",
      "sudo apt-get install -y maven",
      "sudo apt-get clean",
      "sudo groupadd webappgroup",
      "sudo useradd -s /bin/false -g webappgroup -d /opt/webappgroup -m webappuser",
      "sudo mv ~/webapp.service /etc/systemd/system/",
      "sudo chown -R webappuser:webappgroup /opt/webapp",          # Change ownership to webapp folder in home dir
      "sudo chmod g+x /opt/webapp/assignment1-0.0.1-SNAPSHOT.jar", # Add execute permissions
      "sudo systemctl daemon-reload",
      "sudo systemctl enable webapp",
      "sudo systemctl start webapp",
      "sudo systemctl restart webapp",
      "sudo systemctl stop webapp",
      "wget https://s3.amazonaws.com/amazoncloudwatch-agent/debian/amd64/latest/amazon-cloudwatch-agent.deb",
      "sudo dpkg -i -E amazon-cloudwatch-agent.deb",
      "sudo systemctl start amazon-cloudwatch-agent",
      "sudo systemctl enable amazon-cloudwatch-agent",
      "sudo mv ~/CloudWatchAgent.json /opt/aws/amazon-cloudwatch-agent/etc/",
      "sudo systemctl restart amazon-cloudwatch-agent"
    ]
  }
}