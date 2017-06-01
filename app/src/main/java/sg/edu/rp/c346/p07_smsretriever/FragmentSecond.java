package sg.edu.rp.c346.p07_smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecond extends Fragment {

    EditText etWord;
    Button btnRetrieveFrag2, btnEmail2;
    TextView tv2;
    String emailSMS2;


    public FragmentSecond() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        tv2 = (TextView) view.findViewById(R.id.textView3);
        btnRetrieveFrag2 = (Button) view.findViewById(R.id.btnRetrieveFrag2);
        etWord = (EditText) view.findViewById(R.id.etWord);
        btnEmail2 = (Button) view.findViewById(R.id.btnEmail2);

        btnEmail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                // Put essentials like email address, subject & body text
                email.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"jason_lim@rp.edu.sg"});
                email.putExtra(Intent.EXTRA_SUBJECT,
                        "EMAIL FROM SMS");
                email.putExtra(Intent.EXTRA_TEXT,
                        emailSMS2);
                // This MIME type indicates email
                email.setType("message/rfc822");
                // createChooser shows user a list of app that can handle
                // this MIME type, which is, email
                startActivity(Intent.createChooser(email,
                        "Choose an Email client :"));
            }
        });

        btnRetrieveFrag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getContext(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }
                Uri uri = Uri.parse("content://sms");
                String[] args = etWord.getText().toString().split(" ");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                String conditions = "body LIKE ?";
                           for(int i = 0;i < args.length; i++){
                               args[i] = "%" + args[i] + "%" ;
                               if(i!=0){
                                   conditions += ("OR BODY LIKE ?");
                               }
                           }
                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();

                // The filter String
                //           String filter="body LIKE ?";
                // The matches for the ?
               //       String[] filterArgs = {"%" + etWord.getText().toString() + "%"};
                // Fetch SMS Message from Built-in Content Provider

                Cursor cursor = cr.query(uri, reqCols, conditions, args, null);

                String smsBody = "";

                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tv2.setText(smsBody);
                emailSMS2 = smsBody;
            }
        });

        return view;
    }
}
