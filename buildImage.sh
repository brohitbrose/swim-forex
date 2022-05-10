./gradlew build
./gradlew dockerBuildImage
cd build/docker
docker build ./ -f ./Dockerfile -t $1
