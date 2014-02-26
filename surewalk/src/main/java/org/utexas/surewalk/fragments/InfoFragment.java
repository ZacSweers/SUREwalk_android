package org.utexas.surewalk.fragments;

import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.andreabaccega.formedittextvalidator.EmailValidator;
import com.andreabaccega.formedittextvalidator.PatternValidator;
import com.andreabaccega.widget.FormEditText;

import org.utexas.surewalk.R;
import org.utexas.surewalk.classes.OnFragmentReadyListener;
import org.utexas.surewalk.controllers.PreferenceHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoFragment extends SherlockFragment {

    private FormEditText et_name;
    private FormEditText et_eid;
    private FormEditText et_phone;
    private FormEditText et_email;
    private PreferenceHandler mPrefsHandler;

    
    public static InfoFragment newInstance(String title) {
    	InfoFragment infof = new InfoFragment();
    	Bundle args = new Bundle();
    	args.putString("title", title);
    	infof.setArguments(args);
    	return infof;
    }
    
    @Override
    public void onCreate(Bundle bundle) {
    	super.onCreate(bundle);
    	mPrefsHandler = new PreferenceHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {

        View view = inflater.inflate(R.layout.fragment_info, null);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        et_name = (FormEditText) view.findViewById(R.id.et_name);
        et_eid = (FormEditText) view.findViewById(R.id.et_eid);

        // Add EID regex matching
        et_eid.addValidator(new PatternValidator("Not a valid UT EID",
                Pattern.compile("^[A-Za-z]+[0-9]+$")));

        et_phone = (FormEditText) view.findViewById(R.id.et_phone);
        et_email = (FormEditText) view.findViewById(R.id.et_email);
        et_email.addValidator(new EmailValidator("Not a valid email"));
        initializeTextAttributes();

        if (fragReady()) {
            ((OnFragmentReadyListener) getActivity()).onFragmentReady(true);
        }
        
        Button clearBtn = (Button) view.findViewById(R.id.button_clear);
        clearBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                et_name.setText("");
                et_eid.setText("");
                et_phone.setText("");
                et_email.setText("");
            }
        });
        return view;
    }

    private void initializeTextAttributes() {

        et_name.setNextFocusDownId(R.id.et_eid);
        et_eid.setNextFocusDownId(R.id.et_phone);
        et_phone.setNextFocusDownId(R.id.et_email);

        et_name.setText(mPrefsHandler.getName());
        et_eid.setText(mPrefsHandler.getUTEID());
        et_phone.setText(mPrefsHandler.getPhoneNumber());
        et_email.setText(mPrefsHandler.getEmail());

        setUpTextListener(et_name);
        setUpTextListener(et_eid);
        et_phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        setUpTextListener(et_phone);
        setUpTextListener(et_email);

        et_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    getSherlockActivity().findViewById(R.id.next_button).performClick();
                    return true;
                }
                return false;
            }
        });
    }

    private boolean fragReady() {
        return !(et_name.getText().toString().length() == 0
                || et_eid.getText().toString().length() == 0
                || (et_phone.getText().toString().length() == 0 && isTenNumbers(et_phone.getText().toString()))
                || et_email.getText().toString().length() == 0);

    }

    // Not sure this is working correctly with the textlistener
    private boolean isTenNumbers(String s) {
        Pattern p = Pattern.compile("\\d"); // "\d" is for digits in regex
        Matcher m = p.matcher(s);
        int count = 0;
        while(m.find()){
            count++;
        }
        return (count == 10);
    }


    // To clear the error message when they start typing again
    private void setUpTextListener(final FormEditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear error on change
                et.setError(null);

                // Check if it's ready
                if (fragReady()) {
                    ((OnFragmentReadyListener) getActivity()).onFragmentReady(true);
                } else {
                    ((OnFragmentReadyListener) getActivity()).onFragmentReady(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if it's ready
                if (fragReady()) {
                    ((OnFragmentReadyListener) getActivity()).onFragmentReady(true);
                } else {
                    ((OnFragmentReadyListener) getActivity()).onFragmentReady(false);
                }
            }
        });

        if (et.getId() != R.id.et_email) {
            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        et.testValidity();
                    }
                }
            });
        }
    }

    public String[] getInfo(){
        return new String[]{et_name.getText().toString(), et_eid.getText().toString(), et_phone.getText().toString(), et_email.getText().toString()};
    }
}