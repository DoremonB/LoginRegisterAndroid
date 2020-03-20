package com.example.postie.registration;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.postie.MainActivity;
import com.example.postie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static com.example.postie.registration.CreateAccountFragment.VALID_EMAIL_ADDRESS_REGEX;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }
    private EditText email_or_phone,password;
    private Button login_btn;
    private ProgressBar progressBar;
    private TextView createAccountTV,forgotPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_or_phone.setError(null);
                password.setError(null);
                if(email_or_phone.getText().toString().isEmpty()){
                    email_or_phone.setError("Required");
                    return;
                }
                if(password.getText().toString().isEmpty()){
                    password.setError("Required");
                    return;
                }
                if(VALID_EMAIL_ADDRESS_REGEX .matcher(email_or_phone.getText().toString()).find()){
                    progressBar.setVisibility(View.VISIBLE);
                    login(email_or_phone.getText().toString());
                }
                else if(email_or_phone.getText().toString().matches("\\d{10}")){
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseFirestore.getInstance().collection("users")
                            .whereEqualTo("phone",email_or_phone.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        List<DocumentSnapshot> documents=task.getResult().getDocuments();
                                        if(documents.isEmpty()){
                                            email_or_phone.setError("Phone number not found");
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                        else{
                                            String email=documents.get(0).get("email").toString();
                                            login(email);
                                        }
                                    }
                                    else{
                                        String error=task.getException().getMessage();
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                    return;
                }else {
                    email_or_phone.setError("Please enter a valid email or Phone Number");
                    return;
                }

            }
        });

        createAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterActivity)getActivity()).setFragment(new CreateAccountFragment());
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterActivity)getActivity()).setFragment(new ForgetPasswordFragment());
            }
        });
    }

    private void init(View view) {
        email_or_phone=view.findViewById(R.id.email_or_phone);
        password=view.findViewById(R.id.password);
        login_btn=view.findViewById(R.id.login_btn);
        createAccountTV=view.findViewById(R.id.create_account__text);
        forgotPassword=view.findViewById(R.id.forgot_password);
        progressBar=view.findViewById(R.id.progressBar);
    }
    private void login(String email){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent mainIntent=new Intent(getContext(), MainActivity.class);
                    startActivity(mainIntent);
                    getActivity().finish();
                }
                else{
                    String error=task.getException().getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
