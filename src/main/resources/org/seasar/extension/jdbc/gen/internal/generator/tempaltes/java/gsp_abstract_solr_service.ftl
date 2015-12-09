<#import "/lib.ftl" as lib>
<#if lib.copyright??>
${lib.copyright}
</#if>
<#if !lib.copyright??>
<#include "/copyright.ftl">
</#if>
<#if packageName??>
package ${packageName};
</#if>

<#list importNameSet as importName>
import ${importName};
</#list>
<#if staticImportNameSet?size gt 0>

  <#list staticImportNameSet as importName>
import static ${importName};
  </#list>
</#if>
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.SqlFileSelect;
import org.seasar.extension.jdbc.SqlFileUpdate;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.GenericUtil;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

/**
 * Solrサービスの抽象クラスです。
 *
<#if lib.author??>
 * @author ${lib.author}
</#if>
 * @param <ENTITY>
 *            エンティティの型
 */
@Generated(value = {<#list generatedInfoList as info>"${info}"<#if info_has_next>, </#if></#list>}, date = "${currentDate?datetime}")
public abstract class AbstractSolrService<ENTITY> {
    @Resource(name = "searchJdbcManager")
    protected JdbcManager jdbcManager;

    /**
     * エンティティのクラスです。
     */
    protected Class<ENTITY> entityClass;

    /**
     * SQLファイルのパスのプレフィックスです。
     */
    protected String sqlFilePathPrefix;

    /**
     * コンストラクタです。
     *
     */
    @SuppressWarnings("unchecked")
    public AbstractSolrService() {
        Map<TypeVariable<?>, Type> map = GenericUtil
                .getTypeVariableMap(getClass());
        for (Class<?> c = getClass(); c != Object.class; c = c.getSuperclass()) {
            if (c.getSuperclass() == AbstractSolrService.class) {
                Type type = c.getGenericSuperclass();
                Type[] arrays = GenericUtil.getGenericParameter(type);
                setEntityClass((Class<ENTITY>) GenericUtil.getActualClass(arrays[0],
                        map));
                break;
            }
        }
    }

    /**
     * コンストラクタです。
     *
     * @param entityClass
     *            エンティティのクラス
     */
    public AbstractSolrService(Class<ENTITY> entityClass) {
        setEntityClass(entityClass);
    }

    /**
     * 自動検索を返します。
     *
     * @return 自動検索
     */
    protected AutoSelect<ENTITY> select() {
        return jdbcManager.from(entityClass);
    }

    /**
     * すべてのエンティティを検索します。
     *
     * @return すべてのエンティティ
     */
    public List<ENTITY> findAll() {
        return select().getResultList();
    }

    /**
     * 条件付で検索します。
     *
     * @param conditions
     *            条件
     *
     * @return エンティティのリスト
     * @see AutoSelect#where(Map)
     */
    public List<ENTITY> findByCondition(BeanMap conditions) {
        return select().where(conditions).getResultList();
    }

    /**
     * 件数を返します。
     *
     * @return 件数
     */
    public long getCount() {
        return select().getCount();
    }

    /**
     * エンティティを挿入します。
     *
     * @param entity
     *            エンティティ
     * @return 更新した行数
     */
    public int insert(ENTITY entity) {
        return jdbcManager.insert(entity).execute();
    }

    /**
     * エンティティを更新します。
     *
     * @param entity
     *            エンティティ
     * @return 更新した行数
     */
    public int update(ENTITY entity) {
        return jdbcManager.update(entity).execute();
    }

    /**
     * エンティティを削除します。
     *
     * @param entity
     *            エンティティ
     * @return 更新した行数
     */
    public int delete(ENTITY entity) {
        return jdbcManager.delete(entity).execute();
    }

    /**
     * SQLファイル検索を返します。
     *
     * @param <ENTITY>
     *            戻り値のJavaBeansの型
     * @param baseClass
     *            戻り値のJavaBeansのクラス
     * @param path
     *            エンティティのディレクトリ部分を含まないSQLファイルのパス
     * @return SQLファイル検索
     */
    protected <ENTITY> SqlFileSelect<ENTITY> selectBySqlFile(Class<ENTITY> baseClass,
                                                     String path) {
        return jdbcManager.selectBySqlFile(baseClass, sqlFilePathPrefix + path);
    }

    /**
     * SQLファイル検索を返します。
     *
     * @param <ENTITY>
     *            戻り値のJavaBeansの型
     * @param baseClass
     *            戻り値のJavaBeansのクラス
     * @param path
     *            エンティティのディレクトリ部分を含まないSQLファイルのパス
     * @param parameter
     *            <p>
     *            パラメータ。
     *            </p>
     *            <p>
     *            パラメータが1つしかない場合は、値を直接指定します。 パラメータが複数ある場合は、JavaBeansを作って、
     *            プロパティ名をSQLファイルのバインド変数名とあわせます。
     *            JavaBeansはpublicフィールドで定義することもできます。
     *            </p>
     *            <p>
     *            パラメータが1つで型が{@link java.util.Date}、{@link java.util.Calendar}のいずれか場合、{@link org.seasar.extension.jdbc.parameter.Parameter}に定義されたメソッドによりパラメータの時制を指定できます。
     *            </p>
     *            <p>
     *            パラメータが1つで型が{@link String}、<code>ｂyte[]</code>、{@link java.io.Serializable}のいずれかの場合、{@link org.seasar.extension.jdbc.parameter.Parameter}に定義されたメソッドによりパラメータをラージオブジェクトとして扱えます。
     *            </p>
     * @return SQLファイル検索
     */
    protected <ENTITY> SqlFileSelect<ENTITY> selectBySqlFile(Class<ENTITY> baseClass,
                                                     String path, Object parameter) {
        return jdbcManager.selectBySqlFile(baseClass, sqlFilePathPrefix + path,
                parameter);
    }

    /**
     * SQLファイル更新を返します。
     *
     * @param path
     *            エンティティのディレクトリ部分を含まないSQLファイルのパス
     * @return SQLファイル更新
     */
    protected SqlFileUpdate updateBySqlFile(String path) {
        return jdbcManager.updateBySqlFile(sqlFilePathPrefix + path);
    }

    /**
     * SQLファイル更新を返します。
     *
     * @param path
     *            エンティティのディレクトリ部分を含まないSQLファイルのパス
     * @param parameter
     *            パラメータ用のJavaBeans
     *
     * @return SQLファイル更新
     */
    protected SqlFileUpdate updateBySqlFile(String path, Object parameter) {
        return jdbcManager.updateBySqlFile(sqlFilePathPrefix + path, parameter);
    }

    /**
     * エンティティのクラスを設定します。
     *
     * @param entityClass
     *            エンティティのクラス
     */
    protected void setEntityClass(Class<ENTITY> entityClass) {
        this.entityClass = entityClass;
        sqlFilePathPrefix = "META-INF/sql/"
                + StringUtil.replace(entityClass.getName(), ".", "/") + "/";
    }
}
