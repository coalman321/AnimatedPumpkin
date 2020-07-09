package main;

import com.org.ConsoleLibs.ConOutMain;
import com.org.MiscLibs.RequiresCatch;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class main2 {

    private static String pathToOpenCV = "D:\\Github Repos\\AnimatedPumpkin\\Java\\opencv\\";

    public static void main(String[] args){
        ConOutMain.createConsole(false);
        String camPath = "rtsp://admin:admin@192.168.1.175:8554/CH001.sdp";
        String casPath = pathToOpenCV + "build\\etc\\haarcascades\\haarcascade_frontalface_default.xml";
        String piIP = "192.168.1.43";
        System.out.println("created console");
        try {
            UDPServer server = new UDPServer(piIP, 1110, false);
            System.out.println("opened client connection\ndiscovering camera");
            FaceDetection detection = new FaceDetection(camPath, casPath, 2 * 1000, 10*1000);
            System.out.println("starting face detection");
            detection.startCaptureThread();
            System.out.println("waiting for system startup");
            while (!detection.hasInit()) { RequiresCatch.Delay(100); } //allow time to begin
            System.out.println("face detection thread started");
            System.out.println("image size: " + detection.getFrameSize()[0] + " " + detection.getFrameSize()[1]);
            double framex = detection.getFrameSize()[0];
            double scale = 0.0;
            ArrayList<Integer> unsortedfaces;
            while (!ConOutMain.getRef().inputarea.getText().contains("exit")){
                RequiresCatch.Delay(33); // fixes lag issues
                if(detection.getLastFaces().peek() == null) scale = 50;
                else{
                    unsortedfaces = detection.getLastFaces().getList();
                    Collections.sort(unsortedfaces);
                    scale = (unsortedfaces.get(0)/ framex) * 100;
                }
                System.out.println(String.format("%02d, %1d", (int)scale, (detection.hasChangedRecent()? 1 : 0)));
                server.sendMsg(String.format("%02d, %1d", (int)scale, (detection.hasChangedRecent()? 1 : 0)));
                //50.0, 1
            }
            System.out.println("stopping");
            server.close();
            detection.close();
            RequiresCatch.Delay(1000);
            System.exit(0);

        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
