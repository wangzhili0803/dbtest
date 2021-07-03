package com.jerry.bitcoin.helper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;

import com.jerry.baselib.Key;
import com.jerry.baselib.common.bean.DyUser;
import com.jerry.baselib.common.util.StringUtil;

/**
 * @author Jerry
 * @createDate 5/18/21
 * @copyright www.axiang.com
 * @description
 */
public class ContactHelper {

    /**
     * 批量添加通讯录
     */
    public static boolean BatchAddContact(Context context, List<DyUser> list)
        throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex = 0;
        for (DyUser contact : list) {
            String phones = contact.getPhones();
            if (TextUtils.isEmpty(phones)) {
                continue;
            }
            rawContactInsertIndex = ops.size(); // 有了它才能给真正的实现批量添加
            ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .withYieldAllowed(true).build());

            // 添加姓名
            ops.add(ContentProviderOperation
                .newInsert(
                    android.provider.ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID,
                    rawContactInsertIndex)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, contact.getName())
                .withYieldAllowed(true).build());
            // 添加号码
            String[] phoneList = StringUtil.safeSplit(phones, Key.COMMA);
            for (String phone : phoneList) {
                ops.add(ContentProviderOperation
                    .newInsert(
                        android.provider.ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                    .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, phone)
                    .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                    .withValue(Phone.LABEL, "").withYieldAllowed(true).build());
            }
        }
        // 真正添加
        ContentProviderResult[] results = context.getContentResolver()
            .applyBatch(ContactsContract.AUTHORITY, ops);

        return true;
    }
}
