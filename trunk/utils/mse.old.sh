#!/bin/bash

if [[ $# -ne 7 ]] 
then
	echo "Usage: $0 <project_name> <project_path> <from_revision> <to_revision> <step> <infamix_path> <mse_out_dir>"
	exit 0
fi

projectName=$1
projectPath=$2
fromRev=$3
toRev=$4
step=$5
infamixPath=$6
msePath=$7 

rev=$fromRev
while [ "$rev" -le "$toRev" ]
do
	echo "[+] Updating to revision $rev"
	svn up -r $rev $projectPath > /dev/null 2>&1
	mse="_$rev.mse"
	mse="$msePath/$projectName$mse"
	echo "[+] Genrating MSE for revision $rev"
	rm --preserve-root -rf workspace > /dev/null 2>&1
	$infamixPath/inFamix -lang java -path $projectPath -mse $mse
	echo "[-] Cleaning up repository"
	projectStr="\"$projectPath\""
	awkStr="{if(\$2!=$projectStr){print\$2}}"
	toRemove=$(svn st $projectPath | awk $awkStr)
	rm --preserve-root -rf $toRemove > /dev/null 2>&1

	binFolder="$projectPath/bin"
	rm --preserve-root -rf $binFolder > /dev/null 2>&1

        rev=$(($rev + $step))
done
