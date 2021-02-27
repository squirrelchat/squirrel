workdir := $(shell pwd)
compose-test := docker-compose -p squirrel_test -f $(workdir)/docker-compose.test.yml

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

.PHONY: upgrade-deps
upgrade-deps:
	cd packages/api; pnpx ncu -u;
	cd packages/gateway; pnpx ncu -u;
	pnpm i

.PHONY: upgrade-deps-dry
upgrade-deps-dry:
	cd packages/api; pnpx ncu
	cd packages/gateway; pnpx ncu

# Those methods are useful to use as one-liners in CI envs
.PHONY: test-dockerized
test-dockerized:
	$(compose-test) up -d
	$(compose-test) exec -T -w /opt/squirrel node pnpm i
	$(compose-test) exec -T -w /opt/squirrel/packages/api node pnpm run test
	$(compose-test) exec -T -w /opt/squirrel/packages/gateway node pnpm run test
	$(compose-test) down --volumes

.PHONY: lint-dockerized
lint-dockerized:
	# todo
