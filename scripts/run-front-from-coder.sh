#!/bin/bash
cd /home/$(whoami)/task-management-practice-ia/frontend &&
npm run ng serve -- --allowed-hosts 4200--main--$(hostname)--$(whoami | tr '[:upper:]' '[:lower:]')--$CODER_HOST
