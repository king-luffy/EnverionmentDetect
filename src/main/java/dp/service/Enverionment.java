package dp.service;

import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by king_luffy on 2017/7/21.
 */
public class Enverionment {

    private static final Logger logger = Logger.getLogger(Enverionment.class);

    private static final String DLL_NAME_32 = "ICE_JNIRegistry";
    private static final String DLL_NAME_64 = "ICE_JNIRegistry_x64";
    private static String DLL_NAME = DLL_NAME_32;

    public static void printEnverionment(){
        Properties props=System.getProperties(); //获得系统属性集
        String osName = props.getProperty("os.name");
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
            RegistryKey child = Registry.HKEY_CURRENT_USER.openSubKey("Software");
            RegistryKey microsoft = child.openSubKey("Microsoft");
            RegistryKey mysqlserver = microsoft.openSubKey("MSSQLServer");
            RegistryKey sqlsetupinfo = mysqlserver.openSubKey("Setup");
            String edition = sqlsetupinfo.getStringValue("Edition");
            String version = sqlsetupinfo.getStringValue("Patchlevel");
            String sqlRoot = sqlsetupinfo.getStringValue("SQLPath");
            String sqlDBRoot = sqlsetupinfo.getStringValue("SQLDateRoot");
            String otherInfo = sqlsetupinfo.getStringValue("Scripts");

            logger.info("SQL Server Info ----------");
            logger.info("SQL Edition : "+edition);
            logger.info("SQL Version : "+version);
            logger.info("SQL Install Root : "+sqlRoot);
            logger.info("SQL DB Root : "+sqlDBRoot);
            logger.info("SQL Other Info : "+otherInfo);


        } catch (RegistryException e) {
            logger.error(e);
        }
    }

    public static void main(String[] args) {
        printEnverionment();
        printSQLInfo();
    }
}
