package com.nice.fincent.util;

public class CertUtil {

    public String getIssuerName(String issuerName) {
        if("yessign".compareToIgnoreCase(issuerName) == 0) issuerName = "금융결제원";
        else if("kica".compareToIgnoreCase(issuerName) == 0) issuerName = "한국정보인증";
        else if("signkorea".compareToIgnoreCase(issuerName) == 0) issuerName = "코스콤(증권전산)";
        else if("crosscert".compareToIgnoreCase(issuerName) == 0) issuerName = "한국전자인증";
        else if("tradesign".compareToIgnoreCase(issuerName) == 0) issuerName = "한국무역정보통신";
        else if("ncasign".compareToIgnoreCase(issuerName) == 0) issuerName = "한국전산원";
        return issuerName;
    }

    public String getOid(String oid) {

        // 금결원
        if("1.2.410.200005.1.1.1".compareTo(oid) == 0) oid = "범용(개인)";
        if("1.2.410.200005.1.1.2".compareTo(oid) == 0) oid = "은행/신용카드/보험(법인)";
        if("1.2.410.200005.1.1.4".compareTo(oid) == 0) oid = "은행/신용카드/보험(개인)";
        if("1.2.410.200005.1.1.5".compareTo(oid) == 0) oid = "범용(법인)";

        // 정보인증
        if("1.2.410.200004.5.2.1.1".compareTo(oid) == 0) oid = "1등급 법인";
        if("1.2.410.200004.5.2.1.2".compareTo(oid) == 0) oid = "1등급 개인";
        if("1.2.410.200004.5.2.1.7.1".compareTo(oid) == 0) oid = "은행/신용카드/보험용";

        // 코스콤
        if("1.2.410.200004.5.1.1.5".compareTo(oid) == 0) oid = "범용(개인)";
        if("1.2.410.200004.5.1.1.7".compareTo(oid) == 0) oid = "범용(법인)";
        if("1.2.410.200004.5.1.1.9".compareTo(oid) == 0) oid = "증권/보험(개인)";
        if("1.2.410.200004.5.1.1.10".compareTo(oid) == 0) oid = "증권/보험용(개인)";
        if("1.2.410.200004.5.1.1.10.1".compareTo(oid) == 0) oid = "은행/보험용(개인)";

        // 전자인증
        if("1.2.410.200004.5.4.1.1".compareTo(oid) == 0) oid = "범용(개인)";
        if("1.2.410.200004.5.4.1.2".compareTo(oid) == 0) oid = "범용(법인)";
        if("1.2.410.200004.5.4.1.101".compareTo(oid) == 0) oid = "은행/보험용";

        // 무역정보통신
        if("1.2.410.200012.1.1.1".compareTo(oid) == 0) oid = "범용(개인)";
        if("1.2.410.200012.1.1.3".compareTo(oid) == 0) oid = "범용(법인)";
        if("1.2.410.200012.1.1.101".compareTo(oid) == 0) oid = "은행/보험용";

        return oid;
    }

    public String getCN(String subjectName){
        if(subjectName != null && subjectName.length() > 0){
            subjectName = subjectName.substring(subjectName.indexOf("=")+1, subjectName.indexOf(","));
        }
        return subjectName;
    }
}
