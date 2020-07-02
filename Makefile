.PHONY: build tests docs deploy checkdeps repl replincs runtests build-min-js clean docker-build docker-push faux-ard-container chipmunkip deps-up deps-down-d login 

.DEFAULT_GOAL := build
VERSION    := `./bin/version`
IMAGE      := eroslab.cr.usgs.gov:4567/lcmap/mastodon
BRANCH     := $(or $(CI_COMMIT_REF_NAME),`git rev-parse --abbrev-ref HEAD`)
BRANCH     := $(shell echo $(BRANCH) | tr / -)
SHORT_HASH := `git rev-parse --short HEAD`
BASE_TAG   := $(IMAGE):$(BRANCH)-$(VERSION)-$(SHORT_HASH)-base
TAG        := $(IMAGE):$(BRANCH)-$(VERSION)-$(SHORT_HASH)

# LCMAP Standard Makefile targets.  Do not remove.

build: login
	@docker build -t $(TAG) \
                      --rm=true \
                      --compress $(PWD)

tests:  
	@docker run --rm \
                    --entrypoint /app/bin/lein-test-entrypoint.sh \
                    $(TAG)

docs:
	@lein codox

deploy: login
	docker push $(TAG)


checkdeps:
	lein deps

repl:
	@echo "use 'lein figwheel'"

replincs:
	@echo "(require '[mastodon.http :as mhttp] '[mastodon.core :as mcore] '[mastodon.data :as testdata])"

runtests:
	lein test	

clean:
	@rm -rf target/
	@rm -rf docs/

faux-ard-container:
	cd resources/nginx; docker build -t faux-ard .

chipmunkip:
	docker inspect -f "{{ .NetworkSettings.Networks.resources_lcmap_chipmunk.IPAddress }}" ${NAME}

deps-up-d:
	docker-compose -f resources/docker-compose.yml up -d cassandra
	sleep 20
	docker-compose -f resources/docker-compose.yml up -d chipmunk
	sleep 10
	bin/seed
	docker-compose -f resources/docker-compose.yml up -d nemo

deps-down:
	docker-compose -f resources/docker-compose.yml down

login:
	@$(if $(and $(CI_REGISTRY_USER), $(CI_REGISTRY_PASSWORD)), \
          docker login  -u $(CI_REGISTRY_USER) \
                        -p $(CI_REGISTRY_PASSWORD) \
                         $(CI_REGISTRY), \
          docker login eroslab.cr.usgs.gov:4567)



