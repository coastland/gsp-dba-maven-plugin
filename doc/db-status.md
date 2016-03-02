
## データベースの対応状況

 データベースごとの対応状況を以下にまとめています。

### 事前作業

本ツールを使用する場合、データベースによって以下の作業を事前に行なう必要があります。

* MS SQL Server<br/>
  サーバー認証を許可するよう設定

* DB2<br/>
  DB接続用のOSユーザアカウントを作成

### 対応状況の概要

◎...動作確認済み
○...実装済み
×...使用不可

|                    | generate-ddl | execute-ddl | load-data | generate-entity | import-schema | export-schema |
|:-------------------|:------------:|:-----------:|:---------:|:---------------:|:-------------:|:-------------:|
| Oracle             | ◎           | ◎          | ◎        | ◎              | ◎            | ◎            |
| Solr               | ○           | ○          | ○        | ○              | ○            | ○            |
| H2                 | ◎           | ◎          | ◎        | ◎              | ◎            | ◎            |
| MySql              | ◎           | ◎          | ○        | ◎              | ◎            | ◎            |
| Postgresql         | ◎           | ◎          | ◎        | ◎              | ◎            | ◎            |
| MS SQL Server 2008 | ◎           | ◎          | ◎        | ◎              | ×            | ×            |
| DB2 10.5           | ◎           | ◎          | ◎        | ◎              | ×            | ×            |


### load-dataの対応状況

データベースごとに登録可能なデータの型をまとめています。
使用不可の型を記入した場合はnullが設定されますが、
一部の型の場合にはエラーが発生します。

**Oracle**

カラムの型名を指定する必要はありません。

| 型名          | 使用可否 | 記入例 | 備考 |
|:--------------|:--------:|:-------|:-----|
| BFILE         | ×       | -                                                  | BFILE型のカラム名がCSVファイル内に記載されているとエラーが発生し、正常にデータが登録されない。 |
| BINARY_DOUBLE | ×       | -                                                  | - |
| BINARY_FLOAT  | ×       | -                                                  | - |
| BLOB          | ×       | -                                                  | - |
| CHAR          | ○       | text                                                | - |
| CLOB          | ×       | -                                                  | - |
| DATE          | ○       | 1990-08-08                                          | - |
| LONG          | ○       | 1234567890                                          | - |
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


**Postgresql**

カラムの型名を指定する必要はありません。

| 型名      | 使用可否 | 記入例                        | 備考 |
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

**MS SQL Server**

IDENTITYを指定したカラムは使用できません。<br />
カラムの型名を指定する必要はありません。

| 型名 | 使用可否 | 記入例 | 備考 |
|:----|:-------:|:-----|:----|
| BIGINT | ○ | -9223372036854770000 | - |
| BINARY | ○ | 000101001100 | 16進数のビット表記 |
| BIT | ○ | 000101001100 | 16進数のビット表記 |
| CHAR | ○ | text | - |
| DATE | ○ | 1990-08-08 | - |
| DATETIME | ○ | 2007-05-08 12:35:29 | - |
| DATETIME2 | ○ | 2007-05-08 12:35:29 | - |
| DATETIMEOFFSET | ○ | 2007-05-08 12:35:29.1234567 | - |
| DECIMAL | ○ | 1.111 | - |
| FLOAT | ○ | 1.111 | - |
| GEOGRAPHY | × | - | - |
| GEOMETRY | × | - | - |
| HIERARCHYID | ○ | 000101001100 | 16進数のビット表記 |
| IMAGE | ○ | 000101001100 | 16進数のビット表記 |
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
| TIME | ○ | 12:35:29.123 | fractional second precisionに7を設定していても小数点第3位までの精度しか登録できない。 |
| TIMESTAMP | × | - | - |
| TINYINT | ○ | 255 | - |
| UNIQUEIDENTIFIER | ○ | 6F9619FF-8B86-D011-B42D-00C04FC964FF | - |
| VARBINARY | ○ | 000101001100 | 16進数のビット表記 |
| VARCHAR | ○ | text | - |

**DB2**

IDENTITYを指定したカラムは使用できません。<br />
カラムの型名を指定する必要はありません。

| 型名 | 使用可否 | 記入例 | 備考 |
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
