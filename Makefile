workdir := $(shell pwd)
compose-test := docker-compose -p squirrel_test -f $(workdir)/docker-compose.test.yml
# todo: better way to get db dsn?
migrate := .bin/migrate -database cassandra://127.0.0.1 -path migrations

.PHONY: dev
dev:
	docker-compose up -d

.PHONY: test
test:
	cd packages/api; pnpm run test
	cd packages/gateway; pnpm run test

.PHONY: lint
lint:
	# todo

## Deps
.PHONY: upgrade-deps
upgrade-deps:
	cd packages/api; pnpx ncu -u;
	cd packages/gateway; pnpx ncu -u;
	pnpm i

.PHONY: upgrade-deps-dry
upgrade-deps-dry:
	cd packages/api; pnpx ncu
	cd packages/gateway; pnpx ncu

## Database
.PHONY: migration
migration: .bin/migrate
	$(migrate) create -ext cql -dir migrations $(shell bash -c 'read -p "Migration name: " name; echo $$name')

.PHONY: migrate
migrate: .bin/migrate
	$(migrate) up

.PHONY: rollback
rollback: .bin/migrate
	$(migrate) down 1

## Tools/misc
.bin/migrate:
	# todo: account for win/macos installs
	curl -sL https://github.com/golang-migrate/migrate/releases/download/v4.14.1/migrate.linux-amd64.tar.gz | tar -xz
	mv migrate.linux-amd64 .bin/migrate
