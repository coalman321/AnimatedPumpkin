package main;

import com.org.MiscLibs.RequiresCatch;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class FaceDetection {

    private static String pathToOpenCV = "D:\\Github Repos\\AnimatedPumpkin\\Java\\opencv\\";

    static{
        System.load(pathToOpenCV + "build\\java\\x64\\opencv_java343.dll");
        System.load(pathToOpenCV + "build\\bin\\opencv_ffmpeg343_64.dll");
    }

    private VideoCapture capture;
    private CascadeClassifier faceClass;
    private boolean wantStop = false, hasInit = false;
    private long detectWindow;
    private volatile long lastDetectTime = 0;
    private volatile List<Rect> faceList = new ArrayList<>();
    private volatile CircularList<Integer> lastFaces = new CircularList<>(5);
    private volatile int hits = 0;
    private volatile int[] imageSize = new int[2];
    private TimeLatchedBoolean tBool;

    private Runnable run = () -> {
        Mat color = new Mat();
        capture.read(color);
        imageSize[0] = color.cols();
        imageSize[1] = color.rows();
        Mat grey = new Mat();
        MatOfRect faces = new MatOfRect();
        while (!wantStop) {
            capture.read(color);
            Imgproc.cvtColor(color, grey, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(grey, grey);
            Core.flip(grey, grey,0);
            faceClass.detectMultiScale(grey, faces, 1.3, 5);
            faceList = faces.toList();
            hasChanged(faceList);
            hasInit = true;
            RequiresCatch.Delay(9); //pull back timing to output of camera
        }
    };

    public FaceDetection(String videoPath, String cascadePath,long detectWindow, int interactWindow) throws Exception{
        this.detectWindow = detectWindow * 1000000; //convert miliseconds to nanoseconds
        tBool = new TimeLatchedBoolean(interactWindow);
        capture = new VideoCapture(videoPath);
        if(!capture.isOpened()) {
            throw new Exception("Failed to open capture. Check if path is valid");
        }

        faceClass = new CascadeClassifier(cascadePath);
        if(faceClass.empty()){
            throw new Exception("Failed to open classifier. Check if path is valid");
        }
    }

    public synchronized void startCaptureThread(){
        Thread thread = new Thread(run);
        thread.start();
    }

    private void hasChanged(List<Rect> current){
        if(lastFaces.hasModified() && hits < 10) hits ++; // if we detect and hits is less than 10
        else if(hits > 0) hits --; //keep hits from going waay negative
        if(hits > 3) lastDetectTime = System.nanoTime(); // reset detection timer to now
        current.forEach(face -> lastFaces.add(face.x));
    }

    public boolean hasChangedRecent(){
        tBool.set((lastDetectTime + detectWindow) > System.nanoTime());
        return tBool.get();
    }

    public boolean hasInit() {
        return hasInit;
    }

    public void close(){
        wantStop = true;
        capture.release();
    }

    public CircularList<Integer> getLastFaces() {
        return lastFaces;
    }

    public int[] getFrameSize(){
        return imageSize;
    }
}
