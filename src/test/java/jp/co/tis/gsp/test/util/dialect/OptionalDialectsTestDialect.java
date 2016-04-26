package jp.co.tis.gsp.test.util.dialect;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

import jp.co.tis.gsp.tools.db.TypeMapper;
import jp.co.tis.gsp.tools.dba.dialect.Dialect;

public class OptionalDialectsTestDialect extends Dialect {

	public void dropAll(String user, String password, String adminUser, String adminPassword, String schema)
			throws MojoExecutionException {
		throw new MojoExecutionException("OptionalDialectsTestDialect");
	}

	@Override
	public void exportSchema(String user, String password, String schema, File dumpFile) throws MojoExecutionException {

	}

	@Override
	public void importSchema(String user, String password, String schema, File dumpFile) throws MojoExecutionException {
	}

	@Override
	public void createUser(String user, String password, String adminUser, String adminPassword)
			throws MojoExecutionException {
	}

	@Override
	public TypeMapper getTypeMapper() {
		return null;
	}

}