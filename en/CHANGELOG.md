# Changelog

All major changes to this project will be documented in this file.

## 4.5.0 (2022-03-24)
### Updates
#### Change
- Changed the URL of the dependent Seasar Maven repository.
  - Maven 3.8.1 and later versions block external HTTP repositories by default.
    Changed the URL to HTTPS so that Maven 3.8.1 and later versions can still access the dependent Seasar repository.

## 4.4.1 (2020-09-24)
### Updates
#### Change
- Added support for cases where ";" is not added to the end of the generated ALTER TABLE.
  - In some cases, the generated ALTER TABLE did not end with ";", which was inconvenient when executing the generated DDL manually.
    In the past, ";" was not added to the case, but now it is added to the case.

- Fix Dialect customization procedure.
  - Due to the specification of Maven, in order to include a customized Dialect in the dependency of gsp-dba-maven-plugin, it is necessary to create a jar file that contains the Dialect, except in some cases.
    Since there was no mention of the need to create a jar file, it has been specified.
