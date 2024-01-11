# warehouse-rest-app
Simple CRUD REST API app for products and warehouses.


## Installation

Use [maven](https://maven.apache.org/download.cgi) to build the project.<br/>
Execute next command in the root folder of the project with the pom.xml file:

```
mvn clean install
```

## Using
Possible ways of using:<br/>
1. Run the main() method of the Application class in the IDE.
2. Execute next command in the folder containing the .jar file :

```
java -jar Warehouse_rest_app-1.0-SNAPSHOT.jar
```

The default startup using create-drop for database. If this configuration is not suitable, then there is a file [data.sql](src/main/resources/data.sql) for initialization.

The Swagger documentation is available at the URL: http://localhost:8080/swagger-ui.html.

UML database link diagram: [UML Diagram](src/main/resources/db/UML_Diagram.png)
