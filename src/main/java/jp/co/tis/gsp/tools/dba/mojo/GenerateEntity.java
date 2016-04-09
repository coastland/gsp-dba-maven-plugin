/*
 * Copyright (C) 2015 coastland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.tis.gsp.tools.dba.mojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.tis.gsp.tools.db.beans.Erd;
import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.dialect.DialectFactory;
import jp.co.tis.gsp.tools.dba.s2jdbc.gen.GspFactoryImpl;
import jp.co.tis.gsp.tools.dba.util.DialectUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.seasar.extension.jdbc.gen.command.CommandInvoker;
import org.seasar.extension.jdbc.gen.internal.command.CommandInvokerImpl;
import org.seasar.extension.jdbc.gen.internal.command.GenerateEntityCommand;
import org.seasar.extension.jdbc.gen.internal.util.ReflectUtil;
import org.seasar.framework.util.StringUtil;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author kawasima
 */
@Mojo(name = "generate-entity")
public class GenerateEntity extends AbstractDbaMojo {

    /**
     * dicon directory.
     */
    @Parameter(defaultValue = "target/classes")
    protected File diconDir;

    /**
     * table name pattern.
     */
    @Parameter(defaultValue = "(SCHEMA_INFO|.*\\$.*)")
    protected String ignoreTableNamePattern;

    /**
     * entity package name.
     */
    @Parameter(defaultValue = "entity")
    protected String entityPackageName;

    /**
     * gen dialect class name.
     */
    @Parameter
    protected String genDialectClassName;

    /**
     * dialect class name.
     */
    @Parameter
    protected String dialectClassName;

    /**
     * root package.
     */
    @Parameter(required = true)
    protected String rootPackage;

    /**
     * use accessor?
     */
    @Parameter(defaultValue = "false")
    protected Boolean useAccessor;

    /**
     * destination directory where java files are generated.
     */
    @Parameter(defaultValue = "target/generated-sources/entity/")
    protected File javaFileDestDir;

    /**
     * path of entity template file from &quot;org/seasar/extension/jdbc/gen/internal/generator/tempaltes&quot;. <br/>
     */
    @Parameter(defaultValue = "java/gsp_entity.ftl")
    protected String entityTemplate;

    /**
     * primary directory where template files are put.
     * if null, default directory will be used (see link below).
     * @see org.seasar.extension.jdbc.gen.internal.generator.GeneratorImpl#DEFAULT_TEMPLATE_DIR_NAME
     */
    @Parameter
    protected File templateFilePrimaryDir = null;

    /** 実行時に生成するdiconのテンプレート名 */
    private static final String[] templateNames = {"convention", "jdbc", "s2jdbc"};


    /**
     * S2JDBC-GENを使ってEntityクラスを生成する。
     * Entityプロジェクトにはdiconがないので、自動生成する。
     */
    @Override
	protected void executeMojoSpec() throws MojoExecutionException, MojoFailureException {
        Configuration fmConfig = new Configuration();
        fmConfig.setTemplateLoader(new ClassTemplateLoader(Erd.class, "/jp/co/tis/gsp/tools/dba/template/dicon"));
        
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("driver", driver);
        param.put("url", url);
        param.put("user", adminUser);
        /* NULLがfreemarkerに渡るとInvalidReferenceExceptionになるが、
           Mojoのparameterは空要素をNULLと認識するため、ここで空文字に変換する */
        param.put("password", (adminPassword == null) ? "" : adminPassword);
        param.put("rootPackage", rootPackage);

        String[] urlTokens = StringUtils.split(url, ':');
        if(urlTokens.length < 3) {
            throw new MojoExecutionException("Invalid url:" + url);
        }
        String databaseProduct = StringUtils.capitalize(urlTokens[1]);
        if ("Postgresql".equals(databaseProduct)) {
            databaseProduct = "Postgre";
        } else if ("Sqlserver".equals(databaseProduct)) {
            databaseProduct = "Mssql";
        }
        String dialectClass = this.dialectClassName != null ? dialectClassName
                : "org.seasar.extension.jdbc.dialect." + databaseProduct + "Dialect";
        if(StringUtil.equals(databaseProduct, "Solr")) {
            dialectClass = "net.unit8.solr.jdbc.extension.s2jdbc.dialect.SolrDialect";
        }
        param.put("databaseProduct", databaseProduct);
        param.put("dialectClass", dialectClass);

        try {
            if (!diconDir.exists())
                FileUtils.forceMkdir(diconDir);
            for (String templateName : templateNames) {
                Template template = fmConfig.getTemplate(templateName + ".dicon.ftl");
                template.process(param, new FileWriter(
                        new File(diconDir, templateName + ".dicon")
                ));
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Can't generate dicon file.", e);
        } catch (TemplateException e) {
            throw new MojoExecutionException("Can't generate dicon file.", e);
        }

        executeGenerateEntity();
    }

    /**
     * エンティティ生成を実行する。
     */
    private void executeGenerateEntity() {
        Dialect dialect = DialectFactory.getDialect(url, driver);
        DialectUtil.setDialect(dialect);
        final GenerateEntityCommand command = new GenerateEntityCommand();
        command.setSchemaName(schema);
        command.setOverwrite(true);
        command.setApplyDbCommentToJava(true);
        command.setEntityPackageName(entityPackageName);
        command.setIgnoreTableNamePattern(ignoreTableNamePattern);
        command.setEntityTemplateFileName(entityTemplate);
        command.setGenDialectClassName(genDialectClassName);
        command.setShowTableName(true);
        if(!user.equals(schema)){
            command.setShowSchemaName(true);
        }
        command.setGenerationType(dialect.getGenerationType());
        command.setFactoryClassName(GspFactoryImpl.class.getName());
        command.setUseAccessor(useAccessor);
        command.setShowColumnName(true);
        command.setJavaFileDestDir(javaFileDestDir);
        command.setTemplateFilePrimaryDir(templateFilePrimaryDir);

        final List<URL> urlList = new ArrayList<URL>();
        try {
            urlList.add(diconDir.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL(" + diconDir + ") が誤っている可能性があります。", e);
        }

        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        final URLClassLoader newLoader = new URLClassLoader(urlList.toArray(new URL[urlList.size()]),
                                                            oldLoader);
        try {
            Thread.currentThread().setContextClassLoader(newLoader);

            command.setRootPackageName(rootPackage);

            final CommandInvoker invoker = ReflectUtil.newInstance(CommandInvoker.class,
                                                                   CommandInvokerImpl.class.getName());
            invoker.invoke(command);
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

}
