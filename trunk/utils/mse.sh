#!/bin/bash

if [[ $# -ne 4 ]] 
then
	echo "Usage: $0 <project> <from_revision> <to_revision> <step>"
	exit 0
fi

project=$1
fromRev=$2
toRev=$3
step=$4

rev=$fromRev
while [ "$rev" -le "$toRev" ]
do
	echo "[+] Updating to revision $rev"
	svn up -r $rev $project > /dev/null 2>&1
	mse="_$rev.mse"
	mse="$project$mse"
	path="$PWD/$project"

	echo "[+] Genrating MSE for revision $rev"
	cd inFamix
	rm --preserve-root -rf workspace > /dev/null 2>&1
	./inFamix -lang java -path $path -mse $mse > /dev/null 2>&1
	cd ..

	echo "[-] Cleaning up repository"

	projectStr="\"$project\""
	awkStr="{if(\$2!=$projectStr){print\$2}}"
	toRemove=$(svn st $project | awk $awkStr)
	rm --preserve-root -rf $toRemove > /dev/null 2>&1

	binFolder="$project/bin"
	rm --preserve-root -rf $binFolder > /dev/null 2>&1

        rev=$(($rev + $step))
done
