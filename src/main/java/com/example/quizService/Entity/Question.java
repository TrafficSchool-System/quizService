package com.example.quizService.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID från Excel-raden (INTE filen)
    private Integer excelId;

    private String question;
    private String sfi;
    private String correctAnswer;
    private String wrongAnswer1;
    private String wrongAnswer2;
    private String wrongAnswer3;

    @Lob
    private String explanationForStudent;

    // Körkortsbehörigheter (1 = JA, 0 = NEJ)
    private int a;
    private int am;
    private int b;
    private int be;
    private int c;
    private int ce;
    private int d;
    private int de;
    private int ykbC;
    private int ykbD;
    private int adr;
    private int vtl;
    private int ta1i1;
    private int ta1i2;
    private int ta1i3;
    private int ta1i4;
    private int ta1i5;
    private int apv;
    private int yrs;
    private int tra1;

    private String image;
    private int subject;
    private String lang;

    // ✅ KOPPLING TILL EXCELIMPORT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "excel_import_file_id")
    @JsonIgnore
    private ExcelImportFile excelImportFile;

    // =========================
    // Constructors
    // =========================
    public Question() {}

    // =========================
    // Getters / Setters
    // =========================

    public Long getId() {
        return id;
    }

    public Integer getExcelId() {
        return excelId;
    }

    public void setExcelId(Integer excelId) {
        this.excelId = excelId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSfi() {
        return sfi;
    }

    public void setSfi(String sfi) {
        this.sfi = sfi;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public void setWrongAnswer1(String wrongAnswer1) {
        this.wrongAnswer1 = wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public void setWrongAnswer2(String wrongAnswer2) {
        this.wrongAnswer2 = wrongAnswer2;
    }

    public String getWrongAnswer3() {
        return wrongAnswer3;
    }

    public void setWrongAnswer3(String wrongAnswer3) {
        this.wrongAnswer3 = wrongAnswer3;
    }

    public String getExplanationForStudent() {
        return explanationForStudent;
    }

    public void setExplanationForStudent(String explanationForStudent) {
        this.explanationForStudent = explanationForStudent;
    }

    public int getA() { return a; }
    public void setA(int a) { this.a = a; }

    public int getAm() { return am; }
    public void setAm(int am) { this.am = am; }

    public int getB() { return b; }
    public void setB(int b) { this.b = b; }

    public int getBe() { return be; }
    public void setBe(int be) { this.be = be; }

    public int getC() { return c; }
    public void setC(int c) { this.c = c; }

    public int getCe() { return ce; }
    public void setCe(int ce) { this.ce = ce; }

    public int getD() { return d; }
    public void setD(int d) { this.d = d; }

    public int getDe() { return de; }
    public void setDe(int de) { this.de = de; }

    public int getYkbC() { return ykbC; }
    public void setYkbC(int ykbC) { this.ykbC = ykbC; }

    public int getYkbD() { return ykbD; }
    public void setYkbD(int ykbD) { this.ykbD = ykbD; }

    public int getAdr() { return adr; }
    public void setAdr(int adr) { this.adr = adr; }

    public int getVtl() { return vtl; }
    public void setVtl(int vtl) { this.vtl = vtl; }

    public int getTa1i1() { return ta1i1; }
    public void setTa1i1(int ta1i1) { this.ta1i1 = ta1i1; }

    public int getTa1i2() { return ta1i2; }
    public void setTa1i2(int ta1i2) { this.ta1i2 = ta1i2; }

    public int getTa1i3() { return ta1i3; }
    public void setTa1i3(int ta1i3) { this.ta1i3 = ta1i3; }

    public int getTa1i4() { return ta1i4; }
    public void setTa1i4(int ta1i4) { this.ta1i4 = ta1i4; }

    public int getTa1i5() { return ta1i5; }
    public void setTa1i5(int ta1i5) { this.ta1i5 = ta1i5; }

    public int getApv() { return apv; }
    public void setApv(int apv) { this.apv = apv; }

    public int getYrs() { return yrs; }
    public void setYrs(int yrs) { this.yrs = yrs; }

    public int getTra1() { return tra1; }
    public void setTra1(int tra1) { this.tra1 = tra1; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getSubject() { return subject; }
    public void setSubject(int subject) { this.subject = subject; }

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }

    public ExcelImportFile getExcelImportFile() {
        return excelImportFile;
    }

    public void setExcelImportFile(ExcelImportFile excelImportFile) {
        this.excelImportFile = excelImportFile;
    }
}
