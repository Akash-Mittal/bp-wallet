# BP-Wallet

Parent Child based Gradle Project With Spring Boot and GRPC.

### Planned Features.

* The Proto Generation need to be done in a Seperate Project.
* Although Client Should be able to generate its own stub from ``` .proto ``` file , for the sake of  Loose Coupling Client and Server Need to add in proto proejct in  ``` build.gradle ```
* The Server and Client are both based on Spring boot and uses grpc wrapper of spring boot that supports ``` GrpcClient ``` and ``` GrpcServer ``` annotations.
* The Server will not expose Rest API's it will be called via ``` stub ``` 
* Server will be Dockerized.
* Server Will Have peresistance `mysql` running in its own ```docker``` container.
* Client Rest API should return the Status of Excution and Stats like ``` QPS```, ``` Failed RPCs ``` , ``` Sucess RPCs```

### Assumptions:

* About database schema has been kept Simple with One table only.
* The actual applicable schema is included.
* Server Handles Transaction is based on First Come First Serve.
* Wallet Client can be accessed CLI and Swagger.
	SWAGGER URL: http://localhost:8080/swagger-ui.html#/
	CLI Command: Not Implemented
* About "Make sure the client exits when all rounds has been executed."
	Client Runs on Spring Boot Application and has also exposed SWAGGER API for Testing.
	Client Will process the Request and Wait for next.
* Technologies
	Java
	gRPC
	MySQL or PostgreSQL
	Gradle
	JUnit
	SLF4J
	Docker
	Hibernate
	Spring and Spring Boot.
	Swagger
* Junits Coverage of > 80% is out of scope.
* The docker container should be run via Compose/Kubernetes - out of scope.
* The Bp-wallet application doesnot retry failed transactions.
* Some startup 

### How to run the client and the server (run gradlew.bat in root project. first)

#### LOCAL(bp-wallet-mysql)
	If local/remote instance is running nothing else to do.
	Replace the IP/PORT/SCHEMA in application.yaml in bp-wallet-server.	
#### JAVA(bp-wallet-server)
	java -jar -Dspring.profiles.active=development bp-wallet-server\build\libs\bp-wallet-server-1.0.0.jar
	Make Sure DB is Up and its properties are configured in applicable.yaml.
#### JAVA(bp-wallet-client)
	java -jar  bp-wallet-client\build\libs\bp-wallet-client-1.0.0.jar
	Access Client using- http://localhost:8080/swagger-ui.html#/wallet-client-controller/executeUsingPOST
#### Docker(bp-wallet-mysql)
	Run start-grpc-mysql script in root folder it takes 5-10 Minutes on my machine.
	-> Please execute following command to get the IP of docker-machine.
	bash#docker-machine -ip 
	Replace this ip in application.yaml in bp-wallet-server.

	
* Time Needed to start the Apps - 5 Minutes	
(JAVA(bp-wallet-client) + JAVA(bp-wallet-server + H2))
* Time Needed to start the Apps - 5-10 Minutes With Some configuration Changes
(Docker(bp-wallet-mysql) + JAVA(bp-wallet-client) + JAVA(bp-wallet-server))
((bp-wallet-mysql) + JAVA(bp-wallet-client) + JAVA(bp-wallet-server))
* Time Needed to start the Apps -5-10 Minutes With Some configuration Changes	
(Docker(bp-wallet-mysql) + Docker(bp-wallet-client) + Docker(bp-wallet-server))


### Important choices in your solution

* The Whole Structure of the BP-wallet application is loosely coupled 
* Each Client,Server Instances are scalable,
* Docker Instances make it possible to enable containerization.
* The Performance Tuning Options are kept configurable.
* Server Side - Connection Pooling (That Depends on Given Deployment Platform)
* Client Side - Task Executer is Configurable with Concurrent Worker Threads.
* The bp-wallet-proto is shared with Client and Server.
* Not to user Synchronization or any code level locking on DB tansaction as there can be multiple
	instances running.
* Optimistic Locking is implemented (Which can also be configured for retry mechanism[Diables for Now])
* User Registration: User is registered if he is  non existent while making deposit.
* The user ID is Taken from Number of Users param (userID:1 for numberOfuser=1,userID:1,userID:2 for numberOfuser=2)  
* Logging has been minimized via debug for improved performace.

### Transactions Per Seconds.

This is a difficult question as there are many scenarios, and requires performance tuning.

#### Per Transaction:
	Application Variant.
	Single user Making 10 Deposit - 10 Blocking Calls
	Single user Making 5 Withdraws 5 Deposit
	Single user Making 4 Withdraws 4 Deposit 2 Balance
#### Database Variant:
	Embedded H2 DB.
	MYSQL DB.
	Dockerized MYSQL DB.
	Over the Cloud.
#### Database Connection Pooling Variant:
	HIKARI:
	Apache:
#### Platform Variant:
	Wallet Server Deployed Local Machine -(4 GB RAM 4 Cores i5)
	Wallet Server Deployed on Cloud 

### Deduction:

* K Users Should Run Concurrently.
* Each Kth User Can Spawn N number of New Concurrent Requests.
* Each Nth Request can Spawn M number of Concurrent Operations(Rounds)
* Total Number of threads will K x M x N (X Transaction Per Round avg 8)

### Future Aspirations.
* Cloud Ready.
* Load Balancer.
* Service Discovery.
* Authentication.
* UI Client.
* Caching enabled Enities - if needed.

