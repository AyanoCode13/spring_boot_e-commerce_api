docker exec -it spring_boot_e-commerce_api-redis-1 redis-cli
FLUSHALL
FLUSHDB
KEYS *
MONITOR