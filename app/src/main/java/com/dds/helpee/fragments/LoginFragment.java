package com.dds.helpee.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dds.helpee.R;
import com.dds.helpee.activities.HomeActivity;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.Data;
import com.dds.helpee.model.Response;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginFragment extends Fragment
{
    TextInputEditText et_phone, et_user_name,et_password;
    TextView tv_login;
    LinearLayout layout_email, layout_number, linear_email, linear_phone;
    boolean isphone = false;
    ImageView img_email,img_phone;
    String type = "1";
    Call<Response> objInterface;
    ProgressDialog pd;
    SharedPreferences pref;
    SharedPreferences.Editor et;
    public static GoogleSignInClient mGoogleSignInClient;
    public final int RC_SIGN_IN = 505;
    public final int FB_SIGN_IN = 545;
    TextInputLayout email_layout, password_layout, phone_layout;
    View view;
    ImageView img_fb, img_google;
    private FirebaseAuth mAuth;
   String  email = null, first_name = null, image = null, id = null;
    private static final String EMAIL = "email";
    CallbackManager callbackManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        FacebookSdk.sdkInitialize(getActivity());

//        String hash = printHashKey(getActivity());

        et_phone = (TextInputEditText) view.findViewById(R.id.et_phone);
        et_user_name = (TextInputEditText) view.findViewById(R.id.et_user_name);
        et_password = (TextInputEditText) view.findViewById(R.id.et_password);

//        Log.e("hashkey",""+hash);


        img_fb = (ImageView) view.findViewById(R.id.img_fb);
        img_google = (ImageView) view.findViewById(R.id.img_google);

        linear_email = (LinearLayout) view.findViewById(R.id.linear_email);
        linear_phone = (LinearLayout) view.findViewById(R.id.linear_phone);


        email_layout = (TextInputLayout) view.findViewById(R.id.email_layout);
        password_layout = (TextInputLayout) view.findViewById(R.id.password_layout);
        phone_layout = (TextInputLayout) view.findViewById(R.id.phone_layout);

        pref = getActivity().getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        img_phone = (ImageView) view.findViewById(R.id.img_phone);
        img_email = (ImageView) view.findViewById(R.id.img_email);

        tv_login = (TextView) view.findViewById(R.id.tv_login);

        layout_number = (LinearLayout) view.findViewById(R.id.layout_number);
        layout_email = (LinearLayout) view.findViewById(R.id.layout_email);

        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.please_wait));

        mAuth = FirebaseAuth.getInstance();

        et_user_name.addTextChangedListener(new EditTextWatcher(getActivity(),email_layout,et_user_name));
        et_phone.addTextChangedListener(new EditTextWatcher(getActivity(),phone_layout,et_phone));
        et_password.addTextChangedListener(new EditTextWatcher(getActivity(),password_layout,et_password));

        email_layout.setHint(getString(R.string.enter_email));
        phone_layout.setHint(getString(R.string.enter_phone_num));
        password_layout.setHint(getString(R.string.password));

        if(isphone == true)
        {
//            et_user_name.setVisibility(View.GONE);
//            et_phone.setVisibility(View.VISIBLE);
            img_email.setImageResource(R.drawable.radio_off);
            img_phone.setImageResource(R.drawable.radio_on);

            linear_email.setVisibility(View.GONE);
            linear_phone.setVisibility(View.VISIBLE);
        }
        else
        {
//            et_user_name.setVisibility(View.VISIBLE);
//            et_phone.setVisibility(View.GONE);
            img_email.setImageResource(R.drawable.radio_on);
            img_phone.setImageResource(R.drawable.radio_off);

            linear_email.setVisibility(View.VISIBLE);
            linear_phone.setVisibility(View.GONE);
        }
        img_google.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LoginUsingGoogle();
            }
        });

//        final boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
//
//        if (!loggedOut)
//        {
//
//            getUserProfile(AccessToken.getCurrentAccessToken());
//        }
//
//        AccessTokenTracker fbTracker = new AccessTokenTracker()
//        {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2)
//            {
//                if (accessToken2 == null)
//                {
//                    Toast.makeText(getActivity(),"User Logged Out.",Toast.LENGTH_LONG).show();
//                }
//            }
//        };

//        fbTracker.startTracking();

        img_fb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                if(AccessToken.getCurrentAccessToken() != null)
//                {
//                    Log.e("logout","");
//
//                    graphRevokeUserStatusPermission();
//                    LoginManager.getInstance().logOut();
//
//                }
//                else
//                {
                    Log.e("logIn","");
                    type = "3";
                    callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));

//                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile", "user_friends"));
                    LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>()
                    {
                        @Override
                        public void onSuccess(LoginResult loginResult)
                        {
                            Log.e("success",""+loginResult.getAccessToken());
                            boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
                            if (!loggedOut)
                            {
                                if(Profile.getCurrentProfile() != null)
                                {
                                    if(Profile.getCurrentProfile().getProfilePictureUri(200, 200) != null)
                                    {
//                                    Glide.with(getActivity()).load(Profile.getCurrentProfile().getProfilePictureUri(200, 200)).into(imageView);
//                                    Picasso.with(getActivity()).load(Profile.getCurrentProfile().getProfilePictureUri(200, 200)).into(imageView);
                                    }
                                    if(Profile.getCurrentProfile().getName() != null)
                                    {
                                        Log.d("TAG", "Username is: " + Profile.getCurrentProfile().getName());
                                    }
                                }
                                getUserProfile(AccessToken.getCurrentAccessToken());
                            }
                        }
                        @Override
                        public void onCancel()
                        {
                            Log.e("onCancel","");
                        }

                        @Override
                        public void onError(FacebookException error)
                        {
                            Log.e("onError",""+error.toString());
                        }
                    });
                }

//            }
        });

        layout_email.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                type = "1";
                isphone = false;
//                et_user_name.setVisibility(View.VISIBLE);
//                et_phone.setVisibility(View.GONE);
                img_email.setImageResource(R.drawable.radio_on);
                img_phone.setImageResource(R.drawable.radio_off);
//
                linear_email.setVisibility(View.VISIBLE);
                linear_phone.setVisibility(View.GONE);
            }
        });

        layout_number.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                type = "2";
                isphone = true;
//                et_user_name.setVisibility(View.GONE);
//                et_phone.setVisibility(View.VISIBLE);
                img_email.setImageResource(R.drawable.radio_off);
                img_phone.setImageResource(R.drawable.radio_on);

                linear_email.setVisibility(View.GONE);
                linear_phone.setVisibility(View.VISIBLE);
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = et_user_name.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String mobile = et_phone.getText().toString().trim();

                if(type.equals("1"))
                {
                    if (!validateEmail())
                    {
                        return;
                    }
//                    if (!Patterns.EMAIL_ADDRESS.matcher(et_user_name.getText().toString()).matches())
//                    {
//                        Toast.makeText(getActivity(), getActivity().getString(R.string.plz_enter_email), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                }
                if (type.equals("2"))
                {
                    if (!validatePhone())
                    {
                        return;
                    }
                }
                if(!validatePassword())
                {
                    return;
                }
                if(ApiClient.isNetworkAvailable(getActivity()))
                {
                    if(pd != null && (!pd.isShowing()))
                    {
                        pd.show();
                    }
                    if(type.equals("1"))
                    {
                        objInterface = ApiClient.create_Istance().Do_Login(type, email, null, password);
                    }
                    else if(type.equals("2"))
                    {
                        objInterface = ApiClient.create_Istance().Do_Login(type, null, mobile, password);
                    }

                    objInterface.enqueue(new Callback<Response>()
                    {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
                        {
                            if(pd != null && pd.isShowing())
                            {
                                pd.dismiss();
                            }
                            if(response != null)
                            {
                                if(response.isSuccessful())
                                {
                                    Log.e("response",""+new Gson().toJson(response.body()));
                                    if(response.body() != null)
                                    {
                                        if(response.body().getSuccess()== 1)
                                        {
                                            Data data = (Data) response.body().getData();
                                            if(data != null)
                                            {
                                                String token = data.getToken();
                                                String email = data.getEmail();
                                                String f_name = data.getFirstName();
                                                String l_name = data.getLastName();
                                                int userId = data.getId();
                                                String location = data.getLocation();
                                                String mobile = data.getMobile();

                                                et.putString(Const.TYPE, type);
                                                et.putString(Const.TOKEN, token);
                                                et.putString(Const.FIRST_NAME, f_name);
                                                et.putString(Const.LAST_NAME, l_name);
                                                et.putInt(Const.USER_ID, userId);
                                                et.putString(Const.PHONE, mobile);
                                                et.putString(Const.LOCATION, location);
                                                et.putString(Const.EMAIL, email);
                                                et.putBoolean(Const.LOGIN, true);
                                                et.commit();
                                                et.apply();

                                                Intent i_home = new Intent(getActivity(), HomeActivity.class);
                                                startActivity(i_home);
                                            }
                                            String message = (String) response.body().getMessage();
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            String message = (String) response.body().getMessage();
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                else
                                {
                                    if(response.errorBody() != null)
                                    {
                                        String msg = response.errorBody().source().toString();
                                        Log.e("msg",""+msg);
                                        String[] arr = msg.split("=");
                                        if(arr.length == 2)
                                        {
                                            msg = arr[1].replace("]"," ").trim();
                                            if(msg != null)
                                            {
                                                try
                                                {
                                                    JSONObject obh = new JSONObject(msg);
                                                    if(obh.getString("message") != null)
                                                    {
                                                        String message =  obh.getString("message").toString();
                                                        Log.e("message",""+message);
                                                        Toast.makeText(getActivity(), ""+message, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                catch (JSONException e)
                                                {
                                                    e.printStackTrace();
                                                }
                                            }
                                            Log.e("msg",""+msg);
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t)
                        {
                            if(pd != null && pd.isShowing())
                            {
                                pd.dismiss();
                            }
                            Toast.makeText(getActivity() , getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                            Log.e("error",""+t.getMessage() +"  "+t.toString());
                        }
                    });
                }
                else
                {
                    if(pd != null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    class EditTextWatcher implements TextWatcher{

        TextInputLayout layout;

        EditText view;

        Context context;

        public EditTextWatcher(Context con, TextInputLayout layout, EditText view){

            this.view = view;

            this.layout = layout;

            this.context = con;

        }

        @Override

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override

        public void afterTextChanged(Editable editable) {

            switch (view.getId()){

                case R.id.et_user_name :

                    if(isEmailValid(et_user_name.getText().toString()) == false)
                    {
                        email_layout.setError(getString(R.string.email_invalid_validation));

                        et_user_name.requestFocus();
                    }
                    else
                    {
                        email_layout.setErrorEnabled(false);
                    }
                    break;

                case R.id.et_password :
                    if(isPasswordValid(et_password.getText().toString()) == false)
                    {
                        password_layout.setError(getString(R.string.plz_enter_pass));
                        et_password.requestFocus();
                    }
                    else
                    {
                        password_layout.setErrorEnabled(false);
                    }
                    break;
                case R.id.et_phone :
                    if(isPhoneValid(et_phone.getText().toString()) == false)
                    {
                        phone_layout.setError(getString(R.string.enter_phone_number));
                        et_phone.requestFocus();
                    }
                    else
                    {
                        phone_layout.setErrorEnabled(false);
                    }
                    break;
            }

        }

    }
    public static boolean isEmailValid(String email){

        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public static boolean isPasswordValid(String password){

        return  !TextUtils.isEmpty(password) && password.trim().length()==6 ;

    }
    public static boolean isPhoneValid(String phone){

        return  !TextUtils.isEmpty(phone)  ;

    }
    private boolean validatePhone()
    {
        if (et_phone.getText().toString().trim().isEmpty()) {
            phone_layout.setError(getString(R.string.enter_phone_number));
            requestFocus(et_phone);
            return false;
        } else {
            phone_layout.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatePassword()
    {
        if (et_password.getText().toString().trim().isEmpty()) {
            password_layout.setError(getString(R.string.plz_enter_pass));
            requestFocus(et_password);
            return false;
        } else {
            password_layout.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateEmail()
    {
        String email = et_user_name.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            email_layout.setError(getString(R.string.email_invalid_validation));
            requestFocus(et_user_name);
            return false;
        }
        else
        {
            email_layout.setErrorEnabled(false);
        }

        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus())
        {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    public void graphRevokeUserStatusPermission(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken == null)
            return;

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/permissions/",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response)
                    {

                        LoginManager.getInstance().logOut();
                        // response
                    }
                }
        );
        request.setHttpMethod(HttpMethod.DELETE);
        request.executeAsync();
    }
    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void LoginUsingGoogle()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        signIn();
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account != null)
            {
                String name = account.getDisplayName();
                String email = account.getEmail();
                Uri imageUri = account.getPhotoUrl();
                PassGoogleData("4", name, email);
            }


            Log.e("TAG", "signInResult:Success code=" + account);
            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        }
        catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("TAG", "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }
    public void PassGoogleData(String type, String name , String email)
    {

        Call<Response> call = ApiClient.create_Istance().Do_Social_Login(type, email, name);
        call.enqueue(new Callback<Response>()
        {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
            {
                if(pd != null && pd.isShowing())
                {
                    pd.dismiss();
                }
                if(response != null)
                {
                    if(response.isSuccessful())
                    {
                        Log.e("response",""+new Gson().toJson(response.body()));
                        if(response.body() != null)
                        {
                            if(response.body().getSuccess()== 1)
                            {
                                Data data = (Data) response.body().getData();
                                if(data != null)
                                {
                                    String token = data.getToken();
                                    String email = data.getEmail();
                                    String f_name = data.getFirstName();
                                    int userId = data.getId();

                                    if(data.getImage() != null)
                                    {
                                        String image = data.getImage();
                                        et.putString(Const.IMAGE, image);
                                    }
                                    et.putString(Const.TYPE, type);
                                    et.putString(Const.TOKEN, token);
                                    et.putString(Const.FIRST_NAME, f_name);
                                    et.putInt(Const.USER_ID, userId);

                                    et.putString(Const.EMAIL, email);
                                    et.putBoolean(Const.LOGIN, true);
                                    et.commit();
                                    et.apply();

                                    Intent i_home = new Intent(getActivity(), HomeActivity.class);
                                    startActivity(i_home);
                                }
                                String message = (String) response.body().getMessage();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = (String) response.body().getMessage();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        if(response.errorBody() != null)
                        {
                            String msg = response.errorBody().source().toString();
                            Log.e("msg",""+msg);
                            String[] arr = msg.split("=");
                            if(arr.length == 2)
                            {
                                msg = arr[1].replace("]"," ").trim();
                                if(msg != null)
                                {
                                    try
                                    {
                                        JSONObject obh = new JSONObject(msg);
                                        if(obh.getString("message") != null)
                                        {
                                            String message =  obh.getString("message").toString();
                                            Log.e("message",""+message);
                                            Toast.makeText(getActivity(), ""+message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                Log.e("msg",""+msg);
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<Response> call, Throwable t)
            {
                if(pd != null && pd.isShowing())
                {
                    pd.dismiss();
                }
                Toast.makeText(getActivity() , getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                Log.e("error",""+t.getMessage() +"  "+t.toString());
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(callbackManager != null)
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else
        {
        }
    }
//    private void handleFacebookAccessToken(AccessToken token)
//    {
//        Log.d("TAG", "handleFacebookAccessToken:" + token);
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("TAG", "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
////                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("TAG", "signInWithCredential:failure", task.getException());
//                            Toast.makeText(getActivity() , "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }
    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }
    private void getUserProfile(AccessToken currentAccessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        Log.d("TAG", object.toString());
                        try
                        {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                            PassGoogleData("3" , first_name, email);

//                            Intent i_go = new Intent(getActivity(), HomeActivity.class);
//                            startActivity(i_go);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }
    public static String printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("TAG", "printHashKey() Hash Key: " + hashKey);
                return  hashKey;

            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("TAG", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("TAG", "printHashKey()", e);
        }
        return null;
    }
}
