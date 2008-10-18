import os
import os.path
import sys
import shutil


def backtickThis(command):
    output = os.popen(command).readlines()
    #print "".join(output)

def deleteSilently(file):
    if(os.path.exists(file)):
        os.unlink(file)

def rmtreeSilently(path):
    if(os.path.exists(path)):
        shutil.rmtree(path)


buildNumber = sys.argv[1]
linuxTemp = "JaCaVi_linux-gtk-x86_v1.0_r%s.tar" % buildNumber
linuxDeploy = "JaCaVi_linux-gtk-x86_v1.0_r%s.tgz" % buildNumber
windowsDeploy = "JaCaVi_win32-win32-x86_v1.0_r%s.exe" % buildNumber

print "Removing old files"
deleteSilently("bin/linux.gtk.x86/jacavi/" + linuxTemp)
deleteSilently("deploy/" + linuxDeploy)
deleteSilently("deploy/" + windowsDeploy)

print "Collecting data files"
for platformDir in ["bin/linux.gtk.x86/jacavi/", "bin/win32.win32.x86/jacavi/"]:
    for dataDir in ["agents", "cars", "tiles", "tracks"]:
        rmtreeSilently(platformDir + dataDir)
        backtickThis("svn export ../src/JACAVI/%s %s%s" % (dataDir, platformDir, dataDir))

print "Creating archives"
print "  - Linux (tar/gz)"
shutil.copyfile("7z.exe", "bin/linux.gtk.x86/jacavi/7z.exe")
shutil.copyfile("7z.dll", "bin/linux.gtk.x86/jacavi/7z.dll")
workingDir = os.getcwd()
os.chdir("bin/linux.gtk.x86/jacavi/")
backtickThis("7z a -x!7z.* -ttar %s *" % linuxTemp)
backtickThis("7z a -x!7z.* -tgzip ../../../deploy/%s %s" % (linuxDeploy, linuxTemp))
os.chdir(workingDir)
os.unlink("bin/linux.gtk.x86/jacavi/7z.exe")
os.unlink("bin/linux.gtk.x86/jacavi/7z.dll")
print "  - Windows (NSIS installer)"
backtickThis('makensis /DOUTPUT_FILE="deploy/%s" "JaCaVi Windows Installer.nsi"' % windowsDeploy)
print "  - MacOS (???)"

print "Cleaning up"
deleteSilently("bin/linux.gtk.x86/jacavi/" + linuxTemp)
