package com.hkm.dltstclien;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class testingApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acmain2);
        general_test();
    }

    private void general_test() {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, new TestGen(), "general")
                .addToBackStack(null)
                .commit();
    }

    /**
     * extensive conten start from here
     */
   /* private void bind_not_login(ConfigurationSync data) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(data.getFoundation().data.host);
        sb.append("/login");
        getFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, loginfb.newInstance(

                        loginfb.getURL(sb.toString())
                ), "fblogin")
                .addToBackStack(null)
                .commit();
    }

    private void bind_has_login(ConfigurationSync data) {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.main_container,
                        new login_view(),
                        "haslogin")
                .addToBackStack(null)
                .commit();
    }*/


}
