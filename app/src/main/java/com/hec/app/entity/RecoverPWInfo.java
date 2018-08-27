package com.hec.app.entity;

/**
 * Created by hec on 2015/11/16.
 */
public class RecoverPWInfo {
    private String LimitDes;
    private String LimitSymbol;
    private String Question;
    private int QuestionID;
    private String UserName;
    private String NewPassword;
    private int Question1ID;
    private int Question2ID;
    private String Answer1;
    private String Answer2;
    private String email;
    private String MoneyPwd;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMoneyPwd() {
        return MoneyPwd;
    }

    public void setMoneyPwd(String fundPw) {
        this.MoneyPwd = fundPw;
    }

    public String getNewPassword() {
        return NewPassword;
    }

    public void setNewPassword(String newPassword) {
        NewPassword = newPassword;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getQuestion1ID() {
        return Question1ID;
    }

    public void setQuestion1ID(int question1ID) {
        Question1ID = question1ID;
    }

    public int getQuestion2ID() {
        return Question2ID;
    }

    public void setQuestion2ID(int question2ID) {
        Question2ID = question2ID;
    }

    public String getAnswer1() {
        return Answer1;
    }

    public void setAnswer1(String answer1) {
        Answer1 = answer1;
    }

    public String getAnswer2() {
        return Answer2;
    }

    public void setAnswer2(String answer2) {
        Answer2 = answer2;
    }

    public String getLimitDes() {
        return LimitDes;
    }

    public void setLimitDes(String limitDes) {
        LimitDes = limitDes;
    }

    public String getLimitSymbol() {
        return LimitSymbol;
    }

    public void setLimitSymbol(String limitSymbol) {
        LimitSymbol = limitSymbol;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public int getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(int questionID) {
        QuestionID = questionID;
    }
}
