#!/usr/bin/env bash

cd client-jvm && rm -f assets && ln -s ../assets ./ && cd ../
cd server/src/main/resources && rm -f assets && ln -s ../../../../assets ./

