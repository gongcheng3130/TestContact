package com.example.cheng.testcontact;

public class ContactInfo {

	public String VCardData;//电子名片数据字符串，一般用不到
	public String UserName;//联系人名称
	public String UserNamePY;//联系人名称拼音
	public String NickName;//联系人昵称
	public String BirthDay;//联系人生日
	public String StartDate;//周年纪念日
	public String Remark;//联系人备注
	public String CompanyName;//公司
	public String Department;//部门
	public String JobPosition;//职位
	public String PhoneMobile;//手机
	public String PhoneHome;//住宅电话
	public String PhoneCompany;//单位电话
	public String EmailHome;//住宅邮箱
	public String EmailCompany;//公司邮箱
	public String EmailMobile;//手机邮箱
	public String AddressHome;//住宅地址
    public String AddressCompany;//公司地址
	public String AddressOther;//住宅地址
	public String WebHome;//
	public String CreateTime;//联系人创建时间
	public String UpdateTime;//联系人最后修改时间
	public String SortKey;//排序关键字

	@Override
	public String toString() {
		return "ContactInfo{" +
				"UserName='" + UserName + '\'' +
				", UserNamePY='" + UserNamePY + '\'' +
				", NickName='" + NickName + '\'' +
				", BirthDay='" + BirthDay + '\'' +
				", StartDate='" + StartDate + '\'' +
				", Remark='" + Remark + '\'' +
				", CompanyName='" + CompanyName + '\'' +
				", Department='" + Department + '\'' +
				", JobPosition='" + JobPosition + '\'' +
				", PhoneMobile='" + PhoneMobile + '\'' +
				", PhoneHome='" + PhoneHome + '\'' +
				", PhoneCompany='" + PhoneCompany + '\'' +
				", EmailHome='" + EmailHome + '\'' +
				", EmailCompany='" + EmailCompany + '\'' +
				", EmailMobile='" + EmailMobile + '\'' +
				", AddressHome='" + AddressHome + '\'' +
				", AddressCompany='" + AddressCompany + '\'' +
				", AddressOther='" + AddressOther + '\'' +
				", CreateTime='" + CreateTime + '\'' +
				", UpdateTime='" + UpdateTime + '\'' +
				", SortKey='" + SortKey + '\'' +
				'}';
	}

}
