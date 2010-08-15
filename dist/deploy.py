# Prerequisites:
#   - only tested on Windows
#   - NSIS 2.40 (or compatible) is installed and the makensis.exe is in the system PATH


import os
import os.path
import sys
import shutil

PLATFORMS = [
                #(PLATFORM, DISTNAME, BUILDNAME, USE_NSIS_INSTEAD_OF_TGZ)
                ('Linux x86 (tar/gz)', 'linux-gtk-x86', 'linux.gtk.x86', False),
                ('Linux x64 (tar/gz)', 'linux-gtk-x86-64', 'linux.gtk.x86_64', False),
                ('Windows x86 (NSIS installer)', 'win32-win32-x86', 'win32.win32.x86', True),
                ('Windows x64 (NSIS installer)', 'win32-win32-x86-64', 'win32.win32.x86_64', True),
                ('MacOS x86 (tar/gz)', 'macosx-cocoa-x86', 'macosx.cocoa.x86', False),
                ('MacOS x64 (tar/gz)', 'macosx-cocoa-x86-64', 'macosx.cocoa.x86_64', False),
            ]

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
        print '  - Deleting %s' % file
        os.unlink(file)

def rmtreeSilently(path):
    if(os.path.exists(path)):
        shutil.rmtree(path)

def tarGzipIt(path, temp, deploy):
    shutil.copyfile("7za.exe", path + "7za.exe")
    workingDir = os.getcwd()
    os.chdir(path)
    backtickThis("7za a -x!7za.exe -ttar %s *" % temp)
    backtickThis("7za a -x!7za.exe -tgzip ../../../deploy/%s %s" % (deploy, temp))
    os.chdir(workingDir)
    os.unlink(path + "7za.exe")

def buildDistributionName(platform, extension):
    return "JaCaVi_%s_v%s_r%s.%s" % (platform, version, buildNumber, extension)

#linuxTemp = "JaCaVi_linux-gtk-x86_v%s_r%s.tar" % (version, buildNumber)
#linuxDeploy = "JaCaVi_linux-gtk-x86_v%s_r%s.tgz" % (version, buildNumber)
#windowsDeploy = "JaCaVi_win32-win32-x86_v%s_r%s.exe" % (version, buildNumber)
#macosTemp = "JaCaVi_macosx-cocoa-x86_v%s_r%s.tar" % (version, buildNumber)
#macosDeploy = "JaCaVi_macosx-cocoa-x86_v%s_r%s.tgz" % (version, buildNumber)

print "Step 1: Removing old files"
#deleteSilently("bin/linux.gtk.x86/jacavi/" + linuxTemp)
#deleteSilently("deploy/" + linuxDeploy)
#deleteSilently("deploy/" + windowsDeploy)
#deleteSilently("bin/macosx.cocoa.x86/jacavi/" + macosTemp)
#deleteSilently("deploy/" + macosDeploy)
for (platform, distname, buildname, isNsis) in PLATFORMS:
    deleteSilently('deploy/' + buildDistributionName(distname, 'exe' if isNsis else 'tgz'))
for (platform, distname, buildname, isNsis) in PLATFORMS:
    if not isNsis:
        deleteSilently('bin/{}/jacavi/{}'.format(buildname, buildDistributionName(distname, 'tar')))

print "Step 2: Collecting data files"
for (platform, distname, buildname, isNsis) in PLATFORMS:
    platformDir = 'bin/{}/jacavi/'.format(buildname)
    for dataDir in ["agents", "cars", "tiles", "tracks"]:
        rmtreeSilently(platformDir + dataDir)
        backtickThis("svn export ../src/JACAVI/%s %s%s" % (dataDir, platformDir, dataDir))
    shutil.copy("../src/JACAVI/readme.txt", platformDir)
    shutil.copy("../src/JACAVI/epl-v10.html", platformDir)
# special case, Linux needs the libwiiuse.so separately
#if not os.path.exists("bin/linux.gtk.x86/jacavi/libs/"):
#    os.mkdir("bin/linux.gtk.x86/jacavi/libs/")
#shutil.copy("../src/JACAVI/libs/libwiiuse.so", "bin/linux.gtk.x86/jacavi/libs/")

print "Step 3: Creating archives"
for (platform, distname, buildname, isNsis) in PLATFORMS:
    print('  - {}'.format(platform))
    if isNsis:
        backtickThis('makensis /DVERSION="%s" /DOUTPUT_FILE="deploy/%s" "JaCaVi Windows Installer.nsi"' % (version, buildDistributionName(distname, 'exe')))
    else:
        tarGzipIt('bin/{}/jacavi/'.format(buildname), buildDistributionName(distname, 'tar'), buildDistributionName(distname, 'tgz'))

print "Step 4: Cleaning up"
for (platform, distname, buildname, isNsis) in PLATFORMS:
    if not isNsis:
        deleteSilently('bin/{}/jacavi/{}'.format(buildname, buildDistributionName(distname, 'tar')))
