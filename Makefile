SHELL=/bin/bash

APP_HOME := $(shell pwd)
APP_BUILD_HOME := $(APP_HOME)/build
APP_LIB_HOME := $(APP_HOME)/build/lib

## Maven Setup
mvn/cleanInstall:
	mvn clean install

mvn/cleanInstallUpdate:
	mvn clean install -U

run/scraperService:
	java -Xmx1g \
		-Dfile.encoding=UTF-8 \
		-jar target/scraperService-0.0.1-SNAPSHOT.jar

brun/scraperService: mvn/cleanInstall run/scraperService