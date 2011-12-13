#!/bin/bash

if [[ $# -ne 4 ]] 
then
	echo "Usage: $0 <infamix_path> <infamix_lang> <project_path> <mse_path>"
	exit 0
fi

infamixPath=$1
lang=$2
projectPath=$3
mseFile=$4


echo "[+] Genrating MSE for revision $rev"
rm -rf $infamixPath/workspace > /dev/null 2>&1
$infamixPath/inFamix -lang $lang -path $projectPath -mse $mseFile

echo "[-] Cleaning up repository"

projectStr="\"$projectPath\""
awkStr="{if(\$2!=$projectStr){print\$2}}"
toRemove=$(svn st $projectPath | awk $awkStr)
rm -rf $toRemove > /dev/null 2>&1

binFolder="$projectPath/bin"
rm -rf $binFolder > /dev/null 2>&1
