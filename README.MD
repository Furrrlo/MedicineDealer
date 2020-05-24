## Documentation
Check out the [wiki](../../wiki).

## Running
Make sure to have a `mysql` instance running on port `3306`.

Specify the database user and password in the file `database.properties` placed in the project root, 
following this format:
```properties
user=<user>
password=<password>
keyStore.password=<random password>
``` 

The user needs to have the appropriate perms both to create and manage a schema.

Run the gradle task `appRun`:
```
./gradlew appRun 
```
The schema will be automatically created, along with JAXB beans .

## Debugging
Follow the instructions from the running section then, instead of running `appRun`, use `appRunDebug`:
```
./gradlew appRunDebug 
```
You will then need to attach your IDE remote debugger on localhost:5005.
