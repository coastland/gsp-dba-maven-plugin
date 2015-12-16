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

package jp.co.tis.gsp.tools.dba.dialect;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.persistence.GenerationType;

import jp.co.tis.gsp.tools.db.TypeMapper;
import net.unit8.solr.jdbc.ConnectionTypeDetector;
import net.unit8.solr.jdbc.SolrDriver;
import net.unit8.solr.jdbc.impl.EmbeddedConnectionImpl;
import net.unit8.solr.jdbc.impl.SolrConnection;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.util.DriverManagerUtil;
import org.seasar.framework.util.tiger.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrDialect extends Dialect {
	private String url;
    private String solrUrl;
    private Map<String, String> urlParams;
    private static final Logger logger = LoggerFactory.getLogger(SolrDialect.class);
    static {
        DriverManagerUtil.registerDriver(SolrDriver.class);
    }

    private Map<Integer, String> typeToNameMap = Maps
            .map(Types.BIGINT, "BIGINT")
            .$(Types.BLOB, "BLOB")
            .$(Types.BOOLEAN, "BOOLEAN")
            .$(Types.CHAR, "CHAR")
            .$(Types.CLOB, "TEXT")
            .$(Types.DATE, "DATE")
            .$(Types.DECIMAL, "NUMBER")
            .$(Types.DOUBLE, "DOUBLE")
            .$(Types.FLOAT, "FLOAT")
            .$(Types.INTEGER, "INT")
            .$(Types.TIMESTAMP, "TIMESTAMP")
            .$(Types.VARCHAR, "VARCHAR")
            .$(Types.ARRAY, "ARRAY")
            .$();

	@Override
	public void createUser(String user, String password, String adminUser,
			String adminPassword) throws MojoExecutionException {
	}

	@Override
	public void dropAll(String user, String password, String adminUser,
			String adminPassword, String schema) throws MojoExecutionException {
        if (solrUrl == null)
            parseUrl();

        try {
            SolrServer solrServer = null;
            if (solrUrl.startsWith("http://") || solrUrl.startsWith("https://")) {
                solrServer = new HttpSolrServer(solrUrl);
                solrServer.deleteByQuery("*:*");
            } else {
                solrServer = createSolrServer(urlParams);
                solrServer.deleteByQuery("*:*");
                ((EmbeddedSolrServer)solrServer).getCoreContainer().shutdown();
            }
		} catch (Exception e) {
			throw new MojoExecutionException("データ削除中にエラーが発生しました", e);
		}
	}

	@Override
	public void exportSchema(String user, String password, String schema,
			File dumpFile) throws MojoExecutionException {
        String solrUrl = url.substring("jdbc:solr:".length());
        try {
            SolrConnection connection = ConnectionTypeDetector.getInstance().find(solrUrl);
            if (connection instanceof EmbeddedConnectionImpl) {
                System.err.println(solrUrl);
                EmbeddedSolrServer solrServer = (EmbeddedSolrServer) connection.getSolrServer();
                SolrCore core = solrServer.getCoreContainer().getCore(solrServer.getCoreContainer().getDefaultCoreName());
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dumpFile.getAbsolutePath()));
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Export error", e);
        }

	}

	@Override
	public void importSchema(String user, String password, String schema,
			File dumpFile) throws MojoExecutionException {
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

    @Override
    public TypeMapper getTypeMapper() {
        return new TypeMapper(typeToNameMap);
    }
    @Override
    public GenerationType getGenerationType() { return null; }

    @Override
    public boolean canPrintIndex() { return false; }

    @Override
    public boolean canPrintForeignKey() { return false; }

    @Override
    public boolean canPrintView() { return false; }

    private void parseUrl() {
        String[] urlParamTokens = StringUtils.split(url, ";");
        String urlWithoutParams = urlParamTokens[0];
        urlParams = new HashMap<String, String>();
        if (urlParamTokens.length > 1) {
            for (int i=1; i < urlParamTokens.length; i++) {
                String[] nameValuePair = StringUtils.split(urlParamTokens[i], "=", 2);
                if (nameValuePair.length != 2)
                    continue;
                urlParams.put(nameValuePair[0], nameValuePair[1]);
            }
        }
        String[] urlTokens = StringUtils.split(urlWithoutParams, ":", 3);
        solrUrl = urlTokens[2];
    }

    private EmbeddedSolrServer createSolrServer(Map<String,String> urlParams) {
        String solrHome = urlParams.get("SOLR_HOME");
        StringBuilder paramStr = new StringBuilder();
        if (solrHome != null) {
            System.setProperty("solr.solr.home", solrHome);
            paramStr.append(";SOLR_HOME=").append(solrHome);
        }
        String dataDir = urlParams.get("DATA_DIR");
        if (dataDir != null) {
            System.setProperty("solr.data.dir", dataDir);
            paramStr.append(";DATA_DIR=").append(dataDir);
        }
        try {
            DriverManager.getConnection("jdbc:solr:s" + paramStr.toString());
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
        CoreContainer.Initializer initializer = new CoreContainer.Initializer();
        CoreContainer coreContainer = initializer.initialize();
        return new EmbeddedSolrServer(coreContainer, coreContainer.getDefaultCoreName());

    }
}
