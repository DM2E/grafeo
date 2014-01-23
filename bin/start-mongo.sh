#!/bin/bash
MONGO_PATH=mongodb-data
if [[ ! -d $MONGO_PATH ]];then
    mkdir -p $MONGO_PATH
fi
mongod --dbpath $MONGO_PATH
