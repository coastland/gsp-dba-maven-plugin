package jp.co.tis.gsp.tools.dba.s2jdbc.gen;

import org.seasar.extension.jdbc.gen.desc.AssociationType;
import org.seasar.extension.jdbc.gen.desc.EntityDesc;
import org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl;
import org.seasar.extension.jdbc.gen.model.AssociationModel;
import org.seasar.extension.jdbc.gen.model.AssociationModelFactory;
import org.seasar.extension.jdbc.gen.model.AttributeModel;
import org.seasar.extension.jdbc.gen.model.AttributeModelFactory;
import org.seasar.extension.jdbc.gen.model.CompositeUniqueConstraintModelFactory;
import org.seasar.extension.jdbc.gen.model.EntityModel;

import jakarta.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.List;

/**
 * Jakarta EE のパッケージ変更に対応した {@link org.seasar.extension.jdbc.gen.model.EntityModelFactory}。
 * @author Tanaka Tomoyuki
 */
public class JakartaEntityModelFactoryImpl extends EntityModelFactoryImpl {
    /**
     * インスタンスを構築しますｌ
     *
     * @param packageName                           パッケージ名、デフォルトパッケージの場合は{@code null}
     * @param superclass                            エンティティのスーパークラス、スーパークラスを持たない場合は{@code null}
     * @param attributeModelFactory                 属性モデルのファクトリ
     * @param associationModelFactory               関連モデルのファクトリ
     * @param compositeUniqueConstraintModelFactory 複合一意制約モデルのファクトリ
     * @param useAccessor                           エンティティクラスでアクセサを使用する場合 {@code true}
     * @param useComment                            コメントを使用する場合{@code true}
     * @param showCatalogName                       カタログ名を表示する場合{@code true}
     * @param showSchemaName                        スキーマ名を表示する場合{@code true}
     * @param showTableName                         テーブル名を表示する場合{@code true}
     */
    public JakartaEntityModelFactoryImpl(String packageName, Class<?> superclass, AttributeModelFactory attributeModelFactory, AssociationModelFactory associationModelFactory, CompositeUniqueConstraintModelFactory compositeUniqueConstraintModelFactory, boolean useAccessor, boolean useComment, boolean showCatalogName, boolean showSchemaName, boolean showTableName) {
        super(packageName, superclass, attributeModelFactory, associationModelFactory, compositeUniqueConstraintModelFactory, useAccessor, useComment, showCatalogName, showSchemaName, showTableName);
    }

    @Override
    protected void doImportName(EntityModel model, EntityDesc entityDesc) {
        classModelSupport.addImportName(model, Entity.class);
        classModelSupport.addImportName(model, Serializable.class);
        classModelSupport.addImportName(model, Generated.class);
        if (model.getCatalogName() != null || model.getSchemaName() != null
                || model.getTableName() != null) {
            classModelSupport.addImportName(model, Table.class);
        }
        if (superclass != null) {
            classModelSupport.addImportName(model, superclass);
        }
        for (AttributeModel attr : model.getAttributeModelList()) {
            if (attr.isId()) {
                classModelSupport.addImportName(model, Id.class);
                if (attr.getGenerationType() != null) {
                    classModelSupport
                            .addImportName(model, GeneratedValue.class);
                    classModelSupport
                            .addImportName(model, GenerationType.class);
                    if (attr.getGenerationType() == GenerationType.SEQUENCE) {
                        classModelSupport.addImportName(model,
                                SequenceGenerator.class);
                    } else if (attr.getGenerationType() == GenerationType.TABLE) {
                        classModelSupport.addImportName(model,
                                TableGenerator.class);
                    }
                }
            }
            if (attr.isLob()) {
                classModelSupport.addImportName(model, Lob.class);
            }
            if (attr.getTemporalType() != null) {
                classModelSupport.addImportName(model, Temporal.class);
                classModelSupport.addImportName(model, TemporalType.class);
            }
            if (attr.isTransient()) {
                classModelSupport.addImportName(model, Transient.class);
            } else {
                classModelSupport.addImportName(model, Column.class);
            }
            if (attr.isVersion()) {
                classModelSupport.addImportName(model, Version.class);
            }
            classModelSupport.addImportName(model, attr.getAttributeClass());
        }
        for (AssociationModel asso : model.getAssociationModelList()) {
            AssociationType associationType = asso.getAssociationType();
            if (associationType == AssociationType.ONE_TO_MANY) {
                classModelSupport.addImportName(model, List.class);
            }
            classModelSupport.addImportName(model, associationType
                    .getAnnotation());
            if (asso.getJoinColumnModel() != null) {
                classModelSupport.addImportName(model, JoinColumn.class);
            }
            if (asso.getJoinColumnsModel() != null) {
                classModelSupport.addImportName(model, JoinColumn.class);
                classModelSupport.addImportName(model, JoinColumns.class);
            }
        }
        if (!model.getCompositeUniqueConstraintModelList().isEmpty()) {
            classModelSupport.addImportName(model, Table.class);
            classModelSupport.addImportName(model, UniqueConstraint.class);
        }
    }
}
