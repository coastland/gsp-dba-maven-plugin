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
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
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
    
    /*** LoadData Param **/
    @Parameter
    @SuppressWarnings("rawtypes")
    protected Map specifiedEncodingFiles;

    /*** jar内のディレクトリ構造 **/
    private final String DDL_DIR_NAME = "ddlDirectory";
    private final String EXTRADDL_DIR_NAME = "extraDdlDirecotry";
    private final String DATA_DIR_NAME = "dataDirectory";

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

        // ddl及びdataディレクトリの取り出し
        try {
            if (!inputDirectory.exists()) {
                try {
                    FileUtils.forceMkdir(inputDirectory);
                } catch (IOException e) {
                    throw new MojoExecutionException("Can't create dump output directory." + inputDirectory, e);
                }
            }

            JarFile jarFile = JarFileUtil
                    .create(new File(localRepository.getBasedir(), localRepository.pathOf(artifact)));
            allExtract(jarFile, inputDirectory.getPath());
        } catch (IOException e) {
            throw new MojoExecutionException("", e);
        }

        getLog().info("スキーマのインポートを開始します。:");
        createExecuteDdlMojo().executeMojoSpec();
        createLoadDataMojo().executeMojoSpec();
        getLog().info("スキーマのインポートを終了しました");
	}
    private ExecuteDdlMojo createExecuteDdlMojo() {
        ExecuteDdlMojo mojo = new ExecuteDdlMojo();
        copyParameter(mojo);

        mojo.setLog(getLog());
        mojo.ddlDirectory = new File(inputDirectory, DDL_DIR_NAME);
        mojo.extraDdlDirectory = new File(inputDirectory, EXTRADDL_DIR_NAME);

        return mojo;
    }

    private LoadDataMojo createLoadDataMojo() {
        LoadDataMojo mojo = new LoadDataMojo();
        copyParameter(mojo);

        mojo.setLog(getLog());
        mojo.dataDirectory = new File(inputDirectory, DATA_DIR_NAME);
        mojo.specifiedEncodingFiles = specifiedEncodingFiles;

        return mojo;
    }

    private void allExtract(JarFile jar, String destDir) throws IOException {
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
