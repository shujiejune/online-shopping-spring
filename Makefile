# Start the database container
db-up:
	docker compose up -d

# Stop the database container
db-down:
	docker compose down

# DANGER: Stop DB and delete all stored data (resets the password)
clean-db:
	docker compose down -v

# Clean build and run the Spring Boot application
run:
	@set -a; [ -f .env ] && . ./.env; set +a; \
	mvn clean install -DskipTests && mvn spring-boot:run

# Single command to spin up DB and then run the app
up:
	$(MAKE) db-up
	@echo "Waiting 10 seconds for MySQL to initialize..."
	@sleep 10
	$(MAKE) run