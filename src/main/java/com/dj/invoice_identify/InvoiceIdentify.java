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
//public class InvoiceIdentify {
//
//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    }
//
//    public static void start(String imagePath, String outImagePath) {
//        Mat image = Imgcodecs.imread(imagePath);
//        Mat noImage = Imgcodecs.imread("D:/java_demo/invoice_identify/temp.png");
//        modifyResolution(image);
//        Point noPoint = findNo(image, noImage);
//        image = intercept(image, noImage, noPoint);
//        List<Rect> codeRects = findCode(image);
//        for (Rect codeRect : codeRects) {
//            // 切割图片
//            Mat submatMat = image.submat(codeRect);
//            handle(submatMat);
//            Imgcodecs.imwrite(outImagePath, submatMat);
//            submatMat.release();
//            System.out.println("imagePath:" + imagePath + " code:" + identify(outImagePath));
//        }
//        image.release();
//        noImage.release();
//    }
//
//    /**
//     * 修改分辨率（比例缩小）
//     * 宽最高：1000
//     * 高最高：650
//     *
//     * @param image 需要修改分辨率图片的地址
//     */
//    private static void modifyResolution(Mat image) {
//        int width = image.width();
//        int height = image.height();
//        float widthScale = 1000.0f / width;
//        float heightScale = 650.0f / height;
////        float scale = Math.min(Math.min(widthScale, heightScale), 1f);
//        float scale = Math.min(widthScale, heightScale);
//        Imgproc.resize(image, image, new Size(width * scale, height * scale));
//    }
//
//    /**
//     * 寻找No的位置
//     *
//     * @param image
//     * @param noImg
//     * @return
//     */
//    private static Point findNo(Mat image, Mat noImg) {
//        int resultRows = image.rows() - noImg.rows() + 1;
//        int resultCols = image.cols() - noImg.cols() + 1;
//        Mat resultImg = new Mat(resultRows, resultCols, CvType.CV_32FC1);
//        // 进行匹配和标准化
//        Imgproc.matchTemplate(image, noImg, resultImg, Imgproc.TM_CCOEFF_NORMED);
//        Core.normalize(resultImg, resultImg, 0, 1, Core.NORM_MINMAX, -1, new Mat());
//        // 通过函数 minMaxLoc 定位最匹配的位置
//        Core.MinMaxLocResult mmlr = Core.minMaxLoc(resultImg);
//
////        Point matchLocation = mmlr.maxLoc; // 此处使用maxLoc还是minLoc取决于使用的匹配算法
////        Imgproc.rectangle(image, matchLocation,
////                new Point(matchLocation.x + noImg.cols(), matchLocation.y + noImg.rows()),
////                new Scalar(0, 0, 0, 0), 3);
////        Imgcodecs.imwrite("D:/java_demo/invoice_identify/o.png", image);
//
//        resultImg.release();
//
//        return mmlr.maxLoc;
//    }
//
//    /**
//     * 截取No部分
//     *
//     * @param image   需要截取的图片
//     * @param noImage No的图片
//     * @param noPoint No的坐标
//     * @return 截取后的图片
//     */
//    private static Mat intercept(Mat image, Mat noImage, Point noPoint) {
//        double x = Math.min(noPoint.x, image.width());
//        double y = Math.max(noPoint.y - noImage.height(), 0);
//        double width = image.width() - x;
//        double height = Math.min(noImage.height() * 3, image.height());
//        return image.submat(new Rect(new Point(x, y), new Size(width, height)));
//    }
//
//    /**
//     * 寻找编号
//     *
//     * @param image 图片
//     */
//    private static List<Rect> findCode(Mat image) {
//        List<Rect> result = new ArrayList<>();
//        Mat newImage = new Mat(image.rows(), image.cols(), image.type());
//        image.copyTo(newImage);
//        // 转换HSV
//        Imgproc.cvtColor(newImage, newImage, Imgproc.COLOR_BGR2HSV);
//        // 提取蓝色
//        Core.inRange(newImage, new Scalar(110, 50, 50), new Scalar(130, 255, 255), newImage);
//        // 高斯降噪
//        Imgproc.GaussianBlur(newImage, newImage, new Size(9, 9), 0);
//        // 二值化图片
//        Imgproc.adaptiveThreshold(newImage, newImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 31, 20);
//        // 腐蚀膨胀
//        Imgproc.erode(newImage, newImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(13, 13)));
//        // 反转颜色
//        for (int i = 0; i < newImage.rows(); i++) {
//            for (int j = 0; j < newImage.cols(); j++) {
//                newImage.put(i, j, 255 - newImage.get(i, j)[0]);
//            }
//        }
//        // 轮廓描边
//        List<MatOfPoint> contours = new ArrayList<>();
//        Imgproc.findContours(newImage, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//        for (MatOfPoint contour : contours) {
//            // 获取轮廓
//            Rect rect = Imgproc.boundingRect(contour);
//            Rect newRect = new Rect(rect.x + 7, rect.y + 7, rect.width - 14, rect.height - 14);
//            if (newRect.height > 0 && newRect.width > 0 && newRect.width >= newRect.height * 3) {
//                result.add(newRect);
//            }
//        }
////        for (Rect rect : result) {
////            Imgproc.rectangle(image, rect, new Scalar(0, 0, 255));
////        }
////        Imgproc.drawContours(image, contours, -1, new Scalar(0, 0, 255), 1, 1);
////        Imgcodecs.imwrite(outImagePath, image);
//        newImage.release();
//        return result;
//    }
//
//    private static void handle(Mat codeImage) {
//        // 灰值化
//        Imgproc.cvtColor(codeImage, codeImage, Imgproc.COLOR_BGR2GRAY);
//        // 高斯降噪
//        Imgproc.GaussianBlur(codeImage, codeImage, new Size(1, 1), 0);
//        // 二值化图片
//        Imgproc.adaptiveThreshold(codeImage, codeImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 21, 20);
//        // 腐蚀膨胀
//        Imgproc.erode(codeImage, codeImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 1)));
//        // 平滑处理
////        Imgproc.medianBlur(codeImage, codeImage, 3);
////        Imgcodecs.imwrite(outImagePath, codeImage);
////        codeImage.release();
//    }
//
//    /**
//     * 识别文字
//     *
//     * @param imagePath 需要识别的图片
//     * @return 识别后的文字
//     */
//    private static String identify(String imagePath) {
//        ITesseract instance = new Tesseract();
//        instance.setDatapath("D:\\Tesseract-OCR\\tessdata");
//        try {
//            return instance.doOCR(new File(imagePath));
//        } catch (TesseractException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//}
