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

import jp.co.tis.gsp.tools.db.LengthSemantics;
import jp.co.tis.gsp.tools.db.ObjectBrowserErParser;
import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.dialect.DialectFactory;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.seasar.framework.beans.util.Beans;

import java.io.File;
import java.io.IOException;

/**
 * Generate DDL.
 *
 * @author kawasima
 */
@Mojo(name = "generate-ddl")
public class GenerateDdlMojo extends AbstractDbaMojo {

	/**
     * ERD file.
	 */
    @Parameter(property = "gsp-dba.erdFile", required = true)
	protected File erdFile;

	/**
	 * output directory.
	 */
    @Parameter(defaultValue = "target/ddl")
	protected File outputDirectory;

    @Parameter(defaultValue = "BYTE")
    protected LengthSemantics lengthSemantics;

    /**
     * Generate DDL.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
	@Override
	protected void executeMojoSpec() throws MojoExecutionException, MojoFailureException {
		if(outputDirectory.exists()) {
			try {
				FileUtils.cleanDirectory(outputDirectory);
			} catch (IOException e) {
				throw new MojoExecutionException("Can't clean outputDirectory:" + outputDirectory);
			}
		} else {
			try {
				FileUtils.forceMkdir(outputDirectory);
			} catch (IOException e) {
				throw new MojoExecutionException("Can't create outputDirectory:" + outputDirectory);
			}
		}

		ObjectBrowserErParser parser = new ObjectBrowserErParser();
		parser.setOutputDirectory(outputDirectory);
		parser.setSchema(schema);
		parser.setUrl(url);
		parser.setUser(user);

		Dialect dialect = DialectFactory.getDialect(url);
		Beans.copy(this, dialect).execute();
		parser.setTypeMapper(dialect.getTypeMapper());

        parser.setPrintTable(dialect.canPrintTable());
        parser.setPrintIndex(dialect.canPrintIndex());
        parser.setPrintForeignKey(dialect.canPrintForeignKey());
        parser.setPrintView(dialect.canPrintView());
        parser.setLengthSemantics(lengthSemantics);
		try {
			parser.parse(erdFile);
		} catch (Exception e) {
			throw new MojoExecutionException("DDL generate", e);
		}
	}

}
