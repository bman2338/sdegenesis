#!/bin/bash

echo "[+] Copying archives from Masiar"
# Better to download them from SCM, do it if you want.
cp /home/babazadm/mongodb-linux-x86_64-2.0.1.tgz .
cp /home/babazadm/node-v0.6.5.tar.gz .

echo "[+] Expanding mongodb archive"
tar -xf mongodb-linux-x86_64-2.0.1.tgz

echo "[+] Configuring mongodb"
mkdir data
mkdir data/db
chmod 777 /data/db 

echo "[+] Expanding node.js archive"
tar -xf node-v0.6.5.tar.gz

echo "[+] Building and installing node.js"
mkdir node
home=$PWD
cd node-v0.6.5
./configure --prefix=$home/node
make
make install

cd ..

echo "[+] Updating .bash_profile"
echo "export PATH=$PATH:$PWD/node/bin:$PWD/mongodb-linux-x86_64-2.0.1/bin" >> .bash_profile

echo "[-] Cleaning up"
rm mongodb-linux-x86_64-2.0.1.tgz
rm node-v0.6.5.tar.gz
rm -rf node-v0.6.5

echo "[!] Please log out!"

