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
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.JarFileUtil;

import jp.co.tis.gsp.tools.dba.dialect.Dialect;
import jp.co.tis.gsp.tools.dba.dialect.DialectFactory;

/**
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

    @Override
	protected void executeMojoSpec() throws MojoExecutionException, MojoFailureException {
		Dialect dialect = DialectFactory.getDialect(url);
		Beans.copy(this, dialect).execute();
		dialect.dropAll();
		dialect.createUser();

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

        JarFile jarFile = JarFileUtil.create(new File(localRepository.getBasedir(),
                localRepository.pathOf(artifact)));
        String importFilename = StringUtils.defaultIfEmpty(dmpFile, schema + ".dmp");
        InputStream is = null;
        File importFile = new File(inputDirectory, importFilename);
        JarEntry jarEntry = jarFile.getJarEntry(importFilename);
        if (jarEntry == null)
            throw new MojoExecutionException(importFilename + " is not found?");
        try {
            is = jarFile.getInputStream(jarEntry);
            FileUtils.copyInputStreamToFile(is, importFile);
        } catch(IOException e) {
            throw new MojoExecutionException("", e);
        } finally {
            IOUtils.closeQuietly(is);
        }

		getLog().info("スキーマのインポートを開始します。:" + importFile);
		dialect.importSchema(user, password, schema, importFile);
		getLog().info("スキーマのインポートを終了しました");
	}

}
