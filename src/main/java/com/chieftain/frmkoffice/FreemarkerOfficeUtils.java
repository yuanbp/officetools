package com.chieftain.frmkoffice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * com.hc.example [workset_idea_01]
 * Created by Richard on 2018/7/23
 *
 * @author Richard on 2018/7/23
 */
public class FreemarkerOfficeUtils {

    /**
     * 生成docx
     *
     * @throws Exception
     */
    static void makeWord(FreemarkerOfficeBean bean) throws Exception {
        /** 初始化配置文件 **/
        Configuration configuration = new Configuration();
        String fileDirectory = bean.getBasePath();
        /** 加载文件 **/
        configuration.setDirectoryForTemplateLoading(new File(fileDirectory));
        /** 加载模板 **/
        Template template = configuration.getTemplate(bean.getDocumentName());

        /** 指定输出word文件的路径 **/
        String outFilePath = bean.getBasePath() + bean.getOutDataFileName();
        File docFile = new File(outFilePath);
        FileOutputStream fos = new FileOutputStream(docFile);
        Writer out = new BufferedWriter(new OutputStreamWriter(fos), 10240);
        template.process(bean.getDataMap(), out);
        if (out != null) {
            out.close();
        }
        try {
            ZipInputStream zipInputStream = ZipUtils.wrapZipInputStream(new FileInputStream(new File(bean.getBasePath() + bean.getZipFileName())));
            ZipOutputStream zipOutputStream = ZipUtils.wrapZipOutputStream(new FileOutputStream(new File(bean.getBasePath() + bean.getOutDocxFileName())));
            String itemname = "word/document.xml";
            ZipUtils.replaceItem(zipInputStream, zipOutputStream, itemname, new FileInputStream(new File(bean.getBasePath() + bean.getOutDataFileName())));
            System.out.println("success");

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * 生成pdf
     */
    static void makePdfByXcode(FreemarkerOfficeBean bean) {
        long startTime = System.currentTimeMillis();
        try {
            XWPFDocument document = new XWPFDocument(new FileInputStream(new File(bean.getBasePath() + bean.getInDocxFileName())));
            //    document.setParagraph(new Pa );
            File outFile = new File(bean.getBasePath() + bean.getOutPdfFileName());
            outFile.getParentFile().mkdirs();
            OutputStream out = new FileOutputStream(outFile);
            //    IFontProvider fontProvider = new AbstractFontRegistry();
            PdfOptions options = PdfOptions.create();  //gb2312
            PdfConverter.getInstance().convert(document, out, options);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Generate ooxml.pdf with " + (System.currentTimeMillis() - startTime) + " ms.");
    }

    public static void main(String[] args) {
        try{
            FreemarkerOfficeBean bean = new FreemarkerOfficeBean();
            bean.setBasePath("H:\\work_cloud\\workset_idea_01\\OneSelf_Set\\freemarker_xdoxreport\\freemarker_xdocxreport-example\\src\\main\\resources\\template\\");
            bean.setDocumentName("model.xml");
            bean.setOutDataFileName("modeldata.xml");
            bean.setZipFileName("model.zip");
            bean.setOutDocxFileName("model.docx");
            Map<String,Object> dataMap = new HashMap<String,Object>(16){{
                put("example","18");
            }};
            bean.setDataMap(dataMap);
            bean.setInDocxFileName("model.docx");
            bean.setOutPdfFileName("model.pdf");
            makeWord(bean);
            makePdfByXcode(bean);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
