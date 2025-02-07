#!/bin/bash
(cd backend/springboot && mvn clean package) &&
(cd frontend && ng build --configuration production)
