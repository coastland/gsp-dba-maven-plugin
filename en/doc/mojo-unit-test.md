gsp-dba-maven-plugin Test
========

## Overview

This section describes the unit test prepared by gsp-dba-maven-plugin.

## Description

* Style to be executed for each MOJO using [harness](https://maven.apache.org/plugin-testing/maven-plugin-testing-harness/) 
* Provides pom.xml for each test case and define parameters in the xml file.
* The test case model consists of:
```
mojo(goal) - Test case (test method) - Target DB(db2, h2, etc..)
```
* The `mvn clean install` does not run any tests.
* [JPA simple verification](#jpa-simple-verification)
    * A verification with simple JPA (Eclpselink) using the entities generated by integration-test.

## SetUp
1. Modify [jdbc_test.properties](../../src/test/resources/jdbc_test.properties) and DB connections.
    * Since the test is executed using this connection information, modify jdbc_test.properties or match by changing DB.
    * General users are not required. However, prepare DB users as OS users since it is necessary for DB2.
1. Define the dependency of third party JDBC driver in [pom.xml](../../pom.xml)
    * JDBC drivers for Oracle, DB2, and SQLServer are not available in Maven Central.  
      Obtain the jar of JDBC driver and place in the local repository and define dependency relationship in pom.xml.


        * Example of a local installation (change the version as is appropriate)
    
        ```shell
        mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.2.0 -Dpackaging=jar
        mvn install:install-file -Dfile=db2jcc4.jar -DgroupId=com.ibm -DartifactId=db2jcc4 -Dversion=9.7.200.358 -Dpackaging=jar
        mvn install:install-file -Dfile=sqljdbc4.jar -DgroupId=com.microsoft -DartifactId=sqljdbc4 -Dversion=4.0 -Dpackaging=jar
        ```
        
        * Example of dependency definition to be added to pom.xml
        
        ```xml:pom.xml
        <dependency>
            <groupId>com.ibm</groupId>
            <artifactId>db2jcc4</artifactId>
            <version>9.7.200.358</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.oracle</groupId>
            <artifactId>ojdbc6</artifactId>
            <version>11.2.0.2.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.microsoft</groupId>
            <artifactId>sqljdbc4</artifactId>
            <version>4.0</version>
            <scope>test</scope>
        </dependency>
        ```

## Usage
```
mvn -P all_test clean integration-test site
```
It takes about 30 minutes to complete.  
Mojo and simple JPA tests are executed.

## TestCase Level 1

1. [Test resource](../../src/test/resources/jp/co/tis/gsp/tools/dba/mojo)を眺める。
    * Each Mojo has a folder.
    * Mojo has a folder for each test case.
    * A DB name folder is present in the test case folder.
    * The file pom.xml is in the DB name folder.
    * pom.xml specifies the parameters needed for the goal.
2. [Test class and method](../../src/test/java/jp/co/tis/gsp/tools/dba/mojo/GenerateDdlMojoTest.java#L33)
    * `@TestDBPattern(testCase=..., testDb=...)` controls which test cases and which DBs are tested. <br />
        * `testCase`      Which is the test case folder?
        * `testDb`       Which DB folder should be implemented?
    * The above annotation information is read, `mojoTestFixtureList` is generated and passed around.
```java
	@Test
	@TestDBPattern(testCase = "type", testDb = { TestDB.oracle, TestDB.postgresql, TestDB.db2, TestDB.h2,
			TestDB.sqlserver, TestDB.mysql })
	public void testType() throws Exception {

		// MojoTestFixtureList is a DB list of the information needed to run the test, prepared by reading the @TestDBPattern information.
		// In short, the specified DB loop is passed.
		for (MojoTestFixture mf : mojoTestFixtureList) {

/** Preparation phase */
			// File object of the Mojo folder/test case folder/DB folder/pom.xml
			// Define the parameters required for goal execution in pom.xml.
			File pom = new File(getTestCaseDBPath(mf) + "/pom.xml");

/** Goal execution phase */
			// Specify pom.xml, goal name and DB name. This is a standard code.
			GenerateDdlMojo mojo = this.lookupConfiguredMojo(pom, GENERATE_DDL, mf.testDb);
			mojo.execute();

/** Test result verification phase */
			// Verification. Mojo prepares the expected file in advance and matches it with the file that is output.
			
			// Files output after execution
			String actualPath = mojo.outputDirectory.getAbsolutePath();
			Entry actualFiles = DirUtil.collectEntry(actualPath);
			
			// Files of expected value that are prepared
			Entry expectedFiles = DirUtil.collectEntry(getExpectedPath(mf) + FS + "ddl");
			
			// Match
			assertThat("TestDb:" + mf.testDb, actualFiles.equals(expectedFiles), is(true));

		}

```

## TestCase Level 2

* Execute with only a certain DB.
    * Specify with [mojoTest.properties](../../src/test/resources/jp/co/tis/gsp/tools/dba/mojo/mojoTest.properties)
```shell
testDB=db2
```
* Parameter specification details by pom.xml
    * DB connection parameters, etc. are defined in [Parent pom](../../src/test/resources/jp/co/tis/gsp/tools/dba/mojo/testParentPom.xml)
    * A profile is defined in [settings.xml](../../src/test/resources/settings.xml)
    * Flow of solution
        * [jdbc_test.properties](../../src/test/resources/jdbc_test.properties)  
            * [settings.xml](../../src/test/resources/settings.xml)  
                * [testParentPom.xml](../../src/test/resources/jp/co/tis/gsp/tools/dba/mojo/testParentPom.xml)  
                    *  pom.xml of each test case. [Example](../../src/test/resources/jp/co/tis/gsp/tools/dba/mojo/ExecuteDdlMojo_test/type/db2/pom.xml)
    
    
## Troubleshoot

* As soon as you run the Mojo test class on Eclipse with Junit, an `AssertionError`-like error may occur and the program may crash.
    * Full clear and full build of the project* will correct the problem.

## JPA simple verification

* Implemented in the `integration-test` phase. Use the plugin maven-invoker-plugin. [it](../src/it) folder is the main folder.
* DB connection information uses [JDBC_test.properties](../../src/test/resources/jdbc_test.properties) that is used in the Mojo test class.
* Project [simple-jpa-test](../../src/it/simple-jpa-test) is used for each DB and executed.
    1. [edm file of each Db](../../src/it/simple-jpa-test/src/main/resources) of the above mentioned project is input and generate-ddl, execute-ddl and generate-entity are executed.
    1. Execute [Test method](../../src/it/simple-jpa-test/src/test/java/jp/co/tis/gsp/jpatest/AppTest.java#L31).