package com.dds.helpee.api;


import com.dds.helpee.model.CountryListResponse;
import com.dds.helpee.model.Response;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface
{
    @POST("register")
    Call<Response> Do_SignIn(@Query("type") String type,
                             @Query("first_name") String firstname,
                             @Query("last_name") String lastname,
                             @Query("password") String password,
                             @Query("email") String email,
                             @Query("mobile") String mobile,
                             @Query("location") String location);

    @POST("login")
    Call<Response> Do_Login(@Query("type") String type,
                            @Query("email") String email,
                            @Query("mobile") String mobile,
                            @Query("password") String password);

    @POST("sociallogin")
    Call<Response> Do_Social_Login(@Query("type") String type,
                            @Query("email") String email,
                            @Query("name") String name,
                            @Query("image") String image);

    @POST("verifycode")
    Call<Response> VerifyCode(@Query("type") String type,
                            @Query("email") String email,
                            @Query("mobile") String mobile,
                            @Query("code") String code);

    @POST("sendcode")
    Call<Response> ResendCode(@Query("type") String type,
                            @Query("email") String email,
                            @Query("mobile") String mobile);

    @POST("getnumbers")
    Call<Response> GetEmergencyNumber(@Query("country") String country);

    @POST("getnumbers")
    Call<CountryListResponse> GetAllCountries();

    @POST("gettypes")
    Call<Response> GetAlertsTypes();

    @POST("logout")
    Call<Response> Logout();

    @POST("deleteacc")
    Call<Response> DeleteAccount(@Query("id") int userid);

    @POST("showdata")
    Call<Response> UserInfo(@Query("id") int userid);

    @POST("updatedata")
    Call<Response> UpdateUser(@Query("id") int userid,
                              @Query("first_name") String firstname,
                              @Query("last_name") String lastname,
                              @Query("location") String location);

    @POST("getincidents")
    Call<Response> GetAllIncidents(@Query("user_id") int userid);

    @POST("language")
    Call<Response> ChangeLanguage(@Query("id") int userid,
                                  @Query("language")String language );

    @POST("createalert")
    Call<Response> CreateAlert(@Query("user_id") int user_id,
                                  @Query("alerttypeid")int alerttypeid,
                                  @Query("location") String location,
                                  @Query("latitude") double latitude,
                                  @Query("longitude") double longitude,
                                  @Query("country")String country);

    @POST("savetoken")
    Call<Response> SaveFCMToken(@Query("id") int userid,
                                  @Query("devicetoken")String devicetoken );


    @POST("callback")
    Call<Response> CallBack(@Query("id") int userid,
                            @Query("latitude") double latitude,
                            @Query("longitude") double longitude);


//    @POST("gettypes")
//    Call<>

//
//    @POST("contactus")
//    Call<Response> ContactUs(@Body Users user);
//
//    @POST("gethomepagedata")
//    Call<Response> Get_HomePageData(@Body LatLong latlong);
//
//    @GET("getallcategories")
//    Call<Response> GetAll_categories();
//
//    @GET("getpaymentstatus")
//    Call<Response> Get_PaymentStatus();
//
//    @POST("saveuser")
//    Call<Response> SaveUser(@Body Users users);
//
//    @GET("logout")
//    Call<Response> LogOut();
//
//    @GET("removeuser")
//    Call<Response> RemoveUser();
//
//    @GET("getversionhistiry")
//    Call<Response> GetVersionHistory();
//
//    @GET("user/company/getall")
//    Call<Response> GetAllCompany();
//
//    @Multipart
//    @POST("user/company/create")
//    Call<Response> Create_Company(@Part MultipartBody.Part companyLogo,
//                                  @Part List<MultipartBody.Part> file,
//                                  @Part("category_id") RequestBody category_id,
//                                  @Part("company_name") RequestBody company_name,
//                                  @Part("building_number") RequestBody building_number,
//                                  @Part("address_line_1") RequestBody address_line_1,
//                                  @Part("city") RequestBody city,
//                                  @Part("postcode") RequestBody r_postcode,
//                                  @Part("country") RequestBody r_Country,
//                                  @Part("monday_opening") RequestBody monday_opening,
//                                  @Part("monday_closing") RequestBody monday_closing,
//                                  @Part("tuesday_opening") RequestBody tuesday_opening,
//                                  @Part("tuesday_closing") RequestBody tuesday_closing,
//                                  @Part("wednesday_opening") RequestBody wednesday_opening,
//                                  @Part("wednesday_closing") RequestBody wednesday_closing,
//                                  @Part("thursday_opening") RequestBody thursday_opening,
//                                  @Part("thursday_closing") RequestBody thursday_closing,
//                                  @Part("friday_opening") RequestBody friday_opening,
//                                  @Part("friday_closing") RequestBody friday_closing,
//                                  @Part("saturday_opening") RequestBody saturday_opening,
//                                  @Part("saturday_closing") RequestBody saturday_closing,
//                                  @Part("sunday_opening") RequestBody sunday_opening,
//                                  @Part("sunday_closing") RequestBody sunday_closing,
//                                  @Part("email") RequestBody email,
//                                  @Part("telephone") RequestBody telephone,
//                                  @Part("website") RequestBody website,
//                                  @Part("applink") RequestBody app_link,
//                                  @Part("lat") RequestBody lat,
//                                  @Part("long") RequestBody longi,
//                                  @Part("ethos") RequestBody company_ethos,
//                                  @Part("paypal_nonce") RequestBody paypal_nonce,
//                                  @Part("expiry_date") RequestBody expiry_date);
//
//    @Multipart
//    @POST("user/company/update")
//    Call<Response> Update_Company(@Part MultipartBody.Part companyLogo,
//                                  @Part List<MultipartBody.Part> file,
//                                  @Part("id") RequestBody company_id,
//                                  @Part("category_id") RequestBody category_id,
//                                  @Part("company_name") RequestBody company_name,
//                                  @Part("building_number") RequestBody building_number,
//                                  @Part("address_line_1") RequestBody address_line_1,
//                                  @Part("city") RequestBody city,
//                                  @Part("postcode") RequestBody r_postcode,
//                                  @Part("country") RequestBody r_Country,
//                                  @Part("monday_opening") RequestBody monday_opening,
//                                  @Part("monday_closing") RequestBody monday_closing,
//                                  @Part("tuesday_opening") RequestBody tuesday_opening,
//                                  @Part("tuesday_closing") RequestBody tuesday_closing,
//                                  @Part("wednesday_opening") RequestBody wednesday_opening,
//                                  @Part("wednesday_closing") RequestBody wednesday_closing,
//                                  @Part("thursday_opening") RequestBody thursday_opening,
//                                  @Part("thursday_closing") RequestBody thursday_closing,
//                                  @Part("friday_opening") RequestBody friday_opening,
//                                  @Part("friday_closing") RequestBody friday_closing,
//                                  @Part("saturday_opening") RequestBody saturday_opening,
//                                  @Part("saturday_closing") RequestBody saturday_closing,
//                                  @Part("sunday_opening") RequestBody sunday_opening,
//                                  @Part("sunday_closing") RequestBody sunday_closing,
//                                  @Part("email") RequestBody email,
//                                  @Part("telephone") RequestBody telephone,
//                                  @Part("website") RequestBody website,
//                                  @Part("applink") RequestBody app_link,
//                                  @Part("lat") RequestBody lat,
//                                  @Part("long") RequestBody longi,
//                                  @Part("ethos") RequestBody company_ethos,
//                                  @Part("paypal_nonce") RequestBody paypal_nonce,
//                                  @Part("expiry_date") RequestBody expiry_date,
//                                  @Part("deletedimages") RequestBody p_deleteImage);
//
//    @POST("user/company/delete")
//    Call<Response> Delete_Company(@Body Company company); //jjj
//
//    @POST("user/company/get")
//    Call<Response> Get_Company(@Body Company company);  //jjj
//
//    @POST("getcompanydetail")
//    Call<Response> Get_CompanyDetails(@Body Company company);
//
//
//    @POST("getfavouritecompanies")
//    Call<Response> GetFavouriteCompany();
//
//    @POST("user/company/changestatus")
//    Call<Response> ActiveSuspendCompany(@Body Company company);
//
//    @POST("addtofavourite")
//    Call<Response> AddFavouriteCompany(@Body Company company);
//
//    @POST("removefavourite")
//    Call<Response> RemoveFavouriteCompany(@Body Company company);
//
//    @POST("user/company/offer/get")
//    Call<Response> GetOffers(@Body Offers offers);
//
//    @POST("user/company/offer/getall")
//    Call<Response> GetAllOffers(@Body Company company);
//
//    @POST("user/company/offer/update")
//    Call<Response> UpdateOffers(@Body Offers offers);
//
//    @POST("user/company/offer/delete")
//    Call<Response> DeleteOffers(@Body Offers offers);
//
//    @POST("user/company/offer/create")
//    Call<Response> AddOffers(@Body Offers offers);
//
//    @POST("addtocustomers")
//    Call<Response> AddCustomer(@Body Company company);
//
//    @POST("getcompanycustomers")
//    Call<Response> GetCompanyCustomer(@Body Company company);
//
//    @POST("removecustomer")
//    Call<Response> RemoveCustomer(@Body Company company);
//
//    @POST("GetCartegoryProducts")
//    Call<Response> GetCartegoryProducts(@Body Category category);
//
//    @Multipart
//    @POST("user/company/product/create")
//    Call<Response> Create_Product(@Part List<MultipartBody.Part> parts,
//                                  @Part("product_name") RequestBody p_name,
//                                  @Part("description") RequestBody p_desc,
//                                  @Part("price") RequestBody p_price,
//                                  @Part("company_id") RequestBody p_companyId,
//                                  @Part("currency_id") RequestBody p_currency_id);
//
//    @Multipart
//    @POST("user/company/product/update")
//    Call<Response> Update_Product(@Part List<MultipartBody.Part> parts,
//                                  @Part("id") RequestBody p_id,
//                                  @Part("product_name") RequestBody p_name,
//                                  @Part("description") RequestBody p_desc,
//                                  @Part("price") RequestBody p_price,
//                                  @Part("company_id") RequestBody p_companyId,
//                                  @Part("deletedimages") RequestBody p_deleteImage,
//                                  @Part("currency_id") RequestBody p_currency_id);
//
//    @POST("user/company/product/getall")
//    Call<Response> GetAllCompanyProducts(@Body Company company);
//
//    @POST("user/company/product/get")
//    Call<Response> GetProducts(@Body Product product);  // hghhjhjj
//
//    @POST("user/company/product/delete")
//    Call<Response> DeleteProduct(@Body Product product);
//
//    @POST("forgotpassword")
//    Call<Response> ForgotPassword(@Body Users useremail);
//
//    @POST("verifyopt")
//    Call<Response> VerifyOTP(@Body Users useremail_otp_fcmtoken);
//
//    @POST("changepassword")
//    Call<Response> ChangePassword(@Body Users password);
//
//
//    @POST("neabycompanies")
//    Call<Response> NearByCompanies(@Body Company latlongunits);
//
//    @GET("getallcurrencies")
//    Call<Response> GetAllCurrencies();
//
//    @POST("usernotifications")
//    Call<Response> GetNotificationList(@Body Notification notipage);

//    @POST("user/company/pay")
//    Call<Response> SavePaymentNonce(@Body Company company );

}
