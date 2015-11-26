#!/bin/bash
adb shell mount -o rw,remount rootfs /
adb shell chmod 777 /sdcard
adb shell mkdir -p /sdcard/Android/data/com.chromium.fontinstaller/cache/AleoFontPack/
adb push travis/AleoFontPack /sdcard/Android/data/com.chromium.fontinstaller/cache/AleoFontPack