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

import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.dialect.DialectFactory;
import jp.co.tis.gsp.tools.dba.dialect.SolrDialect;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.seasar.extension.jdbc.gen.command.CommandInvoker;
import org.seasar.extension.jdbc.gen.internal.command.AbstractCommand;
import org.seasar.extension.jdbc.gen.internal.command.CommandInvokerImpl;
import org.seasar.extension.jdbc.gen.internal.command.GenerateNamesCommand;
import org.seasar.extension.jdbc.gen.internal.command.GenerateServiceCommand;
import org.seasar.extension.jdbc.gen.internal.util.ReflectUtil;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.MethodUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
*
* @goal generate-service
* @author kawasima
*
*/
public class GenerateService extends AbstractDbaMojo {
	/**
	 * @parameter default-value="target/classes"
	 */
	public File diconDir;

	/**
	 * @parameter
	 * @required
	 */
	protected String rootPackage;

	/**
	 * @parameter default-value="entity"
	 */
	protected String entityPackageName;

	/**
	 * @parameter default-value="service"
	 */
	protected String servicePackageName;

	/**
	 * @parameter default-value="entity.names"
	 */
	protected String namesPackageName;

	/**
	 * @parameter default-value=""
	 */
	protected String ignoreEntityClassNamePattern;

	@Override
	protected void executeMojoSpec() throws MojoExecutionException, MojoFailureException {
		if (ignoreEntityClassNamePattern == null)
			ignoreEntityClassNamePattern = "";
		executeGenerateNames();
        Dialect dialect = DialectFactory.getDialect(url);
        if (dialect instanceof SolrDialect) {
            executeGenerateAbstractSolrService();
        }
		executeGenerateService();
	}

    private void executeGenerateAbstractSolrService() {
        final GenerateServiceCommand command = new GenerateServiceCommand();
        command.setClasspathDir(diconDir);
        command.setNamesPackageName(namesPackageName);
        command.setEntityPackageName(entityPackageName);
        command.setServicePackageName(servicePackageName);
        command.setAbstractServiceTemplateFileName("java/gsp_abstract_solr_service.ftl");
        command.setServiceClassNameSuffix("SolrService");

        final List<URL> urlList = new ArrayList<URL>();
        try {
            urlList.add(diconDir.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        final URLClassLoader newLoader = new URLClassLoader(urlList.toArray(new URL[] {}),
                oldLoader);
        try {
            Thread.currentThread().setContextClassLoader(newLoader);

            command.setRootPackageName(rootPackage);
            Method init = ClassUtil.getDeclaredMethod(
                    AbstractCommand.class,
                    "init",
                    null);
            init.setAccessible(true);
            MethodUtil.invoke(init, command, null);

            Method generateAbstractService = ClassUtil.getDeclaredMethod(
                    GenerateServiceCommand.class,
                    "generateAbstractService",
                    null);
            generateAbstractService.setAccessible(true);
            MethodUtil.invoke(generateAbstractService, command, null);
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }
	private void executeGenerateService() {
        Dialect dialect = DialectFactory.getDialect(url);
        final GenerateServiceCommand command = new GenerateServiceCommand();
		command.setClasspathDir(diconDir);
		command.setNamesPackageName(namesPackageName);
		command.setEntityPackageName(entityPackageName);
		command.setServicePackageName(servicePackageName);
		command.setIgnoreEntityClassNamePattern(ignoreEntityClassNamePattern);
        if (dialect instanceof SolrDialect) {
            command.setServiceTemplateFileName("java/gsp_solr_service.ftl");
        }

		final List<URL> urlList = new ArrayList<URL>();
		try {
			urlList.add(diconDir.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}

		final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		final URLClassLoader newLoader = new URLClassLoader(urlList.toArray(new URL[] {}),
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

	private void executeGenerateNames() {
		final GenerateNamesCommand command = new GenerateNamesCommand();
		command.setNamesPackageName(namesPackageName);
		command.setEntityPackageName(entityPackageName);
		command.setOverwrite(true);
		command.setClasspathDir(diconDir);
		command.setNamesTemplateFileName("java/gsp_names.ftl");
		command.setNamesAggregateTemplateFileName("java/gsp_names_aggregate.ftl");
		command.setIgnoreEntityClassNamePattern(ignoreEntityClassNamePattern);

		final List<URL> urlList = new ArrayList<URL>();
		try {
			urlList.add(diconDir.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}

		final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		final URLClassLoader newLoader = new URLClassLoader(urlList.toArray(new URL[] {}),
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
