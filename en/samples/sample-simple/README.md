# Sample that Works with Minimum Configuration

Sample GSP plugin using H2 with minimum configuration (items not required are set to default).
The following can be executed.

* [DDL generation](#generate-ddl)
* [DDL execution](#execute-ddl)
* [Entity generation](#generate-entity)
* [Data registration](#load-data)

<a name="generate-ddl"></a>

## DDL generation

Generate DDL from an EDM file.
Imports the configured EDM file and outputs DDL under target/ddl (default configuration).

|Parameter |Configuration value|
|:-|:-|
|erdFile| src/main/resources/entity/model.edm |

The edm file in this sample has only a parent table and its dependent child table.

Execution command

````
  mvn gsp-dba:generate-ddl
````

<a name="execute-ddl"></a>

## DDL execution

Executes the DDL generated by [DDL generation](#generate-ddl) to create the table in the DB.

Execution command

````
  mvn gsp-dba:execute-ddl
````


<a name="generate-entity"></a>

## Entity generation

Creates an entity from the table created by [DDL execution](#execute-ddl).
The entity is generated as a package of the configured entity under target/generated-sources/entity (default configuration).

|Parameter |Configuration value|
|:-|:-|
|rootPackage| gsp.sample |

Execution command

````
  mvn gsp-dba:generate-entity
````

<a name="load-data"></a>

## Data registration

Registers the data described in the CSV file in DB.
The CSV file located under the configured CSV directory is read and data is registered in the DB.

|Parameter |Configuration value|
|:-|:-|
|dataDirectory| src/test/resources/testdata |

Execution command

````
  mvn gsp-dba:load-data
````


