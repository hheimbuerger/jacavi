# Prerequisites:
#   - only tested on Windows
#   - NSIS 2.40 (or compatible) is installed and the makensis.exe is in the system PATH


import os
import os.path
import sys
import shutil


if len(sys.argv) < 3:
    print "Syntax: python deploy.py <version string> <build number>"
    print "Example: python deploy.py 1.0.0 666"
    sys.exit(1)

version = sys.argv[1]
buildNumber = sys.argv[2]

print "Deploying with version number '%s' and build number '%s'." % (version, buildNumber)
print


def backtickThis(command):
    output = os.popen(command).readlines()
    #print "".join(output)

def deleteSilently(file):
    if(os.path.exists(file)):
        os.unlink(file)

def rmtreeSilently(path):
    if(os.path.exists(path)):
        shutil.rmtree(path)

def tarGzipIt(path, temp, deploy):
    shutil.copyfile("7z.exe", path + "7z.exe")
    shutil.copyfile("7z.dll", path + "7z.dll")
    workingDir = os.getcwd()
    os.chdir(path)
    backtickThis("7z a -x!7z.* -ttar %s *" % temp)
    backtickThis("7z a -x!7z.* -tgzip ../../../deploy/%s %s" % (deploy, temp))
    os.chdir(workingDir)
    os.unlink(path + "7z.exe")
    os.unlink(path + "7z.dll")


linuxTemp = "JaCaVi_linux-gtk-x86_v%s_r%s.tar" % (version, buildNumber)
linuxDeploy = "JaCaVi_linux-gtk-x86_v%s_r%s.tgz" % (version, buildNumber)
windowsDeploy = "JaCaVi_win32-win32-x86_v%s_r%s.exe" % (version, buildNumber)
macosTemp = "JaCaVi_macosx-carbon-x86_v%s_r%s.tar" % (version, buildNumber)
macosDeploy = "JaCaVi_macosx-carbon-x86_v%s_r%s.tgz" % (version, buildNumber)

print "Step 1: Removing old files"
deleteSilently("bin/linux.gtk.x86/jacavi/" + linuxTemp)
deleteSilently("deploy/" + linuxDeploy)
deleteSilently("deploy/" + windowsDeploy)
deleteSilently("bin/macosx.carbon.x86/jacavi/" + macosTemp)
deleteSilently("deploy/" + macosDeploy)

print "Step 2: Collecting data files"
for platformDir in ["bin/linux.gtk.x86/jacavi/", "bin/win32.win32.x86/jacavi/", "bin/macosx.carbon.x86/jacavi/"]:
    for dataDir in ["agents", "cars", "tiles", "tracks"]:
        rmtreeSilently(platformDir + dataDir)
        backtickThis("svn export ../src/JACAVI/%s %s%s" % (dataDir, platformDir, dataDir))
    shutil.copy("../src/JACAVI/readme.txt", platformDir)
    shutil.copy("../src/JACAVI/epl-v10.html", platformDir)
# special case, Linux needs the libwiiuse.so separately
if not os.path.exists("bin/linux.gtk.x86/jacavi/libs/"):
    os.mkdir("bin/linux.gtk.x86/jacavi/libs/")
shutil.copy("../src/JACAVI/libs/libwiiuse.so", "bin/linux.gtk.x86/jacavi/libs/")

print "Step 3: Creating archives"
print "  - Linux (tar/gz)"
tarGzipIt("bin/linux.gtk.x86/jacavi/", linuxTemp, linuxDeploy)
print "  - Windows (NSIS installer)"
backtickThis('makensis /DVERSION="%s" /DOUTPUT_FILE="deploy/%s" "JaCaVi Windows Installer.nsi"' % (version, windowsDeploy))
print "  - MacOS (tar/gz)"
tarGzipIt("bin/macosx.carbon.x86/jacavi/", macosTemp, macosDeploy)

print "Step 4: Cleaning up"
deleteSilently("bin/linux.gtk.x86/jacavi/" + linuxTemp)
deleteSilently("bin/macosx.carbon.x86/jacavi/" + macosTemp)
