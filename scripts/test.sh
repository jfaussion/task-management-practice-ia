#!/bin/bash
(cd backend/springboot && mvn test) &&
(cd frontend && ng test)
