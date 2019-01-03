package com.chieftain.frmkoffice;

import java.io.Serializable;
import java.util.Map;

/**
 * com.hc.example [workset_idea_01]
 * Created by Richard on 2018/7/23
 *
 * @author Richard on 2018/7/23
 */
public class FreemarkerOfficeBean implements Serializable {

    private static final long serialVersionUID = -5594945789393916636L;

    private String basePath;
    private String documentName;
    private Map<String, Object> dataMap;
    private String outDataFileName;
    private String zipFileName;
    private String outDocxFileName;

    private String inDocxFileName;
    private String outPdfFileName;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public String getOutDataFileName() {
        return outDataFileName;
    }

    public void setOutDataFileName(String outDataFileName) {
        this.outDataFileName = outDataFileName;
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }

    public String getOutDocxFileName() {
        return outDocxFileName;
    }

    public void setOutDocxFileName(String outDocxFileName) {
        this.outDocxFileName = outDocxFileName;
    }

    public String getInDocxFileName() {
        return inDocxFileName;
    }

    public void setInDocxFileName(String inDocxFileName) {
        this.inDocxFileName = inDocxFileName;
    }

    public String getOutPdfFileName() {
        return outPdfFileName;
    }

    public void setOutPdfFileName(String outPdfFileName) {
        this.outPdfFileName = outPdfFileName;
    }
}
