
## Database Support Status

 The support status for each database is summarized below.

### Pre-work

To use this tool, the following tasks are required to be performed in advance depending on the database.

* SQL Server<br/>
  Configure to allow server authentication

* Db2<br/>
  Create an OS user account for DB connection

### Overview of support status

◎...Operation checked
○...Implemented
×...Cannot be used

|            | generate-ddl | execute-ddl | load-data | generate-entity | import-schema | export-schema |
|:-----------|:------------:|:-----------:|:---------:|:---------------:|:-------------:|:-------------:|
| Oracle     | ◎           | ◎          | ◎        | ◎              | ◎            | ◎            |
| Solr       | ○           | ○          | ○        | ○              | ○            | ○            |
| H2         | ◎           | ◎          | ◎        | ◎              | ◎            | ◎            |
| MySQL      | ◎           | ◎          | ○        | ◎              | ◎            | ◎            |
| PostgreSQL | ◎           | ◎          | ◎        | ◎              | ◎            | ◎            |
| SQL Server | ◎           | ◎          | ◎        | ◎              |◎|   ◎|
| Db2        | ◎           | ◎          | ◎        | ◎              |◎           |    ◎       |

#### Tested version
* Oracle
  * 18c / 19c / 21c
* H2
  * 2.1.214
* MySQL
  * 8.0 (MySQL connector uses 5.1.49)
* PostgreSQL
  * 10.0 / 11.5 / 12.2 / 13.2 / 14.0
* SQL Server
  * 2017 / 2019
* Db2
  * 10.5 / 11.5


### Support status for load-data

The types of data that can be registered for each database are summarized.
If a disabled type is entered, it is configured to null, but an error is generated for some types.


**Oracle**

It is not necessary to specify the type name of the column.

| Type Name          | Usability | Examples | Remarks |
|:--------------|:--------:|:-------|:-----|
| BFILE         | ×       | -                                                  | An error occurs when the column name of BFILE type is included in the CSV file and the data is not registered correctly. |
| BINARY_DOUBLE | ×       | -                                                  | - |
| BINARY_FLOAT  | ×       | -                                                  | - |
| BLOB          | ×       | -                                                  | - |
| CHAR          | ○       | text                                                | - |
| CLOB          | ×       | -                                                  | - |
| DATE          | ○       | 1990-08-08                                          | - |
| LONG          | ×       | -                                                  | - |
| LONG ROW      | ×       | -                                                  | - |
| NCHAR         | ○       | text                                                | - |
| NCLOB         | ×       | -                                                  | - |
| NUMBER        | ○       | 1234567890                                          | - |
| VARCHAR       | ○       | text                                                | - |
| RAW           | ×       | -                                                  | - |
| ROWID         | ×       | -                                                  | - |
| TIMESTAMP     | ○       | 1990-08-08 12:12:12 / 1990-08-08 12:12:12.123456789 | - |
| UROWID        | ×       | -                                                  | - |
| VARCHAR2      | ○       | text                                                | - |


**PostgreSQL**

It is not necessary to specify the type name of the column.

| Type Name      | Usability | Examples                       | Remarks |
|:----------|:--------:|:------------------------------|:-----|
| BIGINT    | ○       | -9223372036854770000          | -   |
| BIGSERIAL | ○       | 9223372036854770000           | -   |
| BIT       | ×       | -                            | -   |
| BOOL      | ○       | t/TRUE/y/yes/1/f/FALSE/n/no/0 | -   |
| BOX       | ×       | -                            | -   |
| BYTEA     | ×       | -                            | -   |
| CHAR      | ○       | text                          | -   |
| CIDR      | ×       | -                            | -   |
| CIRCLE    | ×       | -                            | -   |
| DATE      | ○       | 1999-01-08                    | -   |
| FLOAT8    | ○       | 1.111                         | -   |
| INET      | ×       | -                            | -   |
| INTEGER   | ○       | -2147483648                   | -   |
| INTERVAL  | ×       | -                            | -   |
| LINE      | ×       | -                            | -   |
| LSEG      | ×       | -                            | -   |
| MACADDR   | ×       | -                            | -   |
| MONEY     | ×       | -                            | -   |
| NUMERIC   | ○       | 1.111                         | -   |
| PATH      | ×       | -                            | -   |
| POINT     | ×       | -                            | -   |
| POLYGON   | ×       | -                            | -   |
| REAL      | ○       | 1.111                         | -   |
| SERIAL    | ○       | 1                             | -   |
| SMALLINT  | ○       | -32768                        | -   |
| TEXT      | ○       | text                          | -   |
| TIME      | ×       | -                            | -   |
| TIMESTAMP | ○       | 1999-01-08 12:12:12           | -   |
| VARBIT    | ×       | -                            | -   |
| VARCHAR   | ○       | text                          | -   |

**SQL Server**

A column specified as IDENTITY cannot be used. <br />
It is not necessary to specify the type name of the column.

| Type Name | Usability | Examples | Remarks |
|:----|:-------:|:-----|:----|
| BIGINT | ○ | -9223372036854770000 | - |
| BINARY | ○ | 000101001100 | Hexadecimal bit notation |
| BIT | ○ | 000101001100 |  Hexadecimal bit notation |
| CHAR | ○ | text | - |
| DATE | ○ | 1990-08-08 | - |
| DATETIME | ○ | 2007-05-08 12:35:29 | - |
| DATETIME2 | ○ | 2007-05-08 12:35:29 | - |
| DATETIMEOFFSET | ○ | 2007-05-08 12:35:29.1234567 | - |
| DECIMAL | ○ | 1.111 | - |
| FLOAT | ○ | 1.111 | - |
| GEOGRAPHY | × | - | - |
| GEOMETRY | × | - | - |
| HIERARCHYID | ○ | 000101001100 | Hexadecimal bit notation |
| IMAGE | ○ | 000101001100 | Hexadecimal bit notation |
| INT | ○ | -2147483648 | - |
| MONEY | ○ | 1.111 | - |
| NCHAR | ○ | text | - |
| NTEXT | ○ | text | - |
| NUMERIC | ○ | 1.111 | - |
| NVARCHAR | ○ | text | - |
| REAL | ○ | 1.111 | - |
| SMALLDATETIME | ○ | - | - |
| SMALLINT | ○ | -32768 | - |
| SMALLMONEY | ○ | 1.111 | - |
| SQL_VARIANT | × | - | - |
| TEXT | ○ | text | - |
| TIME | ○ | 12:35:29.123 | Although fractional second precision is set to 7, only up to 3 decimal places can be registered. |
| TIMESTAMP | × | - | - |
| TINYINT | ○ | 255 | - |
| UNIQUEIDENTIFIER | ○ | 6F9619FF-8B86-D011-B42D-00C04FC964FF | - |
| VARBINARY | ○ | 000101001100 | Hexadecimal bit notation |
| VARCHAR | ○ | text | - |

**Db2**

A column specified as IDENTITY cannot be used. <br />
It is not necessary to specify the type name of the column.

| Type Name |  Usability | Examples | Remarks |
|:----|:-------:|:-----|:----|
| BIGINT | ○ | -9223372036854770000 | - |
| BLOB | × | - |
| CHARACTER | ○ | text | - |
| CLOB | ○ | text | - |
| DATE | ○ | 2009-04-06 | - |
| DBCLOB | ○ | text | - |
| DECIMAL | ○ | 1.111 | - |
| DOUBLE | ○ | 1.111 | - |
| FLOAT | ○ | 1.111 | - |
| GRAPHIC | ○ | text | - |
| INTEGER | ○ | -2147483648 | - |
| LONG VARCHAR | ○ | text | - |
| LONG VARGRAPHIC | ○ | text | - |
| NUMERIC | ○ | 1.111 | - |
| REAL | ○ | 1.111 | - |
| SMALLINT | ○ | -32768 | - |
| TIME | ○ | 05:04:14.0 | - |
| TIMESTAMP | ○ | 2009-04-06 05:04:14.0 | - |
| VARCHAR | ○ | text | - |
| VARGRAPHIC | ○ | text | - |
| XML | × | - | - |

### Restrictions of general ExportSchema/ImportSchema

The data types that can be handled are based on [load-data status](#support-status-for-load-data).  
For data types that cannot be handled, the column is not output by ExportSchema. Therefore, ImportSchema results in a null value for that column.

The following restrictions are specific to the general ExportSchema/ImportSchema.


**H2**
- OTHER type
    - Not available.
    
**Oracle**
- DATE type
    - Due to load-data restriction, data less than or equal to the time held in DATE type by Oracle is not included.

**SQL Server**
- BINARY type
    - Not available.
