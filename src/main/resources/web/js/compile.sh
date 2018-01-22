#!/usr/bin/env bash
cd $(dirname $0)
coffee -b -o mylib -c src
