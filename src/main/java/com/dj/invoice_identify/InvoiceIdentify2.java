//package com.dj.invoice_identify;
//
//import net.sourceforge.tess4j.ITesseract;
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//import org.opencv.core.*;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Create by ChenLei on 2020/5/7
// * Describe:
// */
//public class InvoiceIdentify2 {
//
//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    }
//
//    public static void start(String imagePath, String outImagePath) {
//        Mat image = Imgcodecs.imread(imagePath);
//        Mat newImage = new Mat(image.rows(), image.cols(), image.type());
//        image.copyTo(newImage);
//        // 灰值化
//        Imgproc.cvtColor(newImage, newImage, Imgproc.COLOR_BGR2GRAY);
//        // 高斯降噪
//        Imgproc.GaussianBlur(newImage, newImage, new Size(3, 3), 2, 2);
//        // Canny边缘检测
//        Imgproc.Canny(newImage, newImage, 20, 60, 3, false);
//        // 膨胀，连接边缘
//        Imgproc.dilate(newImage, newImage, new Mat(), new Point(-1, -1), 3, 1, new Scalar(1));
//        // 寻找轮廓
//        List<MatOfPoint> contours = new ArrayList<>();
//        Imgproc.findContours(newImage, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//        // 寻找最大面积的轮廓
//        int maxIndex = 0;
//        for (int i = 0; i < contours.size(); i++) {
//            Rect rect = Imgproc.boundingRect(contours.get(i));
//            Rect maxRect = Imgproc.boundingRect(contours.get(maxIndex));
//            if (rect.width * rect.height >= maxRect.width * maxRect.height) {
//                maxIndex = i;
//            }
//        }
////        Imgproc.drawContours(image, contours, maxIndex, new Scalar(255, 0, 0), 3);
////        Rect maxRect = Imgproc.boundingRect(contours.get(maxIndex));
////        Mat fImage = new Mat(image, maxRect);
////        Imgcodecs.imwrite(outImagePath, fImage);
//
//        // 用凸包计算出新的轮廓点
//        MatOfInt hull = new MatOfInt();
//        Imgproc.convexHull(contours.get(maxIndex), hull, false);
//        // 取出凸包的点
//        Point[] contourPoints = contours.get(maxIndex).toArray();
//        int[] indices = hull.toArray();
//        List<Point> newPoints = new ArrayList<>();
//        for (int index : indices) {
//            newPoints.add(contourPoints[index]);
//        }
//        // 将凸包转为矩形
//        MatOfPoint2f contourHull = new MatOfPoint2f();
//        contourHull.fromList(newPoints);
//        RotatedRect rotatedRect = Imgproc.minAreaRect(contourHull);
//        Point[] rotatedRectPoint = new Point[4];
//        rotatedRect.points(rotatedRectPoint);
////        for (int i = 0; i < 4; i++) {
////            Imgproc.line(image, rotatedRectPoint[i], rotatedRectPoint[(i + 1) % 4], new Scalar(0, 0, 255), 3);
////        }
//        List<Point> corners = new ArrayList<>();
//        for (Point point : rotatedRectPoint) {
//            corners.add(point);
//        }
//        // 对顶点顺时针排序
//        sortCorners(corners);
//        // 计算目标图像的尺寸
//        Point p0 = corners.get(0);
//        Point p1 = corners.get(1);
//        Point p2 = corners.get(2);
//        Point p3 = corners.get(3);
//        double space0 = getSpacePointToPoint(p0, p1);
//        double space1 = getSpacePointToPoint(p1, p2);
//        double space2 = getSpacePointToPoint(p2, p3);
//        double space3 = getSpacePointToPoint(p3, p0);
//
//        double imgWidth = space1 > space3 ? space1 : space3;
//        double imgHeight = space0 > space2 ? space0 : space2;
//
//        // 如果提取出的图片宽小于高，则旋转90度
//        if (imgWidth < imgHeight) {
//            double temp = imgWidth;
//            imgWidth = imgHeight;
//            imgHeight = temp;
//            Point tempPoint = p0.clone();
//            p0 = p1.clone();
//            p1 = p2.clone();
//            p2 = p3.clone();
//            p3 = tempPoint.clone();
//        }
//
//        Mat quad = Mat.zeros((int) imgHeight * 2, (int) imgWidth * 2, CvType.CV_8UC3);
//
//        MatOfPoint2f cornerMat = new MatOfPoint2f(p0, p1, p2, p3);
//        MatOfPoint2f quadMat = new MatOfPoint2f(new Point(imgWidth * 0.4, imgHeight * 1.6),
//                new Point(imgWidth * 0.4, imgHeight * 0.4),
//                new Point(imgWidth * 1.6, imgHeight * 0.4),
//                new Point(imgWidth * 1.6, imgHeight * 1.6));
//
//        // 提取图像
//        Mat transmtx = Imgproc.getPerspectiveTransform(cornerMat, quadMat);
//        Imgproc.warpPerspective(image, quad, transmtx, quad.size());
//
//        Rect rect = Imgproc.boundingRect(quadMat);
////        Imgcodecs.imwrite(outImagePath, new Mat(quad, rect));
//        Imgcodecs.imwrite(outImagePath, quad);
//        if (true) {
//            return;
//        }
//
//
//        // 连接显示凸包
//        MatOfPoint matOfPoint = new MatOfPoint();
//        matOfPoint.fromList(newPoints);
//        List<MatOfPoint> matOfPoints = new ArrayList<>();
//        matOfPoints.add(matOfPoint);
//        // 新建黑图
//        Mat mat = new Mat(image.rows(), image.cols(), image.type());
//        // 涂上发票的位置
//        Imgproc.drawContours(mat, matOfPoints, -1, new Scalar(255, 255, 255), 3);
//        Rect maxRect = Imgproc.boundingRect(contours.get(maxIndex));
//        Mat mask = new Mat(mat.height() + 2, mat.width() + 2, CvType.CV_8UC1);
//        Imgproc.floodFill(mat, mask, new Point(maxRect.x + maxRect.width / 2, maxRect.y + maxRect.height / 2), new Scalar(255, 255, 255));
//        // 显示发票
//        Core.bitwise_and(image, mat, mat);
//
//        Imgcodecs.imwrite(outImagePath, mat);
//
//
////        MatOfPoint2f contourHull = new MatOfPoint2f();
////        contourHull.fromList(newPoints);
//
////        RotatedRect rotatedRect = Imgproc.minAreaRect(contourHull);
////        Point[] rotatedRectPoint = new Point[4];
////        rotatedRect.points(rotatedRectPoint);
////        for (int i = 0; i < 4; i++) {
////            Imgproc.line(image, rotatedRectPoint[i], rotatedRectPoint[(i + 1) % 4], new Scalar(0, 0, 255), 3);
////        }
////        Imgproc.rectangle(image, maxRect, new Scalar(255, 0, 0), 3);
//
////        Mat newImage = new Mat(image.rows(), image.cols(), image.type());
////        image.copyTo(newImage);
////        Imgproc.cvtColor(newImage, newImage, Imgproc.COLOR_BGR2GRAY);
////        Imgproc.GaussianBlur(newImage, newImage, new Size(61, 61), 0);
////        Imgproc.adaptiveThreshold(newImage, newImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 101, 5);
////        List<MatOfPoint> contours = new ArrayList<>();
////        Imgproc.findContours(newImage, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
////        Imgproc.erode(newImage, newImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15, 15)));
////        for (MatOfPoint contour : contours) {
////            Rect rect = Imgproc.boundingRect(contour);
////            Imgproc.rectangle(image, rect, new Scalar(255, 0, 0));
////        }
////        Imgproc.drawContours(image);
////        Imgcodecs.imwrite(outImagePath, mat);
////        Imgcodecs.imwrite(outImagePath, newImage);
////        newImage.release();
//        image.release();
//    }
//
//    // 对多个点按顺时针排序
//    private static void sortCorners(List<Point> corners) {
//        if (corners.size() == 0) return;
//        Point p1 = corners.get(0);
//        int index = 0;
//        for (int i = 1; i < corners.size(); i++) {
//            Point point = corners.get(i);
//            if (p1.x > point.x) {
//                p1 = point;
//                index = i;
//            }
//        }
//
//        corners.set(index, corners.get(0));
//        corners.set(0, p1);
//
//        Point lp = corners.get(0);
//        for (int i = 1; i < corners.size(); i++) {
//            for (int j = i + 1; j < corners.size(); j++) {
//                Point point1 = corners.get(i);
//                Point point2 = corners.get(j);
//                if ((point1.y - lp.y * 1.0) / (point1.x - lp.x) > (point2.y - lp.y * 1.0) / (point2.x - lp.x)) {
//                    Point temp = point1.clone();
//                    corners.set(i, corners.get(j));
//                    corners.set(j, temp);
//                }
//            }
//        }
//    }
//
//    // 点到点的距离
//    private static double getSpacePointToPoint(Point p1, Point p2) {
//        double a = p1.x - p2.x;
//        double b = p1.y - p2.y;
//        return Math.sqrt(a * a + b * b);
//    }
//}
