#!/bin/bash
# This script will copy the Google Auth Token into the file dataMessagingToken.txt

echo "Type the Google password for the user '3v3nt0rama@googlemail.com' followed by [ENTER]:"
read -s password

tempfoo=`basename $0`
TMPFILE=`mktemp /tmp/${tempfoo}.XXXXXX` || exit 1

curl https://www.google.com/accounts/ClientLogin \
-d Email=3v3nt0rama@googlemail.com \
-d Passwd=$password \
-d accountType=GOOGLE \
-d source=GA-curl-tester \
-d service=ac2dm -s  > $TMPFILE

AUTH_TOKEN=`sed '/^\#/d' $TMPFILE | grep 'Auth'  | tail -n 1 | cut -d "=" -f2-`

echo
echo "Google Auth Token:"
echo $AUTH_TOKEN

echo $AUTH_TOKEN > `dirname $0`/../war/dataMessagingToken.txt

rm $TMPFILE
