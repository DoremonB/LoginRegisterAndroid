package com.example.postie.registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.postie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.postie.registration.CreateAccountFragment.VALID_EMAIL_ADDRESS_REGEX;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPasswordFragment extends Fragment {

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }
    private EditText email;
    private Button resetBtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_password, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        firebaseAuth=FirebaseAuth.getInstance();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setError(null);
                if(VALID_EMAIL_ADDRESS_REGEX .matcher(email.getText().toString()).find()){
                    progressBar.setVisibility(View.VISIBLE);
                    resetBtn.setEnabled(false);
                    firebaseAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.VISIBLE);
                                        Toast.makeText(getContext(), "Password reset email sent successfully", Toast.LENGTH_LONG).show();
                                        getActivity().onBackPressed();//send user back to login fragment
                                    }
                                    else{
                                        String error=task.getException().getMessage();
                                        email.setError(error);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                    resetBtn.setEnabled(true);
                                }
                            });
                }
                else{
                    email.setError("Please provide a valid email");
                }
            }
        });

    }
    private void init(View view) {
        email=view.findViewById(R.id.email);
        resetBtn=view.findViewById(R.id.reset_btn);
        progressBar=view.findViewById(R.id.progressBar);

    }
}
