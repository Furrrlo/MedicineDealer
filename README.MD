## Debugging
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
The schema will be automatically created.