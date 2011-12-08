#!/bin/bash

ssh -f $1@atelier.inf.usi.ch "cd public_html/testnode/; /home/babazadm/mongodb-linux-x86_64-2.0.1/bin/mongod --dbpath=/home/$1/data/db --port=$3; ssh $1@atelier.inf.usi.ch -L $2:atelier.inf.usi.ch:$3 -N"
