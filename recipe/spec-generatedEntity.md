## generate-entityで生成されるエンティティで使用されるアノテーション

generate-entity時に生成されるエンティティで使用されるアノテーションに関して解説します。

### テーブルに関するアノテーション
クラスに対して付与されます

|アノテーション|説明|
|:--:|:--|
|@Generated  | 自動生成されたことを表します。 | 
|@Entity|エンティティであることを表します。 | 
|@Table|主テーブルを表します。属性は以下の通り。<br/> ・name:テーブル名<br/>・catalog:カタログ名<br/>・schema:スキーマ名<br/>・uniqueConstraints:複合ユニークキー制約| 


### カラムに関するアノテーション
各カラムに対応するプロパティのgetterメソッド(パラメータのuseAccessorがfalseの場合はプロパティ自体)に付与されます。

|アノテーション|説明|
|:--:|:--|
|@Id | 主キーであることを表します。|
|@GeneratedValue|DBによって自動採番されることを表します。<br/>属性は以下の通り<br/>・generator:使用するジェネレータ。<br/>・strategy:主キーの値を生成する方法|
| @SequenceGenerator|主キーを作成するシーケンスジェネレータの設定を表します。<br/>@GeneratedValueと同時に使用する必要があります。<br/>属性は以下の通り<br/>・name:このジェネレータを識別するための名前。@GeneratedValueのgeneratorに指定する。<br/>・sequenceName:使用するデータベースシーケンスオブジェクトの名前<br/>・initialValue:主キーの値の初期値<br/>・allocationSize:割り当てる際にキャッシュしておく値の範囲|
|@Lob|largeオブジェクト型の永続化フィールドまたは永続化プロパティであることを表します。|
|@Temporal|時刻を表します型（java.util.Dateおよびjava.util.Calendar）を持つ永続化プロパティまたは永続化フィールドを表します。|
|@Version|楽観的ロック機能を使用するために用いるversionフィールドまたはversionプロパティを表します。<br/>カラム名が正規表現で「VERSION([_]?NO)?」にマッチし、かつEntityのプロパティのデータ型がjava.lang.Integerおよびjava.lang.Longの場合に付与されます。<br />カラム名のパターンはgenerate-entityのパラメータversionColumnNamePatternで変更可能です。|
|@Column|永続化フィールドまたは永続化プロパティと，データベース上のカラムとのマッピングを表します。<br/>使用される属性は以下の通り。<br/>・name:カラム名<br>・columnDefinition:カラムに付加される制約<br/>・length:カラムの長さ<br/>・precision:カラムの精度<br/>・scale:カラムのスケール<br/>・nullable:null値を指定できるかどうか<br/>・unique:ユニークキーであるかどうか|

### リレーションシップに関するアノテーション
結合するテーブルに対応するプロパティのgetterメソッド(パラメータのuseAccessorがfalseの場合はプロパティ自体)に付与されます。

|アノテーション|説明|ER図|
|:--:|:--|:--|
|@ManyToOne|「多対1」で結合することを表します。|テスト2が対象<br/>![ManyToOne](image/relation.png)|
|@OneToMany|「1対多」で結合することを表します。|テスト1が対象<br/>![relation](image/relation.png)|
|@JoinColumn|テーブルを結合する際に使用する外部キーを表します。<br/>使用される属性は以下の通り<br/>・name:対象テーブルを結合するために使用する外部キーカラム名<br/>・referencedColumnName:外部キーカラムによって参照された結合先テーブルのカラム名|テスト2が対象<br/>![relation](image/relation.png)|
|@JoinColumns|複合主キーを使用して結合されることを表します。@JoinColumnを要素として複数持ちます。|テスト2が対象<br/>![joinColumns](image/joinColumns.png)|

### 主キーのプロパティに設定されるアノテーションについて

ここでは、デフォルトのテンプレートファイル([gsp_entity.ftl](../src/main/resources/org/seasar/extension/jdbc/gen/internal/generator/tempaltes/java/gsp_entity.ftl))を使用した場合に主キーのプロパティに設定されるアノテーションの仕様について説明します。

まず、`@Id`は主キーであれば必ず設定されます。

次に、カラムの値がデータベースによって自動採番される場合は、`@GeneratedValue(strategy = GenerationType.IDENTITY)`が設定されます。
ただし、Oracleの場合は自動採番されるカラムかどうかの判定ができないため、自動採番が設定されたカラムであってもこのアノテーションは設定されません。
これは、自動採番されるカラムかどうかの判定に`ResultSetMetaData`の`isAutoIncrement`が内部的に使用されていますが、OracleのJDBCの実装ではこのメソッドが常に`false`を返すようになっているためです。

自動採番されないカラムの場合は、`Dialect`の`getGenerationType`メソッドが返す値によって次のようにアノテーションが設定されます。

- `GenerationType.SEQUENCE`を返す場合
    - 以下2つのアノテーションが設定されます
        - `@GeneratedValue(generator = "{sequenceName}", strategy = GenerationType.AUTO)`
        - `@SequenceGenerator(name = "{sequenceName}", sequenceName = "{sequenceName}", initialValue = 1, allocationSize = {allocationSize})`
    - `{sequenceName}`には`スキーマ名.カラム名_SEQ`という名前が設定されます（例：`PUBLIC.ID_SEQ`）
    - `{allocationSize}`には[generate-entityのパラメータ](../README.md#使用可能なパラメータ-3)で指定した`allocationSize`が設定されます（デフォルトは1）
- `GenerationType.IDENTITY`を返す場合
    - `@GeneratedValue(strategy = GenerationType.IDENTITY)`が設定されます
- それ以外の場合
    - 追加のアノテーションは設定されません

デフォルトでは、`Dialect`の`getGenerationType`は`null`を返します。
ただし、`OracleDialect`の`getGenerationType`は`GenerationType.SEQUENCE`を返すようになっています。

これら以外の`GenerationType`を返すようにしたい場合は、カスタムの`Dialect`を作成して差し替えてください。
`Dialect`を差し替える方法については[Dialectクラスのカスタマイズ例](custom-Dialect.md)を参照してください。
