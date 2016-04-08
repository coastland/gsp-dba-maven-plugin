# generate-ddlゴールの仕様

## 自動採番の細かい指定には`extraDdlDirectory`を使用してください

`generate-ddl`を実行すると、自動採番すべきと判断されたカラムにはその指定がつくようになっています。  
下記の条件を**全て満たす場合**に自動採番カラムと判断します。
[ソースコード](../src/main/java/jp/co/tis/gsp/tools/db/beans/Column.java#L74)もご参照ください。

- 単一主キー
- 外部キーでない
- データ型が数値型
- カラム名の末尾が`_ID`(大文字小文字問わない)

現在、これに該当するが自動採番したくないカラムを指定する方法はありません。

よって特別な指定を施したいときは`extraDdlDirectory`オプションを使用して、
ご使用のDB製品に合ったSQLファイルを実行してください。
指定方法は[execute-ddlゴール](README.md#execute-ddl)を参照してください。

`extraDdlDirectory`で指定されたSQLファイルは`generate-ddl`で生成されたSQLファイルがすべて実行された後に呼び出されます。

以下、PostgreSQLで`project`テーブルの`project_id`列に`SERIAL`で定義されていた自動採番を解除して、
自動採番用に内部的に使用されていたシーケンスを削除するスクリプト例です。

```sql
-- http://www.postgresql.org/message-id/a55915760705110936j1ee5bb67oe366c9161c92fc33@mail.gmail.com
ALTER TABLE project ALTER COLUMN project_id DROP DEFAULT;
ALTER SEQUENCE project_project_id_seq OWNED BY NONE;
DROP SEQUENCE project_project_id_seq;
```
