# Build
./gradlew build -Pprofile=prod --exclude-task test

# Create Docker Image
docker build -t test .

# Tag Docker Image
docker tag test:latest eaexists/tripchemistry:0.1

# Push Docker Image to dockerhub repository
docker push eaexists/tripchemistry:0.1