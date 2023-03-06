#

FROM ubuntu:java

COPY ./target/tictactoe-1.0.0.jar .

EXPOSE 8080

ENTRYPOINT java -jar tictactoe-1.0.0.jar

