#!/bin/sh

SESSION=appdev

tmux -2 new-session -d -s $SESSION

tmux rename-window 'nginx'
tmux send-keys "start-nginx -f ./target -c ./nginx" C-m
tmux new-window -t $SESSION:1 -n 'boot'
tmux send-keys "export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home; boot dev" C-m
tmux new-window -t $SESSION:2 -n 'sass'
tmux send-keys "sass -w ./resources/styles/app.scss:target/styles/app.css" C-m
tmux new-window -t $SESSION:3 -n 'term'

tmux attach -t $SESSION
