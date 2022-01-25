#!/bin/bash

#
# Check if the given server support HTTP range header
# param 1: url
#
function check_ranges_support() {
  ret=`curl -s -I -X HEAD $1 | grep "Accept-Ranges: bytes"`

  if [ -z "$ret" ]; then
    echo "Ranges are nor supported by the server"
    exit 1
  fi
}

#
# Recovers the total length of the given file
# param 1: url
#
function get_length() {
  ret=`curl -s -I -X HEAD $1 | awk '/Content-Length:/ {print $2}'`
  echo $ret | sed 's/[^0-9]*//g'
}

#
# Print the requested part of the remote file
# param 1: url
# param 2: off
# param 3: len
# param 4: output file
#
function print_to_logfile() {
  curl --header "Range: bytes=$2-$3" -s $1 >> $4
}

#
# Clean the previous log file
#
function clean_logfile() {
  rm -f $1
}

# call validation
if [ $# -lt 1 ]; then
  echo "Syntax: remote-tail.sh <URL> [<logfile name>]"
  exit 1
fi

url=$1
offset=0
logfile=tmplog

if [ $# -eq 2 ]; then
  logfile=$2
fi

check_ranges_support $url
clean_logfile $logfile

len=`get_length $url`
off=$((len - offset))

until [ "$off" -gt "$len" ]; do
  len=`get_length $url`

  if [ "$off" -eq "$len" ]; then
    sleep 5 # we refresh every 5 seconds if no changes
  else
    sleep 1 # we refresh every second to not hammer too much the server
    print_to_logfile $url $off $len $logfile
  fi

  off=$len
done