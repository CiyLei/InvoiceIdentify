# 发票识别

## 环境
* OpenCV
* tesseractOCR

## 说明

1. 识别的是发票的代码和号码（一下图片中XXXX的两个部分）
2. 要求输入图片需要以下姿势，可以45°内的倾斜

![Invoice1](Invoice1.svg)
![Invoice2](Invoice2.svg)

## 使用

```java
// 创建 InvoiceIdentify3 对象，并传入需要分析的发票图片
InvoiceIdentify3 identify = new InvoiceIdentify3(new File("D:\\xx.jpg"));
// 开启Debug，将会在输入的目录下，输出分析过程
identify.debug("D:\\debug");
// 设置Tesseract的训练数据目录
identify.setDataPath("D:\\Tesseract-OCR\\tessdata");
// 开始分析
InvoiceIdentify3.Result result = identify.identify();
// 输出发票代码
System.out.println(result.getInvoiceCode());
// 输出发票号码
System.out.println(result.getInvoiceNumber());
```
