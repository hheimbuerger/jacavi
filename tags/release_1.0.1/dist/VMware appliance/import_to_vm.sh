#!/bin/sh

# simple script for moving the changing data over to the development VM

echo Shutting down Apache
sudo apache2ctl stop
echo Removing old Trac and SVN repository
sudo rm -R /srv/trac/jacavi
sudo rm -R /srv/svn/jacavi
echo Untaring Trac and SVN repository
cd /srv/trac; sudo tar xzf /tmp/jacavi_trac.tgz
cd /srv/svn; sudo tar xzf /tmp/jacavi_svn.tgz
echo Importing Trac database
mysql -u root -p -h localhost trac_jacavi < /tmp/jacavi_db.sql
echo Fixing file/directory permissions
sudo chown www-data:www-data /srv/trac
sudo chown svn:svn /srv/svn
echo Restoring the VM versions of some configuration files
sudo cp /home/user/trac.ini /srv/trac/jacavi/conf/
sudo cp /home/user/svnserve.conf /srv/svn/jacavi/conf/
sudo cp /home/user/passwd /srv/svn/jacavi/conf/
sudo rm /srv/svn/jacavi/conf/*~
echo Removing the configuration files for the SVN hooks -- they aren\'t used in the VM
sudo rm /srv/svn/jacavi/hooks/*
sudo rm /srv/svn/jacavi/conf/hooks.conf
echo Restarting Apache
sudo apache2ctl start
echo To do:
echo   - Remove Trac passwords
echo   - Add TRAC_ADMIN
