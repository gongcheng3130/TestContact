package com.example.cheng.testcontact;

public class ContactInfo {

	public String VCardData;
	public String UserName;
	public String CompanyName;
	public String CompanyAddress;
	public String Department;
	public String JobPosition;
	public String PhoneNum1;
	public String PhoneNum1Type;
	public String PhoneNum2;
	public String PhoneNum2Type;
	public String PhoneNum3;
	public String PhoneNum3Type;
	public String Email;
	public String CreateTime;
	public String UpdataTime;
	public String SortKey;

	@Override
	public String toString() {
		return "ContactInfo{" +
				"VCardData='" + VCardData + '\'' +
				", UserName='" + UserName + '\'' +
				", CompanyName='" + CompanyName + '\'' +
				", CompanyAddress='" + CompanyAddress + '\'' +
				", Department='" + Department + '\'' +
				", JobPosition='" + JobPosition + '\'' +
				", PhoneNum1='" + PhoneNum1 + '\'' +
				", PhoneNum1Type='" + PhoneNum1Type + '\'' +
				", PhoneNum2='" + PhoneNum2 + '\'' +
				", PhoneNum2Type='" + PhoneNum2Type + '\'' +
				", PhoneNum3='" + PhoneNum3 + '\'' +
				", PhoneNum3Type='" + PhoneNum3Type + '\'' +
				", Email='" + Email + '\'' +
				", CreateTime='" + CreateTime + '\'' +
				", UpdataTime='" + UpdataTime + '\'' +
				", SortKey='" + SortKey + '\'' +
				'}';
	}

}
