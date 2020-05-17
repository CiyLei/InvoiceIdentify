# 发票识别

## 环境
* OpenCV
* tesseractOCR

## 说明

1. 识别的是发票的代码和号码（一下图片中XXXX的两个部分）
2. 要求输入图片需要以下姿势，可以45°内的倾斜
<table>
  <tr>
    <td>
      <svg width="501" height="301" xmlns="http://www.w3.org/2000/svg">
        <g fill="none" stroke="black">
        <rect id="left" height="300" width="500" y="0" x="0" stroke-width="1.5" stroke="#000" fill="#ECE2E0" />
        <line stroke-linejoin="undefined" stroke-dasharray="5,5" id="svg_1" y2="300" x2="20" y1="0" x1="20" stroke-width="1.5" stroke="#000" fill="none"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="15" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="45" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="75" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="105" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="135" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="165" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="195" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="225" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="255" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="285" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <line stroke-linejoin="undefined" stroke-dasharray="5,5" id="svg_1" y2="300" x2="480" y1="0" x1="480" stroke-width="1.5" stroke="#000" fill="none"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="15" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="45" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="75" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="105" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="135" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="165" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="195" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="225" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="255" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="285" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="25" rx="40" id="svg_5" cy="40" cx="250" fill-opacity="null" stroke-opacity="null" stroke-width="3" stroke="#f00" fill="none"/>
        <ellipse ry="20" rx="34" id="svg_5" cy="40" cx="250" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#f00" fill="none"/>
        <rect id="left" height="200" width="420" y="80" x="40" stroke-width="1" stroke="#000" fill="#none" />
        <rect id="left" height="50" width="50" y="15" x="40" stroke-width="1" stroke="#000" fill="#000" />
        <text y="30" x="100" stroke-width="1" fill="#000">XXXXXXXXXX</text>
        <text y="30" x="320" stroke-width="1" fill="#000">No</text>
        <text y="30" x="350" stroke-width="1" fill="#00f" stroke="#00f">XXXXXXXX</text>
        </g>
      </svg>
    </td>
  </tr>
</table>
<table>
  <tr>
    <td>
      <svg width="301" height="501" xmlns="http://www.w3.org/2000/svg">
        <g fill="none" stroke="black" transform="translate(300,0)rotate(90)">
        <rect id="left" height="300" width="500" y="0" x="0" stroke-width="1.5" stroke="#000" fill="#ECE2E0" />
        <line stroke-linejoin="undefined" stroke-dasharray="5,5" id="svg_1" y2="300" x2="20" y1="0" x1="20" stroke-width="1.5" stroke="#000" fill="none"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="15" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="45" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="75" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="105" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="135" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="165" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="195" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="225" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="255" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="285" cx="10" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <line stroke-linejoin="undefined" stroke-dasharray="5,5" id="svg_1" y2="300" x2="480" y1="0" x1="480" stroke-width="1.5" stroke="#000" fill="none"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="15" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="45" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="75" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="105" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="135" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="165" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="195" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="225" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="255" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="5" rx="5" id="svg_5" cy="285" cx="490" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#000" fill="#fff"/>
        <ellipse ry="25" rx="40" id="svg_5" cy="40" cx="250" fill-opacity="null" stroke-opacity="null" stroke-width="3" stroke="#f00" fill="none"/>
        <ellipse ry="20" rx="34" id="svg_5" cy="40" cx="250" fill-opacity="null" stroke-opacity="null" stroke-width="1" stroke="#f00" fill="none"/>
        <rect id="left" height="200" width="420" y="80" x="40" stroke-width="1" stroke="#000" fill="#none" />
        <rect id="left" height="50" width="50" y="15" x="40" stroke-width="1" stroke="#000" fill="#000" />
        <text y="30" x="100" stroke-width="1" fill="#000">XXXXXXXXXX</text>
        <text y="30" x="320" stroke-width="1" fill="#000">No</text>
        <text y="30" x="350" stroke-width="1" fill="#00f" stroke="#00f">XXXXXXXX</text>
        </g>
      </svg>
    </td>
  </tr>
</table>

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