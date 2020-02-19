package com.genomics.pedigreeaudit.model;

public class AuditResponse {

    private String referralId;

    private String status;

    public AuditResponse(){

    }

    public AuditResponse(String referralId, String status){
        this.referralId = referralId;
        this.status = status;
    }

    public String getReferralId(){
        return referralId;
    }

    public void setReferralId(String val){
        referralId = val;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String val){
        status = val;
    }
}
