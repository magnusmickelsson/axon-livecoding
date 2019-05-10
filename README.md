# axon-livecoding
Repository related to a talk on using Axon/CQRS/Event Sourcing. Tech: Spring Boot, Kotlin/Java mix, Axon, REST.

Associated presentation [here](Axon-for-dummies.odp) (Open Office format).

# How do I build it?

Prerequisities:

* Maven or a Maven-capable IDE.
* Java 11 (should work with 8+ but not tested) 

Run:

    mvn clean verify

... in the project directory.

If the build goes well, run the main class it.actuallyrocks.livecoding.LivecodingApp.
The server starts on port 8080 and dumps a lot of stuff in the log.

If you run into problems, feel free to let me know at magnus (dot) mickelsson (at) gmail (dot) com.

# REST API - admin

| Operation | Method | URL | Payload |
|-----------|--------|-----|---------|
|Open account|POST | http://127.0.0.1:8080/admin/account/ | {"id": "1", "owner": "Magnus"} |
|Deposit|POST|http://127.0.0.1:8080/admin/account/{account id}/deposit | {"id": "1", "amount": "190", "comment": "This is a comment", "source": "The box under my matress", "owner": "Magnus"}|
|Withdraw|POST|http://127.0.0.1:8080/admin/account/{account id}/withdraw | {"id": "1", "amount": "190", "comment": "This is a comment", "target": "The box under my matress", "owner": "Magnus"}|
|Close account|DELETE | http://127.0.0.1:8080/admin/account/{account id} | - |
|Get balance|GET|http://127.0.0.1:8080/admin/account/{account id}/balance|-|
|List all transactions|GET|http://127.0.0.1:8080/admin/account/{account id}/transactions|-|

Ensure you use JSON content types if needed, and UTF-8 encoding.

# REST API - query

http://127.0.0.1:8080/api/

It's a HATEOAS REST API, can be explored.

Hope you find it useful. Not that the code is NOT production quality, it's
for teaching/exporing/learning purposes ONLY.

Usually you wouldn't read an Axon aggregate directly.
