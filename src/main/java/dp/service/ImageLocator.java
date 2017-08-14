package dp.service;

import marvin.image.MarvinImage;
import marvin.image.MarvinSegment;
import marvin.io.MarvinImageIO;

//import java.awt.*;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static marvin.MarvinPluginCollection.*;

/**
 * Created by king_luffy on 2017/8/12.
 */
public class ImageLocator {
    public ImageLocator(){
        MarvinImage original = MarvinImageIO.loadImage("./配置文件界面.png");
        MarvinImage subimage = MarvinImageIO.loadImage("./配置块.png");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println(df.format(new Date(System.currentTimeMillis())));
        MarvinSegment seg1;

        int startx = 0, starty = 0;
        seg1 = findSubimage(subimage, original, startx, starty, 0.97);
        if(seg1 != null){
            System.out.println("Found:" + seg1.x1 + " " + seg1.y1 + " width:"+ (seg1.x2-seg1.x1) + " height:"+(seg1.y2-seg1.y1) );
            drawRect(original, seg1.x1, seg1.y1, seg1.x2-seg1.x1, seg1.y2-seg1.y1);
            MarvinImageIO.saveImage(original, "./result.png");
        }else{
            System.out.println("NOT FOUND");
        }

//        List<MarvinSegment> segmentList = findAllSubimages(subimage,original,0.3);
//        if(segmentList!=null && segmentList.size()>0){
//            for (MarvinSegment seg1:segmentList) {
//
//                System.out.println("Found:" + seg1.x1 + " " + seg1.y1 + " width:"+ (seg1.x2-seg1.x1) + " height:"+(seg1.y2-seg1.y1) );
//                drawRect(original, seg1.x1, seg1.y1, seg1.x2-seg1.x1, seg1.y2-seg1.y1);
//                MarvinImageIO.saveImage(original, "E:\\msC_desktop\\window_out.png");
//            }
//        }else{
//            System.out.println("NOT FOUND");
//        }
        //829,315
        //drawRect(original, 355, 65, 534, 272);
        //MarvinImageIO.saveImage(original, "./test.png");
        System.out.println(df.format(new Date(System.currentTimeMillis())));

    }
    private void drawRect(MarvinImage image, int x, int y, int width, int height){
        //x-=4; y-=4; width+=8; height+=8;
        image.drawRect(x, y, width, height, Color.red);
    }
    public static void main(String[] args) {
        new ImageLocator();

        System.out.println("over");
    }
}
