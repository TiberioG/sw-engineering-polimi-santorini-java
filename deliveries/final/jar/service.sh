#!/bin/sh
SERVICE_NAME=SantoriniServer
PATH_TO_JAR=/home/tiberio/santorini/server.jar
PATH_TO_LOGFILE=/var/www/santorini/html/log/logfile.log
PID_PATH_NAME=/home/tiberio/santorini/server-pid
PORT=1234
case $1 in
start)
       echo "Starting $SERVICE_NAME ..."
  if [ ! -f $PID_PATH_NAME ]; then
       nohup java -jar $PATH_TO_JAR $PORT $PATH_TO_LOGFILE >> server.out 2>&1&
                   echo $! > $PID_PATH_NAME
       echo "$SERVICE_NAME is started at port $PORT and logging at $PATH_TO_LOGFILE"
  else
       echo "$SERVICE_NAME is already running ..."
  fi
;;
stop)
  if [ -f $PID_PATH_NAME ]; then
         PID=$(cat $PID_PATH_NAME);
         echo "$SERVICE_NAME stoping ..."
         kill $PID;
         echo "$SERVICE_NAME stopped ..."
         rm $PID_PATH_NAME
         rm $PATH_TO_LOGFILE
  else
         echo "$SERVICE_NAME is not running ..."
  fi
;;
restart)
  if [ -f $PID_PATH_NAME ]; then
      PID=$(cat $PID_PATH_NAME);
      echo "$SERVICE_NAME stopping ...";
      kill $PID;
      sleep 2
      pkill java;
      echo "$SERVICE_NAME stopped ...";
      rm $PID_PATH_NAME
      rm  $PATH_TO_LOGFILE
      sleep 2;
      echo "$SERVICE_NAME starting ..."
      nohup nohup java -jar $PATH_TO_JAR $PORT $PATH_TO_LOGFILE >> server.out 2>&1&
      echo $! > $PID_PATH_NAME
      echo "$SERVICE_NAME is started at port $PORT and logging at $PATH_TO_LOGFILE"
  else
      echo "$SERVICE_NAME is not running ..."
     fi     ;;
 esac
