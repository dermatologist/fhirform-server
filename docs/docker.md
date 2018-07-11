docker build -t beapen/fhirform:100718 .
docker run -d --name fhirform -p 8091:8080 beapen/fhirform:100718