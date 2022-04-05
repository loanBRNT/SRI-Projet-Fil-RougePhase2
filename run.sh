#! /bin/sh

cd ./moteurs

cd bingbong && make run &
echo "bingbong est en cours d'execution"
cd bongala && make run &
echo "bongala est en cours d'execution"

exit 0
