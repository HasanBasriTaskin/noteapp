#!/bin/bash
# Script to build and run the Docker containers for the Note App

# Create necessary directories if they don't exist
mkdir -p sql

# Check if the init.sql file exists
if [ ! -f sql/init.sql ]; then
    echo "Warning: sql/init.sql not found. DB initialization may fail."
    echo "Please make sure the SQL initialization file exists."
fi

# Build and start the containers in detached mode
docker-compose up -d

echo "===================================================="
echo "Note App containers started!"
echo "===================================================="
echo "MySQL Database: localhost:3306"
echo "Backend API: localhost:8080"
echo "PHPMyAdmin: http://localhost:8081"
echo "===================================================="
echo "To stop the containers, run: docker-compose down"
echo "To view logs, run: docker-compose logs -f"