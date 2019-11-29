#!/bin/bash

docker login -u dockeruser -p dockerpass  your-docker-hosts
docker-compose kill
docker-compose rm
docker-compose up -d