#!/bin/bash

echo $1

find ./ -depth -name "*skeleton*" | sed 's/\(.*\)skeleton\(.*\)/mv \1skeleton\2 \1`$1`\2/g' | xargs -I {} sh -c {}

find ./ -iname "*.xml" | while read x; do sed -i 's/skeleton/`$1`/g' $x; done

