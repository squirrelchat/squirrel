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
