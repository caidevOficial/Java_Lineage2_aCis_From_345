@echo off
title aCis geodata converter

java -Xmx512m -cp ./libs/*; net.sf.l2j.gameserver.geoengine.converter.GeoDataConverter

pause
