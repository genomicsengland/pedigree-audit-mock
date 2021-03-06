FROM openjdk:8-jdk-alpine
VOLUME /tmp

#COPY ./*.crt /usr/local/share/ca-certificates/
#RUN update-ca-certificates

#Datadog APM
RUN mkdir datadog
RUN  wget -O /datadog/dd-java-agent.jar "https://repo1.maven.org/maven2/com/datadoghq/dd-java-agent/0.43.0/dd-java-agent-0.43.0.jar"

RUN  chgrp -R 0 /datadog && chmod -R g+rwX /datadog

COPY target/pedigree-audit-?.?.?.jar pedigree-audit.jar
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:datadog/dd-java-agent.jar", "-Dcom.sun.management.jmxremote","-Dcom.sun.management.jmxremote.port=7199","-Dcom.sun.management.jmxremote.authenticate=false","-Dcom.sun.management.jmxremote.ssl=false", "-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=prod","-jar","pedigree-audit.jar"]