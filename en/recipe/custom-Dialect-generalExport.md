## Implementation of General Mode Export/Import

- OracleDialect provided by gsp-dba-maven-plugin exports the schema using the export function (expdp) unique to DBMS.  
This section shows how to create a dialect to export in the general mode using DDL files and CSV data instead of DBMS-specific exports.
The Dialect will be stored in a jar to be created separately, refer to [Example of Dialect Class Customization](./custom-Dialect.md) for creating the jar to store the Dialect.

### Step

1. Create **Oracle12cDialect** that inherits **jp.co.tis.gsp.tools.dba.dialect.Dialect**.
    ```java
    package jp.co.tis.gsp.tools.dba.dialect;

    public class Oracle12cDialect extends Dialect {

    }
    ```

2. Copy all the implementation code of **jp.co.tis.gsp.tools.dba.dialect.OracleDialect** to **Oracle12cDialect**.
    ```java
    package jp.co.tis.gsp.tools.dba.dialect;

    import java.io.BufferedReader;
    import java.io.File;
    import java.io.InputStreamReader;
    import java.nio.charset.Charset;
    ...

    /**
     * copy&paste from jp.co.tis.gsp.tools.dba.dialect.OracleDialect
     */
    public class Oracle12cDialect extends Dialect {

        private static final List<String> USABLE_TYPE_NAMES = new ArrayList<String>();

        static {
            USABLE_TYPE_NAMES.add("CHAR");
            USABLE_TYPE_NAMES.add("DATE");
    ....
    ```
    
3. Modify the constructor of Oracle12cDialect.
    ```java
    //public OracleDialect() {
    public Oracle12cDialect() {
        GenDialectRegistry.deregister(
                org.seasar.extension.jdbc.dialect.OracleDialect.class
        );
        GenDialectRegistry.register(
                org.seasar.extension.jdbc.dialect.OracleDialect.class,
                new ExtendedOracleGenDialect()
        );
    }
    ```
    
4. Comment out the exportSchema() and importSchema() of Oracle12cDialect.
    ```java
    /***
    Call jp.co.tis.gsp.tools.dba.dialect.Dialect#exportSchema()

      	@Override
      	public void exportSchema(ExportParams params) throws MojoExecutionException {
      	    ...
      	}
    ***/

    /***
    Call jp.co.tis.gsp.tools.dba.dialect.Dialect#importSchema()

    	@Override
    	public void importSchema(ImportParams params) throws MojoExecutionException{
            ...
        }
    ***/
    ```

5. Modify plugin definition
Configure the following 
- Specify the location of DDL and additional DDL folders to package in the parameters ddlDirectory and extraDdlDirectory.
- Add a dependency on the jar where Dialect exists.
    ```xml
      <plugin>
        <groupId>jp.co.tis.gsp</groupId>
        <artifactId>gsp-dba-maven-plugin</artifactId>
        <version>4.0.0</version>
        <configuration>
          ...
        </configuration>
        <executions>
          <execution>
            <id>export-schema</id>
            <phase>install</phase>
            <goals>
              <goal>export-schema</goal>
            </goals>
            <configuration>
              <!-- Specify the location of the DDL and additional DDL folders to be packaged -->
              <ddlDirectory>target/ddl</ddlDirectory>
              <extraDdlDirectory>src/main/resources/extraDDL</extraDdlDirectory>
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
    ```
