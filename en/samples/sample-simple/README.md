# 最小限の設定で動作するサンプル

最小限の設定（必須でないものはデフォルト）でH2を使用したGSPプラグインのサンプル。
以下が実行できる。

* [DDLの生成](#generate-ddl)
* [DDLの実行](#execute-ddl)
* [Entityの生成](#generate-entity)
* [データの登録](#load-data)

<a name="generate-ddl"></a>

## DDLの生成

edmファイルからDDLを生成する。
設定されたedmファイルを読み込み、target/ddl（デフォルト設定）の下にDDLが出力される。

|パラメータ|設定値|
|:-|:-|
|erdFile| src/main/resources/entity/model.edm |

本サンプルでのedmファイルは、親テーブルとそれに依存する子テーブルが用意してあるだけである。

実行コマンド

````
  mvn gsp-dba:generate-ddl
````

<a name="execute-ddl"></a>

## DDLの実行

[DDLの生成](#generate-ddl)によって生成されたDDLを実行し、DBにテーブルを作成する。

実行コマンド

````
  mvn gsp-dba:execute-ddl
````


<a name="generate-enitity"></a>

## Entityの生成

[DDLの実行](#execute-ddl)によってできたテーブルを元に、Entityを生成する。
設定されたEntityのパッケージとして、target/generated-sources/entity（デフォルト設定）の下に生成される。

|パラメータ|設定値|
|:-|:-|
|rootPackage| gsp.sample |

実行コマンド

````
  mvn gsp-dba:generate-entity
````

<a name="load-data"></a>

## データの登録

CSVファイルの記述されたデータをDBに登録する。
設定されたCSVのディレクトリの下に置かれているCSVファイルを読み込み、DBにデータが登録される。

|パラメータ|設定値|
|:-|:-|
|dataDirectory| src/test/resources/testdata |

実行コマンド

````
  mvn gsp-dba:load-data
````


