package dp.service;

import com.alibaba.fastjson.JSON;
import dp.po.DBConfig;
import dp.po.DBInfo;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by king_luffy on 2017/7/25.
 */
public class DBComparer {

    private static final Logger logger = Logger.getLogger(DBComparer.class);

    public static final String GET_DB_ALL_FIELDS_INFO_SQL="SELECT a.name, b.rows FROM sysobjects AS a INNER JOIN sysindexes AS b ON a.id = b.id WHERE (a.type = 'u') AND (b.indid IN (0, 1)) ORDER BY a.name,b.rows DESC";

    public void fetchDBFeilds(DBConfig dbConfig){
        Connection connection = null;
        try {
            connection =DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUserName(), dbConfig.getUserPwd());
        } catch (SQLException e) {
            logger.error(e);
        }
        if (connection==null){
            return;
        }

        try {
            List<DBInfo> dbInfos = getRoomServiceFeeInfo(connection);

            if(dbInfos==null){
                return;
            }

            logger.info(JSON.toJSONString(dbInfos,true));

        } catch (Exception e) {
            logger.error(e);
        }finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }


    }

    public List<DBInfo> getRoomServiceFeeInfo(Connection conn) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(GET_DB_ALL_FIELDS_INFO_SQL);
            rs = stmt.executeQuery();

            return convertRS(rs, DBInfo.class);
        } finally {
            this.closeIO(stmt, rs);
        }
    }

    private void closeIO(Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }

            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException e) {
            logger.error("Fail to close stmt", e);

        }
    }

    public static <T> List<T> convertRS(ResultSet rs, Class<T> cls) throws Exception {
        // 检索此 ResultSet 对象的列的编号、类型和属性。
        List<T> list = new ArrayList<T>();
        ResultSetMetaData rsmd = rs.getMetaData();
        // 得到当前的列数
        int colCount = rsmd.getColumnCount();
        Field[] fields = cls.getDeclaredFields();
        while (rs.next()) { // while控制行数
            T obj = cls.newInstance();
            for (int i = 1; i <= colCount; i++) {// for循环控制列数
                String key = rsmd.getColumnName(i);// 得到当前列的列名
                for (int j = 0; j < fields.length; j++) {
                    Field f = fields[j];
                    if (f.getName().equalsIgnoreCase(key)) {
                        f = cls.getDeclaredField(f.getName());// 得到属性的set方法
                        f.setAccessible(true);// 把方法设置为可访问
                        f.set(obj, rs.getObject(i));
                        break;
                    }
                }
            }
            list.add(obj);
        }
        return list;
    }
}
