#!/bin/bash

SCRIPTDIR=`dirname $0`

if [ "X" == "X$PORT" ]; then
    PORT=80
fi

LOGFILE=/tmp/callableapis-webapp.log
CALLABLEAPIS_WEBAPP_OPTS=-Dcom.callableapis.bindurl=http://0.0.0.0:$PORT/

export LOGFILE CALLABLEAPIS_WEBAPP_OPTS
( "$SCRIPTDIR/bin/callableapis-webapp" 2>&1 ) > $LOGFILE < /dev/null &
