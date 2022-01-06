SHELL:=/bin/bash
include .env

.PHONY: all gradlew clean build changelog currentVersion markNextVersion release publish

all: test

gradlew:
	./gradlew wrapper --gradle-version=$(GRADLE_VERSION) --distribution-type=bin

clean:
	./gradlew clean

test:
	./gradlew test

build:
	./gradlew build

changelog:
	git log "$(CHANGELOG_START_TAG)...$(CHANGELOG_END_TAG)" \
    	--pretty=format:'* %s [View commit](http://github.com/benfortuna/mstor/commit/%H)' --reverse | grep -v Merge

currentVersion:
	./gradlew -q currentVersion

markNextVersion:
	NEXT_VERSION=$(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
	./gradlew markNextVersion -Prelease.version=$(NEXT_VERSION)

release: build
	./gradlew release

publish:
	./gradlew publish
