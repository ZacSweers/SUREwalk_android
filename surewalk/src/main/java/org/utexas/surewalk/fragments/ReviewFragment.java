package org.utexas.surewalk.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import org.utexas.surewalk.R;
import org.utexas.surewalk.data.WalkRequest;


public class ReviewFragment extends SherlockFragment {

    private String comments = "";
    private boolean done = false;
    private EditText et_comments_dialog = null;
    public TextView tv_name;
    public TextView tv_email;
    public TextView tv_start;
    public TextView tv_end;
    public TextView tv_phone;
    public TextView tv_eid;
    private Button bt_comments;

    public static ReviewFragment newInstance(String title) {
    	ReviewFragment rf = new ReviewFragment();
    	Bundle args = new Bundle();
    	args.putString("title", title);
    	rf.setArguments(args);
    	return rf;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, null);
        captureViews(view);
        return view;
    }

    public void captureViews(View root) {
        tv_eid = (TextView) root.findViewById(R.id.tv_review_eid);
        tv_name = (TextView) root.findViewById(R.id.tv_review_name);
        tv_phone = (TextView) root.findViewById(R.id.tv_review_phone);
        tv_start = (TextView) root.findViewById(R.id.tv_review_start);
        tv_end = (TextView) root.findViewById(R.id.tv_review_destination);
        tv_email = (TextView) root.findViewById(R.id.tv_review_email);
        bt_comments = (Button) root.findViewById(R.id.bt_review_comments);
        bt_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCommentsDialog();
            }
        });
    }

    public void populateFields(WalkRequest request) {
        tv_eid.setText(request.getEID());
        tv_name.setText(request.getName());
        tv_phone.setText(request.getPhoneNumber());
        tv_start.setText(request.getStartLocation());
        tv_end.setText(request.getEndLocation());
        tv_email.setText(request.getEmail());
    }

    public void displayCommentsDialog() {
        final EditText et_comments = new EditText(getActivity());

        if (!comments.equals("")) {
            et_comments.setText(comments);
            et_comments.setSelectAllOnFocus(true);
        }

        // Show keyboard
        et_comments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                et_comments.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(et_comments, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });

        et_comments.setLines(3);
        et_comments.setHint("Specific directions, name of the building, which entrance, etc...");
        new AlertDialog.Builder(getActivity())
                .setTitle("Comments")
                .setMessage("Please give any other information that might be necessary!")
                .setView(et_comments)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        comments = et_comments.getText().toString();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing happens
                    }
                }).show();
    }

    public String getComments() {
        if (comments != null) {
            return comments;
        } else {
            return "";
        }
    }
}