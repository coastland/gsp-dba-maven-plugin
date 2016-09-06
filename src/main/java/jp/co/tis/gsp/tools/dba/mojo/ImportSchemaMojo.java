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
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.seasar.framework.util.JarFileUtil;

import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.dialect.DialectFactory;
import jp.co.tis.gsp.tools.dba.dialect.param.ImportParams;

/**
 * import-schema.
 * 
 * リポジトリから取得したダンプファイルをインポートする。
 * 
 * @author kawasima
 */
@Mojo(name = "import-schema", requiresProject = true)
public class ImportSchemaMojo extends AbstractDbaMojo {
    @Parameter(defaultValue = "target/dump")
	protected File inputDirectory;

    @Component
    private MavenProject project;

    @Component
    protected RepositorySystem repositorySystem;

    @Parameter(property="localRepository", required = true, readonly = true)
    private ArtifactRepository localRepository;

    @Parameter(defaultValue = "${project.groupId}")
    protected String groupId;

    @Parameter(defaultValue = "${project.artifactId}")
    protected String artifactId;

    @Parameter(defaultValue = "${project.version}")
    protected String version;

    final protected String delimiter = ";";
    final Charset UTF8 = Charset.forName("UTF-8");

    @Override
	protected void executeMojoSpec() throws MojoExecutionException, MojoFailureException {
		Dialect dialect = DialectFactory.getDialect(url, driver);
		dialect.createUser(user, password, adminUser, adminPassword);
		dialect.dropAll(user, password, adminUser, adminPassword, schema);

        Artifact artifact = repositorySystem.createArtifact(groupId, artifactId, version, "jar");
        ArtifactResolutionRequest schemaArtifactRequest = new ArtifactResolutionRequest()
                .setRemoteRepositories(project.getRemoteArtifactRepositories())
                .setArtifact(artifact);
        ArtifactResolutionResult schemaArtifactResult = repositorySystem.resolve(schemaArtifactRequest);
        if (schemaArtifactResult.hasExceptions()) {
            for (Exception e : schemaArtifactResult.getExceptions()) {
                getLog().error("インポートするダンプファイルの取得に失敗しました。", e);
            }
        }
        
        ImportParams params = createImportParams();

        JarFile jarFile = JarFileUtil.create(new File(localRepository.getBasedir(),
                localRepository.pathOf(artifact)));

        try {
            
		    if(!inputDirectory.exists()) {
		    	try {
			    	FileUtils.forceMkdir(inputDirectory);
    			} catch (IOException e) {
	    			throw new MojoExecutionException("Can't create outputDirectory:" + inputDirectory);
		    	}
	    	}
        	
            // jarの解凍
            extractJarAll(jarFile, inputDirectory.getAbsolutePath());
            
        } catch(IOException e) {
            throw new MojoExecutionException("", e);
        }

		getLog().info("スキーマのインポートを開始します。");
		dialect.importSchema(params);
		getLog().info("スキーマのインポートを終了しました");
	}
    
	private ImportParams createImportParams() {
        String importFilename = StringUtils.defaultIfEmpty(dmpFile, schema + ".dmp");
        File importFile = new File(inputDirectory, importFilename);

	    ImportParams param = new ImportParams();
	    
	    param.setUser(user);
	    param.setPassword(password);
	    param.setAdminUser(adminUser);
	    param.setAdminPassword(adminPassword);
	    param.setSchema(schema);
	    param.setDelimiter(delimiter);
	    param.setCharset(UTF8);
	    param.setOnError(onError);
	    param.setInputDirectory(inputDirectory);
	    param.setDumpFile(importFile);
	    param.setLogger(getLog());
	    
	    return param;
	}

    /**
     * 指定Jarファイルを指定ディレクトリに解凍します。
     * 
     * @param jar
     * @param destDir
     * @throws IOException
     */
    private void extractJarAll(JarFile jar, String destDir) throws IOException {
        Enumeration<JarEntry> enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
            java.io.File f = new java.io.File(destDir + java.io.File.separator + file.getName());
            if (file.isDirectory()) {
                f.mkdir();
                continue;
            }
            java.io.InputStream is = jar.getInputStream(file);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
            while (is.available() > 0) {
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }
     }

}
