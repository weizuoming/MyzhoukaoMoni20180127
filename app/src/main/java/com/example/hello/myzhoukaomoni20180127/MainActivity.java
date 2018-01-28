package com.example.hello.myzhoukaomoni20180127;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.hello.myzhoukaomoni20180127.activity.CustomBanner;
import com.example.hello.myzhoukaomoni20180127.activity.CutomBean;
import com.example.hello.myzhoukaomoni20180127.activity.OkHttp3Util_03;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private CustomBanner customBanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customBanner = findViewById(R.id.custom_banner);

        //customBanner.setTimeSeconds(5);

        Map<String, String> params = new HashMap<>();

        OkHttp3Util_03.doPost("https://www.zhaoapi.cn/ad/getAd", params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    final CutomBean cutomBean = new Gson().fromJson(json,CutomBean.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> imageUrls = new ArrayList<>();

                            final List<CutomBean.DataBean> data = cutomBean.getData();
                            for (int i = 0;i<data.size();i++) {
                                imageUrls.add(data.get(i).getIcon());
                            }


                            //设置图片地址的数据
                            customBanner.setImageUrls(imageUrls);
                            //设置点击事件
                            customBanner.setOnBannerListner(new CustomBanner.OnBannerListner() {
                                @Override
                                public void onBannerClick(int position) {
                                    Toast.makeText(MainActivity.this,data.get(position).getTitle(),Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                }
            }
        });



    }
}
