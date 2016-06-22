package bamin.com.bamindaijia.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bamin.com.bamindaijia.R;
import bamin.com.bamindaijia.constant.Constant;
import bamin.com.bamindaijia.utils.IsMobileNOorPassword;

public class GetContactsActivity extends Activity implements View.OnClickListener {

    private GridView mGridView_contants;
    String username, usernumber;
    private EditText mEditText_constacts;
    private String mLongPhone;
    private List<String> mPhoneList = new ArrayList<>();
    private ImageView mImageView_cancleInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_contacts);
        initSP();
        findID();
        initUI();
        seListener();
    }

    private void initSP() {
        SharedPreferences sp = getSharedPreferences(Constant.SP.CONTACTS, MODE_PRIVATE);
        mLongPhone = sp.getString(Constant.SP.PHONE_NUM, "");
        Log.e("initSP", "取出的常用联系人" + mLongPhone);
        if (!"".equals(mLongPhone)) {
            String[] split = mLongPhone.split(",");
            mPhoneList.addAll(Arrays.asList(split));
        }
    }

    private void findID() {
        mGridView_contants = (GridView) findViewById(R.id.gridView_contants);
        mEditText_constacts = (EditText) findViewById(R.id.editText_constacts);
        mImageView_cancleInput = (ImageView) findViewById(R.id.imageView_cancleInput);
    }

    private void initUI() {
        mGridView_contants.setAdapter(new MyAdapter());
    }

    private void seListener() {
        findViewById(R.id.imageView_contacts).setOnClickListener(this);
        findViewById(R.id.textView_cancle).setOnClickListener(this);
        findViewById(R.id.textView_ok).setOnClickListener(this);
        mEditText_constacts.addTextChangedListener(new MyTextWatcher());
        mImageView_cancleInput.setOnClickListener(this);
    }

    class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length()==0){
                mImageView_cancleInput.setVisibility(View.GONE);
            }else{
                mImageView_cancleInput.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.PERMISSION.READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                startContacts();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);
            while (phone.moveToNext()) {
                usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).toString().trim();
                String phoneNum = usernumber.replaceAll(" ", "");
                mEditText_constacts.setText(phoneNum);
                mEditText_constacts.setSelection(phoneNum.length());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_cancleInput:
                mEditText_constacts.setText("");
                break;
            case R.id.textView_cancle:
                pushDownOut();
                break;
            case R.id.textView_ok:
                String phoneNum = mEditText_constacts.getText().toString().trim();
                boolean mobileNO = IsMobileNOorPassword.isMobileNO(phoneNum);
                if (mobileNO) {
                    if (mPhoneList.contains(phoneNum)) {

                    } else {
                        if (mPhoneList.size() >= 12) {
                            mPhoneList.remove(0);
                        }
                        mPhoneList.add(phoneNum);
                        SharedPreferences sp = getSharedPreferences(Constant.SP.CONTACTS, MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        String longPhoneNum = "";
                        for (int i = 0; i < mPhoneList.size(); i++) {
                            longPhoneNum = longPhoneNum+mPhoneList.get(i) + ",";
                        }
                        Log.e("onClick", "存入的常用联系人" + longPhoneNum);
                        edit.putString(Constant.SP.PHONE_NUM, longPhoneNum);
                        edit.commit();
                    }
                    Intent intent = new Intent();
                    intent.putExtra(Constant.INTENT_KEY.THE_CONTACTS,phoneNum);
                    setResult(Constant.REQUEST_RESULT_CODE.CHOSSE_CONTACTS, intent);
                    pushDownOut();
                } else {
                    Toast.makeText(GetContactsActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageView_contacts:
                if (ContextCompat.checkSelfPermission(GetContactsActivity.this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(GetContactsActivity.this, new String[]{Manifest.permission.READ_CONTACTS},
                            Constant.PERMISSION.READ_CONTACTS);
                } else {
                    startContacts();
                }
                break;
        }
    }

    private void startContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, Constant.REQUEST_RESULT_CODE.PICK_CONTACT);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPhoneList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View inflate = getLayoutInflater().inflate(R.layout.contacts_item, null);
            Button button_contacts = (Button) inflate.findViewById(R.id.button_contacts);
            final String phone = mPhoneList.get(position);
            if (mPhoneList.size() > 0) {
                button_contacts.setText(phone);
            }
            button_contacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditText_constacts.setText(phone);
                    mEditText_constacts.setSelection(phone.length());
                }
            });
            return inflate;
        }
    }

    /**
     * 从上往下退出
     */
    public void pushDownOut(){
        finish();
        overridePendingTransition(R.anim.push_up_in,R.anim.push_down_out);
    }

    @Override
    public void onBackPressed() {
        pushDownOut();
    }
}
