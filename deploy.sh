# Build
./gradlew build -Pprofile=prod --exclude-task test

# Create Docker Image
docker build -t test .

# Tag Docker Image
docker tag test:latest eaexists/tripchemistry:host

# Push Docker Image to dockerhub repository
docker push eaexists/tripchemistry:host