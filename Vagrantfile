# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|
  config.vm.box = "ubuntu/trusty64"

  config.vm.provision "shell", inline: <<-SHELL
    wget https://archive.cloudera.com/cdh5/ubuntu/trusty/amd64/cdh/archive.key -O archive.key
    sudo apt-key add archive.key
    sudo wget 'https://archive.cloudera.com/cdh5/ubuntu/trusty/amd64/cdh/cloudera.list' \
      -O /etc/apt/sources.list.d/cloudera.list
    sudo apt-get update
    sudo apt-get install -y default-jre
    sudo apt-get install -y flume-ng
    sudo apt-get install -y maven
  SHELL

  config.vm.provision "file", source: "./flume.conf", destination: "/home/vagrant/flume.conf"
  config.vm.provision "file", source: "./target/flume-kinesis-0.0.16.jar", destination: "/home/vagrant/flume-kinesis-0.0.16.jar"

  config.vm.provision "shell", inline: <<-SHELL
    sudo mkdir -p /etc/flume-ng/plugins.d/flume-kinesis/lib
    sudo cp /home/vagrant/flume.conf /etc/flume-ng/conf/
    sudo cp /home/vagrant/flume-kinesis-0.0.16.jar /etc/flume-ng/plugins.d/flume-kinesis/lib

    #ssh into the vm and run this command to start flume agent
    #sudo flume-ng agent -n agent -c /etc/flume-ng/conf -f /etc/flume-ng/conf/flume.conf --plugins-path /etc/flume-ng/plugins.d &
  SHELL
end

