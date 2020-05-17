package com.dj.invoice_identify;

import java.io.File;
import java.io.IOException;

/**
 * Create by ChenLei on 2020/5/7
 * Describe:
 */
public class Main {


    public static void main(String[] args) throws InterruptedException, IOException {

//        InvoiceIdentify.start("D:/java_demo/invoice_identify/t1.JPG", "D:/java_demo/invoice_identify/o1.png");
//        InvoiceIdentify.start("D:/java_demo/invoice_identify/t4.png", "D:/java_demo/invoice_identify/o2.png");
//        InvoiceIdentify.start("D:/java_demo/invoice_identify/t5.png", "D:/java_demo/invoice_identify/o3.png");
//        InvoiceIdentify2.start("D:/java_demo/invoice_identify/t6.JPG", "D:/java_demo/invoice_identify/o6.png");
//        InvoiceIdentify2.start("D:/java_demo/invoice_identify/t7.JPG", "D:/java_demo/invoice_identify/o7.png");
//        InvoiceIdentify2.start("D:/java_demo/invoice_identify/t8.JPG", "D:/java_demo/invoice_identify/o8.png");
//        InvoiceIdentify2.start("D:/java_demo/invoice_identify/t9.JPG", "D:/java_demo/invoice_identify/o9.png");
//        InvoiceIdentify2.start("D:/java_demo/invoice_identify/t10.png", "D:/java_demo/invoice_identify/o10.png");
//        InvoiceIdentify2.start("D:/java_demo/invoice_identify/t11.png", "D:/java_demo/invoice_identify/o11.png");
        long c = System.currentTimeMillis();
//        InvoiceIdentify3 identify = new InvoiceIdentify3(new File("D:/java_demo/invoice_identify/t.png"));
//        InvoiceIdentify3 identify = new InvoiceIdentify3(new File("D:/java_demo/invoice_identify/t6.JPG"));
//        InvoiceIdentify3 identify = new InvoiceIdentify3(new File("D:/java_demo/invoice_identify/t7.JPG"));
//        InvoiceIdentify3 identify = new InvoiceIdentify3(new File("D:/java_demo/invoice_identify/t8.JPG"));
//        InvoiceIdentify3 identify = new InvoiceIdentify3(new File("D:/java_demo/invoice_identify/t9.JPG"));
//        InvoiceIdentify3 identify = new InvoiceIdentify3(new File("D:/java_demo/invoice_identify/t10.png"));
//        InvoiceIdentify3 identify = new InvoiceIdentify3(new File("D:/java_demo/invoice_identify/t11.png"));
        InvoiceIdentify3 identify = new InvoiceIdentify3(new File("D:\\java_demo\\invoice_identify\\20200514_103244.jpg"));
        identify.debug("D:\\java_demo\\invoice_identify\\debug");
        identify.setDataPath("D:\\Tesseract-OCR\\tessdata");
        System.out.println(identify.identify());
        System.out.println("识别总时间:" + (System.currentTimeMillis() - c));
//        identify.setInImageFile(new File("D:/java_demo/invoice_identify/20200514_103244.jpg"));
//        System.out.println(identify.identify());
//        identify.setInImageFile(new File("D:/java_demo/invoice_identify/20200514_103248.jpg"));
//        System.out.println(identify.identify());
//        identify.setInImageFile(new File("D:/java_demo/invoice_identify/20200514_103256.jpg"));
//        System.out.println(identify.identify());
//        identify.setInImageFile(new File("D:/java_demo/invoice_identify/20200514_103301.jpg"));
//        System.out.println(identify.identify());
    }
}
