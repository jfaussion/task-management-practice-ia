#!/bin/bash
(cd backend/springboot && mvn spring-boot:run) &
(cd frontend && ng serve) &
wait
