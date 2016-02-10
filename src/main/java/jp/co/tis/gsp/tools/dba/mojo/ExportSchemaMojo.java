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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.util.DialectUtil;

/**
 * @author kawasima
 */
@Mojo(name = "export-schema", requiresProject = true)
public class ExportSchemaMojo extends AbstractDbaMojo {

    @Parameter(defaultValue = "target/dump")
	protected File outputDirectory;
    
    public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	@Component( role = Archiver.class, hint = "jar" )
    protected JarArchiver jarArchiver;

    @Component
    private MavenProject project;

	@Override
	protected void executeMojoSpec() throws MojoExecutionException, MojoFailureException {
		Dialect dialect = DialectUtil.getDialect();
		if (!outputDirectory.exists()) {
			try {
				FileUtils.forceMkdir(outputDirectory);
			} catch (IOException e) {
				throw new MojoExecutionException("Can't create dump output directory." + outputDirectory, e);
			}
		}
		getLog().info(schema+"スキーマのExportを開始します。:" + outputDirectory + System.getProperty("line.separator") + dmpFile);
		
		File exportFile;
		try {
			exportFile = dialect.exportSchema();
		} catch (Exception e) {
			throw new MojoExecutionException("データのExportに失敗しました。 ", e);
		}
        jarArchiver.addFile(exportFile, exportFile.getName());
        jarArchiver.setDestFile(new File(outputDirectory, jarName()));
        try {
            jarArchiver.createArchive();
        } catch(IOException e) {
            throw new MojoExecutionException("アーカイブに失敗しました。", e);
        }
		getLog().info(schema+"スキーマのExport完了 ");
	}


    private String jarName() {
        if (project == null) {
            return "dump-test.jar";
        } else {
            return project.getArtifactId() + "-testdata-" + project.getVersion() + ".jar";
        }
    }
}
