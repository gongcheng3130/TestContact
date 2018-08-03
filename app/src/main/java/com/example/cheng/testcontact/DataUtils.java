package com.example.cheng.testcontact;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.RawContacts.Data;
import android.text.TextUtils;

public class DataUtils {

	private ContentResolver cr;
	private Context context;

	public DataUtils(Context context) {
		this.context = context;
		cr = context.getContentResolver();
	}

	public List<ContactInfo> getAllPhoneContact(){
		String[] strs = null;
		List<ContactInfo> lists = new ArrayList<>();
		ContactInfo contacts = null;
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
//		Uri uri = Uri.parse("content://com.android.contacts/contacts");//查联系人表
		Cursor cs = cr.query(uri, new String[]{"_id", "sort_key_alt", "contact_last_updated_timestamp"}, null, null, null);
		Cursor aa = null;
		try {
			if(cs!=null){
				while (cs.moveToNext()) {
					contacts = new ContactInfo();
					//获取联系人ID
					int id = cs.getInt(cs.getColumnIndex(Phone._ID));

					//获取联系人头像
					Bitmap contactPhoto = getContactPhoto(id);
					contacts.UserHead = contactPhoto;
					//获取联系人创建时间
					if(cs.getColumnIndex(Phone.CONTACT_PRESENCE)!=-1){
						contacts.CreateTime = cs.getString(cs.getColumnIndex(Phone.CONTACT_PRESENCE));
					}
					//获取联系人修改时间
					if(cs.getColumnIndex(Phone.CONTACT_STATUS_TIMESTAMP)!=-1){
						contacts.UpdateTime = cs.getString(cs.getColumnIndex(Phone.CONTACT_LAST_UPDATED_TIMESTAMP));
					}
					//获取联系人排序关键字
					if(cs.getColumnIndex(Phone.SORT_KEY_ALTERNATIVE)!=-1){
						contacts.SortKey = cs.getString(cs.getColumnIndex(Phone.SORT_KEY_ALTERNATIVE));
					}

					//根据联系人ID查找联系人信息
					uri = Uri.parse("content://com.android.contacts/contacts/"+id+"/data");
					aa = cr.query(uri, null, null, null, null);
					while (aa.moveToNext()) {
						//取得mimeType类型,扩展的数据都在这个类型里面
						String mimeType = aa.getString(aa.getColumnIndex(Data.MIMETYPE));

						//获取各种联系人名称相关
						if (StructuredName.CONTENT_ITEM_TYPE.equals(mimeType)) {
							if(aa.getColumnIndex(StructuredName.DISPLAY_NAME)!=-1){//联系人姓名
								String displayName = aa.getString(aa.getColumnIndex(StructuredName.DISPLAY_NAME));
								contacts.UserName = displayName;
							}
							if(aa.getColumnIndex(StructuredName.PREFIX)!=-1){//联系人前缀，如：先生
								String prefix = aa.getString(aa.getColumnIndex(StructuredName.PREFIX));
							}
							if(aa.getColumnIndex(StructuredName.FAMILY_NAME)!=-1){//联系人姓
								String familyName = aa.getString(aa.getColumnIndex(StructuredName.FAMILY_NAME));
							}
							if(aa.getColumnIndex(StructuredName.MIDDLE_NAME)!=-1){//联系人中间名
								String middleName = aa.getString(aa.getColumnIndex(StructuredName.MIDDLE_NAME));
							}
							if(aa.getColumnIndex(StructuredName.GIVEN_NAME)!=-1){//联系人称呼
								String givenName = aa.getString(aa.getColumnIndex(StructuredName.GIVEN_NAME));
								if(TextUtils.isEmpty(contacts.UserName)) contacts.UserName = givenName;
							}
							if(aa.getColumnIndex(StructuredName.SUFFIX)!=-1){//联系人后缀，如：小
								String suffix = aa.getString(aa.getColumnIndex(StructuredName.SUFFIX));
							}
							if(aa.getColumnIndex(StructuredName.PHONETIC_FAMILY_NAME)!=-1){//姓名姓拼音
								String phoneticFamilyName = aa.getString(aa.getColumnIndex(StructuredName.PHONETIC_FAMILY_NAME));
								contacts.UserNamePY = phoneticFamilyName;
							}
							if(aa.getColumnIndex(StructuredName.PHONETIC_MIDDLE_NAME)!=-1){//联系人中间名拼音
								String phoneticMiddleName = aa.getString(aa.getColumnIndex(StructuredName.PHONETIC_MIDDLE_NAME));
							}
							if(aa.getColumnIndex(StructuredName.PHONETIC_GIVEN_NAME)!=-1){//联系人称呼拼音
								String phoneticGivenName = aa.getString(aa.getColumnIndex(StructuredName.PHONETIC_GIVEN_NAME));
							}
						}

						// 获取各种电话信息
						if (Phone.CONTENT_ITEM_TYPE.equals(mimeType)) {
							int phoneType = aa.getInt(aa.getColumnIndex(Phone.TYPE));// 取出电话类型
							if(aa.getColumnIndex(Phone.NUMBER)!=-1) {
								if (phoneType == Phone.TYPE_MOBILE) {// 手机
									String mobile = aa.getString(aa.getColumnIndex(Phone.NUMBER));
									contacts.PhoneMobile = mobile;
								}
								if (phoneType == Phone.TYPE_HOME) {// 住宅电话
									String home = aa.getString(aa.getColumnIndex(Phone.NUMBER));
									contacts.PhoneHome = home;
								}
								if (phoneType == Phone.TYPE_WORK) {// 单位电话
									String work = aa.getString(aa.getColumnIndex(Phone.NUMBER));
									contacts.PhoneCompany = work;
								}
								if (phoneType == Phone.TYPE_FAX_WORK) {// 单位传真
									String faxWork = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_FAX_HOME) {// 住宅传真
									String faxHome = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_PAGER) {// 寻呼机
									String pager = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_CALLBACK) {// 回拨号码
									String callBack = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_COMPANY_MAIN) {// 公司总机
									String workMain = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_CAR) {// 车载电话
									String car = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_ISDN) {// ISDN
									String isdn = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_MAIN) {// 总机
									String main = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_RADIO) {// 无线装置
									String radio = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_TELEX) {// 电报
									String telex = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_TTY_TDD) {// TTY_TDD
									String tiyTdd = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_WORK_MOBILE) {// 单位手机
									String workMobile = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_WORK_PAGER) {// 单位寻呼机
									String workPager = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_ASSISTANT) {// 助理
									String assistant = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
								if (phoneType == Phone.TYPE_MMS) {// 彩信
									String mms = aa.getString(aa.getColumnIndex(Phone.NUMBER));
								}
							}
						}

						// 查找event地址
						if (ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE.equals(mimeType)) { // 取出时间类型
							int eventType = aa.getInt(aa.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE));
							if(aa.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE)!=-1){
								if (eventType == ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY) {// 生日
									String birthday = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
									contacts.BirthDay = birthday;
								}
								if (eventType == ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY) {// 周年纪念日
									String startDate = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
									contacts.StartDate = startDate;
								}
								if (eventType == ContactsContract.CommonDataKinds.Event.TYPE_OTHER) {// 其他日子
									String startDate = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
								}
							}
						}

						// 获取备注信息
						if (ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE.equals(mimeType)) {
							if(aa.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE)!=-1){
								String remark = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
								contacts.Remark = remark;
							}
						}

						// 获取昵称信息
						if (ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE.equals(mimeType)) {
							if(aa.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME)!=-1){
								String nickName = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
								contacts.NickName = nickName;
							}
						}

						// 获取组织信息
						if(Organization.CONTENT_ITEM_TYPE.equals(mimeType)){
							int type = aa.getInt(aa.getColumnIndex(Organization.TYPE));//取出组织类型
							if (type == Organization.TYPE_CUSTOM) {
								if(aa.getColumnIndex(Organization.COMPANY)!=-1){
									String company = aa.getString(aa.getColumnIndex(Organization.COMPANY));//公司名称
									contacts.CompanyName = company;
								}
								if(aa.getColumnIndex(Organization.TITLE)!=-1){
									String jobTitle = aa.getString(aa.getColumnIndex(Organization.TITLE));//公司职位
									contacts.JobPosition = jobTitle;
								}
								if(aa.getColumnIndex(Organization.DEPARTMENT)!=-1){
									String department = aa.getString(aa.getColumnIndex(Organization.DEPARTMENT));//公司部门
									contacts.Department = department;
								}
							}
						}

                        // 获取网站信息
                        if (ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE.equals(mimeType)) {
                            int webType = aa.getInt(aa.getColumnIndex(ContactsContract.CommonDataKinds.Website.TYPE));// 取出网站类型
                            if (aa.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL) != -1) {
                                if (webType == ContactsContract.CommonDataKinds.Website.TYPE_CUSTOM) {// 主页
                                    String home = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                                    contacts.WebHome = home;
                                }else if (webType == ContactsContract.CommonDataKinds.Website.TYPE_HOME) {// 主页
                                    String home = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                                    contacts.WebHome = home;
                                }
                                if (webType == ContactsContract.CommonDataKinds.Website.TYPE_HOMEPAGE) {// 个人主页
                                    String homePage = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                                    if(TextUtils.isEmpty(contacts.WebHome)) contacts.WebHome = homePage;
                                }
                                if (webType == ContactsContract.CommonDataKinds.Website.TYPE_WORK) {// 工作主页
                                    String work = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                                    contacts.WebWork = work;
                                }
                            }
                        }

						// 查找通讯地址
						if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mimeType)) { // 取出通讯地址
							int postalType = aa.getInt(aa.getColumnIndex(StructuredPostal.TYPE)); // 通讯地址类型
							if (postalType == StructuredPostal.TYPE_WORK) {//单位通讯地址
								if(aa.getColumnIndex(StructuredPostal.DATA)!=-1){
									String address = aa.getString(aa.getColumnIndex(StructuredPostal.DATA));
									contacts.AddressCompany = address;
								}
								if(aa.getColumnIndex(StructuredPostal.STREET)!=-1){
									String street = aa.getString(aa.getColumnIndex(StructuredPostal.STREET));
								}
								if(aa.getColumnIndex(StructuredPostal.CITY)!=-1){
									String city = aa.getString(aa.getColumnIndex(StructuredPostal.CITY));
								}
								if(aa.getColumnIndex(StructuredPostal.POBOX)!=-1){
									String box = aa.getString(aa.getColumnIndex(StructuredPostal.POBOX));
								}
								if(aa.getColumnIndex(StructuredPostal.NEIGHBORHOOD)!=-1){
									String area = aa.getString(aa.getColumnIndex(StructuredPostal.NEIGHBORHOOD));
								}
								if(aa.getColumnIndex(StructuredPostal.REGION)!=-1){
									String region = aa.getString(aa.getColumnIndex(StructuredPostal.REGION));
								}
								if(aa.getColumnIndex(StructuredPostal.POSTCODE)!=-1){
									String postCode = aa.getString(aa.getColumnIndex(StructuredPostal.POSTCODE));
								}
								if(aa.getColumnIndex(StructuredPostal.COUNTRY)!=-1){
									String country = aa.getString(aa.getColumnIndex(StructuredPostal.COUNTRY));
								}
							}
							if (postalType == StructuredPostal.TYPE_HOME) {// 住宅通讯地址
								if(aa.getColumnIndex(StructuredPostal.DATA)!=-1){
									String address = aa.getString(aa.getColumnIndex(StructuredPostal.DATA));
									contacts.AddressHome = address;
								}
								if(aa.getColumnIndex(StructuredPostal.STREET)!=-1){
									String homeStreet = aa.getString(aa.getColumnIndex(StructuredPostal.STREET));
								}
								if(aa.getColumnIndex(StructuredPostal.CITY)!=-1){
									String homeCity = aa.getString(aa.getColumnIndex(StructuredPostal.CITY));
								}
								if(aa.getColumnIndex(StructuredPostal.POBOX)!=-1){
									String homeBox = aa.getString(aa.getColumnIndex(StructuredPostal.POBOX));
								}
								if(aa.getColumnIndex(StructuredPostal.NEIGHBORHOOD)!=-1){
									String homeArea = aa.getString(aa.getColumnIndex(StructuredPostal.NEIGHBORHOOD));
								}
								if(aa.getColumnIndex(StructuredPostal.REGION)!=-1){
									String homeRegion = aa.getString(aa.getColumnIndex(StructuredPostal.REGION));
								}
								if(aa.getColumnIndex(StructuredPostal.POSTCODE)!=-1){
									String homePostCode = aa.getString(aa.getColumnIndex(StructuredPostal.POSTCODE));
								}
								if(aa.getColumnIndex(StructuredPostal.COUNTRY)!=-1){
									String homeCountry = aa.getString(aa.getColumnIndex(StructuredPostal.COUNTRY));
								}
							}
							if (postalType == StructuredPostal.TYPE_OTHER) {// 其他通讯地址
								if(aa.getColumnIndex(StructuredPostal.DATA)!=-1){
									String address = aa.getString(aa.getColumnIndex(StructuredPostal.DATA));
									contacts.AddressOther = address;
								}
								if(aa.getColumnIndex(StructuredPostal.STREET)!=-1){
									String otherStreet = aa.getString(aa.getColumnIndex(StructuredPostal.STREET));
								}
								if(aa.getColumnIndex(StructuredPostal.CITY)!=-1){
									String otherCity = aa.getString(aa.getColumnIndex(StructuredPostal.CITY));
								}
								if(aa.getColumnIndex(StructuredPostal.POBOX)!=-1){
									String otherBox = aa.getString(aa.getColumnIndex(StructuredPostal.POBOX));
								}
								if(aa.getColumnIndex(StructuredPostal.NEIGHBORHOOD)!=-1){
									String otherArea = aa.getString(aa.getColumnIndex(StructuredPostal.NEIGHBORHOOD));
								}
								if(aa.getColumnIndex(StructuredPostal.REGION)!=-1){
									String otherRegion = aa.getString(aa.getColumnIndex(StructuredPostal.REGION));
								}
								if(aa.getColumnIndex(StructuredPostal.POSTCODE)!=-1){
									String otherPostCode = aa.getString(aa.getColumnIndex(StructuredPostal.POSTCODE));
								}
								if(aa.getColumnIndex(StructuredPostal.COUNTRY)!=-1){
									String otherCountry = aa.getString(aa.getColumnIndex(StructuredPostal.COUNTRY));
								}
							}
						}

						// 查找email地址
						if (Email.CONTENT_ITEM_TYPE.equals(mimeType)) {
							int emailType = aa.getInt(aa.getColumnIndex(Email.TYPE));// 取出邮件类型
							if(aa.getColumnIndex(Email.DATA)!=-1){
								// 住宅邮件地址
								if (emailType == Email.TYPE_CUSTOM) {
									String homeEmail = aa.getString(aa.getColumnIndex(Email.DATA));
									contacts.EmailHome = homeEmail;
								}else if (emailType == Email.TYPE_HOME) {
									String homeEmail = aa.getString(aa.getColumnIndex(Email.DATA));
									contacts.EmailHome = homeEmail;
								}
								// 单位邮件地址
								if (emailType == Email.TYPE_CUSTOM) {
									String jobEmail = aa.getString(aa.getColumnIndex(Email.DATA));
									contacts.EmailCompany = jobEmail;
								}else if (emailType == Email.TYPE_WORK) {// 单位邮件地址
									String jobEmail = aa.getString(aa.getColumnIndex(Email.DATA));
									contacts.EmailCompany = jobEmail;
								}
								// 手机邮件地址
								if (emailType == Email.TYPE_CUSTOM) {
									String mobileEmail = aa.getString(aa.getColumnIndex(Email.DATA));
									contacts.EmailMobile = mobileEmail;
								}else if (emailType == Email.TYPE_MOBILE) {
									String mobileEmail = aa.getString(aa.getColumnIndex(Email.DATA));
									contacts.EmailMobile = mobileEmail;
								}
							}
						}

						// 即时消息
						if (ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE.equals(mimeType)) {
							int protocol = aa.getInt(aa.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));// 取出即时消息类型
							if (ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM == protocol) {
								String custom = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
							}
							if (ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN == protocol) {
								String msn = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
							}
							if (ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ == protocol) {
								String qq = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
							}
							if (ContactsContract.CommonDataKinds.Im.PROTOCOL_GOOGLE_TALK == protocol) {
								String google = aa.getString(aa.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
							}
						}
					}
					//获取联系人电子名片数据
					if(!TextUtils.isEmpty(contacts.UserName) && !TextUtils.isEmpty(contacts.PhoneMobile)){
						if(cs.getColumnIndex(Phone.LOOKUP_KEY)!=-1){
							String lookupKey = cs.getString(cs.getColumnIndex(Phone.LOOKUP_KEY));
							Uri VCard = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
							AssetFileDescriptor fd = cr.openAssetFileDescriptor(VCard, "r");
							FileInputStream fis = fd.createInputStream();// 字节流
							contacts.VCardData = parseToString(fis);
						}
						lists.add(contacts);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(aa!=null) aa.close();
			if(cs!=null) cs.close();
		}
		if(lists.size()>0){//开始排序
			strs = new String[lists.size()];
			for (int i = 0; i < lists.size(); i++) {
				strs[i] = lists.get(i).SortKey;
			}
			lists = sortContact(lists, strs);
		}
		return lists;
	}

	//读取字节流
	private String parseToString(InputStream is) throws Exception{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte buffer[] = new byte[1024];
		int len = 0;
		while((len=is.read(buffer))!=-1){
			baos.write(buffer, 0, len);
		}
		String msg = baos.toString();
		baos.flush();
		baos.close();
		return msg;
	}

	//联系人排序
	public List<ContactInfo> sortContact(List<ContactInfo> list, String[] strs) {
		List<ContactInfo> lists = new ArrayList<>();
		getSortOfChinese(strs);
		for (int i = 0; i < strs.length; i++) {
			for (int j = 0; j < list.size(); j++) {
				if(list.get(j).SortKey.equals(strs[i])){
					lists.add(list.get(j));
				}
			}
		}
		return lists;
	}

	//按汉字拼音排序
	public static String[] getSortOfChinese(String[] strs) {
		// Collator 类是用来执行区分语言环境这里使用CHINA
		Comparator<Object> cmp = Collator.getInstance(java.util.Locale.CHINA);
		// JDKz自带对数组进行排序。
		Arrays.sort(strs, cmp);
		return strs;
	}

	//获取手机联系人头像
	private Bitmap getContactPhoto(int contactId) {
		Bitmap photo = null;
		Cursor dataCursor = cr.query(ContactsContract.Data.CONTENT_URI
				, new String[]{ContactsContract.Data.DATA15},
				ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'"
				, new String[]{String.valueOf(contactId)}
				, null);
		if (dataCursor != null) {
			if (dataCursor.getCount() > 0) {
				dataCursor.moveToFirst();
				byte[] bytes = dataCursor.getBlob(dataCursor.getColumnIndex("data15"));
				if (bytes != null) photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			}
			dataCursor.close();
		}
		return photo;
	}

}