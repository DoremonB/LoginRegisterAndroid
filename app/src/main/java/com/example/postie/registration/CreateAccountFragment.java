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
import android.widget.TextView;
import android.widget.Toast;

import com.example.postie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountFragment extends Fragment {

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    private EditText email,phone,password,confirm_password;
    private Button create_account_btn;
    private ProgressBar progressBar;
    private TextView login_text;
    private FirebaseAuth firebaseAuth;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        firebaseAuth=FirebaseAuth.getInstance();

        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RegisterActivity)getActivity()).setFragment(new LoginFragment());
            }
        });

        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setError(null);
                phone.setError(null);
                password.setError(null);
                confirm_password.setError(null);
                if(email.getText().toString().isEmpty()){
                   email.setError("Required");
                   return;
                }
                if(password.getText().toString().isEmpty()){
                    email.setError("Required");
                    return;
                }
                if(confirm_password.getText().toString().isEmpty()){
                    email.setError("Required");
                    return;
                }
                if(phone.getText().toString().isEmpty()){
                    email.setError("Required");
                    return;
                }
                if(!VALID_EMAIL_ADDRESS_REGEX .matcher(email.getText().toString()).find()){
                    email.setError("Please enter a valid email");
                    return;
                }
                if(phone.getText().toString().length()!=10){
                    phone.setError("Please Enter a Valid Phone Number");
                    return;
                }
                if(!password.getText().toString().equals(confirm_password.getText().toString())){
                    confirm_password.setError("Password mismatch!");
                    return;
                }

                createAccount();
            }
        });

    }

    private void init(View view) {
        email=view.findViewById(R.id.email);
        phone=view.findViewById(R.id.phone);
        password=view.findViewById(R.id.password);
        confirm_password=view.findViewById(R.id.confirm_password);
        create_account_btn=view.findViewById(R.id.create_account_btn);
        login_text=view.findViewById(R.id.login_text);
        progressBar=view.findViewById(R.id.progressBar);

    }

    private void createAccount() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getSignInMethods().isEmpty()){
                        //task.getResult().getSignInMethods() this returns a list..and if it is empty ...email is not registered yet
                        ((RegisterActivity)getActivity()).setFragment(new OTPFragment(email.getText().toString(),phone.getText().toString(),password.getText().toString()));
                    }
                    else{
                        //Email already exists
                        email.setError("Email already exists");
                    }
                }
                else{
                    String error=task.getException().getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
