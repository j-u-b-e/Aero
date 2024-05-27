# Flight registration application

Flight registration application built using Kotlin and SpringBoot. Using H2 as in memory database.

## Running locally

Execute main method from IDE in AeroApplication class.

## Running on Docker

Execute below commands to build and run application.

```shell
docker build -t aero-app . 
docker run -p 8080:8080 aero-app 
```

## SWAGGER

Run Swagger locally

```shell
http://localhost:8080/swagger-ui/index.html
```

## Local testing
1. Open swagger.
2. Create a flight with POST request. Flight will be in Scheduled state. 
3. Execute PUT request to register flight departure. Only flight number is required. Flight will be in Departed state.
4. Execute PUT request to register flight arrival.
   1. Flight will be in Arrived state. 
   2. Flight will be added to a list of available flights.
   3. One of terminal workers will pick up the flight.
   4. For simplicity and testing there are 3 terminal workers which run in Coroutines.
   5. After getting a flight terminal worker will spend 10 seconds to process the flight. 
   After processing terminal worker will clear the terminal and be available for other flights.
