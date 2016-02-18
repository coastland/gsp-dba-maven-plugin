# generate-ddlゴールの仕様

## 自動採番の細かい指定には`extraDdlDirectory`を使用してください

`generate-ddl`を実行すると、自動採番すべきと判断されたカラムにはその指定がつくようになっています。その条件は以下です。
[ソースコード](../src/main/java/jp/co/tis/gsp/tools/db/beans/Column.java#L74)もご参照ください。

- ObjectBrowserで見た時、カラムの「デフォルト値」に`AUTO_INCREMENT`と記載されている。
- または、外部キーでない単一主キーかつ、`_ID`(大文字小文字問わない)が末尾についていてかつ、データ型が数値型である。

現在、これに該当するが自動採番したくないカラムを指定する方法はありません。

よって特別な指定を施したいときは`extraDdlDirectory`オプションを使用して、
ご使用のDB製品に合ったSQLファイルを実行してください。
指定方法は[execute-ddlゴール](README.md#execute-ddl)を参照してください。

`extraDdlDirectory`で指定されたSQLファイルは`generate-ddl`で生成されたSQLファイルがすべて実行された後に呼び出されます。
