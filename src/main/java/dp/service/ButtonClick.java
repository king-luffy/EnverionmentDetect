package dp.service;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

/**
 * Created by king_luffy on 2017/8/14.
 */
public class ButtonClick {

    public static void moveMouseAndClick(){
        try
        {
            Robot myRobot = new Robot();

            int x = 100;
            int y = 100;

            // 移动鼠标到坐标（x,y)处，并点击左键
            myRobot.mouseMove(x, y);				// 移动鼠标到坐标（x,y）处
            myRobot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);		// 模拟按下鼠标左键
            myRobot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);	// 模拟释放鼠标左键
        } catch (AWTException e)
        {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        moveMouseAndClick();
    }
}
