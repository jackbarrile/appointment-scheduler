# appointment-scheduler

## Design Considerations

- Although the requirements stated not to use a heavy framework, given my language of choice is Java, I decided to use Spring Boot due to all of the out-of-the-box design patterns the framework enforces, plus the Spring initialize ([https://start.spring.io/](https://start.spring.io/)) provides a wired up WebApp within seconds, meaning there was minimal time spent on configuration or setup
- I also decided to use lombok, which is the greatest Java library known to humankind as it eliminates most, if not all, boilerplate code via annotations
- Appointment was chosen as a domain since the access patterns are based on Appointments, making it abide by ReST principles
    - Additionally, a Member class would only have one member variable, UserId, so a Member class is unnecessary if using an Appointment domain, given the requirements
- I decided to apply business logic in the Controller itself. If the requirements were larger in scope, I would have considered creating a Service layer to house the business logic for re-usability and a larger separation of duties. However, given the small set of requirements, I decided collapsing the logic in the Controller itself made it easier to test and quicker to develop. In the future, refactoring and creating a Service layer is a matter of creating boilerplate code and copy-pasting
- I used a Map for storing Member Appointments and to keep track of Member Appointment dates as Maps provide quick writes for storing and quick reads for validation checks and retrieval
    - Given the simple requirements, I serialize the Map manually, although if the requirements were more complex, serializing a Model using Spring can be done without any additional code
- I decided to use an Integer as the UserId data type since it is the simplest choice and makes it easier to read in the URL
- I like to use verbose unit test names as it is clear what functionality breaks when that test fails
    - I also like to note that out of the box static scan analyzers (such as Sonar) tend to not scan unit tests as a method name this long would surely violate code style toll gates
    - The tests also create Members, given storage is not persistent
    - Note that the Controller class itself is tested, but not the Controller layer itself - Spring Boot comes with an embedded server which can also be used in transactional unit tests for fully testing the API, but that is overkill given the small set of requirements and time constraint
- A 409 is thrown when attempting to create an appointment on the same day as the request "Conflicts" with the current state of the Member's Appointment collection - i.e. it is attempting to add an Appointment for a Date where an appointment exists
    - All other HTTP codes should be straightforward

## API Documentation

- POST `/appointment`
    - Parameters
        - Body
            - userId - The Member's unique identifier, in Integer form
            - appointmentStartTime - The requested Appointment's start time, in YYYY-DD-MM HH:MM:SS format

            ```json
            {
            	"userId": 1,
            	"appointmentStartTime": "2021-09-09 12:00:00"
            }
            ```

    - Responses
        - 200

            ```json
            {
            	"appointmentId": 1,
            	"userId": 1,
            	"appointmentStartTime": "2021-09-09 12:00:00",
            	"appointmentEndTime": "2021-09-09 12:30:00"
            }
            ```

        - 400 - if invalid parameters are provided
        - 400 - if the DateTime format differs than expectation
        - 400 - if the DateTime provided is not on the hour or half-hour
        - 404 - if the Member associated with the userId is not found
        - 409 - if an Appointment already exists on the Date provided
- GET `/appointment/{userId}`
    - Parameters
        - Path parameters
            - userId - The Member's unique identifier, in Integer form
    - Responses
        - 200

            ```json
            {
            	"userId": 1,
            	"appointments": [
            		{
            			"appointmentStartTime": "2021-09-09 12:00:00",
            			"appointmentEndTime": "2021-09-09 12:30:00"
            		}
            	]
            }
            ```

        - 400 - if invalid parameters are provided
        - 404 - if the Member associated with the userId is not found

## Usage

>Note: the below Docker build commands take a bit of time due to dependency downloads. Given more time, I would have setup a Docker repo to cache the image.

### Running the Application

```bash
docker build -t springio/gs-spring-boot-docker . # this takes a bit since it needs to download dependencies
docker run -p 8080:8080 springio/gs-spring-boot-docker
```

### Running Tests

- Un-comment line 3 in the Dockerfile: `RUN mvn test` and run the same commands to start the container as if running the application
- Alternatively, if you would just like to run the tests, you may un-comment line 3 in the Dockerfile: `RUN mvn test`, and comment line 6 in the Dockerfile `ENTRYPOINT ["java","-jar","/app.jar"]` and then run the same commands to start the container as if running the application