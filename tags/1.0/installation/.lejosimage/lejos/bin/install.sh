#!/bin/sh
# install lejos on the new file system
. ./funcs.sh
rootfs=/media/rootfs
bootfs=/media/bootfs
mkdir $bootfs 2> /dev/null
mkdir $rootfs 2> /dev/null
mount $1 $bootfs
mount $2 $rootfs
LEJOS_HOME=$rootfs/home/root/lejos
log "Prepare install"
if [ ! -e $bootfs/lejosimage.bz2 ]
then
  error "Missing lejos install files"
fi
if [ ! -e $bootfs/ejre* ]
then
  error "Missing jre install files"
fi
log "Deleting old files"
rm -rf $rootfs/*
log "Expand image"
tar -C $rootfs -jxf $bootfs/lejosimage.bz2
if [ ! -e $rootfs/lejosimage ]
then
  error "Missing lejos image"
fi
current=${PWD}
cd $rootfs/lejosimage
log "Start install"
./update_sdcard.sh $bootfs $rootfs $current
# début installation programme L2A2
log "Installing Palets"
cp $bootfs/InterfaceTextuelle.jar $rootfs/home/lejos/programs/
log "Setting up default program"
echo "lejos.default_program = /home/lejos/programs/InterfaceTextuelle.jar" >> $rootfs/home/root/lejos/settings.properties
echo "lejos.default_autoRun = ON" >> $rootfs/home/root/lejos/settings.properties
log "Replacing EV3Menu.jar with fixed default startup"
mv $rootfs/home/root/lejos/bin/utils/EV3Menu.jar $rootfs/home/root/lejos/bin/utils/EV3Menu.jar.old
mv $bootfs/GraphicStartup.jar $rootfs/home/root/lejos/bin/utils/EV3Menu.jar
# fin installation programme L2A2
log "Remove temp files"
cd $rootfs
rm -rf lejosimage
cd /
log "Configure boot"
mv $bootfs/boot.scr $bootfs/setup.scr
log "Sync disks"
sync
sync
log "Unmount disks"
umount $rootfs
umount $bootfs
