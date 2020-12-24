## Example of Dialect Class Customization

Describes an example of how to customize the handling of data for full-width and half-width spaces only.

As mentioned in the load-data of README.md, data of full-width and half-width spaces will be registered in the DB as null.
To change this configuration, create a class inheriting the dialect class of the DB used, write the conditions and configure the class to use the dialect class created in pom.
The following example shows how to register data with full-width spaces as null in OracleDB.

### Step
Follow the steps below.
1. Create a jar with Dialect
2. Change the configuration to use the Dialect created.

Each step is described below.

#### Create a jar with Dialect
* Create a new project in Maven to deploy the Dialect.
  ```
  $ mvn archetype:generate -Dfilter=org.apache.maven.archetypes:maven-archetype-quickstart
  ```
  Creating a project is an interactive process.
  In this document, it is assumed that the following is entered.
  ```
  (omitted)
  Define value for property 'groupId': jp.co.tis.gsp.tools.dba.dialect
  Define value for property 'artifactId': my-dialect
  Define value for property 'version' 1.0-SNAPSHOT: : 0.1.0
  Define value for property 'package' jp.co.tis.gsp.tools.dba.dialect: : jp.co.tis.gsp.tools.dba.dialect
  Confirm properties configuration:
  groupId: jp.co.tis.gsp.tools.dba.dialect
  artifactId: my-dialect
  version: 0.1.0
  package: jp.co.tis.gsp.tools.dba.dialect
  Y: : Y
  (omitted)
  ```
* Remove the following files from the generated files.
  * App.java
  * AppTest.java
* Modify the generated pom.xml.
  The parts to be modified are as follows.
  * Set maven.compiler.source and maven.compiler.target to 1.6 or higher.
  * Add the version of GSP in use to the dependencies.
  An example is given below.
  ```xml
    <!-- omitted -->
    <properties>
      <!-- omitted -->
      <!-- Specify 1.6 or higher -->
      <maven.compiler.source>1.6</maven.compiler.source>
      <maven.compiler.target>1.6</maven.compiler.target>
    </properties>
    <!-- omitted -->
    <dependencies>
      <dependency>
        <!-- Add the GSP in use -->
        <groupId>jp.co.tis.gsp</groupId>
        <artifactId>gsp-dba-maven-plugin</artifactId>
        <version>4.4.0</version>
        <scope>provided</scope>
      </dependency>
      <!-- omitted -->
    <dependencies>
    <!-- omitted -->
  ```

* Create a **CustomOracleDialect** that extends **jp.co.tis.gsp.tools.dba.dialect.OracleDialect**.
  ```java
  public class CustomOracleDialect extends OracleDialect{
      
      public void setObjectInStmt(PreparedStatement stmt, int parameterIndex, String value, int sqlType) throws SQLException {
          
          Pattern p = Pattern.compile("^　*$");
          Matcher m = p.matcher(value);
          
          if(sqlType == UN_USABLE_TYPE) {
              stmt.setNull(parameterIndex, Types.NULL);
          } else if(m.matches()) {
              stmt.setNull(parameterIndex, sqlType);
          } else {
              stmt.setObject(parameterIndex, value, sqlType);
          }
      }
  }
  ```

* Build and install to the repository.
  ```
  $ mvn install
  ```

#### Change the configuration to use the Dialect created.
* Add a setting to the GSP plugin in the pom of the project to be applied so that it will load the class created.
Here is an example of the configuration.
  ```xml
  <!-- ommit -->
  <plugins>
    <!-- omitted -->
    <plugin>
      <groupId>jp.co.tis.gsp</groupId>
      <artifactId>gsp-dba-maven-plugin</artifactId>
      <version>
        The version of gsp-dba-maven-plugin to use
      </version>
      <executions>
        <execution>
          <id>load-data</id>
          <phase>pre-integration-test</phase>
          <goals>
            <goal>load-data</goal>
          </goals>
          <configuration>
            <!-- omitted -->
            <optionalDialects>
              <!-- Specify it by the fully qualified class name of the class created. -->
              <oracle>jp.co.tis.gsp.tools.dba.dialect.CustomOracleDialect</oracle>
            </optionalDialects>
          </configuration>
        </execution>
      </executions>
      <dependencies>
        <!-- Add the jar with Dialect to the dependencies. -->
        <dependency>
          <groupId>jp.co.tis.gsp.tools.dba.dialect</groupId>
          <artifactId>my-dialect</artifactId>
          <version>0.1.0</version>
        </dependency>
      </dependencies>
    </plugin>
  </plugins>
  ```

  After adding the settings, please run the GSP plugin. Data with only single-byte spaces will not become null, but will be registered in the DB as single-byte spaces.

  Note: Due to the specification of the library used to read CSV, half-width spaces before and after the data are ignored, so please enclose half-width spaces with "" when registering them.

