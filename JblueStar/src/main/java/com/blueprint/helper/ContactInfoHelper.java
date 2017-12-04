package com.blueprint.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class ContactInfoHelper {
	/**
	 * ��ϵ����Ϣ�Ĺ�����
	 * @param context
	 *            ������
	 * @return
	 */
	public static List<ContactInfo> getContactInfos(Context context) {
		// �����ṩ�ߵĽ�����
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
				null, null, null);
		List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			//System.out.println("id=" + id);
			if (id != null) {
				ContactInfo info = new ContactInfoHelper().new ContactInfo();
				Cursor datacursor = resolver.query(datauri, new String[] {
						"data1", "mimetype" }, "raw_contact_id=?",
						new String[] { id }, null);
				while (datacursor.moveToNext()) {
					String data1 = datacursor.getString(0);
					String mimetype = datacursor.getString(1);
					if ("vnd.android.cursor.item/name".equals(mimetype)) {
						// ����
						info.name = data1;
					} else if ("vnd.android.cursor.item/phone_v2"
							.equals(mimetype)) {
						// �绰
						info.phone = data1;
					} else if ("vnd.android.cursor.item/email_v2"
							.equals(mimetype)) {
						// ����
						info.email = data1;
					}
				}
				contactInfos.add(info);
			}
		}
		return contactInfos;
	}

	public class ContactInfo {
		public String name;
		public String email;
		public String phone;
	}
}
