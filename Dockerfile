FROM maven:3-openjdk-11 AS build

WORKDIR /usr/src/app

COPY pom.xml .
COPY src src/

COPY ./KeyCheckSMT/pom.xml ./KeyCheckSMT/pom.xml
COPY ./KeyCheckSMT/src ./KeyCheckSMT/src

RUN mvn clean package -DskipTests
# Also build SMT from here
RUN  mvn -f ./KeyCheckSMT/pom.xml clean package

FROM confluentinc/cp-kafka-connect:6.0.2-1-ubi8

COPY --from=build /usr/src/app/target/kafka-connect-target/usr/share/kafka-connect /plugins/
COPY --from=build /usr/src/app/KeyCheckSMT/target/*.jar /plugins/kafka-connect-ably/


ADD bin/docker-entrypoint.sh /bin/docker-entrypoint.sh
ENTRYPOINT ["/bin/docker-entrypoint.sh"]
