#!/bin/bash

SCRIPTDIR=`dirname $0`

( "$SCRIPTDIR/bin/callableapis-webapp" 2>&1 ) > /tmp/callableapis-webapp.log < /dev/null &
