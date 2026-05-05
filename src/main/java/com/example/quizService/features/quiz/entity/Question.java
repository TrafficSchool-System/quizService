package com.example.quizService.features.quiz.entity;

import com.example.quizService.features.admin.entity.ExcelImportFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Question - Quiz question entity
 * 
 * Purpose:
 * - Stores quiz questions with answers and metadata
 * - Supports multiple driver's license categories
 * - Tracks source via Excel import file reference
 * 
 * Image Handling:
 * - image field contains full URLs to external images
 * - Example: "https://trafikteori.nu/ElevMedeL/GrunD/korfalt1.jpg"
 * - URLs are used directly by frontend - no local storage needed
 * 
 * Relationships:
 * - ManyToOne ExcelImportFile (tracks which Excel file imported this question)
 * 
 * Business Rules:
 * - correctAnswer + 3 wrongAnswers = 4 total answers
 * - Answers shuffled when converted to DTO
 * - Driver's license fields: 1 = applicable, 0 = not applicable
 * - Subject: 1-5 (different traffic knowledge categories)
 */
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Excel row ID (for tracking source, NOT primary key)
     */
    private Integer excelId;

    /**
     * Question text in Swedish
     */
    private String question;

    /**
     * Question text in simplified Swedish (SFI - Svenska för invandrare)
     */
    private String sfi;

    /**
     * The correct answer text
     */
    private String correctAnswer;

    /**
     * Wrong answer alternative 1
     */
    private String wrongAnswer1;

    /**
     * Wrong answer alternative 2
     */
    private String wrongAnswer2;

    /**
     * Wrong answer alternative 3
     */
    private String wrongAnswer3;

    /**
     * Explanation shown to student after answering
     */
    @Lob
    private String explanationForStudent;

    // Driver's license categories (1 = applicable, 0 = not applicable)
    private int a; // Motorcykel
    private int am; // Moped klass I
    private int b; // Personbil
    private int be; // Personbil med släpvagn
    private int c; // Lastbil
    private int ce; // Lastbil med släpvagn
    private int d; // Buss
    private int de; // Buss med släpvagn
    private int ykbC; // Yrkeskompetensbevis C
    private int ykbD; // Yrkeskompetensbevis D
    private int adr; // ADR (farligt gods)
    private int vtl; // Vägtrafik för lärare
    private int ta1i1; // Trafikant 1 intensiv 1
    private int ta1i2; // Trafikant 1 intensiv 2
    private int ta1i3; // Trafikant 1 intensiv 3
    private int ta1i4; // Trafikant 1 intensiv 4
    private int ta1i5; // Trafikant 1 intensiv 5
    private int apv; // APV (allmän trafikkunskap)
    private int yrs; // YRS (yrkestrafik)
    private int tra1; // Trafikant 1

    /**
     * Image URL - full URL to external image
     * Example: "https://trafikteori.nu/ElevMedeL/GrunD/korfalt1.jpg"
     * 
     * Frontend uses URL directly - no local image storage required.
     */
    private String image;

    /**
     * Subject category (1-5)
     * 1 = ?, 2 = ?, 3 = ?, 4 = ?, 5 = ?
     */
    private int subject;

    /**
     * Language code (e.g., "sv", "en")
     */
    private String lang;

    /**
     * Reference to Excel import file that imported this question
     * Null if question was created manually
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "excel_import_file_id")
    @JsonIgnore
    private ExcelImportFile excelImportFile;

    // =========================
    // Constructors
    // =========================

    public Question() {
    }

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

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getAm() {
        return am;
    }

    public void setAm(int am) {
        this.am = am;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getBe() {
        return be;
    }

    public void setBe(int be) {
        this.be = be;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getCe() {
        return ce;
    }

    public void setCe(int ce) {
        this.ce = ce;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public int getDe() {
        return de;
    }

    public void setDe(int de) {
        this.de = de;
    }

    public int getYkbC() {
        return ykbC;
    }

    public void setYkbC(int ykbC) {
        this.ykbC = ykbC;
    }

    public int getYkbD() {
        return ykbD;
    }

    public void setYkbD(int ykbD) {
        this.ykbD = ykbD;
    }

    public int getAdr() {
        return adr;
    }

    public void setAdr(int adr) {
        this.adr = adr;
    }

    public int getVtl() {
        return vtl;
    }

    public void setVtl(int vtl) {
        this.vtl = vtl;
    }

    public int getTa1i1() {
        return ta1i1;
    }

    public void setTa1i1(int ta1i1) {
        this.ta1i1 = ta1i1;
    }

    public int getTa1i2() {
        return ta1i2;
    }

    public void setTa1i2(int ta1i2) {
        this.ta1i2 = ta1i2;
    }

    public int getTa1i3() {
        return ta1i3;
    }

    public void setTa1i3(int ta1i3) {
        this.ta1i3 = ta1i3;
    }

    public int getTa1i4() {
        return ta1i4;
    }

    public void setTa1i4(int ta1i4) {
        this.ta1i4 = ta1i4;
    }

    public int getTa1i5() {
        return ta1i5;
    }

    public void setTa1i5(int ta1i5) {
        this.ta1i5 = ta1i5;
    }

    public int getApv() {
        return apv;
    }

    public void setApv(int apv) {
        this.apv = apv;
    }

    public int getYrs() {
        return yrs;
    }

    public void setYrs(int yrs) {
        this.yrs = yrs;
    }

    public int getTra1() {
        return tra1;
    }

    public void setTra1(int tra1) {
        this.tra1 = tra1;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public ExcelImportFile getExcelImportFile() {
        return excelImportFile;
    }

    public void setExcelImportFile(ExcelImportFile excelImportFile) {
        this.excelImportFile = excelImportFile;
    }
}
