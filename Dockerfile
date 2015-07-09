FROM java
ADD . /java/src/app
WORKDIR /java/src/app
CMD /java/src/app/grpc-java/gradlew installDist
CMD /java/src/app/grpc-java/examples/build/install/grpc-examples/bin/mapreduce-server
EXPOSE 10000
