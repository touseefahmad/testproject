package com.example.enigma_pc.smartmailforblinds;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enigma_pc.smartmailforblinds.Response.EmailModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by enigma-pc on 4/5/2017.
 */

public class RetrieveMailsFromGmail extends Activity implements EasyPermissions.PermissionCallbacks{

    GoogleAccountCredential mCredential;
   /* private TextView mOutputText;
    private Button mCallApiButton;*/
    ProgressDialog mProgress;
    ArrayList<Message> emailsList;
    MyAdapter myAdapter;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Gmail API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { GmailScopes.MAIL_GOOGLE_COM };

    private List<Message> msgList;
    ListView lvMails;
    Button btnChooser;
    Button btnNext;
    boolean isCancled=false;

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.retrievemail_activity);
        lvMails=(ListView)findViewById(R.id.lvMails);
        btnChooser=(Button)findViewById(R.id.btnCallChoosser);
        btnChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO call intent;
                isCancled=true;
                Intent intent=new Intent(RetrieveMailsFromGmail.this,ChooserActivity.class);
                startActivity(intent);
                RetrieveMailsFromGmail.this.finish();
            }
        });
       btnNext=(Button)findViewById(R.id.btnNextMails);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call retrieve messages and add to list
            }
        });

      /*  LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);*/
/*
        mCallApiButton = new Button(this);
        mCallApiButton.setText(BUTTON_TEXT);
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                mOutputText.setText("");
                getResultsFromApi();
                mCallApiButton.setEnabled(true);
            }
        });*/
     /*   activityLayout.addView(mCallApiButton);

        mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        mOutputText.setText(
                "Click the \'" + BUTTON_TEXT +"\' button to test the API.");
        activityLayout.addView(mOutputText);*/

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Gmail API ...");

       // setContentView(activityLayout);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        emailsList=new ArrayList<Message>();
        getResultsFromApi();
        lvMails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isCancled=true;
                Message model= emailsList.get(position);
                Intent intent=new Intent(RetrieveMailsFromGmail.this,ReadMailActivity.class);
                intent.putExtra("TO",model.getPayload().getHeaders().get(0).getValue());
                intent.putExtra("FROM",model.getPayload().getHeaders().get(16).getValue());
                intent.putExtra("Subject",model.getPayload().getHeaders().get(20).getValue());
                intent.putExtra("BODY",model.getSnippet());

                startActivity(intent);
                RetrieveMailsFromGmail.this.finish();
            }
        });

    }



    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(getApplicationContext(),"No Network Connection",Toast.LENGTH_SHORT).show();
         //   mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getApplicationContext(),"This app requires Google Play Services. Please install \" +\n" +
                            " \"Google Play Services on your device and relaunch this app",Toast.LENGTH_SHORT).show();

                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                RetrieveMailsFromGmail.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Gmail API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<Message>> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Gmail API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<Message> doInBackground(Void... params) {
            try {

                return listMessagesMatchingQuery(mService,"me","full");
               // return listMessagesWithLabels(mService,"me",getDataFromApi());
                //return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }



        public  List<Message> listMessagesMatchingQuery(Gmail service, String userId,
                                                              String query) throws IOException {
            ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

            List<Message> messages = new ArrayList<Message>();
            while (response.getMessages() != null && !isCancled) {
               // List<Message> emailsList=new ArrayList<Message>();
            //    String snipet=response.getMessages().get(0).getSnippet();
                messages.addAll(response.getMessages());

                for(int i=0;i<response.getMessages().size();i++) {
                    if(!isCancled){
                        emailsList.add(getMessage(mService, userId, response.getMessages().get(i).getId()));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(emailsList.size()>0 && emailsList.size()<2){
                                    myAdapter=new MyAdapter(getApplicationContext(),R.layout.view_email_list_row,emailsList);
                                    lvMails.setAdapter(myAdapter);
                                }else{
                                    myAdapter.notifyDataSetChanged();
                                }
                            }
                        });

                    }


                }




                // On retrieving mails from next page firs time there will be no token available
                //next time we have to keep track of emails retrieved so we have to send token
                // to retrieve mails from next page
                //Currently this is disabled
                //TODO This will be used to retrieve the next page of emails
                /*if (response.getNextPageToken() != null) {
                    String pageToken = response.getNextPageToken();
                    response = service.users().messages().list(userId).setQ(query)
                            .setPageToken(pageToken).execute();
                } else {
                    break;
                }*/
            }

            for (Message message : messages) {
                System.out.println(message.toPrettyString());
            }

            return messages;
        }
        public  Message getMessage(Gmail service, String userId, String messageId)
                throws IOException {
            Message message = service.users().messages().get(userId, messageId).execute();

// retrieve from payload /headers/ Delievered-to, From, Subject, date;
            /*com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64 base=new com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64(true);
            byte[] emailBytes = base.decodeBase64(message.getRaw());
            String strMsg=emailBytes.toString();*/


            System.out.println("Message snippet: " + message.getSnippet());

            return message;
        }


        public List<Message> listMessagesWithLabels(Gmail service, String userId,
                                                    List<String> labelIds) throws IOException {
            List inbox=new ArrayList<String>();
            inbox.add("INBOX");
            ListMessagesResponse response = service.users().messages().list(userId)
                    .setLabelIds(inbox).execute();

            List<Message> messages = new ArrayList<Message>();
            while (response.getMessages() != null) {
                messages.addAll(response.getMessages());
                if (response.getNextPageToken() != null) {
                    String pageToken = response.getNextPageToken();
                    response = service.users().messages().list(userId).setLabelIds(labelIds)
                            .setPageToken(pageToken).execute();
                } else {
                    break;
                }
            }

            for (Message message : messages) {
                System.out.println(message.toPrettyString());
            }

            return messages;
        }

        /**
         * Fetch a list of Gmail labels attached to the specified account.
         * @return List of Strings labels.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get the labels in the user's account.
            String user = "me";
            List<String> labels = new ArrayList<String>();
            ListLabelsResponse listResponse =
                    mService.users().labels().list(user).execute();
          /*  msgList=new ArrayList<Message>();
            ListMessagesResponse messagesResponse=mService.users().messages().list(user).execute();
            Message message = mService.users().messages().get(user, messagesResponse.getMessages().get(0).getId()).setFormat("raw").execute();
            for(Message msg:messagesResponse.getMessages()){
                Base64 base64Url = new Base64(true);
                byte[] emailBytes = base64Url.decodeBase64(msg.getRaw());
                String strMsg=emailBytes.toString();
                msgList.add(msg);
            }*/
            for (Label label : listResponse.getLabels()) {
                labels.add(label.getName());
            }
            return labels;
        }


        @Override
        protected void onPreExecute() {

            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<Message> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                Toast.makeText(getApplicationContext(),"No Results Returned",Toast.LENGTH_SHORT).show();

            } else {
                //output.add(0, "Data retrieved using the Gmail API:");
               // mOutputText.setText(TextUtils.join("\n", msgList.get(0).getPayload().getHeaders()));
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            isCancled=true;
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            RetrieveMailsFromGmail.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(getApplicationContext(),mLastError.getMessage(),Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(getApplicationContext(),"Request cancelled.",Toast.LENGTH_SHORT).show();

            }
        }
    }




    public class MyAdapter extends ArrayAdapter<Message> {

        ArrayList<Message> emailList = new ArrayList<>();

        public MyAdapter(Context context, int textViewResourceId, ArrayList<Message> objects) {
            super(context, textViewResourceId, objects);
            emailList = objects;
        }

        @Override
        public int getCount() {
            if(isCancled){
                return 0;
            }
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.view_email_list_row, null);
            TextView tvSubject = (TextView) v.findViewById(R.id.tvSubject);
            TextView tvTo = (TextView) v.findViewById(R.id.tvTo);
            TextView tvFrom = (TextView) v.findViewById(R.id.tvFrom);
            TextView tvContent = (TextView) v.findViewById(R.id.tvContent);

            if(!isCancled) {

                if(emailsList.get(position).getPayload().getHeaders().size()>20) {
                    tvSubject.setText("Subject: " + emailsList.get(position).getPayload().getHeaders().get(20).getValue().toString());
                    tvTo.setText("TO: " + emailsList.get(position).getPayload().getHeaders().get(0).getValue().toString());
                    tvFrom.setText("FROM: " + emailsList.get(position).getPayload().getHeaders().get(16).getValue().toString());
                    tvContent.setText(emailsList.get(position).getSnippet());
                }
            }

            return v;

        }

    }


}
