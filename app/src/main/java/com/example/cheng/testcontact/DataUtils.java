package com.example.cheng.testcontact;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
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

	public DataUtils(Context context) {
		cr = context.getContentResolver();
	}

	public List<ContactInfo> getAllPhoneContact(){
		String[] strs = null;
		List<ContactInfo> lists = new ArrayList<>();
		ContactInfo contacts = null;
		List<Map<String, String>> numbers = null;
//		ContactsContract.Contacts.CONTENT_URI
		Uri uri = Uri.parse("content://com.android.contacts/contacts");//查联系人表
		Cursor cs = cr.query(uri, new String[]{"_id", "sort_key_alt", "contact_last_updated_timestamp"}, null, null, null);
		Cursor aa = null;
		try {
			if(cs!=null){
				while (cs.moveToNext()) {
					numbers = new ArrayList<>();//放置当前联系人所有的手机号与类型
					contacts = new ContactInfo();
					int id = cs.getInt(cs.getColumnIndex(Phone._ID));
					if(cs.getColumnIndex(Phone.CONTACT_LAST_UPDATED_TIMESTAMP)!=-1){
						contacts.UpdataTime = cs.getString(cs.getColumnIndex(Phone.CONTACT_LAST_UPDATED_TIMESTAMP));
					}
					if(cs.getColumnIndex(Phone.SORT_KEY_ALTERNATIVE)!=-1){
						contacts.SortKey = cs.getString(cs.getColumnIndex(Phone.SORT_KEY_ALTERNATIVE));
					}
//					ContactsContract.Data.CONTENT_URI
//					Uri uri = Uri.parse("content://com.android.contacts/data");
//					String selection = Data.RAW_CONTACT_ID + " = ? ";
//					String[] selectionArgs = {id+""};
//					String[] projection = {"mimetype", "data1", "data2", "data4"};
					uri = Uri.parse("content://com.android.contacts/contacts/"+id+"/data");//根据联系人ID查找联系人信息
					aa = cr.query(uri, null, null, null, null);
					while (aa.moveToNext()) {
						String mimetype = aa.getString(aa.getColumnIndex(Data.MIMETYPE));//mimetype类型
						if(StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)){//名称
							contacts.UserName = aa.getString(aa.getColumnIndex(StructuredName.DISPLAY_NAME));
						}
						if (Email.CONTENT_ITEM_TYPE.equals(mimetype)) {//Email
							contacts.Email = aa.getString(aa.getColumnIndex(Email.DATA));
						}
						if(Organization.CONTENT_ITEM_TYPE.equals(mimetype)){//组织
							int type = aa.getInt(aa.getColumnIndex(Organization.TYPE));//取出组织类型
							if (type == Organization.TYPE_CUSTOM) {
								if(aa.getColumnIndex(Organization.COMPANY)!=-1){
									contacts.CompanyName = aa.getString(aa.getColumnIndex(Organization.COMPANY));//代表公司名称
								}
								if(aa.getColumnIndex(Organization.TITLE)!=-1){
									contacts.JobPosition = aa.getString(aa.getColumnIndex(Organization.TITLE));//代表公司职位
								}
								if(aa.getColumnIndex(Organization.DEPARTMENT)!=-1){
									contacts.Department = aa.getString(aa.getColumnIndex(Organization.DEPARTMENT));//代表公司部门
								}
							}
						}
						if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mimetype)) {//地址
							int type = aa.getInt(aa.getColumnIndex(StructuredPostal.TYPE));//取出地址类型
							if(type==StructuredPostal.TYPE_WORK){//代表单位地址
								contacts.CompanyAddress = aa.getString(aa.getColumnIndex(StructuredPostal.DATA));
							}
						}
						if(Phone.CONTENT_ITEM_TYPE.equals(mimetype)){//手机号
							String number = aa.getString(aa.getColumnIndex(Phone.NUMBER));
							String type = aa.getString(aa.getColumnIndex(Phone.TYPE));
							Map<String, String> map = new HashMap<String, String>();
							map.put("PhoneNumber", number);
							map.put("PhoneType", type);
							numbers.add(map);
						}
					}
					aa.close();
					if(numbers.size()>0){
						for (int i = 0; i < numbers.size(); i++) {
							if(i==0){
								contacts.PhoneNum1 = numbers.get(0).get("PhoneNumber");
								contacts.PhoneNum1Type = numbers.get(0).get("PhoneType");
							}else if(i==1){
								contacts.PhoneNum2 = numbers.get(1).get("PhoneNumber");
								contacts.PhoneNum2Type = numbers.get(1).get("PhoneType");
							}else if(i==2){
								contacts.PhoneNum3 = numbers.get(2).get("PhoneNumber");
								contacts.PhoneNum3Type = numbers.get(2).get("PhoneType");
							}
						}
					}
					if(!TextUtils.isEmpty(contacts.UserName) && !TextUtils.isEmpty(contacts.PhoneNum1)){
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
			cs.close();
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

}