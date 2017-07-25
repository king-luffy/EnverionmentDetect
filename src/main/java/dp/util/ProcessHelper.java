package dp.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.apache.log4j.Logger;
/**
 * Created by king_luffy on 2017/7/25.
 */
public class ProcessHelper {
    private static final Logger LOGGER = Logger.getLogger(ProcessHelper.class);

    public ProcessHelper() {
    }

    public static void main(String[] args) {
        System.out.println(execCMD("cmd.exe /c netstat /an | findstr 1433"));
    }

    public static String execCMD(String cmsStr) {
        if(cmsStr==null || cmsStr.isEmpty()) {
            return "";
        } else {
            try {
                Runtime rt = Runtime.getRuntime();
                Process pr = rt.exec(cmsStr);
                pr.getOutputStream().close();
                BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream(), Charset.forName("GBK")));

                StringBuilder sb = new StringBuilder();
                String line;
                while((line = br.readLine()) != null) {
                    ;sb.append(line);
                    LOGGER.info(line);
                }

                return sb.toString();
            } catch (Exception var6) {
                LOGGER.error("error in execCMD", var6);
                return "";
            }
        }
    }
}
