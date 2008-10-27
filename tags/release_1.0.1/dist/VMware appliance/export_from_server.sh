#!/bin/sh

# simple script for moving the changing data over to the development VM

echo Taring Trac and SVN repository
tar cz -C /srv/trac -f jacavi_trac.tgz jacavi
tar cz -C /srv/svn -f jacavi_svn.tgz jacavi
echo Dumping Trac database
mysqldump -u root -p trac_jacavi > jacavi_db.sql
echo Uploading files via SCP to hheimbuerger.homeip.net
scp jacavi_*.* user@hheimbuerger.homeip.net:/tmp
