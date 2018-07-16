docker stop fhirform
docker rm fhirform
docker rmi beapen/fhirform:160718
docker build -t beapen/fhirform:160718 .
docker run -d --name fhirform -p 8091:8080 beapen/fhirform:160718