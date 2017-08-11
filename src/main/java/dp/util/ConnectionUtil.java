package dp.util;

import com.alibaba.fastjson.JSON;
import dp.po.DBConfig;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by king_luffy on 2017/7/25.
 */
public class ConnectionUtil {

    private static final Logger logger = Logger.getLogger(ConnectionUtil.class);

    public static final String SQLSERVER_URL = "jdbc:jtds:sqlserver://%s:%d;DatabaseName=%s;";
    public static final Integer DEFAULT_PORT = 1433;

    public static DBConfig tryConnection(DBConfig dbConfig){

        DBConfig result = null;

        logger.info("Test default port, dbConfig:" + JSON.toJSONString(dbConfig));
        DBConfig info = getValidConnectInfo(dbConfig.getDbIp(), DEFAULT_PORT, dbConfig);
        if (info != null) {
            logger.info("Fetch db info success!");
            return info;
        }

        logger.info("Test with shell,dbConfig:" + JSON.toJSONString(dbConfig));
        List<Long> pids = getSqlServerPids();
        List<String> hostPorts = getHostsAndPorts(pids);
        for (String hostPort : hostPorts) {
            String[] arr = hostPort.split(":");
            String host = arr[0];
            int port = Integer.parseInt(arr[1]);
            DBConfig configuration = getValidConnectInfo(host, port, dbConfig);
            if (configuration != null) {
                logger.info("Fetch db info success!");
                return configuration;
            }
        }

        logger.error("Fetch db info failed!");

        return result;
    }

    public static String formatUrl(DBConfig dbConfig){
        return String.format(SQLSERVER_URL,dbConfig.getDbIp(),DEFAULT_PORT,dbConfig.getDbName());
    }

    public static DBConfig getValidConnectInfo(String host, int port, DBConfig dbConfig) {
        DBConfig configuration = null;
        String url = String.format(SQLSERVER_URL, host, port, dbConfig.getDbName());
        try (Connection connection = DriverManager.getConnection(url, dbConfig.getUserName(), dbConfig.getUserPwd())) {
            logger.info("Test connection success, connection:" + connection);
            configuration = new DBConfig();
            configuration.setUserName(dbConfig.getUserName());
            configuration.setUserPwd(dbConfig.getUserPwd());
            configuration.setUrl(url);
        } catch (Exception e) {
            logger.error("Try connect database failed, url:" + url, e);
        }
        return configuration;
    }

    public static List<Long> getSqlServerPids() {
        List<Long> pids = new ArrayList<>();
        String tasks = ProcessHelper.execCMD("tasklist /nh /fi \"imagename eq sqlserver.exe\"");
        if (isEmpty(tasks)) {
            logger.error("Execute tasklist failed, get empty result.");
            return pids;
        }
        for (String task : tasks.split("\n")) {
            if (isEmpty(task)) {
                continue;
            }
            String[]  taskArr = task.split("\\s+");
            try {
                pids.add(Long.parseLong(taskArr[1]));
            } catch (Exception e) {
                logger.error("Parse pid failed, taskArr:" + JSON.toJSONString(taskArr), e);
            }
        }
        return pids;
    }

    public static List<String> getHostsAndPorts(List<Long> pids) {
        List<String> result = new ArrayList<>();
        String cmd = "cmd /C netstat -ano | findstr \"%d\"";
        for (Long pid : pids) {
            String rows = ProcessHelper.execCMD(String.format(cmd, pid));
            if (isEmpty(rows)) {
                logger.error("Cannot get host and port by pid, pid=" + pid);
                continue;
            }
            for (String row : rows.split("\n")) {
                if (row == null || isEmpty(row.trim())) {
                    continue;
                }
                String[]  taskArr = row.trim().split("\\s+");
                if (taskArr.length > 1 && taskArr[1].matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d+")) {
                    result.add(taskArr[1]);
                }
            }
        }
        return result;
    }
    public static boolean isEmpty(String input){
        if(input==null || input.isEmpty()){
            return true;
        }else {
            return false;
        }
    }

}
