# grpc-spring-boot-demo
Parent Child based Gradle Project With Spring Boot and GRPC.

Planned Features.

* The Proto Generation need to be done in a Seperate Project.
* Although Client Should be able to generate its own stup from ``` .proto ``` file , for the sake of  Loose Coupling Client and Server Need to add in proto proejct in  ``` build.gradle ```
* The Server and Client are both based on Spring boot and uses grpc wrapper of spring boot that supports ``` GrpcClient ``` and ``` GrpcServer ``` annotations.
* The Server will not expose Rest API's it will be called via ``` stub ``` 
* Server will be Dockerized.
* Server Will Have peresistance `mysql` running in its own ```docker``` container.
* Client Rest API should return the Status of Excution and Stats like ``` QPS```, ``` Failed RPCs ``` , ``` Sucess RPCs```
