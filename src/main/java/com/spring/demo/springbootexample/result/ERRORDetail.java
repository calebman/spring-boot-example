package com.spring.demo.springbootexample.result;


/**
 * 平台错误描述定义，可根据索引查询具体错误描述。
 */
public enum ERRORDetail {

    RC_0000000("0000000", "成功"),
    
    //参数错误【参数为空】
    RC_0101001("0101001", "id为空"),
    RC_0101002("0101002", "name为空"),
    RC_0101003("0101003", "分页参数为空"),

    //参数错误【格式错误】
    RC_0102001("0102001", "biz_sequence_id格式错误"),

    //参数错误【参数无效】
    RC_0103001("0103001", "id_type无效"),

    //请求错误
    RC_0201004("0201004", "version错误"),
    RC_0201005("0201005", "请求地址错误"),

    //认证错误【状态错误】
    RC_0301001("0301001", "证书已经注销或未注册"),

    //认证错误【验证失败】
    RC_0302001("0302001", "eID签名错误"),

    //操作失败
    RC_0303001("0303001", "sms用户处理失败"),
    RC_0303002("0303002", "用户拒绝处理"),

    //系统错误
    RC_0401001("0401001", "服务异常"),
    RC_0401002("0401002", "无法返回"),
    RC_0401003("0401003", "返回错误"),
	RC_0401006("0401006", "应用服务器缓存异常"),
	RC_0401007("0401007", "应用服务器超时"),
	
	RC_6666666("0500000","异常,没有找到合适的结果描述");

    private String index;
    private String meaning;

    ERRORDetail(String index, String meaning) {
        this.index = index;
        this.setMeaning(meaning);
    }

    public String getIndex() {
        return this.index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    
    public String getResultCode(){
    	return this.index.substring(0,2);
    }
    
    public String getDetails(){
    	return this.index+"("+this.meaning+")";
    }

    public static ERRORDetail getEnum(String index) {
        for (ERRORDetail st : ERRORDetail.values()) {
            if (st.index.equals(index)) {
                return st;
            }
        }
        return RC_6666666;
    }
    
    

}
