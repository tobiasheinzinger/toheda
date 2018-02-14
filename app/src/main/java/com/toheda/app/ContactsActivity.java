package com.toheda.app;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toheda.app.model.ContactModel;
import com.toheda.app.util.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContactsActivity extends AppCompatActivity {

    private static final CharSequence FILTER_CONSTRAINT_ALL = "FILTER_CONSTRAINT_ALL";

    private static final CharSequence FILTER_CONSTRAINT_KNOWN = "FILTER_CONSTRAINT_KNOWN";

    private static final CharSequence FILTER_CONSTRAINT_UNKNOWN = "FILTER_CONSTRAINT_UNKNOWN";

    private static final int REQUEST_CODE_ADD_CONTACT = 1;

    private static final int REQUEST_CODE_INVITE_CONTACT = 2;

    private ArrayAdapter<ContactModel> adapter;
    private String contactsServiceUrl;
    private ProgressBar progressBar;
    private CharSequence filterConstraint = FILTER_CONSTRAINT_KNOWN;
    private JsonFileHandler jsonFileHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        adapter = new ContactsAdapter(this, new ArrayList<ContactModel>());
        adapter.setNotifyOnChange(true);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ContactModel contact = (ContactModel) parent.getItemAtPosition(position);

                        Toast.makeText(ContactsActivity.this, contact.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String serverUrl = sharedPreferences.getString(getString(R.string.server_url), "not available");
        String contactsServicePath = sharedPreferences.getString(getString(R.string.contacts_request), "not available");

        contactsServiceUrl = TextUtils.join("/", new String[]{serverUrl, contactsServicePath});

        jsonFileHandler = new JsonFileHandler(this);

        initContacts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ADD_CONTACT:
                if (resultCode == RESULT_OK) {
                    refreshContacts();
                }
                break;

            case REQUEST_CODE_INVITE_CONTACT:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Thanks for inviting your friend", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contacts_activity, menu);
        return true;
    }

    public void addContact(MenuItem menuItem) {
        Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra("finishActivityOnSaveCompleted", true);

        this.startActivityForResult(intent, 1);
    }

    public void refreshContacts(MenuItem menuItem) {
        refreshContacts();
    }

    public void toggleContacts(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.contacts_known:
                filterConstraint = FILTER_CONSTRAINT_KNOWN;
                break;
            case R.id.contacts_unknown:
                filterConstraint = FILTER_CONSTRAINT_UNKNOWN;
                break;
            case R.id.contacts_all:
                filterConstraint = FILTER_CONSTRAINT_ALL;
                break;
        }

        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
        } else {
            menuItem.setChecked(true);
        }

        adapter.getFilter().filter(filterConstraint);
    }


    private void initContacts() {
        if (adapter.getCount() <= 0) {
            List<ContactModel> contacts = jsonFileHandler.read();

            adapter.clear();
            adapter.addAll(contacts);
            adapter.getFilter().filter(filterConstraint);
        }

        if (adapter.getCount() <= 0) {
            refreshContacts(null);
        }
    }

    public void refreshContacts() {
        new ContactsLoaderAsyncTask(this).execute();
    }

    private void checkContacts(final List<ContactModel> contacts) {
        final Gson gson = new Gson();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(gson.toJson(contacts));
        } catch (JSONException e) {
            return;
        }

        final JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.POST, contactsServiceUrl, jsonArray, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        Type listType = new TypeToken<List<String>>() {
                        }.getType();
                        List<String> knownIds = gson.fromJson(response.toString(), listType);

                        for (ContactModel contact : contacts) {
                            contact.setKnown(knownIds.contains(contact.getId()));
                        }

                        jsonFileHandler.save(contacts);

                        adapter.clear();
                        adapter.addAll(contacts);
                        adapter.getFilter().filter(filterConstraint);

                        progressBar.setVisibility(View.GONE);

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(getApplicationContext(), "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })

        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        request.setRetryPolicy(new

                DefaultRetryPolicy(
                2 * 60 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        getWindow().

                setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        RequestQueueSingleton.getInstance(this).

                addToRequestQueue(request);
    }

    class ContactsLoaderAsyncTask extends AsyncTask<String, Integer, List<ContactModel>> {

        private ProgressDialog progressDialog;
        private final Context context;

        public ContactsLoaderAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected List<ContactModel> doInBackground(String... params) {
            List<ContactModel> contacts = new ArrayList<>();

            String order = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
            ContentResolver cr = getContentResolver();
            Cursor crContacts = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, order);

            while (crContacts.moveToNext()) {
                String id = crContacts.getString(crContacts.getColumnIndex(ContactsContract.Contacts._ID));
                String name = crContacts.getString(crContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String photoUri = crContacts.getString(crContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                String phone = null;
                String email = null;

                if (Integer.parseInt(crContacts.getString(crContacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor crPhones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", new String[]{id}, null);

                    while (crPhones.moveToNext()) {
                        int type = crPhones.getInt(crPhones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        switch (type) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE: {
                                phone = crPhones.getString(crPhones
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }
                        }
                    }
                    crPhones.close();
                }

                Cursor crEmails = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                + " = ?", new String[]{id}, null);
                while (crEmails.moveToNext()) {
                    email = crEmails.getString(crEmails
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                crEmails.close();

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(phone)) {
                    continue;
                }

                ContactModel contact = new ContactModel();
                contact.setId(id);
                contact.setName(name);
                contact.setPhone(phone);
                contact.setEmail(email);
                contact.setPhotoUri(photoUri);

                contacts.add(contact);

                publishProgress(crContacts.getPosition());
            }

            crContacts.close();

            return contacts;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context,
                    "ProgressDialog", "");
        }

        @Override
        protected void onPostExecute(List<ContactModel> result) {
            progressDialog.dismiss();

            checkContacts(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setMessage("Reading contacts " + values[0]);
        }
    }

    class ContactsAdapter extends ArrayAdapter<ContactModel> {

        private ArrayList<ContactModel> originalContacts;
        private ArrayList<ContactModel> filteredContacts;
        private Filter filter;

        public ContactsAdapter(Context context, ArrayList<ContactModel> contacts) {
            super(context, 0, contacts);
            this.originalContacts = contacts;
            this.filteredContacts = contacts;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ContactModel contact = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_contacts, parent, false);
            }

            ImageView imageViewImage = (ImageView) convertView.findViewById(R.id.contact_image);
            TextView textViewName = (TextView) convertView.findViewById(R.id.contact_name);
            TextView textViewPhone = (TextView) convertView.findViewById(R.id.contact_phone);
            TextView textViewEmail = (TextView) convertView.findViewById(R.id.contact_email);
            ImageButton emailButton = (ImageButton) convertView.findViewById(R.id.contact_email_button);

            imageViewImage.setImageURI(contact.getPhotoUri() != null ? Uri.parse(contact.getPhotoUri()) : null);
            textViewName.setText(contact.getName());
            textViewPhone.setText(contact.getPhone());
            textViewEmail.setText(contact.getEmail());

            String subject = "Invitation to ToHeDa";
            Spanned text = Html.fromHtml(new StringBuilder()
                    .append("<p><b>Hi " + contact.getName() + "</b></p>")
                    .append("<small><p>I'm using ToHeDa now.</p></small>")
                    .append("<small><p>Would you like to join me?")
                    .toString());
            emailButton.setOnClickListener(new SendEmailOnClickListener(contact.getEmail(), subject, text));
            emailButton.setVisibility(contact.isKnown() ? View.INVISIBLE : View.VISIBLE);

            return convertView;
        }

        public int getCount() {
            return filteredContacts.size();
        }

        public ContactModel getItem(int position) {
            return filteredContacts.get(position);
        }

        @Override
        public Filter getFilter() {
            if (filter == null)
                filter = new ContactFilter();

            return filter;
        }

        private class ContactFilter<T extends ContactModel> extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ContactModel> filteredContacts = new ArrayList<>();

                if (TextUtils.equals(constraint, FILTER_CONSTRAINT_ALL)) {
                    filteredContacts.addAll(originalContacts);

                } else {
                    for (ContactModel contact : originalContacts) {
                        if ((contact.isKnown()
                                && TextUtils.equals(constraint, FILTER_CONSTRAINT_KNOWN))
                                || (!contact.isKnown()
                                && TextUtils.equals(constraint, FILTER_CONSTRAINT_UNKNOWN))) {
                            filteredContacts.add(contact);
                        }
                    }
                }

                FilterResults result = new FilterResults();
                result.count = filteredContacts.size();
                result.values = filteredContacts;

                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredContacts = (ArrayList<ContactModel>) results.values;
                notifyDataSetChanged();
            }
        }

        private class SendEmailOnClickListener implements View.OnClickListener {

            private final String email;
            private final String subject;
            private final Spanned text;

            public SendEmailOnClickListener(String email, String subject, Spanned text) {
                this.email = email;
                this.subject = subject;
                this.text = text;
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, text);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 2);
                }
            }
        }
    }

    class JsonFileHandler<T extends ContactModel> {

        private final Gson gson;
        private final Context context;

        public JsonFileHandler(final Context context) {
            this.context = context;
            gson = new Gson();
        }

        public void save(List<ContactModel> data) {
            String jsonString = gson.toJson(data);

            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("contacts.json", Context.MODE_PRIVATE));
                outputStreamWriter.write(jsonString);
                outputStreamWriter.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        private List<ContactModel> read() {
            List<ContactModel> contacts = new ArrayList<ContactModel>();

            try {
                InputStream inputStream = context.openFileInput("contacts.json");

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();

                    ContactModel[] contactArray = gson.fromJson(stringBuilder.toString(), ContactModel[].class);
                    contacts = new ArrayList<ContactModel>(Arrays.asList(contactArray));
                }
            } catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }

            return contacts;
        }
    }
}