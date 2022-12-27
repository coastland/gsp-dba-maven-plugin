package jp.co.tis.gsp.tools.dba.dialect.param;

import java.io.File;

public class ExportParams {

    private String adminUser;

    private String adminPassword;

    private String user;

    private String password;

    private String connectIdentifier;

    private String schema;

    private File dumpFile;

    private File ddlDirectory;

    private File extraDdlDirectory;

    private File dataDirectory;

    private File outputDirectory;

    public String getUser() {
        return user;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConnectIdentifier() {
        return connectIdentifier;
    }

    public void setConnectIdentifier(String connectIdentifier) {
        this.connectIdentifier = connectIdentifier;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public File getDumpFile() {
        return dumpFile;
    }

    public void setDumpFile(File dumpFile) {
        this.dumpFile = dumpFile;
    }

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

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public File getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

}
