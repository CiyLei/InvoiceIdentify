package com.dj.invoice_identify;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by ChenLei on 2020/5/9
 * Describe: 发票识别
 */
public class InvoiceIdentify3 {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * 分析时间
     */
    private long analysisTime = 0L;
    /**
     * 待分析的图片
     */
    private File inImageFile;
    /**
     * 调试输出目录
     */
    private File debugFile;
    /**
     * 轮廓颜色
     */
    private Scalar profileColor = new Scalar(0, 0, 255);
    /**
     * 轮廓宽度
     */
    private int profileWidth = 3;
    /**
     * ORC文字识别
     */
    private ITesseract instance = new Tesseract();
    /**
     * 发票代码正则
     */
    private static String INVOICE_CODE_MATCH = "\\d{10}";
    /**
     * 发票号码正则
     */
    private static String INVOICE_NUMBER_MATCH = "\\d{8}";
    /**
     * 调试输出标记
     */
    private int debugMark = 0;

    public InvoiceIdentify3(File inImageFile) {
        this.inImageFile = inImageFile;
    }

    /**
     * 设置识别的图片
     *
     * @param inImageFile 识别的图片
     */
    public void setInImageFile(File inImageFile) {
        this.inImageFile = inImageFile;
    }

    /**
     * 设置OCR数据目录
     *
     * @param dataPath OCR数据目录
     */
    public void setDataPath(String dataPath) {
        instance.setDatapath(dataPath);
    }

    /**
     * 开启Debug，输出一步步的效果图
     *
     * @param debugPath 效果图存放路径
     */
    public void debug(String debugPath) {
        debugFile = new File(debugPath);
        if (!debugFile.exists()) {
            debugFile.mkdirs();
        }
    }

    /**
     * 开始识别
     */
    public Result identify() {
        analysisTime = System.currentTimeMillis();
        debugMark = 0;
        Mat mat = interceptInvoice();
        Result result = findCode(mat);
        mat.release();
        return result;
    }

    /**
     * 截取发票
     *
     * @return 发票
     */
    private Mat interceptInvoice() {
        Mat image = Imgcodecs.imread(inImageFile.getAbsolutePath());
        // 复制一个文件
        Mat newImage = new Mat(image.rows(), image.cols(), image.type());
        // 灰值化
        Imgproc.cvtColor(image, newImage, Imgproc.COLOR_BGR2GRAY);
        debugWrite(newImage);
        // 高斯降噪
        Imgproc.GaussianBlur(newImage, newImage, new Size(3, 3), 2, 2);
        debugWrite(newImage);
        // Canny边缘检测
        Imgproc.Canny(newImage, newImage, 20, 60, 3, false);
        debugWrite(newImage);
        // 膨胀，连接边缘
        Imgproc.dilate(newImage, newImage, new Mat(), new Point(-1, -1), 3, 1, new Scalar(1));
        debugWrite(newImage);
        // 寻找轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(newImage, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        // 在临时图片上画上轮廓
        if (debugFile != null) {
            Mat tmpImage = new Mat(image.rows(), image.cols(), image.type());
            image.copyTo(tmpImage);
            Imgproc.drawContours(tmpImage, contours, -1, profileColor, profileWidth);
            debugWrite(tmpImage);
            tmpImage.release();
        }
        // 寻找最大面积的轮廓
        int maxIndex = 0;
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            Rect maxRect = Imgproc.boundingRect(contours.get(maxIndex));
            if (rect.width * rect.height >= maxRect.width * maxRect.height) {
                maxIndex = i;
            }
        }
        // 用凸包计算出新的轮廓点
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(contours.get(maxIndex), hull, false);
        // 取出凸包的点
        Point[] contourPoints = contours.get(maxIndex).toArray();
        int[] indices = hull.toArray();
        List<Point> newPoints = new ArrayList<>();
        for (int index : indices) {
            newPoints.add(contourPoints[index]);
        }
        // 将凸包转为矩形
        MatOfPoint2f contourHull = new MatOfPoint2f();
        contourHull.fromList(newPoints);
        RotatedRect rotatedRect = Imgproc.minAreaRect(contourHull);
        // 根据矩阵的角度，排序点
        Point[] rotatedRectPoint = sortCorners(rotatedRect);
        // 在临时图片上画上旋转矩阵
        if (debugFile != null) {
            Mat tmpImage = new Mat(image.rows(), image.cols(), image.type());
            image.copyTo(tmpImage);
            for (int i = 0; i < 4; i++) {
                Imgproc.line(tmpImage, rotatedRectPoint[i], rotatedRectPoint[(i + 1) % 4], new Scalar(0, 0, 255), profileWidth);
            }
            debugWrite(tmpImage);
            tmpImage.release();
        }
        // 计算目标图像的尺寸
        Point p0 = rotatedRectPoint[0];
        Point p1 = rotatedRectPoint[1];
        Point p2 = rotatedRectPoint[2];
        Point p3 = rotatedRectPoint[3];
        // 计算边长
        double space0 = getSpacePointToPoint(p0, p1);
        double space1 = getSpacePointToPoint(p1, p2);
        double space2 = getSpacePointToPoint(p2, p3);
        double space3 = getSpacePointToPoint(p3, p0);
        double imgWidth = space0 > space2 ? space0 : space2;
        double imgHeight = space1 > space3 ? space1 : space3;
        // 指定旋转点
        Mat quad = Mat.zeros((int) imgHeight * 2, (int) imgWidth * 2, CvType.CV_8UC3);
        MatOfPoint2f cornerMat = new MatOfPoint2f(p0, p1, p2, p3);
        MatOfPoint2f quadMat = new MatOfPoint2f(new Point(0, 0),
                new Point(imgWidth, 0),
                new Point(imgWidth, imgHeight),
                new Point(0, imgHeight));
        // 提取图像
        Mat transmtx = Imgproc.getPerspectiveTransform(cornerMat, quadMat);
        Imgproc.warpPerspective(image, quad, transmtx, quad.size());

        Rect rect = Imgproc.boundingRect(quadMat);
        Mat resultMat = new Mat(quad, rect);
        debugWrite(resultMat);

        cornerMat.release();
        quad.release();

        newImage.release();
        image.release();
        return resultMat;
    }

    /**
     * 寻找发票代码和发票号码
     *
     * @param invoice 发票图片
     */
    private Result findCode(Mat invoice) {
        // 截取头部5分之一
        Mat topMat = new Mat(invoice, new Rect(0, 0, invoice.width(), invoice.height() / 5));
        debugWrite(topMat);
        // 寻找发票代码和号码
        String invoiceCode = findInvoiceCode(topMat);
        String invoiceNumber = findInvoiceNumber(topMat);

        topMat.release();

        return new Result(invoiceCode, invoiceNumber);
    }

    /**
     * 寻找发票代码
     *
     * @param image
     */
    private String findInvoiceCode(Mat image) {
        Mat leftImage = new Mat(image, new Rect(0, 0, image.width() / 2, image.height()));
        Mat newImage = new Mat(leftImage.rows(), leftImage.cols(), leftImage.type());
        // 灰值化
        Imgproc.cvtColor(leftImage, newImage, Imgproc.COLOR_BGR2GRAY);
        debugWrite(newImage);
        // 高斯降噪
        Imgproc.GaussianBlur(newImage, newImage, new Size(3, 3), 0);
        debugWrite(newImage);
        // 二值化图片
        Imgproc.adaptiveThreshold(newImage, newImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 31, 20);
        debugWrite(newImage);
        // 腐蚀膨胀
        int swellWidth = newImage.width() / 60;
        Imgproc.erode(newImage, newImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(swellWidth, swellWidth)));
        debugWrite(newImage);
        // 反转颜色
        Core.bitwise_not(newImage, newImage);
        debugWrite(newImage);
        // 寻找轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(newImage, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        // 寻找符合条件的区域
        List<Rect> codeRect = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            // 获取轮廓
            Rect rect = Imgproc.boundingRect(contour);
            /**
             * 条件：
             * 1.宽度占比在0.1 - 0.4
             * 2.宽度为高度4倍以上
             * 3.x轴为宽的0.2以上
             */
            float widthP = (float) rect.width / newImage.width();
            float whP = (float) rect.width / rect.height;
            float xP = (float) rect.x / newImage.width();
            if (widthP > 0.1 && widthP < 0.4 && whP > 4 && xP > 0.2) {
                codeRect.add(rect);
            }
        }
        if (debugFile != null) {
            // 圈出适配的范围
            Imgproc.cvtColor(newImage, newImage, Imgproc.COLOR_GRAY2BGR);
            for (Rect rect : codeRect) {
                Imgproc.rectangle(newImage, rect, profileColor, 3);
            }
            debugWrite(newImage);
        }
        String resultText = "";
        // 截取
        for (Rect rect : codeRect) {
            Mat codeMat = new Mat(leftImage, rect);
            Image codeImage = HighGui.toBufferedImage(codeMat);
            debugWrite(codeMat);
            String text = identifyText((BufferedImage) codeImage);
            Pattern pattern = Pattern.compile(INVOICE_CODE_MATCH);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                resultText = matcher.group(0);
                break;
            }
            codeMat.release();
        }
//        if (debugFile != null) {
//            // 在原图上圈出适配的部分
//            for (Rect rect : codeRect) {
//                Imgproc.rectangle(leftImage, rect, profileColor, profileWidth);
//            }
//            debugWrite(leftImage);
//        }

        newImage.release();
        leftImage.release();
        return resultText;
    }

    /**
     * 寻找发票号码
     *
     * @param image
     */
    private String findInvoiceNumber(Mat image) {
        Mat rightImage = new Mat(image, new Rect(image.width() / 2 - 1, 0, image.width() / 2, image.height()));
        Mat newImage = new Mat(rightImage.rows(), rightImage.cols(), rightImage.type());
        // 转化HLS
        Imgproc.cvtColor(rightImage, newImage, Imgproc.COLOR_BGR2HLS);
        debugWrite(newImage);
        // 高斯降噪
        Imgproc.GaussianBlur(newImage, newImage, new Size(3, 3), 0);
        debugWrite(newImage);
        // 提取蓝色
        Core.inRange(newImage, new Scalar(110, 70, 70), new Scalar(130, 200, 200), newImage);
        debugWrite(newImage);
        // 反转颜色
        Core.bitwise_not(newImage, newImage);
        debugWrite(newImage);
        // 腐蚀膨胀
        int swellWidth = newImage.width() / 60;
        Imgproc.erode(newImage, newImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(swellWidth, swellWidth)));
        debugWrite(newImage);
        // 反转颜色
        Core.bitwise_not(newImage, newImage);
        debugWrite(newImage);
        // 寻找轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(newImage, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        // 寻找符合条件的区域
        List<Rect> codeRect = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            // 获取轮廓
            Rect rect = Imgproc.boundingRect(contour);
            /**
             * 条件：
             * 1.宽度占比在0.1 - 0.4
             * 2.宽度为高度3倍以上
             * 3.x轴为宽的0.3以上
             */
            float widthP = (float) rect.width / newImage.width();
            float whP = (float) rect.width / rect.height;
            float xP = (float) rect.x / newImage.width();
            if (widthP > 0.1 && widthP < 0.4 && whP > 3 && xP > 0.3) {
                codeRect.add(rect);
            }
        }
        if (debugFile != null) {
            // 圈出适配的范围
            Imgproc.cvtColor(newImage, newImage, Imgproc.COLOR_GRAY2BGR);
            for (Rect rect : codeRect) {
                Imgproc.rectangle(newImage, rect, profileColor, profileWidth);
            }
            debugWrite(newImage);
        }
        String resultText = "";
        // 截取
        for (Rect rect : codeRect) {
            Mat codeMat = new Mat(rightImage, rect);
            Image codeImage = HighGui.toBufferedImage(codeMat);
            debugWrite(codeMat);
            String text = identifyText((BufferedImage) codeImage);
            Pattern pattern = Pattern.compile(INVOICE_NUMBER_MATCH);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                resultText = matcher.group(0);
                break;
            }
            codeMat.release();
        }
//        if (debugFile != null) {
//            // 在原图上圈出适配的部分
//            for (Rect rect : codeRect) {
//                Imgproc.rectangle(rightImage, rect, profileColor, profileWidth);
//            }
//            debugWrite(rightImage);
//        }

        newImage.release();
        rightImage.release();
        return resultText;
    }

    /**
     * 图片识别文字
     *
     * @param image 图片
     * @return 识别的文字
     */
    private String identifyText(BufferedImage image) {
        try {
            long cur = System.currentTimeMillis();
            String result = instance.doOCR(image);
            if (debugFile != null) {
                System.out.println("分析文字：" + (System.currentTimeMillis() - cur));
            }
            return result;
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 调试输出
     *
     * @param mat 图片
     */
    private void debugWrite(Mat mat) {
        // 输出分析时间
        if (debugFile != null) {
            debugMark++;
            System.out.println("分析标记：" + debugMark + " 时间：" + (System.currentTimeMillis() - analysisTime));
            // 获取输入文件的文件名称和后缀名
            String inImageFileName = inImageFile.getName();
            String[] split = inImageFileName.split("\\.");
            String fileName = split[0];
            String suffixName = split.length > 1 ? split[1] : split[0];
            // 输出文件的路径文件名
            String outImagePath = debugFile.getAbsolutePath() + File.separator + fileName + "_" + debugMark + "." + suffixName;
            Imgcodecs.imwrite(outImagePath, mat);
            // 更新分析时间
            analysisTime = System.currentTimeMillis();
        }
    }

    /**
     * 对多个点按顺时针排序
     *
     * @param rotatedRect
     */
    private Point[] sortCorners(RotatedRect rotatedRect) {
        Point[] rotatedRectPoint = new Point[4];
        // 旋转角度
        double angle = Math.abs(rotatedRect.angle);// / Math.PI * 180.0;
//        angle = angle > 90 ? angle - 90 : angle;
        // 获取4个点
        rotatedRect.points(rotatedRectPoint);
        if (rotatedRect.size.width <= rotatedRect.size.height && angle >= 45 && angle <= 90) {
            // 正常摆放的发票且左边略微抬起
            swapPoint(rotatedRectPoint, 0, 2);
            swapPoint(rotatedRectPoint, 1, 3);
        } else if (rotatedRect.size.width >= rotatedRect.size.height && angle >= 0 && angle <= 45) {
            // 正常摆放的发票且右边略微抬起
            swapPoint(rotatedRectPoint, 0, 1);
            swapPoint(rotatedRectPoint, 1, 2);
            swapPoint(rotatedRectPoint, 2, 3);
        } else if (rotatedRect.size.width <= rotatedRect.size.height && angle >= 0 && angle <= 45) {
            // 竖着摆放的发票且右边略微抬起
            swapPoint(rotatedRectPoint, 0, 2);
            swapPoint(rotatedRectPoint, 1, 3);
        } else if (rotatedRect.size.width >= rotatedRect.size.height && angle >= 45 && angle <= 90) {
            // 竖着摆放的发票且左边略微抬起
            swapPoint(rotatedRectPoint, 0, 3);
            swapPoint(rotatedRectPoint, 3, 2);
            swapPoint(rotatedRectPoint, 2, 1);
        }
        return rotatedRectPoint;
    }

    /**
     * 交换坐标
     *
     * @param points
     * @param a
     * @param b
     */
    private void swapPoint(Point[] points, int a, int b) {
        Point tmp = points[a];
        points[a] = points[b];
        points[b] = tmp;
    }

    /**
     * 点到点的距离
     *
     * @param p1
     * @param p2
     * @return
     */
    private double getSpacePointToPoint(Point p1, Point p2) {
        double a = p1.x - p2.x;
        double b = p1.y - p2.y;
        return Math.sqrt(a * a + b * b);
    }

    public static class Result {
        /**
         * 发票代码
         */
        private String invoiceCode;
        /**
         * 发票号码
         */
        private String invoiceNumber;

        public Result(String invoiceCode, String invoiceNumber) {
            this.invoiceCode = invoiceCode;
            this.invoiceNumber = invoiceNumber;
        }

        public String getInvoiceCode() {
            return invoiceCode;
        }

        public void setInvoiceCode(String invoiceCode) {
            this.invoiceCode = invoiceCode;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "invoiceCode='" + invoiceCode + '\'' +
                    ", invoiceNumber='" + invoiceNumber + '\'' +
                    '}';
        }
    }
}
