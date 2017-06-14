#!/bin/bash

find ./ -depth -name "*skeleton*" | sed 's/\(.*\)skeleton\(.*\)/mv \1skeleton\2 \1newproject\2/g' | xargs -I {} sh -c {}

