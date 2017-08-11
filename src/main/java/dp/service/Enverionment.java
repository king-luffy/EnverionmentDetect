package dp.service;

import com.ice.jni.registry.*;
import dp.po.DBConfig;
import dp.util.ConnectionUtil;
import dp.util.ProcessHelper;
import org.apache.log4j.Logger;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by king_luffy on 2017/7/21.
 */
public class Enverionment {

    private static final Logger logger = Logger.getLogger(Enverionment.class);

    private static final String DLL_NAME_32 = "ICE_JNIRegistry";
    private static final String DLL_NAME_64 = "ICE_JNIRegistry_x64";
    private static String DLL_NAME = DLL_NAME_32;

    public static String systemName;

    public static void printHeadInfo(){
        logger.info("必须使用管理员权限运行本程序,否则一些信息将无法获取!");
    }

    public static void printEnverionment(){
        Properties props=System.getProperties(); //获得系统属性集
        String osName = props.getProperty("os.name");
        systemName = osName;
        String osArch = props.getProperty("os.arch");
        String osVersion = props.getProperty("os.version");
        logger.info("System Info ----------");
        logger.info("System name : "+osName);
        logger.info("System bit : "+osArch);
        logger.info("System version : "+osVersion);
    }

    public static void printSQLInfo(){
        System.loadLibrary(DLL_NAME);
        try {
            //Key to SQL
            RegistryKey child = Registry.HKEY_LOCAL_MACHINE.openSubKey("Software");
            RegistryKey microsoft = child.openSubKey("Microsoft");
            RegistryKey mysqlserver = null;
            try {
                mysqlserver = microsoft.openSubKey("MSSQLServer");
            }catch (NoSuchKeyException e){
                logger.info("No SQl Server install in current Computer!");
                return;
            }
            logger.info("[ SQL Server Info ]");

            //Setup key for Sql server
            RegistryKey sqlsetupinfo = mysqlserver.openSubKey("Setup");
            Enumeration enumeration =sqlsetupinfo.keyElements();
            while (enumeration.hasMoreElements()){
                String eleName = (String)enumeration.nextElement();
                String tmpResult;
                switch (eleName){
                    case "Edition":
                        tmpResult =sqlsetupinfo.getStringValue("Edition");
                        logger.info("SQL Edition : "+tmpResult);
                        break;
                    case "Patchlevel":
                        tmpResult =sqlsetupinfo.getStringValue("Patchlevel");
                        logger.info("SQL Version : "+tmpResult+"（若要支持jdbc,则至少需要大版本8，小版本2039之后，包括2039，具体见：https://wiki.sankuai.com/pages/viewpage.action?pageId=998724980）");
                        break;
                    case "SQLPath":
                        tmpResult = sqlsetupinfo.getStringValue("SQLPath");
                        logger.info("SQL Install Root : "+tmpResult);
                        break;
                    case "SQLDataRoot":
                        tmpResult = sqlsetupinfo.getStringValue("SQLDataRoot");
                        logger.info("SQL DB Root : "+tmpResult);
                        break;
                    case "Scripts":
                        RegistryValue otherInfoValue = sqlsetupinfo.getValue("Scripts");
                        if(otherInfoValue.getType()==RegistryValue.REG_MULTI_SZ){
                            byte[] bytes =otherInfoValue.getByteData();
                            tmpResult = new String(bytes);
                        }else{
                            tmpResult = sqlsetupinfo.getStringValue("Scripts");
                        }
                        logger.info("SQL Other Info : "+tmpResult);
                        break;
                }
            }

            //MSSQLServer/CurrentVersion
            RegistryKey mssqlserver = mysqlserver.openSubKey("MSSQLServer");
            RegistryKey currentVersion = mssqlserver.openSubKey("CurrentVersion");
            enumeration =currentVersion.keyElements();
            while (enumeration.hasMoreElements()){
                String eleName = (String)enumeration.nextElement();
                String tmpResult;
                switch (eleName){
                    case "CurrentVersion":
                        tmpResult =sqlsetupinfo.getStringValue("CurrentVersion");
                        logger.info("Current Version : "+tmpResult);
                        break;
                    case "CSDVersion":
                        tmpResult =sqlsetupinfo.getStringValue("CSDVersion");
                        logger.info("CSDVersion : "+tmpResult);
                        break;
                }
            }

        } catch (RegistryException e) {
            logger.error(e);
        }
    }

    public static void print1433PortInfo(){
        logger.info("[ 1433 Port Info ]");
        ProcessHelper.execCMD("cmd.exe /c netstat /an | findstr 1433");
    }

    public static void main(String[] args) {
        //Enverionment Detect
        logger.info("[ Begin Enverionment Detection: ]");
        printHeadInfo();
        printEnverionment();

        if(systemName.toLowerCase().contains("windows")){
            printSQLInfo();
            print1433PortInfo();
        }else{

        }

        logger.info("[ End Enverionment Detection. ]");
        logger.info("");

        //DB info Fetch
        logger.info("[ Begin Fetch DBInfo: ]");

        DBComparer dbComparer = new DBComparer();
        DBConfig dbConfig = new DBConfig();
        dbConfig.setDbIp("172.16.7.128");
        dbConfig.setDbName("krd");
        dbConfig.setUserName("sa");
        dbConfig.setUserPwd("123456");
        dbConfig.setUrl(ConnectionUtil.formatUrl(dbConfig));
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (dbConfig!=null) {
            dbComparer.fetchDBFeilds(dbConfig);
        }
        logger.info("[ End Fetch DBInfo. ]");
        logger.info("");
    }
}
