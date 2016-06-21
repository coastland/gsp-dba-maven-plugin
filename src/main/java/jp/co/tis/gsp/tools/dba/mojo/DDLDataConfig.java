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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

public class DDLDataConfig extends AbstractMojo {

	@Parameter(defaultValue = "target/ddl", required = true)
	protected File ddlDirectory;

	@Parameter(required = true)
	protected File extraDdlDirectory;

	@Parameter(property = "gsp-dba.dataDirectory", required = true)
	protected File dataDirectory;

	public File getDdlDirectory() {
		return ddlDirectory;
	}

	public void setDdlDirectory(File ddlDirectory) {
		this.ddlDirectory = ddlDirectory;
	}

	public File getExtraDdlDirectory() {
		return extraDdlDirectory;
	}

	public void setExtraDdlDirectory(File extraDdlDirectory) {
		this.extraDdlDirectory = extraDdlDirectory;
	}

	public File getDataDirectory() {
		return dataDirectory;
	}

	public void setDataDirectory(File dataDirectory) {
		this.dataDirectory = dataDirectory;
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// NOP
	}

}
