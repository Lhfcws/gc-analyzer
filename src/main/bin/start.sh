#!/usr/bin/env bash

COMMAND=$1
shift

if [ "$COMMAND" = "web" ]; then
  CLASS=com.datastory.gc.visual.webserver.WebServer
elif [ "$COMMAND" = "local" ]; then
  CLASS=com.datastory.gc.visual.webserver.LocalFilePageLauncher
else
  CLASS=$COMMAND
fi

java -cp gc-analyzer.jar $CLASS $@