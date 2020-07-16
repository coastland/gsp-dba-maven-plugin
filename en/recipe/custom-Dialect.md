## Example of Dialect Class Customization

Describes an example of how to customize the handling of data for full-width and half-width spaces only.

As mentioned in the load-data of README.md, data of full-width and half-width spaces will be registered in the DB as null.
To change this configuration, create a class inheriting the dialect class of the DB used, write the conditions and configure the class to use the dialect class created in pom.
The following example shows how to register data with full-width spaces as null in OracleDB.

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

After customization, add a configuration to the GSP plugin of pom used in the project to import the class that was created.  
A configuration example is shown below.

```xml
<plugins>
  <plugin>
    <groupId>jp.co.tis.gsp</groupId>
    <artifactId>gsp-dba-maven-plugin</artifactId>
    <version>
      Version of gsp-dba-maven-plugin used
    </version>
    <executions>
      <execution>
        <id>load-data</id>
        <phase>pre-integration-test</phase>
        <goals>
          <goal>load-data</goal>
        </goals>
        <configuration>
          <optionalDialects>
            <!-- Specify with the fully qualified class name of the class created. -->
            <oracle>jp.co.tis.gsp.tools.dba.dialect.CustomOracleDialect</oracle>
          </optionalDialects>
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```

After adding the configuration, run the GSP plugin. Data with only half-width spaces will not be changed to null and will be registered in the DB as half-width spaces.

Note: Based on the library specification used to import the CSV file, leading and trailing half-width spaces in the data are ignored, enclose them in "" to register them.
