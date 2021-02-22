package com.dds.helpee.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dds.helpee.LocaleManager1;
import com.dds.helpee.R;
import com.dds.helpee.activities.HomeActivity;
import com.dds.helpee.activities.MainActivity;
import com.dds.helpee.adapters.CountriesAdapter;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.model.Const;
import com.dds.helpee.model.Data;
import com.dds.helpee.model.Number;
import com.dds.helpee.model.Response;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.dds.helpee.activities.MainActivity.listCountries;
import static com.dds.helpee.api.ApiClient.BASE_URL;

public class RegistrationFragment extends Fragment implements CountriesAdapter.CallBack
{
    CountriesAdapter countryAdapter = null;
    TextInputEditText et_first_name, et_last_name, et_password, et_phone, et_user_name, et_location;
    TextView tv_register, tv_submit,tv_send_to, tv_resend;
    LinearLayout layout_email, layout_number;
    boolean isphone = false;
    ImageView img_email, img_phone, img_fb, img_google;
    RelativeLayout relative_verification, relative_regis;
    Call<Response> objInterFace = null;
    String type = "1", country = null, languageCode = null;
    ProgressDialog pd;
    EditText et_1, et_2, et_3, et_4;
    private EditText[] editTexts;
    View view;
    int code;
    SharedPreferences pref;
    SharedPreferences.Editor et;
    CountriesAdapter.CallBack obj ;
    Dialog d_country;
    TextInputLayout email_layout, password_layout, phone_layout, location_layout, last_name_layout, first_name_layout;
//    List<Number> listCountries = new ArrayList<>();
    LinearLayout linear_email, linear_phone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_registration, container, false);

//        getAllCountries();


        obj = this;
        linear_email = (LinearLayout) view.findViewById(R.id.linear_email);
        linear_phone = (LinearLayout) view.findViewById(R.id.linear_phone);


        location_layout = (TextInputLayout) view.findViewById(R.id.location_layout);
        last_name_layout = (TextInputLayout) view.findViewById(R.id.last_name_layout);
        first_name_layout = (TextInputLayout) view.findViewById(R.id.first_name_layout);
        password_layout = (TextInputLayout) view.findViewById(R.id.password_layout);
        phone_layout = (TextInputLayout) view.findViewById(R.id.phone_layout);
        email_layout = (TextInputLayout) view.findViewById(R.id.email_layout);

        et_phone = (TextInputEditText) view.findViewById(R.id.et_phone);
        et_user_name = (TextInputEditText) view.findViewById(R.id.et_user_name);
        et_password = (TextInputEditText) view.findViewById(R.id.et_password);
        et_first_name = (TextInputEditText) view.findViewById(R.id.et_first_name);
        et_last_name = (TextInputEditText) view.findViewById(R.id.et_last_name);
        et_location = (TextInputEditText) view.findViewById(R.id.et_location);

        et_1 = (EditText) view.findViewById(R.id.et_1);
        et_2 = (EditText) view.findViewById(R.id.et_2);
        et_3 = (EditText) view.findViewById(R.id.et_3);
        et_4 = (EditText) view.findViewById(R.id.et_4);

        img_fb = (ImageView) view.findViewById(R.id.img_fb);
        img_google = (ImageView) view.findViewById(R.id.img_google);

        relative_verification = (RelativeLayout) view.findViewById(R.id.relative_verification);
        relative_regis = (RelativeLayout) view.findViewById(R.id.relative_regis);

        img_phone = (ImageView) view.findViewById(R.id.img_phone);
        img_email = (ImageView) view.findViewById(R.id.img_email);

        tv_register = (TextView) view.findViewById(R.id.tv_register);
        tv_submit = (TextView) view.findViewById(R.id.tv_submit);
        tv_send_to = (TextView) view.findViewById(R.id.tv_send_to);
        tv_resend = (TextView) view.findViewById(R.id.tv_resend);

        layout_number = (LinearLayout) view.findViewById(R.id.layout_number);
        layout_email = (LinearLayout) view.findViewById(R.id.layout_email);

        pref = getActivity().getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
        et = pref.edit();

        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.please_wait));

        editTexts = new EditText[]{et_1, et_2, et_3, et_4};
        et_1.addTextChangedListener(new PinTextWatcher(0));
        et_2.addTextChangedListener(new PinTextWatcher(1));
        et_3.addTextChangedListener(new PinTextWatcher(2));
        et_4.addTextChangedListener(new PinTextWatcher(3));

        et_1.setOnKeyListener(new PinOnKeyListener(0));
        et_2.setOnKeyListener(new PinOnKeyListener(1));
        et_3.setOnKeyListener(new PinOnKeyListener(2));
        et_4.setOnKeyListener(new PinOnKeyListener(3));

        et_user_name.addTextChangedListener(new EditTextWatcher(getActivity(),email_layout,et_user_name));
        et_phone.addTextChangedListener(new EditTextWatcher(getActivity(),phone_layout,et_phone));
        et_password.addTextChangedListener(new EditTextWatcher(getActivity(),password_layout,et_password));
        et_first_name.addTextChangedListener(new EditTextWatcher(getActivity(),first_name_layout,et_first_name));
        et_last_name.addTextChangedListener(new EditTextWatcher(getActivity(),last_name_layout,et_last_name));
        et_location.addTextChangedListener(new EditTextWatcher(getActivity(),location_layout,et_location));

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

                linear_email.setVisibility(View.VISIBLE);
                linear_phone.setVisibility(View.GONE);
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Register();
            }
        });

        tv_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VerifyCode();
//                relative_verification.setVisibility(View.VISIBLE);
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

        tv_resend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ResendCode();
            }
        });

        img_fb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                type = "3";
            }
        });

        img_google.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                type = "4";
            }
        });

        et_location.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                 d_country = new Dialog(getActivity());
                d_country.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d_country.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
                d_country.setContentView(R.layout.dialog_country);

                RecyclerView rcv_country = (RecyclerView) d_country.findViewById(R.id.rcv_country);

                TextView tv_cancel = (TextView) d_country.findViewById(R.id.tv_cancel);
                TextView tv_done = (TextView) d_country.findViewById(R.id.tv_done);
                EditText et_search = (EditText) d_country.findViewById(R.id.et_search);
                ImageView img_cancel = (ImageView) d_country.findViewById(R.id.img_cancel);

                countryAdapter = new CountriesAdapter(getActivity(), listCountries, obj);
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                rcv_country.setLayoutManager(manager);
                rcv_country.setAdapter(countryAdapter);

                img_cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        et_search.setText("");

                        countryAdapter.notifyDataSetChanged();
                    }
                });

                et_search.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {
                        if(s.length() > 0)
                        {
                            filter(s.toString());
                            img_cancel.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            img_cancel.setVisibility(View.GONE);

                            countryAdapter = new CountriesAdapter(getActivity(), listCountries, obj);
                            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                            rcv_country.setLayoutManager(manager);
                            rcv_country.setAdapter(countryAdapter);
                            countryAdapter.notifyDataSetChanged();
                        }
                    }
                });
                tv_done.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        et_location.setText(country);
                        d_country.dismiss();
                    }
                });
                tv_cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        d_country.dismiss();
                    }
                });
                d_country.show();
            }
        });

        return view;
    }
    public  void filter(String text)
    {
        List<Number> filteredList = new ArrayList<>();
        for (Number item : listCountries)
        {
            if (item.getEnglishName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        countryAdapter.filterList(filteredList);
    }
    public void ResendCode()
    {
        if (ApiClient.isNetworkAvailable(getActivity()))
        {
            if (pd != null && (!pd.isShowing()))
            {
                pd.show();
            }

            Call<Response> call = null;
            if(type.equals("1"))
            {
                call = ApiClient.create_Istance().ResendCode(type, et_user_name.getText().toString(), null);
            }
            else if(type.equals("2"))
            {
                call = ApiClient.create_Istance().ResendCode(type, null, et_phone.getText().toString());
            }
            call.enqueue(new Callback<Response>()
            {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
                {
                    if (pd != null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                    if(response != null)
                    {
                        Log.e("toast",""+new Gson().toJson(response.body()));
                        if(response.body() != null && response.isSuccessful())
                        {
                            if(response.body().getSuccess() == 1)
                            {
                                String message = (String) response.body().getMessage();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t)
                {
                    if (pd != null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    Log.e("error", "" + t.getMessage() + "  " + t.toString());
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
    public void Register()
    {
        String firstName = et_first_name.getText().toString().trim();
        String lastName = et_last_name.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String email = et_user_name.getText().toString().trim();
        String location = et_location.getText().toString().trim();
        String mobile = et_phone.getText().toString().trim();

        if (TextUtils.isEmpty(firstName))
        {
            first_name_layout.setError(getString(R.string.plz_enter_first_name));
            requestFocus(et_first_name);

//            Toast.makeText(getActivity(), getActivity().getString(R.string.plz_enter_first_name), Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            first_name_layout.setErrorEnabled(false);
        }
        if (TextUtils.isEmpty(lastName))
        {
            last_name_layout.setError(getString(R.string.plz_enter_last_name));
            requestFocus(et_last_name);
//            Toast.makeText(getActivity(), getActivity().getString(R.string.plz_enter_last_name), Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            last_name_layout.setErrorEnabled(false);
        }
        if (TextUtils.isEmpty(password))
        {
            password_layout.setError(getString(R.string.plz_enter_pass));
            requestFocus(et_password);
//            Toast.makeText(getActivity(), getActivity().getString(R.string.plz_enter_pass), Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            password_layout.setErrorEnabled(false);
        }

        if (type.equals("1"))
        {
            if (TextUtils.isEmpty(email))
            {
                email_layout.setError(getString(R.string.plz_enter_email));
                requestFocus(et_user_name);

                Toast.makeText(getActivity(), getActivity().getString(R.string.plz_enter_email), Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                email_layout.setErrorEnabled(false);
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(et_user_name.getText().toString()).matches())
            {
                email_layout.setError(getString(R.string.email_invalid_validation));
                requestFocus(et_user_name);
                return;
            }
            else
            {
                email_layout.setErrorEnabled(false);
            }
        }
        if (type.equals("2"))
        {
            if (TextUtils.isEmpty(mobile))
            {
                phone_layout.setError(getString(R.string.enter_phone_number));
                requestFocus(et_phone);
                return;
            }
            else
            {
                phone_layout.setErrorEnabled(false);
            }
        }
        if (TextUtils.isEmpty(location))
        {
            location_layout.setError(getString(R.string.plz_enter_location));
            requestFocus(et_location);
//            Toast.makeText(getActivity(), getActivity().getString(R.string.plz_enter_location), Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            location_layout.setErrorEnabled(false);
        }
        if (ApiClient.isNetworkAvailable(getActivity()))
        {
            if (pd != null && (!pd.isShowing()))
            {
                pd.show();
            }
            if (type.equals("1"))
            {
                tv_send_to.setText("");
                tv_send_to.setText(getString(R.string.sent_to)+" "+email);
                objInterFace = ApiClient.create_Istance().Do_SignIn(type, firstName, lastName, password, email, null, location);
            }
            else if (type.equals("2"))
            {
                tv_send_to.setText("");
                tv_send_to.setText(getString(R.string.sent_to)+" "+mobile);
                objInterFace = ApiClient.create_Istance().Do_SignIn(type, firstName, lastName, password, null, mobile, location);
            }
//            if(type.equals("1"))
//            {
//                objInterFace = ApiClient.create_Istance().Do_SignIn(type, firstName, lastName, password,email,null, location);
//            }

//            ApiInterface retrofit = ApiClient.create_Istance();
//            objInterFace = retrofit.Do_SignIn(type, firstName, lastName, password, email, null, location);

            objInterFace.enqueue(new Callback<Response>()
            {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
                {
                    if (pd != null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                    if (response != null && response.isSuccessful())
                    {
                        Log.e("response", "" + new Gson().toJson(response.body()));
                        if (response.body() != null)
                        {
                            if (response.body().getSuccess() == 1)
                            {
                                double message = (double) response.body().getMessage();
                                if (message != 0)
                                {
                                    code = (int) message;
                                    Log.e("languageCode",""+languageCode);
                                    if(languageCode != null)
                                    {
                                        String language_code = languageCode.toLowerCase();
                                        et.putString(Const.LANGUAGE , language_code);
                                        et.commit();
                                        et.apply();

                                        updateResources(getActivity(), language_code);
                                        LocaleManager1.setNewLocale(getActivity(), language_code);

//                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame,new RegistrationFragment()).commit();
                                        MainActivity.pager.getAdapter().notifyDataSetChanged();
                                        relative_verification.setVisibility(View.VISIBLE);
                                        relative_regis.setVisibility(View.VISIBLE);
                                    }
                                    relative_verification.setVisibility(View.VISIBLE);
                                    relative_regis.setVisibility(View.GONE);
                                }
                            }
                            else
                            {
                                String message = (String) response.body().getMessage();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                @Override
                public void onFailure(Call<Response> call, Throwable t)
                {
                    if (pd != null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    Log.e("error", "" + t.getMessage() + "  " + t.toString());
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
    public  void VerifyCode()
    {
        String email = et_user_name.getText().toString();
        String mobile = et_phone.getText().toString();

        String et1 = et_1.getText().toString().trim();
        String et2 = et_2.getText().toString().trim();
        String et3 = et_3.getText().toString().trim();
        String et4 = et_4.getText().toString().trim();

        String code  = et1+et2+et3+et4;

        if(ApiClient.isNetworkAvailable(getActivity()))
        {
            if(pd != null && (!pd.isShowing()))
            {
                pd.show();
            }

//            ApiInterface itemService = getRetrofitInstance(Data.class, new GetItemDetailsDeserializer()).create(ApiInterface.class);
//            Call<Response> call = itemService.VerifyCode(type, email, mobile, code);
//
//            call.enqueue(new Callback<Response>()
//            {
//                @Override
//                public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
//                {
//                    if(pd != null && pd.isShowing())
//                    {
//                        pd.dismiss();
//                    }
//                    if(response != null)
//                    {
//                        Log.e("response",""+new Gson().toJson(response.body()));
//                        if(response.isSuccessful())
//                        {
//                            if(response.body().getSuccess() == 1)
//                            {
////                                Gson gson = new GsonBuilder()
////                                        .registerTypeAdapter(Message.class, new GetItemDetailsDeserializer())
////                                        .create();
//
//                                Gson gson = new GsonBuilder()
//                                        .setLenient()
//                                        .create();
////                                Message person = gson.fromJson(response.body().getMessage().toString(), Message.class);
//                                Data person = new Gson().fromJson(response.body().getMessage().toString(), Data.class);
////                                UserDate customObject = customGson.fromJson(userJson, UserDate.class);
////                                Object m = response.body().getMessage();
//
//                                Log.e("m",""+person.getEmail());
////                                String name = (Message) m.
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Response> call, Throwable t)
//                {
//                    if(pd != null && pd.isShowing())
//                    {
//                        pd.dismiss();
//                    }
//                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
//                }
//            });

            Call<Response> call = ApiClient.create_Istance().VerifyCode(type, email, mobile, code);

            call.enqueue(new Callback<Response>()
            {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
                {
                    if(pd != null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                    if(response != null && response.isSuccessful())
                    {
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
                                        String location = data.getLocation();
                                        int userId = data.getId();
                                        String mobile = data.getMobile();

                                        et.putString(Const.TOKEN, token);
                                        et.putString(Const.FIRST_NAME, f_name);
                                        et.putString(Const.LAST_NAME, l_name);
                                        et.putInt(Const.USER_ID, userId);
                                        et.putString(Const.PHONE, mobile);
                                        et.putString(Const.LOCATION, location);
                                        et.putString(Const.EMAIL, email);
                                        et.putBoolean(Const.LOGIN, true);
                                        et.commit();

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
                }
                @Override
                public void onFailure(Call<Response> call, Throwable t)
                {
                    if(pd != null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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
    private static Converter.Factory createGsonConverter(Type type, Object typeAdapter) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, typeAdapter);
        Gson gson = gsonBuilder.create();

        return GsonConverterFactory.create(gson);
    }

    public static Retrofit getRetrofitInstance(Type type, Object typeAdapter) {
        return new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(createGsonConverter(type, typeAdapter))
                .build();
    }

    @Override
    public void getCountryName(String countryName, Number objCountry)
    {
        if(objCountry != null)
        {
//            Number objget = listCountries.get(pos);
//            languageCode = objget.getLanguage();

            languageCode = objCountry.getLanguage();
        }
        country = countryName;
        et_location.setText(country);
        Log.e("country",""+country);

        if(d_country != null && d_country.isShowing())
        {
            d_country.dismiss();
        }
    }
    private static void updateResources(Context context, String language)
    {
        Locale locale = new Locale(language);

        Configuration config = context.getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >= 17)
        {
            config.setLocale(locale);
        }
        else
        {
            config.locale = locale;
        }
        locale.setDefault(locale);
        context.getResources().updateConfiguration(config,  context.getResources().getDisplayMetrics());

    }
    public class GetItemDetailsDeserializer implements JsonDeserializer<Data>
    {
        @Override
        public Data deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            Data obj = new Data();

            if(json.isJsonPrimitive() == false)
            {

                Log.e("primitive",""+json.getAsJsonObject());
            }
            else
            {
                Log.e("   not primitive",""+json.getAsJsonObject());

                final JsonObject jsonObject1 = json.getAsJsonObject();

                final int  success = jsonObject1.get("success").getAsInt();
                final JsonObject jsonObject = jsonObject1.get("message").getAsJsonObject();

//            final JsonObject jsonObject = json.getAsJsonObject();

                final int userid = jsonObject.get("userid").getAsInt();
                final String firstname = jsonObject.get("firstname").getAsString();
                final String lastname = jsonObject.get("lastname").getAsString();
                final String email = jsonObject.get("email").getAsString();
                final String location = jsonObject.get("location").getAsString();
//            final String token = jsonObject.get("token").getAsString();
                final String mobile = jsonObject.get("mobile").getAsString();

                obj = new Data();
                obj.setId(userid);
                obj.setFirstName(firstname);
                obj.setLastName(lastname);
                obj.setEmail(email);
                obj.setLocation(location);
                obj.setToken(null);
                obj.setMobile(mobile);

            }
            return obj;
        }
    }
    public class PinTextWatcher implements TextWatcher
    {
        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex)
        {
            this.currentIndex = currentIndex;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editTexts.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            newTypedString = s.subSequence(start, start + count).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            String text = newTypedString;

            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0)); // TODO: We can fill out other EditTexts

            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
            else if (text.length() == 0)
                moveToPrevious();
        }

        private void moveToNext()
        {
            if (!isLast)
                editTexts[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && isLast)
            {
                // isLast is optional
//                Log.e("otp",""+newTypedString);
                editTexts[currentIndex].clearFocus();
                hideKeyboard();
            }
        }

        private void moveToPrevious()
        {
            if (!isFirst)
                editTexts[currentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled()
        {
            for (EditText editText : editTexts)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

        private void hideKeyboard()
        {
            if (getActivity().getCurrentFocus() != null)
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
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
                case R.id.et_first_name :

                    if(isPhoneValid(et_first_name.getText().toString()) == false)
                    {
                        first_name_layout.setError(getString(R.string.plz_enter_first_name));

                        et_first_name.requestFocus();
                    }
                    else
                    {
                        first_name_layout.setErrorEnabled(false);
                    }
                    break;
                case R.id.et_last_name :

                    if(isPhoneValid(et_last_name.getText().toString()) == false)
                    {
                        last_name_layout.setError(getString(R.string.plz_enter_last_name));

                        et_last_name.requestFocus();
                    }
                    else
                    {
                        last_name_layout.setErrorEnabled(false);
                    }
                    break;
                case R.id.et_location :

                    if(isPhoneValid(et_location.getText().toString()) == false)
                    {
                        location_layout.setError(getString(R.string.plz_enter_location));

                        et_location.requestFocus();
                    }
                    else
                    {
                        location_layout.setErrorEnabled(false);
                    }
                    break;
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
    public class PinOnKeyListener implements View.OnKeyListener
    {
        private int currentIndex;
        PinOnKeyListener(int currentIndex)
        {
            this.currentIndex = currentIndex;
        }
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP)
            {
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editTexts[currentIndex - 1].requestFocus();
            }
            return false;
        }
    }
//    public void getAllCountries()
//    {
//        Call<CountryListResponse> call = ApiClient.create_Istance().GetAllCountries();
//        call.enqueue(new Callback<CountryListResponse>()
//        {
//            @Override
//            public void onResponse(Call<CountryListResponse> call, retrofit2.Response<CountryListResponse> response)
//            {
//                if(response != null && response.isSuccessful())
//                {
//                    if(response.body() != null)
//                    {
//                        listCountries = response.body().getNumberList();
//
//                        if(listCountries != null && listCountries.size() > 0 )
//                        {
//                            Toast.makeText(getActivity(), "countrylist"+listCountries.size() , Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//                else
//                {
//                    Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CountryListResponse> call, Throwable t)
//            {
//                Log.e("failure", ""+t.toString());
//                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}


