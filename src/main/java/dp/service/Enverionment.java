package dp.service;

import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import com.ice.jni.registry.RegistryValue;
import dp.util.ProcessHelper;
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
            RegistryKey mysqlserver = microsoft.openSubKey("MSSQLServer");

            //Setup key for Sql server
            RegistryKey sqlsetupinfo = mysqlserver.openSubKey("Setup");
            String edition = sqlsetupinfo.getStringValue("Edition");
            String version = sqlsetupinfo.getStringValue("Patchlevel");
            String sqlRoot = sqlsetupinfo.getStringValue("SQLPath");
            String sqlDBRoot = sqlsetupinfo.getStringValue("SQLDataRoot");
            String otherInfo = null;
            RegistryValue otherInfoValue = sqlsetupinfo.getValue("Scripts");
            if(otherInfoValue.getType()==RegistryValue.REG_MULTI_SZ){
                byte[] bytes =otherInfoValue.getByteData();
                otherInfo = new String(bytes);
            }else{
                otherInfo = sqlsetupinfo.getStringValue("Scripts");
            }

            //print info
            logger.info("--------------------SQL Server Info --------------------");
            logger.info("SQL Edition : "+edition);
            logger.info("SQL Version : "+version+"（若要支持jdbc,则至少需要大版本8，小版本2039之后，包括2039，具体见：https://wiki.sankuai.com/pages/viewpage.action?pageId=998724980）");
            logger.info("SQL Install Root : "+sqlRoot);
            logger.info("SQL DB Root : "+sqlDBRoot);
            logger.info("SQL Other Info : "+otherInfo);

        } catch (RegistryException e) {
            logger.error(e);
        }
    }

    public static void print1433PortInfo(){
        logger.info("--------------------1433 Port Info --------------------");
        ProcessHelper.execCMD("cmd.exe /c netstat /an | findstr 1433");
    }

    public static void main(String[] args) {
        logger.info("--------------------Begin Enverionment Detection --------------------");
        printHeadInfo();
        printEnverionment();

        if(systemName.toLowerCase().contains("windows")){
            printSQLInfo();
            print1433PortInfo();
        }else{

        }


        logger.info("--------------------End Enverionment Detection --------------------");
    }
}
