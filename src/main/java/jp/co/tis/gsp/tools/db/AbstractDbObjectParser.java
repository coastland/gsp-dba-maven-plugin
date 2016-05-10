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

package jp.co.tis.gsp.tools.db;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jp.co.tis.gsp.tools.db.beans.Erd;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.Locale;

public class AbstractDbObjectParser {

	protected File outputDirectory;
	protected String schema;
	protected String url;
	protected String user;
	protected String ddlTemplateFileDir;
	private final Configuration fmConfig = new Configuration();
	protected final LinkedList<TemplateLoader> templateLoaderList = new LinkedList<TemplateLoader>();
	protected TypeMapper typeMapper;

	public AbstractDbObjectParser() {
		super();
		fmConfig.setEncoding(Locale.JAPANESE, "UTF-8");
		fmConfig.setNumberFormat(System.getProperty("gsp.freemarker.number_format", "#"));
	}

	protected void setupTemplateLoader() {
		if (ddlTemplateFileDir != null) {
			try {
				FileTemplateLoader templateLoader = new FileTemplateLoader(new File("./" + ddlTemplateFileDir));
				templateLoaderList.add(templateLoader);
			} catch (IOException e) {
				// configurationが設定されているにも関わらず到達できない
				throw new IllegalArgumentException("failed to reach project resource", e);
			}
		}

		if (url != null) {
			String[] urlTokens = StringUtils.split(url, ':');
			if(urlTokens.length < 3) {
				throw new IllegalArgumentException("url isn't jdbc url format.");
			}
			templateLoaderList.add(
					new ClassTemplateLoader(Erd.class, "/jp/co/tis/gsp/tools/db/template/"
							+urlTokens[1]+"/")
			);
		}

		templateLoaderList.add(
				new ClassTemplateLoader(AbstractDbObjectParser.class, "/jp/co/tis/gsp/tools/db/template/"));
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	protected Writer getWriter(String name) throws IOException {
		if (outputDirectory == null) {
			return new OutputStreamWriter(System.out);
		}

		File outputFile = new File(outputDirectory, name + ".sql");
		return new FileWriter(outputFile);
	}

	protected Template getTemplate(String templateName) throws IOException {
		Template template = null;

		for(TemplateLoader templateLoader :templateLoaderList) {
			fmConfig.setTemplateLoader(templateLoader);
			try {
				template = fmConfig.getTemplate(templateName);
			} catch(IOException ignore) {
			}
			if(template != null) return template;
		}
		throw new IOException("テンプレート(" + templateName + ")が見つかりません");
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTypeMapper(TypeMapper typeMapper) {
		this.typeMapper = typeMapper;
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

    public void setUser(String user) {
        this.user = user;
    }

	public void setDdlTemplateFileDir(String ddlTemplateFileDir) {
		this.ddlTemplateFileDir = ddlTemplateFileDir;
	}
}