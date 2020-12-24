## Example of GenDialect Class Customization

This example describes how to change the correspondence when converting a Number type of Oracle database to a Java class.

Currently, number type of Oracle determines the Java type to which the number of digits and decimals are mapped. The default configuration is as given below:
- With decimal part: BigDecimal  
- Single digit: boolean  
- Less than 5 digits: Short  
- Less than 10 digits: Integer  
- Less than 19 digits: Long  
- 19 digits or more: BigInteger  

 (conditions at the top take precedence.)

Mapped in ExtendedOracleGenDialect, and when customization is required, create a class that inherits this class, write the conditions, and configure such that the class created in pom is loaded.
The Dialect will be stored in a jar to be created separately, refer to [Example of Dialect Class Customization](./custom-Dialect.md) for creating the jar to store the Dialect.
  
A customization example is given below. In the example, numbers less than 10 digits are mapped to integer, and numbers more than 19 digits are mapped to BigDecimal.

```java
public class CustomOracleGenDialect extends ExtendedOracleGenDialect {

    public CustomOracleGenDialect() {
        super();
        columnTypeMap.put("number", new OracleColumnType("number($p,$s)", BigDecimal.class) {
            @Override
            public Class<?> getAttributeClass(int length, int precision, int scale) {
                if (scale != 0) {
                    return BigDecimal.class;
                }
                if (precision < 10) {
                    return Integer.class;
                }
                if (precision < 19) {
                    return Long.class;
                }

                return BigDecimal.class;
            }
        });
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
        <id>generate-entity</id>
        <phase>generate-sources</phase>
        <goals>
          <goal>generate-entity</goal>
        </goals>
        <configuration>
          <genDialectClassName>
            <!-- Specify with the fully qualified class name of the class created. -->
            jp.co.tis.gsp.tools.dba.dialect.CustomOracleGenDialect
          </genDialectClassName>
        </configuration>
      </execution>
    </executions>
    <dependencies>
      <!-- Add the jar containing the Dialect to the dependencies. -->
      <dependency>
        <groupId>jp.co.tis.gsp.tools.dba.dialect</groupId>
        <artifactId>my-dialect</artifactId>
        <version>0.1.0</version>
      </dependency>
    </dependencies>
  </plugin>
</plugins>
```

After adding the configuration, run the GSP plugin. Entity is generated according to the customized rules.
